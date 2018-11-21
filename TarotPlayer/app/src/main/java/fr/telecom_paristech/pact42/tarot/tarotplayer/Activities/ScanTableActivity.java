/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;
import android.widget.TextView;

import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.CardAcquisition;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Divers.PhotoDegueuException;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
/**
 *  This activity is used to take a picture of the other players' cards, analyze them and store them in the current game object.
 *  @version 1.0
 *  @see PlayerIddleActivity
 */
public class ScanTableActivity extends AppCompatActivity {
    /**
     * Number of scanned cards
     */
    public static int cardsScanned = 0;
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
     *  @see TarotGame#printLastCards(TableLayout, Context)
     *  @see PlayerIddleActivity
     *  @see CardAcquisition
     */
    @Override
    protected void onResume() {

        super.onResume();
        TableLayout cardsArray = findViewById(R.id.cardsArrayHand);
        currentGame.printLastCards(cardsArray, this);

        TextView cardNumber = findViewById(R.id.scannedCards);
        cardNumber.setText(String.valueOf(cardsScanned));
        if (cardsScanned < 1) {
            android.hardware.Camera camera = CardAcquisition.openFrontalCamera();
            CardAcquisition.takePicture(camera);
            Handler handler = new Handler();
            handler.postDelayed(new Thread(new Runnable() {
                public void run() {
                    cardAcquisition();
                }
            }), 1000);
        } else {
            Intent playeriddleActivity = new Intent(ScanTableActivity.this, PlayerIddleActivity.class);
            playeriddleActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                    currentGame);
            startActivity(playeriddleActivity);
        }
    }

    /**
     * Setter of the number of scanned cards. It is often reset.
     * @param i
     *      New value of the number of card scanned.
     */
    public static void setCardsScanned(int i) {
        cardsScanned = i;
    }
    /**
     * Called when a picture was taken to analyze it using the image recognition library. It tells if the
     * recognition was correct or not.
     * @see SuccessfulScanActivity
     * @see UnsuccessfulScanTableActivity
     * @see PhotoDegueuException
     * @see CardAcquisition#cardRecognitionTable()
     */
    private void cardAcquisition() {
        String response = null;
        boolean error;
        try {
            response = CardAcquisition.cardRecognitionTable();
            error = false;
        } catch (PhotoDegueuException e) {
            e.printStackTrace();
            error = true;
        }
        if (error) {
            Intent unsuccessfulScanTableActivity = new Intent(ScanTableActivity.this,
                    UnsuccessfulScanTableActivity.class);
            onPause();
            unsuccessfulScanTableActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                    currentGame);
            ScanTableActivity.this.startActivity(unsuccessfulScanTableActivity);
        } else {
            currentGame.addPlayedCard(response);
            cardsScanned++;
            Intent successfulScanActivity = new Intent(ScanTableActivity.this, SuccessfulScanActivity.class);
            startActivity(successfulScanActivity);
        }

    }
}
