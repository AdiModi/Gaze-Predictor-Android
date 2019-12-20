package com.adimodi96.snapfeatures;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;
    Size cameraPictureSize;
    SurfaceHolder surfaceHolder;
    Context context;

    public CameraView(Context context, Camera camera) {
        super(context);
        this.context = context;
        this.camera = camera;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
    }

    public void pausePreview() {
        camera.stopPreview();
    }

    public void resumePreview() {
        camera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Camera.Parameters cameraParameters = camera.getParameters();
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            cameraParameters.set("orietation", "portrait");
            cameraParameters.setRotation(90);
            camera.setDisplayOrientation(90);
        } else {
            cameraParameters.set("orietation", "landscape");
            cameraParameters.setRotation(0);
            camera.setDisplayOrientation(0);
        }
        cameraPictureSize = cameraParameters.getSupportedPictureSizes().get(0);

        cameraParameters.setPictureSize(cameraPictureSize.width, cameraPictureSize.height);
        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        cameraParameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        cameraParameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        cameraParameters.setJpegQuality(100);
        cameraParameters.setVideoStabilization(true);

        camera.setParameters(cameraParameters);

        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        resumePreview();
        /*surfaceHolder.setFixedSize(1, 1);*/
        surfaceHolder.setKeepScreenOn(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        /*camera.release();*/
    }
}
