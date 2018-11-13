package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.AIPlayer;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.RealPlayer;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile.CardFile;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static String playerName1="";
    private static String playerName2="";
    private static String playerName3="";
    private TarotGame currentGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ImageView goButton = (ImageView) findViewById(R.id.playerGo);

        goButton.setOnClickListener(this);

        currentGame = (TarotGame) getIntent().getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");
    }
    @Override
    public void onClick(View view) {
        // we have to specify an error if the players' name have not been written.
        Intent scanHandActivity = new Intent(PlayerActivity.this, fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.ScanHandActivity.class);

        setPlayers();
        startGame();
        scanHandActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",currentGame);
        startActivity(scanHandActivity);

    }

    private void startGame() {
        try {
            Game.initGame(  new RealPlayer(0),
                            new RealPlayer(1),
                            new RealPlayer(2),
                            new AIPlayer(3, new CardFile(MainActivity.aiCardsPath)));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setPlayers() {
        EditText name1 = (EditText) findViewById(R.id.player1Name);
        EditText name2 = (EditText) findViewById(R.id.player2Name);
        EditText name3 = (EditText) findViewById(R.id.player3Name);

        playerName1 = name1.getText().toString();
        playerName2 = name2.getText().toString();
        playerName3 = name3.getText().toString();

        currentGame.setPlayer(playerName1);
        currentGame.setPlayer(playerName2);
        currentGame.setPlayer(playerName3);

        String players =    playerName1 + "\n" +
                            playerName2 + "\n" +
                            playerName3;

        String fileName = "players.txt";
        File file=null;
        PrintWriter pw=null;
        try {
            file = new File(MainActivity.path,fileName);
            pw = new PrintWriter(new FileOutputStream(file));
            pw.print(players);
        }
        catch (Exception e) {
            Log.e("WritingFile", "The file could not be written: " + e.getLocalizedMessage());
            Log.d("WritingFile", file.getAbsolutePath());}
        finally {
            try {pw.close();}
            catch (Exception e) {}
        }
    }
    public static String getPlayerName1(){return playerName1;}
    public static String getPlayerName2(){return playerName2;}
    public static String getPlayerName3(){return playerName3;}
}
