/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

/**
 *  This activity is used to indicate the difficulty of the AI of the application. It has two buttons, so it implements the interface onclicklistener
 *  @version 1.0
 *
 *  @see PlayerActivity
 */
public class DifficultyActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     *  This is the current game. It constains all the information about the cards, the players,etc.
     *  @see #onCreate(Bundle)
     *  @see TarotGame
     */
    private TarotGame currentGame;
    /**
     *  We have to initiate all the buttons, Imageview, and set them as listeners. We have to the initiate current game aswell.
     *
     *  @see AppCompatActivity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        Button button1 = findViewById(R.id.difficulty1);
        Button button2 = findViewById(R.id.difficulty2);
        Button button3 = findViewById(R.id.difficulty2);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        currentGame = new TarotGame();
    }
    /**
     *  This method defines what to if we click the validation of the card button
     *   to start the PlayerActivity. We create all the important files to make the app work
     * @param view
     *  The listener
     * @see View.OnClickListener#onClick(View)
     * @see PlayerActivity
     */
    @Override
    public void onClick(View view) {
        Button button = (Button) view;

        setDifficulty(button.getText().toString());

        createFile(MainActivity.AI_CARDS_PATH);
        createFile(Game.GAME_FILE_NAME);
        createFile(Game.BID_FILE_NAME);
        createFile(Game.CHIEN_FILE_NAME);

        Intent playerActivity = new Intent(DifficultyActivity.this, PlayerActivity.class);
        playerActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME", currentGame);
        startActivity(playerActivity);
    }

    /**
     *  We set the difficilty of the IA and write it down in  a file.
     * @param s
     *  The difficulty selected
     *  @see #onClick(View)
     */
    private void setDifficulty(String s) {
        currentGame.setDifficulty(s);

        String name = "difficulty.txt";
        File file = null;
        PrintWriter pw = null;
        try {
            file = new File(MainActivity.MAIN_PATH, name);
            pw = new PrintWriter(new FileOutputStream(file));
            pw.print(s);
        } catch (Exception e) {
            Log.e("WritingFile", "The file could not be written: " + e.getLocalizedMessage());
            Log.d("WritingFile", file.getAbsolutePath());
        } finally {
            try {
                pw.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * This method is used to delete and recreate all the files of the app.
     * @param path
     *  The absolute path of the file to create.
     */
    private void createFile(String path) {
        try {
            File file = new File(path);

            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            Log.e("Creating file", "The file could not be created: " + e.getLocalizedMessage());
        }
    }
}
