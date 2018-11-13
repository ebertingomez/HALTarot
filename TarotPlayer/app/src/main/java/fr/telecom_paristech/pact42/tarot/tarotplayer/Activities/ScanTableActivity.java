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

import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.CardAcquisition;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.SuccessfulScanDialog;

public class ScanTableActivity extends AppCompatActivity {
    private static int cardsScanned=0;
    private String cardList="";
    private TarotGame currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_hand);

        currentGame = (TarotGame) getIntent().getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");
    }

    @Override
    protected void onResume() {
        super.onResume();
            Handler handler = new Handler();
            handler.postDelayed(new Thread(new Runnable() {
                public void run() {
                    cardAcquisition();
                    addCardsToFile(cardList);
                    cardList = "";
                    finish();
                }
            }), 100);

    }

    private void cardAcquisition() {
        String response = CardAcquisition.cardRecognition();
        cardsScanned++;
        Intent successfulScanActivity = new Intent(ScanTableActivity.this, SuccessfulScanActivity.class);
        startActivity(successfulScanActivity);
    }

    private void addCardsToFile(String text) {
        String name = "playedCards.txt";
        //File file = new File(getCacheDir()+name);
        File file=null;
        PrintWriter pw=null;
        try {
            file = new File(MainActivity.path,name);
            pw = new PrintWriter(new FileOutputStream(file));
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
}
