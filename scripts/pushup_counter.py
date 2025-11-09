import cv2
import numpy as np
from rtmlib import Body, draw_skeleton

# This script uses rtmlib for pose estimation and OpenCV for capture & rendering.
# Extension: only count push-ups when both elbow angle and body alignment (hip) are within "good" thresholds.

# Initialize RTMlib Body pose estimator
# mode='lightweight' uses RTMPose-Light (MobileNetV2); 'balanced' is ResNet50; 'performance' is ShuffleNet.
device, backend = 'cpu', 'onnxruntime'
body = Body(mode='lightweight', backend=backend, device=device)

# Angle calculation helper
def calculate_angle(a, b, c):
    a, b, c = np.array(a), np.array(b), np.array(c)
    rad = np.arctan2(c[1]-b[1], c[0]-b[0]) - np.arctan2(a[1]-b[1], a[0]-b[0])
    ang = np.abs(rad * 180.0 / np.pi)
    return 360-ang if ang>180 else ang

# Good-form thresholds
ELBOW_DOWN_MAX = 90    # elbow flex angle at bottom <= 90°
ELBOW_UP_MIN = 160     # elbow extension at top >= 160°
HIP_ANGLE_TOL = 15     # acceptable deviation from straight (180°)

cap = cv2.VideoCapture(0)
count, stage = 0, None

while cap.isOpened():
    ret, frame = cap.read()
    if not ret: break

    kp, scores = body(frame)  # returns list of (x,y)
    if kp is not None:
        # Key COCO indices
        shoulder, elbow, wrist = kp[5], kp[7], kp[9]
        hip, knee = kp[11], kp[13]

        # Compute angles
        elbow_ang = calculate_angle(shoulder, elbow, wrist)
        hip_ang = calculate_angle(shoulder, hip, knee)

        # Form check
        elbow_ok = elbow_ang < ELBOW_DOWN_MAX if stage=='up' else elbow_ang > ELBOW_UP_MIN
        hip_ok = abs(hip_ang-180) < HIP_ANGLE_TOL
        good_form = elbow_ok and hip_ok

        # Count logic: only transitions with good_form
        if elbow_ang > ELBOW_UP_MIN:
            stage = 'up'
        if elbow_ang < ELBOW_DOWN_MAX and stage=='up' and hip_ok:
            stage, count = 'down', count+1

        # Visualization
        vis = draw_skeleton(frame.copy(), kp, scores, kpt_thr=0.3)
        # Color-coded feedback
        if not hip_ok:
            cv2.putText(vis, 'Keep your back straight!', (10,120), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0,0,255),2)
        if elbow_ang > ELBOW_DOWN_MAX or elbow_ang < ELBOW_UP_MIN:
            cv2.putText(vis, 'Lower further or raise higher for full rep', (10,150), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0,165,255),2)

        # Overlay counts and angles
        cv2.putText(vis, f'Reps: {count}', (10,30), cv2.FONT_HERSHEY_SIMPLEX,1,(0,255,0),2)
        cv2.putText(vis, f'Elbow: {int(elbow_ang)}deg', (10,70), cv2.FONT_HERSHEY_SIMPLEX,0.8,(255,255,255),2)
        cv2.putText(vis, f'Hip: {int(hip_ang)}deg', (10,100), cv2.FONT_HERSHEY_SIMPLEX,0.8,(255,255,255),2)
        cv2.imshow('Good-Form Push-up Detector', vis)
    else:
        cv2.imshow('Good-Form Push-up Detector', frame)

    if cv2.waitKey(10)&0xFF==ord('q'): break

cap.release()
cv2.destroyAllWindows()





