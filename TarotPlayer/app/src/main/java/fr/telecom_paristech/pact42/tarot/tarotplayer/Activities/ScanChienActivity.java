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
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.EnchereLibrary;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Divers.PhotoDegueuException;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
/**
 *  This activity is used to take a picture of each chien card, analyze it and store it in the current game object.
 *  @version 1.0
 *  @see ChangeChienCardActivity
 *  @see PlayerIddleActivity
 */
public class ScanChienActivity extends AppCompatActivity {
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
     *  @see TarotGame#printChienCards(TableLayout, Context)
     *  @see PlayerIddleActivity
     *  @see ChangeChienCardActivity
     *  @see EnchereLibrary#enchereTableValue
     *  @see CardAcquisition
     */
    @Override
    protected void onResume() {
        super.onResume();
        cardsScanned = currentGame.getChienList().size();
        TableLayout cardsArray = findViewById(R.id.cardsArrayHand);
        currentGame.printChienCards(cardsArray, this);

        TextView cardNumber = findViewById(R.id.scannedCards);
        cardNumber.setText(String.valueOf(cardsScanned));
        if (cardsScanned < 6) {
            android.hardware.Camera camera = CardAcquisition.openFrontalCamera();
            CardAcquisition.takePicture(camera);
            Handler handler = new Handler();
            handler.postDelayed(new Thread(new Runnable() {
                public void run() {
                    cardAcquisition();
                }
            }), 1000);
        } else {
            int myEncherevalue = EnchereLibrary.enchereTableValue.get(EnchereActivity.myEnchere);
            int player1Encherevalue = EnchereLibrary.enchereTableValue.get(EnchereActivity.player1Enchere);
            int player2Encherevalue = EnchereLibrary.enchereTableValue.get(EnchereActivity.player2Enchere);
            int player3Encherevalue = EnchereLibrary.enchereTableValue.get(EnchereActivity.player3Enchere);

            cardsScanned = 0;

            if ((EnchereActivity.myEnchere.equals("PE")
                    || EnchereActivity.myEnchere.equals("GA") && (myEncherevalue > player1Encherevalue
                            && myEncherevalue > player2Encherevalue && myEncherevalue > player3Encherevalue))) {

                Intent changeChienCardActivity = new Intent(ScanChienActivity.this, ChangeChienCardActivity.class);
                changeChienCardActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                        currentGame);
                startActivity(changeChienCardActivity);
            } else {
                Intent playerIddleActivity = new Intent(ScanChienActivity.this, PlayerIddleActivity.class);
                playerIddleActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                        currentGame);
                startActivity(playerIddleActivity);
            }
        }
    }

    /**
     * Called when a picture was taken to analyze it using the image recognition library. It tells if the
     * recognition was correct.
     * @see SuccessfulScanActivity
     * @see UnsuccessfulScanChienActivity
     * @see PhotoDegueuException
     * @see CardAcquisition#cardRecognitionChien()
     */
    private void cardAcquisition() {
        String response = null;
        boolean error;
        try {
            response = CardAcquisition.cardRecognitionChien();
            error = false;
        } catch (PhotoDegueuException e) {
            e.printStackTrace();
            error = true;
        }
        if (error) {
            Intent unsuccessfulScanChienActivity = new Intent(ScanChienActivity.this,
                    UnsuccessfulScanChienActivity.class);
            unsuccessfulScanChienActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                    currentGame);
            startActivity(unsuccessfulScanChienActivity);
        } else {
            currentGame.addChienCard(response);
            Intent successfulScanActivity = new Intent(ScanChienActivity.this, SuccessfulScanActivity.class);
            startActivity(successfulScanActivity);
        }
    }
}
