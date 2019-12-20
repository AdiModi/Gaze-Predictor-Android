package com.adimodi96.snapfeatures;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class Regressor {

    private float[] faceGrid;
    private ByteBuffer faceImageByteBuffer, leftEyeImageByteBuffer, rightEyeImageByteBuffer, faceGridByteBuffer;
    private Interpreter interpreter;
    private Context context;
    private Bitmap faceImageBitmap, leftEyeImageBitmap, rightEyeImageBitmap;
    private Object[] inputs;
    private Map<Integer, Object> outputs;
    float[][] coordinates;


    Regressor(Context context) {
        super();
        this.context = context;
        try {
            interpreter = new Interpreter(loadModelFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        rightEyeImageByteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        rightEyeImageByteBuffer.order(ByteOrder.nativeOrder());

        leftEyeImageByteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        leftEyeImageByteBuffer.order(ByteOrder.nativeOrder());

        faceImageByteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        faceImageByteBuffer.order(ByteOrder.nativeOrder());

        faceGridByteBuffer = ByteBuffer.allocateDirect(4 * 625);
        faceGridByteBuffer.order(ByteOrder.nativeOrder());

        this.inputs = null;
        this.coordinates = new float[][]{{0.0f, 0.0f}};
        this.outputs = new HashMap<>();
        this.outputs.put(0, coordinates);
    }

    public float[] regress(Features features) {
        setImageTensor(features);
        interpreter.runForMultipleInputsOutputs(this.inputs, this.outputs);
        Toast.makeText(context, coordinates[0][0] + ", " + coordinates[0][1], Toast.LENGTH_SHORT).show();
        return coordinates[0];
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd("regressor_model.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long declaredLength = assetFileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void setImageTensor(Features features) {

        rightEyeImageByteBuffer.rewind();
        leftEyeImageByteBuffer.rewind();
        faceImageByteBuffer.rewind();
        faceGridByteBuffer.rewind();

        rightEyeImageBitmap = features.getRightEyeImageBitmap();
        leftEyeImageBitmap = features.getLeftEyeImageBitmap();
        faceImageBitmap = features.getFaceImageBitmap();
        faceGrid = features.getFaceGrid();

        int[] rightEyePixels = new int[224 * 224];
        int[] leftEyePixels = new int[224 * 224];
        int[] facePixels = new int[224 * 224];
        rightEyeImageBitmap.getPixels(rightEyePixels, 0, 224, 0, 0, 224, 224);
        leftEyeImageBitmap.getPixels(leftEyePixels, 0, 224, 0, 0, 224, 224);
        faceImageBitmap.getPixels(facePixels, 0, 224, 0, 0, 224, 224);

        int red, blue, green;
        for (int i = 0; i < rightEyePixels.length; i++) {

            red = Color.red(rightEyePixels[i]);
            green = Color.green(rightEyePixels[i]);
            blue = Color.blue(rightEyePixels[i]);

            rightEyeImageByteBuffer.putFloat(blue * 1.0f);
            rightEyeImageByteBuffer.putFloat(green * 1.0f);
            rightEyeImageByteBuffer.putFloat(red * 1.0f);

            red = Color.red(leftEyePixels[i]);
            green = Color.green(leftEyePixels[i]);
            blue = Color.blue(leftEyePixels[i]);

            leftEyeImageByteBuffer.putFloat(blue * 1.0f);
            leftEyeImageByteBuffer.putFloat(green * 1.0f);
            leftEyeImageByteBuffer.putFloat(red * 1.0f);

            red = Color.red(facePixels[i]);
            green = Color.green(facePixels[i]);
            blue = Color.blue(facePixels[i]);

            faceImageByteBuffer.putFloat(blue * 1.0f);
            faceImageByteBuffer.putFloat(green * 1.0f);
            faceImageByteBuffer.putFloat(red * 1.0f);
        }

        for (int i = 0; i < faceGrid.length; i++) {
            faceGridByteBuffer.putFloat(faceGrid[i]);
        }

        inputs = new Object[]{rightEyeImageByteBuffer, leftEyeImageByteBuffer, faceImageByteBuffer, faceGridByteBuffer};

        coordinates[0][0] = 0.0f;
        coordinates[0][1] = 0.0f;
    }
}
