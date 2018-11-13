package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.CardAcquisition;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.SuccessfulScanDialog;

public class PlayerIddleActivity extends AppCompatActivity implements View.OnClickListener {
    private TarotGame currentGame;
    private int counter = 0;
    private String cardList="";
    private static int cardsScanned=0;
    private SuccessfulScanDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_iddle);


        ImageView button = (ImageView) findViewById(R.id.nextPlayer);

        ImageView card1 = (ImageView) findViewById(R.id.firstCard);
        ImageView card2 = (ImageView) findViewById(R.id.secondCard);
        ImageView card3 = (ImageView) findViewById(R.id.thirdCard);

        TextView text1 = (TextView) findViewById(R.id.firstCardName);
        TextView text2 = (TextView) findViewById(R.id.secondCardName);
        TextView text3 = (TextView) findViewById(R.id.thirdCardName);

        button.setOnClickListener(this);

        currentGame = (TarotGame) getIntent().getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(isMyTurn()){
            String card2Play = getCard();
            Intent cardDecisionActivity = new Intent(PlayerIddleActivity.this, CardDecisionActivity.class);
            cardDecisionActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",currentGame);
            cardDecisionActivity.putExtra("card2Play",card2Play);
            startActivity(cardDecisionActivity);
        }

    }
    @Override
    public void onClick(View view) {
        if(isMyTurn()){
            String card2Play = getCard();
            Intent cardDecisionActivity = new Intent(PlayerIddleActivity.this, CardDecisionActivity.class);
            cardDecisionActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",currentGame);
            cardDecisionActivity.putExtra("card2Play",card2Play);
            startActivity(cardDecisionActivity);
        }
        Intent scanTableActivity = new Intent(PlayerIddleActivity.this, ScanTableActivity.class);
        startActivity(scanTableActivity);

    }

    private boolean isMyTurn() {
        try {
            if (Game.play() == 2){
                counter++;
                return true;
            }
            else if (Game.play() == 1){
                counter++;
                return false;
            }
            else {
                finish();
                //end game
            }
        }
        catch (Exception e) {e.printStackTrace();}
        return false;
    }

    public String getCard() {
        return String.valueOf((counter+1)/2)+"A";
    }
}
