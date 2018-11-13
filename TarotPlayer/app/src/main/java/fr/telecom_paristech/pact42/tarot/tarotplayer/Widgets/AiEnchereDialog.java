package fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.PlayerIddleActivity;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;


public class AiEnchereDialog extends Dialog implements View.OnClickListener {
    TarotGame currentGame;
    public AiEnchereDialog(@NonNull Context context, int enchere, TarotGame currentGame) {
        super(context);
        this.currentGame = currentGame;
        setContentView(R.layout.ai_encheres);
        Button goButton = (Button) findViewById(R.id.ai_enchere_result_go);
        ImageView image = (ImageView) findViewById(R.id.ai_enchere_result_image);
        TextView text = (TextView) findViewById(R.id.ai_enchere_result_text);

        image.setImageResource(enchere);

        goButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        try {Game.bid();}
        catch (Exception e) {
            e.printStackTrace();}

        Intent playerIddleActivity = new Intent(getContext(), PlayerIddleActivity.class);

        playerIddleActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",currentGame);
        getContext().startActivity(playerIddleActivity);
    }
}
