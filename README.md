# Clurash

Classify ur Trash Bangkit Capstone

This is a Flask API for image classification using a pre-trained Keras model.

## Prerequisites

Before running the application, ensure you have the following dependencies installed:

- Python 3.x
- pip

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/Syauqi-ty/Clurash
   ```

2. Navigate to directory:

   ```bash
   cd Cluras
   ```

3. Setup venv:

   ```bash
   python -m venv venv
   venv\Scripts\activate
   ```

4. Install the required library:

   ```bash
   pip install flask
   pip install keras
   pip install pillow
   pip install numpy
   pip install tensorflow
   ```

5. Run the app:

   ```bash
   python app.py
   ```

## Important

IMPORTANT: When making a request to the API, use the "form data" body type with the key "image" to upload the image file. Do NOT use JSON format for the request.
