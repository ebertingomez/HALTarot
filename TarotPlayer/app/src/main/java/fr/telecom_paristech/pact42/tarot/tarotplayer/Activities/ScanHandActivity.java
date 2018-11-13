package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.CardAcquisition;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

public class ScanHandActivity extends AppCompatActivity {
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
        TableLayout cardsArray = (TableLayout) findViewById(R.id.cardsArrayHand);
        currentGame.printCards(cardsArray,this);

        TextView cardNumber = (TextView) findViewById(R.id.scannedCards);
        cardNumber.setText(String.valueOf(cardsScanned));


        if ( cardsScanned < 6){
            Handler handler = new Handler();
            handler.postDelayed(new Thread(new Runnable() {
                public void run() {
                    cardAcquisition();
                }
            }), 500);
        }
        else{
            addCardsToFile(cardList);
            Intent enchereActivity = new Intent(ScanHandActivity.this, EnchereActivity.class);

            enchereActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",currentGame);

            startActivity(enchereActivity);
        }
    }

    private void cardAcquisition() {
        String response = CardAcquisition.cardRecognition();
        if(cardsScanned == 0){cardList = cardList + response;}
        else cardList = cardList+"\n"+response;

        currentGame.addPlayingCard(response);

        cardsScanned++;
        Intent successfulScanActivity = new Intent(ScanHandActivity.this, SuccessfulScanActivity.class);
        startActivity(successfulScanActivity);
    }

    private void addCardsToFile(String text) {

        String name = "cartes.txt";
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


}
