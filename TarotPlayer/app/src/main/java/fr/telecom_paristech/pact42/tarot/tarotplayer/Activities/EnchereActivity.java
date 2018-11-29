/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.EnchereLibrary;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.AiEnchereDialog;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.InformationDialog;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.PopupEnchereDialog;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.WarningDialog;
/**
 *  This activity is used set all the encheres of the players and to generate the AI's. It has a button, so it implements the interface onclicklistener
 *  @version 1.0
 *  @see PlayerIddleActivity
 *  @see ScanChienActivity
 *  @see AiEnchereDialog
 *  @see PopupEnchereDialog
 */
public class EnchereActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * The player 1's enchere
     * @see #setEnchere(String, Drawable)
     */
    public static String player1Enchere = null;
    /**
     * The player 2's enchere
     * @see #setEnchere(String, Drawable)
     */
    public static String player2Enchere = null;
    /**
     * The player 3's enchere
     * @see #setEnchere(String, Drawable)
     */
    public static String player3Enchere = null;
    /**
     * The AI's enchere
     * @see #getMyEnchere()
     */
    public static String myEnchere = null;
    /**
     * The number of the player whose enchere where are treating.
     * @see #setActualPlayer(int)
     */
    private int actualPlayer;
    /**
     *  This is the current game. It constains all the information about the cards, the players,etc.
     *  @see #onCreate(Bundle)
     *  @see TarotGame
     */
    private TarotGame currentGame;
    /**
     * This is the absolute path of the file where all the enchere will be writtten.
     */
    private String pathEncheres = Game.BID_FILE_NAME;
    /**
     *  We have to initiate all the buttons, Imageview, and set them as listeners. We have to import the current game aswell.
     *
     *  @see AppCompatActivity#onCreate(Bundle)
     *  @see Game#bid()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enchere);

        try {
            int i = Game.bid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView goButton = findViewById(R.id.enchereGo);
        ImageView info = findViewById(R.id.information);
        ImageView enchere1 = findViewById(R.id.enchere1);
        ImageView enchere2 = findViewById(R.id.enchere2);
        ImageView enchere3 = findViewById(R.id.enchere3);

        TextView name1 = findViewById(R.id.enchere1Name);
        TextView name2 = findViewById(R.id.enchere2Name);
        TextView name3 = findViewById(R.id.enchere3Name);

        name1.setText(PlayerActivity.getPlayerName1());
        name2.setText(PlayerActivity.getPlayerName2());
        name3.setText(PlayerActivity.getPlayerName3());

        goButton.setOnClickListener(this);
        info.setOnClickListener(this);
        enchere1.setOnClickListener(this);
        enchere2.setOnClickListener(this);
        enchere3.setOnClickListener(this);

        currentGame = getIntent()
                .getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");

    }
    /**
     *  This method defines what to if we click the info button, each player face to set their enchere, or
     *  the button to set the IA's enchere.
     * @param view
     *  The listener
     * @see View.OnClickListener#onClick(View)
     * @see PlayerIddleActivity
     * @see ScanChienActivity
     */
    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i) {
        case R.id.enchereGo:
            calculateEnchere();
            break;
        case R.id.information:
            String message = "Click on the button to validate the played card";
            InformationDialog infoBox = new InformationDialog(this, message);
            infoBox.show();
            break;
        default:
            setActualPlayer(i);
            PopupEnchereDialog box = new PopupEnchereDialog(this);
            box.show();
            break;
        }
    }

    /**
     * This method is used to set the order of the actual player whose enchere is being selected
     * @param nameInd
     *      Order of the player
     */
    public void setActualPlayer(int nameInd) {
        this.actualPlayer = nameInd;
    }

    /**
     * This method is used to save the enchere of the actual player in a file and to update the face of
     * each player in function of their enchere
     * @param enchere
     *      The enchere of the player
     * @param image
     *      The image of the player which is going to be updated.
     * @see #actualPlayer
     */
    public void setEnchere(String enchere, Drawable image) {
        switch (actualPlayer) {
        case R.id.enchere1:
            EnchereActivity.player1Enchere = enchere;
            ImageView enchere1 = findViewById(R.id.enchere1);
            enchere1.setImageDrawable(image);
            break;
        case R.id.enchere2:
            EnchereActivity.player2Enchere = enchere;
            ImageView enchere2 = findViewById(R.id.enchere2);
            enchere2.setImageDrawable(image);
            break;
        case R.id.enchere3:
            EnchereActivity.player3Enchere = enchere;
            ImageView enchere3 = findViewById(R.id.enchere3);
            enchere3.setImageDrawable(image);
            break;
        }
        File file = null;
        PrintWriter pw = null;
        try {
            file = new File(pathEncheres);
            pw = new PrintWriter(new FileOutputStream(file, true));
            pw.println(enchere);
        } catch (Exception e) {
            Log.e("WritingFile", "The file could not be written: " + e.getLocalizedMessage());
            Log.d("WritingFile", file.getAbsolutePath());
        } finally {
            try {
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to calculate the AI's enchere and to show the result.
     * @see #getMyEnchere()
     * @see AiEnchereDialog
     */
    private void calculateEnchere() {
        if (player1Enchere != null && player2Enchere != null && player3Enchere != null) {
            int enchere = getMyEnchere();
            AiEnchereDialog box = new AiEnchereDialog(this, enchere, currentGame);
            box.show();
        } else {
            String message = "Be sure to set all the encheres";
            WarningDialog box = new WarningDialog(this, message);
            box.show();
        }

    }

    /**
     * This method is used to read the file where all the encheres are written and get the AI's.
     * @return
     *    The ID of the image of the selected enchere
     *    @see Game#bid()
     *    @see EnchereLibrary#enchereTable
     */
    private int getMyEnchere() {
        String line, last = null;
        try {
            Game.bid();
            BufferedReader input = new BufferedReader(new FileReader(pathEncheres));

            while ((line = input.readLine()) != null) {
                last = line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        EnchereActivity.myEnchere = last;
        return EnchereLibrary.enchereTable.get(last);
    }

    /**
     * This method is a getter of the current tarot game
     * @return
     *      The current tarot game object reference.
     */
    public TarotGame getCurrentGame() {
        return currentGame;
    }
}
