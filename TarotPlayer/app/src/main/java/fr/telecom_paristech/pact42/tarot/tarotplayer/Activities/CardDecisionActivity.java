package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;

import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotCard;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

public class CardDecisionActivity extends AppCompatActivity implements View.OnClickListener {

    private TarotGame currentGame;
    private String card2Play;

    private boolean test=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_decision);
        currentGame = (TarotGame) getIntent().getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");
        card2Play   = getIntent().getStringExtra("card2Play");
        ImageView button = (ImageView) findViewById(R.id.validButton);
        button.setOnClickListener(this);
    }


    @Override
    public void onResume(){
        super.onResume();
        TableLayout table = (TableLayout) findViewById(R.id.cardsArray);
        currentGame.showCard(card2Play);
        currentGame.printCards(table,this);
        currentGame.hideCard(card2Play);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}


