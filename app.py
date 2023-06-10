from flask import Flask, request, jsonify, Blueprint
from keras.models import load_model
from PIL import Image, ImageOps
import numpy as np
import mysql.connector

db = mysql.connector.connect(
    host="localhost",
    user="root",
    password="",
    database="clurash",
)
cursor = db.cursor()
create_table_query = """
    CREATE TABLE IF NOT EXISTS users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL,
        password VARCHAR(255) NOT NULL,
        points INT DEFAULT 0
    )
"""
cursor.execute(create_table_query)
api_bp = Blueprint("api", _name_)
app = Flask(_name_)
model = None  # Define a global variable
class_names = None


def new_model():
    global model
    model = load_model("keras_Model.h5", compile=False)


# Load the model during application startup


# Load the labels
def load_class():
    global class_names
    class_names = open("labels.txt", "r").readlines()


new_model()
load_class()


# Define an API endpoint
@api_bp.route("/predict", methods=["POST"])
def predict():
    # Get the image from the request
    file = request.files["image"]

    # Load and preprocess the image
    image = Image.open(file).convert("RGB")
    size = (224, 224)
    image = ImageOps.fit(image, size, Image.Resampling.LANCZOS)
    image_array = np.asarray(image)
    normalized_image_array = (image_array.astype(np.float32) / 127.5) - 1
    data = np.ndarray(shape=(1, 224, 224, 3), dtype=np.float32)
    data[0] = normalized_image_array

    # Make predictions
    prediction = model.predict(data)
    index = np.argmax(prediction)
    class_name = class_names[index].split(" ", 1)[1].strip()
    confidence_score = prediction[0][index]

    # Prepare the response
    response = {
        "class_name": class_name,
        "confidence_score": float(confidence_score),
    }

    return jsonify(response)


@api_bp.route("/register", methods=["POST"])
def register_user():
    data = request.get_json()

    # Extract username, email, and password from the request body
    username = data.get("username")
    email = data.get("email")
    password = data.get("password")

    # Perform validation on the input data
    if not username or not email or not password:
        return jsonify({"error": "Missing required fields"}), 400

    # Check if the user already exists in the database
    query = "SELECT * FROM users WHERE username = %s OR email = %s"
    cursor.execute(query, (username, email))
    existing_user = cursor.fetchone()

    if existing_user:
        return jsonify({"error": "User already exists"}), 409

    # Insert the new user into the database
    insert_query = "INSERT INTO users (username, email, password) VALUES (%s, %s, %s)"
    cursor.execute(insert_query, (username, email, password))
    db.commit()

    # Return a success message
    return jsonify({"message": "User registered successfully"}), 200


@api_bp.route("/login", methods=["POST"])
def login_user():
    data = request.get_json()

    # Extract username and password from the request body
    username = data.get("username")
    password = data.get("password")

    # Perform validation on the input data
    if not username or not password:
        return jsonify({"error": "Missing required fields"}), 400

    # Check if the user exists in the database
    query = "SELECT * FROM users WHERE username = %s AND password = %s"
    cursor.execute(query, (username, password))
    user = cursor.fetchone()

    if user:
        # Extract username and ID from the user tuple
        user_id = user[0]
        username = user[1]

        # Return the username and ID upon successful login
        return (
            jsonify(
                {"message": "Login successful", "username": username, "id": user_id}
            ),
            200,
        )
    else:
        return jsonify({"error": "Invalid credentials"}), 401


@api_bp.route("/update-points", methods=["PUT"])
def update_points():
    data = request.get_json()

    # Extract username from the request body
    username = data.get("username")

    # Perform validation on the input data
    if not username:
        return jsonify({"error": "Missing required fields"}), 400

    # Check if the user exists in the database
    query = "SELECT * FROM users WHERE username = %s"
    cursor.execute(query, (username,))
    user = cursor.fetchone()

    if user:
        # Update the user's points
        update_query = "UPDATE users SET points = points + 10 WHERE username = %s"
        cursor.execute(update_query, (username,))
        db.commit()

        return jsonify({"message": "Points updated successfully"}), 200
    else:
        return jsonify({"error": "User not found"}), 404


@api_bp.route('/points', methods=['GET'])
def get_user_points():
    username = request.args.get('username')

    # Perform validation on the input data
    if not username:
        return jsonify({'error': 'Missing username parameter'}), 400

    # Check if the user exists in the database
    query = "SELECT points FROM users WHERE username = %s"
    cursor.execute(query, (username,))
    user = cursor.fetchone()

    if user:
        points = user[0]
        return jsonify({'username': username, 'points': points}), 200
    else:
        return jsonify({'error': 'User not found'}), 404


app.register_blueprint(api_bp, url_prefix="/api")

# Run the Flask app
if _name_ == "_main_":
    port = 5000  # Specify the desired port number
    app.run(port=port)
    print(f"Running on http://127.0.0.1:{port}/")
