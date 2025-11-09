# Push-up Counter (rtmlib + OpenCV)

This folder contains a standalone Python script that uses `rtmlib` pose estimation and OpenCV to count push-ups with basic form checks (elbow depth and straight hips).

## Files
- `pushup_counter.py`: Main script (copied from your working version).
- `requirements.txt`: Dependencies to run the script.

## Windows setup (PowerShell)
```powershell
# From project root (FitnessTracker/)
cd scripts

# Optional: Create and activate a virtual environment
python -m venv .venv
. .venv\Scripts\Activate.ps1

# Install dependencies
pip install -r requirements.txt

# Run (press 'q' to quit)
python .\pushup_counter.py
```

## Notes
- The script uses `onnxruntime` on CPU by default. If you have a supported GPU, you can try `onnxruntime-gpu` and adjust the backend accordingly in `pushup_counter.py`.
- Make sure your webcam is accessible and not used by another app.
- If `rtmlib` models need to be downloaded on first run, ensure you have an internet connection.

## Android integration
This script runs on desktop Python. If you want this feature directly inside the Android app, we should implement pose estimation natively (e.g., MediaPipe or ONNX runtime in Kotlin) and port the counting logic. Tell me if you want me to proceed with an in-app camera feature.





