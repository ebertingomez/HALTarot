/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.EnchereActivity;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.PlayerIddleActivity;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.ScanChienActivity;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

/**
 * This class is used to show the AI's enchere.
 * @version 1.0
 * @see ScanChienActivity
 * @see PlayerIddleActivity
 * @see Dialog
 * @see View.OnClickListener
 */
public class AiEnchereDialog extends Dialog implements View.OnClickListener {
    /**
     *  This is the current game. It constains all the information about the cards, the players,etc.
     *  @see TarotGame
     */
    TarotGame currentGame;

    /**
     * The constructor of this class. It instantiates all the elements of the dialog.
     * @param context
     *      The context of the application from where it is called
     * @param enchere
     *      The selected enchere for the AI player.
     * @param currentGame
     *      The reference to the current game to be passed to another activity.
     */
    public AiEnchereDialog(@NonNull Context context, int enchere, TarotGame currentGame) {
        super(context);
        this.currentGame = currentGame;
        setContentView(R.layout.ai_encheres);
        Button goButton = findViewById(R.id.ai_enchere_result_go);
        ImageView image = findViewById(R.id.ai_enchere_result_image);
        TextView text = findViewById(R.id.ai_enchere_result_text);

        image.setImageResource(enchere);

        goButton.setOnClickListener(this);

    }
    /**
     *  This method makes the application continue with its flow and identifies if the chien has to be
     *  scanned or not.
     * @param view
     *  The listener
     * @see View.OnClickListener#onClick(View)
     * @see ScanChienActivity
     * @see PlayerIddleActivity
     */
    @Override
    public void onClick(View view) {
        if (EnchereActivity.myEnchere.equals("PA") && EnchereActivity.player1Enchere.equals("PA")
                && EnchereActivity.player2Enchere.equals("PA") && EnchereActivity.player3Enchere.equals("PA")) {
            Intent playerIddleActivity = new Intent(getContext(), PlayerIddleActivity.class);

            playerIddleActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                    currentGame);
            getContext().startActivity(playerIddleActivity);
        } else {
            Intent scanChienActivity = new Intent(getContext(), ScanChienActivity.class);

            scanChienActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME", currentGame);
            getContext().startActivity(scanChienActivity);
        }
    }
}
