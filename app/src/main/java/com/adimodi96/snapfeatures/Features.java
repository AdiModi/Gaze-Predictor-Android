package com.adimodi96.snapfeatures;

import android.graphics.Bitmap;

public class Features {
    private Bitmap rightEyeImageBitmap, leftEyeImageBitmap, faceImageBitmap;
    private float[] faceGrid;

    public Features() {
        super();
    }

    public Features(Bitmap rightEyeImageBitmap, Bitmap leftEyeImageBitmap, Bitmap faceImageBitmap, float[] faceGrid) {
        this.rightEyeImageBitmap = rightEyeImageBitmap;
        this.leftEyeImageBitmap = leftEyeImageBitmap;
        this.faceImageBitmap = faceImageBitmap;
        this.faceGrid = faceGrid;
    }

    public float[] getFaceGrid() {
        return faceGrid;
    }

    public void setFaceGrid(float[] faceGrid) {
        this.faceGrid = faceGrid;
    }

    public Bitmap getRightEyeImageBitmap() {
        return rightEyeImageBitmap;
    }

    public void setRightEyeImageBitmap(Bitmap rightEyeImageBitmap) {
        this.rightEyeImageBitmap = rightEyeImageBitmap;
    }

    public Bitmap getLeftEyeImageBitmap() {
        return leftEyeImageBitmap;
    }

    public void setLeftEyeImageBitmap(Bitmap leftEyeImageBitmap) {
        this.leftEyeImageBitmap = leftEyeImageBitmap;
    }

    public Bitmap getFaceImageBitmap() {
        return faceImageBitmap;
    }

    public void setFaceImageBitmap(Bitmap faceImageBitmap) {
        this.faceImageBitmap = faceImageBitmap;
    }
}
