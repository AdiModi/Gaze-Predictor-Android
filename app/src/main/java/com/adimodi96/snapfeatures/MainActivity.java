package com.adimodi96.snapfeatures;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

public class MainActivity extends Activity {

    Camera camera;
    CameraView cameraView;
    Button captureButton;
    PictureCallback pictureCallback;
    RelativeLayout relativeLayout_for_CameraView;
    Regressor regressor;
    FaceDetector faceDetector;
    Frame frame;

    Bitmap imageBitmap, faceImageBitmap, leftEyeImageBitmap, rightEyeImageBitmap;
    float[] faceGrid;

    private Features extractFeatures(Bitmap imageBitmap) {
        frame = new Frame.Builder().setBitmap(imageBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        /*Extracting only the first face*/
        Face face = faces.valueAt(0);
        faceImageBitmap = Bitmap.createScaledBitmap(Bitmap.createBitmap(imageBitmap, (int) (face.getPosition().x), (int) (face.getPosition().y), (int) (face.getWidth()), (int) (face.getHeight())), 224, 224, false);

        double left_x = 0, left_y = 0, right_x = 0, right_y = 0, distance_between_eyes = 0;
        double interval_width = (imageBitmap.getWidth() / 25), interval_height = (imageBitmap.getHeight() / 25);
        faceGrid = new float[625];

        /*Getting Landmarks for Right and Left Eyes*/
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == Landmark.LEFT_EYE) {
                right_x = landmark.getPosition().x;
                right_y = landmark.getPosition().y;
            } else if (landmark.getType() == Landmark.RIGHT_EYE) {
                left_x = landmark.getPosition().x;
                left_y = landmark.getPosition().y;
            }
        }

        /*Getting Distance Between Eyes*/
        distance_between_eyes = Math.sqrt(Math.pow(left_x - right_x, 2) + Math.pow(left_y - right_y, 2));
        float halfBoxSize = (int) (distance_between_eyes * 0.4);

        /*Extracting Eyes*/
        leftEyeImageBitmap = Bitmap.createScaledBitmap(Bitmap.createBitmap(imageBitmap, (int) (left_x - halfBoxSize), (int) (left_y - halfBoxSize), (int) (2 * halfBoxSize), (int) (2 * halfBoxSize)), 224, 224, false);
        rightEyeImageBitmap = Bitmap.createScaledBitmap(Bitmap.createBitmap(imageBitmap, (int) (right_x - halfBoxSize), (int) (right_y - halfBoxSize), (int) (2 * halfBoxSize), (int) (2 * halfBoxSize)), 224, 224, false);

        /*Creating Face Grid*/
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                if (((j * interval_width) > face.getPosition().x) && (j * interval_width) < (face.getPosition().x + face.getWidth()) &&
                        ((i * interval_height) > face.getPosition().y) && (i * interval_height) < (face.getPosition().y + face.getHeight())) {
                    faceGrid[(i * 25) + j] = 1.0f;
                } else {
                    faceGrid[(i * 25) + j] = 0.0f;

                }
            }
        }

        return new Features(rightEyeImageBitmap, leftEyeImageBitmap, faceImageBitmap, faceGrid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout_for_CameraView = findViewById(R.id.relativeLayout_for_cameraView);
        captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera != null) {
                    camera.takePicture(null, null, pictureCallback);
                }
            }
        });

        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                long startTime = System.currentTimeMillis();
                imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageBitmap = Bitmap.createBitmap(imageBitmap, (int) ((imageBitmap.getWidth() - imageBitmap.getHeight()) / 2), 0, imageBitmap.getHeight(), imageBitmap.getHeight());
                cameraView.resumePreview();
                Features features = extractFeatures(imageBitmap);
                float[] coordinates = regressor.regress(features);
                long endTime = System.currentTimeMillis();
                Utils.saveFeaturesAndPrediction(features, coordinates);

                Toast.makeText(MainActivity.this, "Time Taken to Predict: " + (endTime - startTime), Toast.LENGTH_SHORT).show();
            }
        };

        camera = Camera.open(1);
        cameraView = new CameraView(this, camera);

        faceDetector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setMode(FaceDetector.FAST_MODE)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        relativeLayout_for_CameraView.addView(cameraView);

        regressor = new Regressor(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.pausePreview();
    }
}
