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
 * This activity is used to change/take the cards of the chien.
 * @version 1.0
 * @see EnchereActivity
 * @see ScanChienActivity
 */
public class ChangeChienCardActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     *  This is the current game. It constains all the information about the cards, the players,etc.
     *  @see #onCreate(Bundle)
     *  @see TarotGame
     */
    private TarotGame currentGame;
    /**
     *  This is the card that is going to be selected to be shown and changed with the playing cards.
     */
    private ImageView card2Change;
    /**
     *  An instruction to show the card number to be changed.
     */
    private TextView changeCardInstruction;
    /**
     *  A temporal counter to determine the number of cards already changed
     */
    private int counter = 0;

    /**
     *  We have to initiate all the buttons, Imageview, and set them as listeners. We have to import current game aswell.
     *
     *  @see AppCompatActivity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_chien_card);
        currentGame = getIntent()
                .getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");
        ImageView button = findViewById(R.id.validButton);
        ImageView info = findViewById(R.id.information);
        card2Change = findViewById(R.id.card2Change);
        changeCardInstruction = findViewById(R.id.changeInstruction);
        button.setOnClickListener(this);
        info.setOnClickListener(this);
    }

    /**
     * This method is used to update the visualisation of this activity
     *  @see AppCompatActivity#onResume()
     *  @see TarotGame#printChienCards(TableLayout, Context)
     */
    @Override
    public void onResume() {
        super.onResume();
        TableLayout table = findViewById(R.id.cardsArray);
        currentGame.printGameCards(table, this);
        card2Change.setImageResource(currentGame.getChienCard(counter).getImageID());
    }

    /**
     *  This method defines what to if we click the info button or the validation of the change of card. We will update
     *  the activity too and start the playeriddleactivity
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
            if (counter < 6) {
                String card2change = currentGame.getCard2Change();
                int cardPosition = currentGame.getIndexOf(card2change);
                changeCardInstruction.setText("Change the card number " + String.valueOf(cardPosition));
                currentGame.changeCard(card2change, currentGame.getChienCard(counter).getName());
                counter++;
                card2Change.setImageResource(currentGame.getChienCard(counter).getImageID());
            } else {
                Intent playerIddleActivity = new Intent(ChangeChienCardActivity.this, PlayerIddleActivity.class);
                playerIddleActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                        currentGame);
                startActivity(playerIddleActivity);
            }
            break;
        case R.id.information:
            String message = "Click on the button to validate the played card";
            InformationDialog box = new InformationDialog(this, message);
            box.show();
            break;
        }
    }

}
