from flask import Flask, request, jsonify
from keras.models import load_model
from PIL import Image, ImageOps
import numpy as np

app = Flask(__name__)
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


# Define an API endpoint
@app.route("/predict", methods=["POST"])
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


# Run the Flask app
if __name__ == "__main__":
    new_model()
    load_class()
    port = 5000  # Specify the desired port number
    app.run(port=port)
    print(f"Running on http://127.0.0.1:{port}/")
