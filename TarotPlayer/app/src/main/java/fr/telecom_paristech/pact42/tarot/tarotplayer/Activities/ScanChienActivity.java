package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

public class ScanChienActivity extends AppCompatActivity {
    private static int cardsScanned = 0;
    private String cardList="";
    private android.hardware.Camera camera = null;
    private SurfaceHolder mHolder=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_hand);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ( cardsScanned < 7){
            Handler handler = new Handler();
            handler.postDelayed(new Thread(new Runnable() {
                public void run() {
                    cardAcquisition();
                }
            }), 1000);
        }
        else{
            addCardsToFile(cardList);
            Intent scanHandActivity = new Intent(ScanChienActivity.this, EnchereActivity.class);
            startActivity(scanHandActivity);
        }
    }

    private void cardAcquisition() {
        String response = null;
        android.hardware.Camera camera = openFrontalCamera();
        do {
            String path = takePicture(camera);
            response = analyse(path);
        }
        while (response.equals("error"));

        if(cardsScanned == 0){cardList = cardList + response;}
        else cardList = cardList+"\n"+response;
        cardsScanned++;
        camera.release();
        Intent successfulScanActivity = new Intent(ScanChienActivity.this, SuccessfulScanActivity.class);
        startActivity(successfulScanActivity);
    }

    private void addCardsToFile(String text) {
        String name = "chien.txt";
        //File file = new File(getCacheDir()+name);
        File file=null;
        PrintWriter pw=null;
        try {
            file = new File(MainActivity.path,name);
            pw = new PrintWriter(new FileOutputStream(file));

            if (cardsScanned != 0) {pw.println();}
            pw.print(text);
        }
        catch (Exception e) {
            Log.e("WritingFile", "The file could not be written: " + e.getLocalizedMessage());
            Log.d("WritingFile", file.getAbsolutePath());}
        finally {
            try {pw.close();}
            catch (Exception e) {}
        }
    }

    private String analyse(String path) {
        try { Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        return "Good";
    }

    private String takePicture(android.hardware.Camera camera) {
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

        String path = null;

        android.hardware.Camera.PictureCallback pngCallback = new android.hardware.Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                BufferedOutputStream bo = null;
                try {
                    String path = MainActivity.path + "/photo"+(cardsScanned)+".jpeg";
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
    private android.hardware.Camera openFrontalCamera() {
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
