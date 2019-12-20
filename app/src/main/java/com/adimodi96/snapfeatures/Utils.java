package com.adimodi96.snapfeatures;

import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {

    public static File[] getOutputFiles(String[] featureNames, long timestamp) {
        File mediaStorageFolderPath = new File(Environment.getExternalStorageDirectory() + File.separator + "Gaze Predictor" + File.separator + "Image Features");
        if (!mediaStorageFolderPath.exists()) {
            if (!mediaStorageFolderPath.mkdirs()) {
                return null;
            }
        }

        File[] files = new File[featureNames.length + 1];
        for (int i = 0; i < featureNames.length; i++) {
            files[i] = new File(mediaStorageFolderPath.getPath() + File.separator + timestamp + "_" + featureNames[i] + ".jpg");
        }

        File logFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Gaze Predictor" + File.separator + "Log File.csv");

        files[files.length - 1] = logFile;

        return files;
    }

    public static void saveImageBitmap(Bitmap bitmapImage, File imageFilePath) {
        if (imageFilePath == null) {
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageFilePath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writecsv(File file, long timestamp, float[] coordinates, float[] faceGrid){
        try{
            if(!file.exists()){
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("Instance,");
                fileWriter.write("X,");
                fileWriter.write("Y,");
                for (int i = 0; i<faceGrid.length; i++){
                    if(i == faceGrid.length - 1){
                        fileWriter.write("grid_" + i + "\n");
                    } else {
                        fileWriter.write("grid_" + i + ",");
                    }
                }
                fileWriter.close();
            }

            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(timestamp + ",");
            fileWriter.write(coordinates[0] + ",");
            fileWriter.write(coordinates[1] + ",");
            for (int i = 0; i<faceGrid.length; i++){
                if(i == faceGrid.length - 1){
                    fileWriter.write(faceGrid[i] + "\n");
                } else {
                    fileWriter.write(faceGrid[i] + ",");
                }
            }
            fileWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void saveFeaturesAndPrediction(Features features, float[] coordinates) {
        long timestamp = System.currentTimeMillis();

        File[] files = getOutputFiles(new String[]{"Right_Eye", "Left_Eye", "Face"}, timestamp);
        writecsv(files[files.length - 1], timestamp, coordinates, features.getFaceGrid());

        saveImageBitmap(features.getRightEyeImageBitmap(), files[0]);
        saveImageBitmap(features.getLeftEyeImageBitmap(), files[1]);
        saveImageBitmap(features.getFaceImageBitmap(), files[2]);
    }
}
