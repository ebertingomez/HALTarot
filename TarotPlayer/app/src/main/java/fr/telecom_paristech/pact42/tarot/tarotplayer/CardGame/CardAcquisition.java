package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.MainActivity;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.ScanHandActivity;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.SuccessfulScanActivity;

public class CardAcquisition {
    private android.hardware.Camera camera = null;
    private SurfaceHolder mHolder=null;

    static {
        System.loadLibrary("native-lib");
    }

    public static String cardRecognition() {
        String response = null;
        android.hardware.Camera camera = openFrontalCamera();
        do {
            String path = takePicture(camera);
            response = analyse(path);
        }
        while (response.equals("error"));
        camera.release();
        return response;

    }

    private static String analyse(String path) {

        return analyzeFromJNI(path);
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native static String analyzeFromJNI(String path);

    private static String takePicture(android.hardware.Camera camera) {
        SurfaceTexture surfaceTexture = new SurfaceTexture(10);
        try {
            camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }

        android.hardware.Camera.Parameters params = camera.getParameters();
        params.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        //params.setAutoWhiteBalanceLock(false);
        //params.setRecordingHint(true);
        params.setPictureSize(1028,768);
        params.setPictureFormat(ImageFormat.JPEG);
        //camera.setParameters(params);

        String path = MainActivity.path + "/photo"+".jpeg";

        android.hardware.Camera.PictureCallback pngCallback = new android.hardware.Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                BufferedOutputStream bo = null;
                try {
                    String path = MainActivity.path + "/photo"+".jpeg";
                    bo = new BufferedOutputStream( new FileOutputStream(path));
                    bo.write(data);
                } catch (Exception e) {
                    Log.e("TakePic", "Camera failed to take Picture: " + e.getMessage());
                } finally {
                    try { bo.close();} catch (Exception e) {}
                }
            }
        };
        try{
            camera.startPreview();
            camera.takePicture(null, null, pngCallback);}
        catch (Exception e){Log.e("TakePic2", "Camera failed to take Picture: " + e.getMessage());
            e.printStackTrace();}

        return path;
    }
    private static android.hardware.Camera openFrontalCamera() {
        int cameraCount = 0;
        android.hardware.Camera cam = null;
        android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
        cameraCount = android.hardware.Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx<cameraCount; camIdx++) {
            android.hardware.Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = android.hardware.Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("FrontalCamera", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
        return cam;
    }
}
