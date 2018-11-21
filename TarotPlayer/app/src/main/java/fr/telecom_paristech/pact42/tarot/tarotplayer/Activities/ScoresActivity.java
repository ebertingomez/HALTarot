/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.InformationDialog;
/**
 *  This activity is used to show all players' scores and to start a new game.
 *  @version 1.0
 *  @see DifficultyActivity
 */
public class ScoresActivity extends AppCompatActivity implements View.OnClickListener {
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
        setContentView(R.layout.activity_scores);
        ImageView button = findViewById(R.id.button);
        ImageView info = findViewById(R.id.information);
        button.setOnClickListener(this);
        info.setOnClickListener(this);
        currentGame = getIntent()
                .getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");
    }

    /**
     * Dispatch onResume() to fragments. Note that for better inter-operation with
     * older versions of the platform, at the point of this call the fragments
     * attached to the activity are <em>not</em> resumed. This means that in some
     * cases the previous state may still be saved, not allowing fragment
     * transactions that modify the state. To correctly interact with fragments in
     * their proper state, you should instead override {@link #onResumeFragments()}.
     *
     * Called to initiate the different elements of the view and update it.
     */
    @Override
    protected void onResume() {
        super.onResume();
        TextView player1 = findViewById(R.id.scoreWinnerMessage);
        TextView player1Score = findViewById(R.id.scoreWinnerScore);
        TextView player2 = findViewById(R.id.scorePlayerName2);
        TextView player2Score = findViewById(R.id.scorePlayerScore2);
        TextView player3 = findViewById(R.id.scorePlayerName3);
        TextView player3Score = findViewById(R.id.scorePlayerScore3);
        TextView player4 = findViewById(R.id.scorePlayerName4);
        TextView player4Score = findViewById(R.id.scorePlayerScore4);

        player1.setText(get1Place());
        player2.setText(get2Place());
        player3.setText(get3Place());
        player4.setText(get4Place());

        player1Score.setText(get1PlaceScore());
        player2Score.setText(get2PlaceScore());
        player3Score.setText(get3PlaceScore());
        player4Score.setText(get4PlaceScore());

    }
    /**
     * Called when a view has been clicked. It is used to distinguish if the user asks for information or if he wants to start
     * a new game.
     *
     * @param view The view that was clicked.
     * @see DifficultyActivity
     * @see android.view.View.OnClickListener#onClick(View)
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
        case R.id.button:
            Intent difficultyActivity = new Intent(ScoresActivity.this, DifficultyActivity.class);
            startActivity(difficultyActivity);
            break;
        case R.id.information:
            String message = "Click on the button to validate the played card";
            InformationDialog infoBox = new InformationDialog(this, message);
            infoBox.show();
            break;
        }
    }

    /**
     * Getter of the first place player
     * @return
     *      The name of the first place player.
     */
    public String get1Place() {
        return "AI";
    }
    /**
     * Getter of the second place player
     * @return
     *      The name of the second place player.
     */
    public String get2Place() {
        return currentGame.getPlayerList().get(0);
    }
    /**
     * Getter of the third place player
     * @return
     *      The name of the third place player.
     */
    public String get3Place() {
        return currentGame.getPlayerList().get(1);
    }
    /**
     * Getter of the fourth place player
     * @return
     *      The name of the fourth place player.
     */
    public String get4Place() {
        return currentGame.getPlayerList().get(2);
    }
    /**
     * Getter of the first place player's score
     * @return
     *      The name of the first place player's score.
     */
    public String get1PlaceScore() {
        return "100";
    }
    /**
     * Getter of the second place player's score
     * @return
     *      The name of the second place player's score.
     */
    public String get2PlaceScore() {
        return "90";
    }
    /**
     * Getter of the third place player's score
     * @return
     *      The name of the third place player's score.
     */
    public String get3PlaceScore() {
        return "80";
    }
    /**
     * Getter of the fourth place player's score
     * @return
     *      The name of the fourth place player's score.
     */
    public String get4PlaceScore() {
        return "70";
    }
}
