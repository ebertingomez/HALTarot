/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.InformationDialog;
/**
 *  This activity is used when the other players are choosing their play. There is a button to
 *  indicate that they already finish their turn to scan the card which is in the table.
 *  @version 1.0
 *  @see ScanTableActivity
 *  @see ScoresActivity
 */
public class PlayerIddleActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     *  This is the current game. It contains all the information about the cards, the players,etc.
     *  @see #onCreate(Bundle)
     *  @see TarotGame
     */
    private TarotGame currentGame;
    /**
     * Variable which counts the turns which passed.
     */
    private static int counter = 0;
    /**
     *  The path of the file where all the played cards are stored.
     */
    private String pathGame = Game.GAME_FILE_NAME;
    /**
     * Used to determine if it is the turn of the IA, the others players' turn, or if the game concluded
     * @see Game#play()
     */
    private int reference;
    /**
     * Used to determine if the IA has to play.
     */
    private static boolean IAplays;
    /**
     * Used to stored the last value of the reference when the AI has to play.
     */
    private static int lastReferenceValue;
    /**
     *  We have to initiate all the buttons, imageviews, and set them as listeners. We have to import the current game aswell.
     *  We start the game and ask the AI for its information about the turns.
     * @param savedInstanceState
     *       Parameters stored after calling onStop()
     *  @see AppCompatActivity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_iddle);
        ImageView button = findViewById(R.id.nextPlayer);
        ImageView info = findViewById(R.id.information);
        button.setOnClickListener(this);
        info.setOnClickListener(this);
        try {
            if (counter == 0) {
                reference = Game.play();
                //PlayerIddleActivity.IAplays = reference==1 ? true : false;
                //PlayerIddleActivity.lastReferenceValue = reference;
            }
            if (IAplays) {
                PlayerIddleActivity.IAplays = false;
                reference = PlayerIddleActivity.lastReferenceValue;
            } else {
                reference = Game.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentGame = getIntent()
                .getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");
    }
    /**
     * This method is used to update the visualisation of this activity.
     *  @see AppCompatActivity#onResume()
     *  @see TarotGame#printLastCards(TableLayout, Context)
     */
    @Override
    protected void onResume() {
        super.onResume();
        TableLayout cardsArray = findViewById(R.id.cardScannedArray);
        currentGame.printLastCards(cardsArray, this);
    }
    /**
     * Called when a view has been clicked. It is used to distinguish if the user asks for information or if a player
     * finished his turn. It informs the AI that someone played.
     *
     * @param view The view that was clicked.
     * @see CardDecisionActivity
     * @see ScanTableActivity
     * @see android.view.View.OnClickListener#onClick(View)
     * @see Game#play()
     */
    @Override
    public void onClick(View view) {
        PlayerIddleActivity.counter++;
        int id = view.getId();
        switch (id) {
        case R.id.nextPlayer:
            if (reference == 1) {
                PlayerIddleActivity.IAplays = true;
                try {
                    PlayerIddleActivity.lastReferenceValue = Game.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String card2Play = getCard();
                currentGame.addPlayedCard(card2Play);
                Intent cardDecisionActivity = new Intent(PlayerIddleActivity.this, CardDecisionActivity.class);
                cardDecisionActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                        currentGame);
                cardDecisionActivity.putExtra("card2Play", card2Play);
                startActivity(cardDecisionActivity);
                break;
            } else if (reference == 2) {
                ScanTableActivity.setCardsScanned(0);
                Intent scanTableActivity = new Intent(PlayerIddleActivity.this, ScanTableActivity.class);
                scanTableActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                        currentGame);
                startActivity(scanTableActivity);
                break;
            } else {
                counter = 0;
                Intent scoresActivity = new Intent(PlayerIddleActivity.this, ScoresActivity.class);
                scoresActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                        currentGame);
                startActivity(scoresActivity);
                break;
            }
        case R.id.information:
            String message = "Click on the button to validate the played card";
            InformationDialog infoBox = new InformationDialog(this, message);
            infoBox.show();
            break;
        }
    }

    /**
     * Used to get the card to play decided by the AI. It has the read the last line of a file used by the AI.
     * @return
     *      The card to play decided by the AI.
     */
    public String getCard() {
        String last=null, line;
        try {
            BufferedReader input = new BufferedReader(new FileReader(pathGame));
            while ((line = input.readLine()) != null) {
                last = line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return last;
    }
}
