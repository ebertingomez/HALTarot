/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.InformationDialog;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.WarningDialog;
/**
 *  This activity is used to set the name of each player.
 *  @version 1.0
 *  @see ScanHandActivity
 *  @see MainActivity
 *  @see #setPlayers()
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Player 1's name
     * @see #getPlayerName1()
     * @see #setPlayers()
     */
    private static String playerName1 = "";
    /**
     * Player 2's name
     * @see #getPlayerName1()
     * @see #setPlayers()
     */
    private static String playerName2 = "";
    /**
     * Player 3's name
     * @see #getPlayerName1()
     * @see #setPlayers()
     */
    private static String playerName3 = "";
    /**
     *  This is the current game. It constains all the information about the cards, the players,etc.
     *  @see #onCreate(Bundle)
     *  @see TarotGame
     */
    private TarotGame currentGame = new TarotGame();
    /**
     * List of all the assets which contains the files for the image recognition
     */

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ImageView goButton = findViewById(R.id.playerGo);
        ImageView info = findViewById(R.id.information);
        goButton.setOnClickListener(this);
        info.setOnClickListener(this);

    }
    /**
     * Called when a view has been clicked. It is used to distinguish if the user asks for information or if he wants to set
     * the other players' names.
     *
     * @param view The view that was clicked.
     * @see ScanHandActivity
     * @see android.view.View.OnClickListener#onClick(View)
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
        case R.id.playerGo:
            if (setPlayers()) {
                initFiles();
                Intent scanHandActivity = new Intent(PlayerActivity.this,
                        fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.ScanHandActivity.class);
                scanHandActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",
                        currentGame);
                startActivity(scanHandActivity);
            }
            break;
        case R.id.information:
            String message = "Click on the button to validate the played card";
            InformationDialog infoBox = new InformationDialog(this, message);
            infoBox.show();
            break;
        }

    }

    /**
     * Creation of all the files needed to run the application. The files for the image recognition
     * the ones which will contain the game status
     */
    private void initFiles() {
        createFile(MainActivity.AI_CARDS_PATH);
        createFile(Game.GAME_FILE_NAME);
        createFile(Game.BID_FILE_NAME);
        createFile(Game.CHIEN_FILE_NAME);
        createFile(MainActivity.MAIN_PATH+"/log.txt");

        createFileFromAssets("arbre_couleur_carte");
        createFileFromAssets("arbre_type_carte");
        createFileFromAssets("carreau.png");
        createFileFromAssets("coeur.png");
        createFileFromAssets("couleurs");
        createFileFromAssets("descripteurs");
        createFileFromAssets("liste");
        createFileFromAssets("pique.png");
        createFileFromAssets("points_caracteristiques");
        createFileFromAssets("trefle.png");
    }

    /**
     * Initialization of all the assets stored on the application
     * @param asset The name of the asset to generate
     */
    private void createFileFromAssets(String asset) {
        OutputStream os = null;
        InputStream is = null;
        AssetManager mngr= this.getAssets();
        try {
            is = mngr.open(asset);
            os =  new FileOutputStream(new File(MainActivity.MAIN_PATH+"/"+asset));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Called when all the names has been set to store them in the current game object.
     * @return
     *      If all the players' names has been set.
     * @see WarningDialog
     * @see #setPlayers()
     */
    private boolean setPlayers() {

        EditText name1 = findViewById(R.id.player1Name);
        EditText name2 = findViewById(R.id.player2Name);
        EditText name3 = findViewById(R.id.player3Name);

        playerName1 = name1.getText().toString();
        playerName2 = name2.getText().toString();
        playerName3 = name3.getText().toString();

        if (!playerName1.equals("") && !playerName2.equals("") && !playerName3.equals("")) {
            currentGame.setPlayer(playerName1);
            currentGame.setPlayer(playerName2);
            currentGame.setPlayer(playerName3);
            return true;
        } else {
            String message = "Be sure to set all the names";
            WarningDialog box = new WarningDialog(this, message);
            box.show();
        }
        return false;
    }

    /**
     * Getter of the player 1's name
     * @return
     *      The  player 1's name
     */
    public static String getPlayerName1() {
        return playerName1;
    }
    /**
     * Getter of the player 2's name
     * @return
     *      The  player 2's name
     */
    public static String getPlayerName2() {
        return playerName2;
    }
    /**
     * Getter of the player 3's name
     * @return
     *      The  player 3's name
     */
    public static String getPlayerName3() {
        return playerName3;
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