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

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.InformationDialog;
/**
 *  This activity is used to show all players' scores and to start a new game.
 *  @version 1.0
 *  @see PlayerActivity
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
        TextView winner = findViewById(R.id.scoreWinnerMessage);
        TextView winnerScore = findViewById(R.id.scoreWinnerScore);
        TextView loser = findViewById(R.id.scoreLoserMessage);
        TextView loserScore = findViewById(R.id.scoreLoserScore);

        if (Game.getTakerPoints()>Game.getOthersPoints()){
            winner.setText("Taker");
            loser.setText("Others");
            winnerScore.setText(Double.toString(Game.getTakerPoints()));
            loserScore.setText(Double.toString(Game.getOthersPoints()));
        }
        else {
            winner.setText("Others");
            loser.setText("Taker");
            winnerScore.setText(Double.toString(Game.getOthersPoints()));
            loserScore.setText(Double.toString(Game.getTakerPoints()));
        }
    }
    /**
     * Called when a view has been clicked. It is used to distinguish if the user asks for information or if he wants to start
     * a new game.
     *
     * @param view The view that was clicked.
     * @see PlayerActivity
     * @see android.view.View.OnClickListener#onClick(View)
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
        case R.id.button:
            Intent playerActivity = new Intent(ScoresActivity.this, PlayerActivity.class);
            startActivity(playerActivity);
            break;
        case R.id.information:
            String message = "Click on the button to validate the played card";
            InformationDialog infoBox = new InformationDialog(this, message);
            infoBox.show();
            break;
        }
    }
}
