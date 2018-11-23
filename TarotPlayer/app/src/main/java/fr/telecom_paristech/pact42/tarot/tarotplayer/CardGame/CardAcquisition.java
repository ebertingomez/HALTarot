/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

import android.content.res.AssetManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.MainActivity;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Divers.PhotoDegueuException;
/**
 *  This class is used to do all the card acquisition process. So it takes a picture and analyzes it.
 *  @version 1.0
 *  @see PhotoDegueuException
 *  @see android.hardware.Camera
 */
public class CardAcquisition {
    /**
     * Loading the native Library on C++ to use openCV
     */
    static {
        System.loadLibrary("native-lib");
    }

    /**
     *  This method is used to analyze a picture taken by the application. The picture has to be the one of a hand card
     * @return
     *      The value of the card
     * @throws PhotoDegueuException
     *      When the image recognition library could not get the card.
     * @see TarotCardLibrary#cards
     */
    public static String cardRecognitionHand() throws PhotoDegueuException {
        String res = "!!";
        String path = MainActivity.MAIN_PATH;
        res = analyse(path);
        res = res.toUpperCase();
        if (!TarotCardLibrary.cards.contains(res))
            throw new PhotoDegueuException();
        else
            return res;
    }
    /**
     *  This method is used to analyze a picture taken by the application. The picture has to be the one of a chien card
     * @return
     *      The value of the card
     * @throws PhotoDegueuException
     *      When the image recognition library could not get the card.
     * @see TarotCardLibrary#cards
     */
    public static String cardRecognitionChien() throws PhotoDegueuException {
        return CardAcquisition.cardRecognitionHand();
    }
    /**
     *  This method is used to analyze a picture taken by the application. The picture has to be the one of a table card
     * @return
     *      The value of the card
     * @throws PhotoDegueuException
     *      When the image recognition library could not get the card.
     * @see TarotCardLibrary#cards
     */
    public static String cardRecognitionTable() throws PhotoDegueuException {
        return CardAcquisition.cardRecognitionHand();
    }

    /**
     * This method is used to take a picture and store it in a predefined file.
     * @param camera
     *      The camera with which we will take the picture.
     * @see android.hardware.Camera
     * @see android.hardware.Camera#takePicture(Camera.ShutterCallback, Camera.PictureCallback, Camera.PictureCallback)
     */
    //TODO Use Camera2
    public static void takePicture(android.hardware.Camera camera) {
        SurfaceTexture surfaceTexture = new SurfaceTexture(10);
        try {
            camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }

        android.hardware.Camera.PictureCallback pngCallback = new android.hardware.Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                BufferedOutputStream bo = null;
                String path = MainActivity.MAIN_PATH + "/image.png";
                File pictureFile = new File(path);
                try {

                    bo = new BufferedOutputStream(new FileOutputStream(pictureFile));
                    bo.write(data);
                } catch (Exception e) {
                    Log.e("TakePic", "Camera failed to take Picture: " + e.getMessage());
                } finally {
                    try {
                        bo.close();
                        camera.release();

                    } catch (Exception e) {
                    }
                }
            }
        };

        try {
            camera.startPreview();
            camera.takePicture(null, null, pngCallback);
        } catch (Exception e) {
            Log.e("TakePic2", "Camera failed to take Picture: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * This method is used to select the frontal camera of the cellphone if there is one.
     * @return
     *      The reference of the frontal camera.
     */
    public static android.hardware.Camera openFrontalCamera() {
        int cameraCount;
        android.hardware.Camera cam = null;
        android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
        cameraCount = android.hardware.Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
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

    /**
     * This method is used to analyze a picture after giving a path
     * @param path
     *      The absolute path of the picture
     * @return
     *      THe valeur of the card
     */
    private static String analyse(String path) {
        return analyzeFromJNI(path);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application to analyze the picture taken and determine
     * if what kind of card it is.
     */
    public native static String analyzeFromJNI(String path);
}
