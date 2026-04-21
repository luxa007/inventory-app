from fastapi import FastAPI, UploadFile, File
import uvicorn

app = FastAPI()

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    # This is the "Brain". It receives the file from Java.
    # For your hackathon demo, this returns the data your HTML needs.
    print(f"Received file: {file.filename}")
    return {
        "name": "Organic Tomatoes", 
        "category": "Vegetables"
    }

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
