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
import android.widget.TextView;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.InformationDialog;

/**
 *  This activity is used to indicate the card that is going to be played. It has a button, so it implements the interface onclicklistener
 *  @version 1.0
 *  @see PlayerIddleActivity
 */
public class CardDecisionActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     *  This is the current game. It constains all the information about the cards, the players,etc.
     *  @see #onCreate(Bundle)
     *  @see TarotGame
     */
    private TarotGame currentGame;
    /**
     *  This is the card that is going to be selected to be shown and played.
     */
    private String card2Play;
    /**
     *  An instruction to show the card number to be played.
     */
    private TextView playCardInstruction;

    /**
     *  We have to initiate all the buttons, Imageview, and set them as listeners. We have to import the current game aswell.
     *  @see AppCompatActivity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_decision);
        currentGame = getIntent()
                .getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");
        card2Play = getIntent().getStringExtra("card2Play");
        ImageView button = findViewById(R.id.validButton);
        ImageView info = findViewById(R.id.information);
        playCardInstruction = findViewById(R.id.playInstruction);
        button.setOnClickListener(this);
        info.setOnClickListener(this);
    }

    /**
     * This method is used to update the visualisation of this activity
     *  @see AppCompatActivity#onResume()
     *  @see TarotGame#printLastCards(TableLayout, Context)
     */
    @Override
    public void onResume() {
        super.onResume();
        int cardPosition = currentGame.getIndexOf(card2Play);
        playCardInstruction.setText("Play the card number " + String.valueOf(cardPosition));
        TableLayout table = findViewById(R.id.cardsArray);
        currentGame.showCard(card2Play);
        currentGame.printGameCards(table, this);
        currentGame.hideCard(card2Play);
    }

    /**
     *  This method defines what to if we click the info button or the validation of the card button
     *   to start the PlayerIddleActivity.
     * @param view
     *  The listener
     * @see View.OnClickListener#onClick(View)
     * @see PlayerIddleActivity
     */
    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {
        case R.id.validButton:
            Intent playeriddleActivity = new Intent(CardDecisionActivity.this, PlayerIddleActivity.class);
            playeriddleActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                    currentGame);
            startActivity(playeriddleActivity);
            break;
        case R.id.information:
            String message = "Click on the button to validate the played card";
            InformationDialog box = new InformationDialog(this, message);
            box.show();
            break;
        }

    }
}
