package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
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
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.AiEnchereDialog;
import fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets.PopupEnchereDialog;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

public class EnchereActivity extends AppCompatActivity implements View.OnClickListener {
    private String player1Enchere = null;
    private String player2Enchere = null;
    private String player3Enchere = null;
    private int actualPlayer;
    private TarotGame currentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enchere);
        ImageView goButton = (ImageView) findViewById(R.id.enchereGo);
        ImageView enchere1 = (ImageView) findViewById(R.id.enchere1);
        ImageView enchere2 = (ImageView) findViewById(R.id.enchere2);
        ImageView enchere3 = (ImageView) findViewById(R.id.enchere3);

        TextView name1 = (TextView) findViewById(R.id.enchere1Name);
        TextView name2 = (TextView) findViewById(R.id.enchere2Name);
        TextView name3 = (TextView) findViewById(R.id.enchere3Name);

        name1.setText(PlayerActivity.getPlayerName1());
        name2.setText(PlayerActivity.getPlayerName2());
        name3.setText(PlayerActivity.getPlayerName3());

        goButton.setOnClickListener(this);
        enchere1.setOnClickListener(this);
        enchere2.setOnClickListener(this);
        enchere3.setOnClickListener(this);

        currentGame = (TarotGame) getIntent().getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i){
            case R.id.enchereGo:
                calculateEnchere();
                break;
            default:
                setActualPlayer(i);
                showDialog(R.layout.popup_encheres);
                break;
        }
    }

    public int getActualPlayer(){return  actualPlayer;}
    public void setActualPlayer(int nameInd){this.actualPlayer = nameInd;}
    public void setEnchere(String enchere, Drawable image){
        switch (actualPlayer){
            case R.id.enchere1:
                player1Enchere = enchere;
                ImageView enchere1 = (ImageView) findViewById(R.id.enchere1);
                enchere1.setImageDrawable(image);
                break;
            case R.id.enchere2:
                player2Enchere = enchere;
                ImageView enchere2 = (ImageView) findViewById(R.id.enchere2);
                enchere2.setImageDrawable(image);
                break;
            case R.id.enchere3:
                player3Enchere = enchere;
                ImageView enchere3 = (ImageView) findViewById(R.id.enchere3);
                enchere3.setImageDrawable(image);
                break;
        }
    }



    private void calculateEnchere() {
        writeEnchereToFile();
        int enchere = getMyEnchere();
        AiEnchereDialog box = new AiEnchereDialog(this,enchere,currentGame);
        box.show();

    }

    private int getMyEnchere() {
        try {Game.bid();}
        catch (Exception e) {e.printStackTrace();}

        return R.drawable.smiley1;
    }

    private void writeEnchereToFile() {
        String encheres =   PlayerActivity.getPlayerName1() +"\t"+ player1Enchere + "\n" +
                        PlayerActivity.getPlayerName2() +"\t"+ player2Enchere + "\n" +
                        PlayerActivity.getPlayerName3() +"\t"+ player3Enchere;
        String fileName = "encheres.txt";
        File file=null;
        PrintWriter pw=null;
        try {
            file = new File(MainActivity.path,fileName);
            pw = new PrintWriter(new FileOutputStream(file));
            pw.print(encheres);
        }
        catch (Exception e) {
            Log.e("WritingFile", "The file could not be written: " + e.getLocalizedMessage());
            Log.d("WritingFile", file.getAbsolutePath());}
        finally {
            try {pw.close();}
            catch (Exception e) {}
        }
    }

    public Dialog onCreateDialog(int d){
        PopupEnchereDialog box = new PopupEnchereDialog(this);
        return box;
    }

    public void onPrepareDialog (int id, PopupEnchereDialog box) {}

    public TarotGame getCurrentGame() {
        return currentGame;
    }
}
