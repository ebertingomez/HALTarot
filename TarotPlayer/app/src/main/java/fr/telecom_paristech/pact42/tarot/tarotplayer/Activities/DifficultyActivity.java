package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

public class DifficultyActivity extends AppCompatActivity implements View.OnClickListener {

    private TarotGame currentGame = new TarotGame();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        Button button1 = (Button) findViewById(R.id.difficulty1);
        Button button2 = (Button) findViewById(R.id.difficulty2);
        Button button3 = (Button) findViewById(R.id.difficulty2);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

    }

    @Override
    public Dialog onCreateDialog(int d){
        Dialog box = new Dialog(this);
        box.setContentView(d);
        return box;
    }
    @Override
    public void onPrepareDialog (int id, Dialog box) {}

    @Override
    public void onClick(View view) {
        Button button = (Button) view;

        setDifficulty(button.getText().toString());

        Intent playerActivity = new Intent(DifficultyActivity.this, PlayerActivity.class);
        playerActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME",currentGame);
        startActivity(playerActivity);
    }

    private void setDifficulty(String s) {
        currentGame.setDifficulty(s);

        String name = "difficulty.txt";
        //File file = new File(getCacheDir()+name);
        File file=null;
        PrintWriter pw=null;
        try {
            file = new File(MainActivity.path,name);
            pw = new PrintWriter(new FileOutputStream(file));
            pw.print(s);
        }
        catch (Exception e) {
            Log.e("WritingFile", "The file could not be written: " + e.getLocalizedMessage());
            Log.d("WritingFile", file.getAbsolutePath());}
        finally {
            try {pw.close();}
            catch (Exception e) {}
        }
    }
}
