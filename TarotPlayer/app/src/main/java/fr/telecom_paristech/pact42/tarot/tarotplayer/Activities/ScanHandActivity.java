/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.CardAcquisition;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Divers.PhotoDegueuException;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
/**
 *  This activity is used to take a picture of each AI's card, analyze it and store it in the current game object.
 *  @version 1.0
 *  @see ChangeChienCardActivity
 *  @see PlayerIddleActivity
 */
public class ScanHandActivity extends AppCompatActivity {
    /**
     * Number of scanned cards
     */
    public static int cardsScanned;
    /**
     *  This is the current game. It constains all the information about the cards, the players,etc.
     *  @see #onCreate(Bundle)
     *  @see TarotGame
     */
    private TarotGame currentGame;
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_hand);
        currentGame = getIntent()
                .getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");
    }
    /**
     * This method is used to take a picture of a card, order its acquisition and
     * update the visualisation of this activity. It decides whether to keep taking pictures or to continue with
     * the flow of the application.
     *  @see AppCompatActivity#onResume()
     *  @see TarotGame#printGameCards(TableLayout, Context)
     *  @see EnchereActivity
     *  @see CardAcquisition
     */
    @Override
    protected void onResume() {
        super.onResume();
        cardsScanned = currentGame.getCardList().size();
        TableLayout cardsArray = findViewById(R.id.cardsArrayHand);
        currentGame.printGameCards(cardsArray, this);

        TextView cardNumber = findViewById(R.id.scannedCards);
        cardNumber.setText(String.valueOf(cardsScanned));
        if (cardsScanned < 18) {
            android.hardware.Camera camera = CardAcquisition.openFrontalCamera();
            CardAcquisition.takePicture(camera);
            Handler handlerbis = new Handler();
            handlerbis.postDelayed(new Thread(new Runnable() {
                public void run() {
                    cardAcquisition();
                }
            }), 1000);
        } else {
            Intent enchereActivity = new Intent(ScanHandActivity.this, EnchereActivity.class);
            enchereActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME", currentGame);
            startActivity(enchereActivity);
        }
    }
    /**
     * Called when a picture was taken to analyze it using the image recognition library. It tells if the
     * recognition was correct.
     * @see SuccessfulScanActivity
     * @see UnsuccessfulScanHandActivity
     * @see PhotoDegueuException
     * @see CardAcquisition#cardRecognitionHand()
     */
    private void cardAcquisition() {
        String response = null;
        boolean error;
        try {
            response = CardAcquisition.cardRecognitionHand();
            error = false;
        } catch (PhotoDegueuException e) {
            e.printStackTrace();
            error = true;
        }
        if (error) {
            Intent unsuccessfulScanHandActivity = new Intent(ScanHandActivity.this, UnsuccessfulScanHandActivity.class);
            unsuccessfulScanHandActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                    currentGame);
            ScanHandActivity.this.startActivity(unsuccessfulScanHandActivity);
        } else {
            currentGame.addPlayingCard(response);
            Intent successfulScanActivity = new Intent(ScanHandActivity.this, SuccessfulScanActivity.class);
            startActivity(successfulScanActivity);
        }
    }
}
