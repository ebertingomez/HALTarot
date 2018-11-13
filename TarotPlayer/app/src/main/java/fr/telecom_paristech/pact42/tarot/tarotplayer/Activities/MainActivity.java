package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;

import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

public class MainActivity extends AppCompatActivity implements  View.OnTouchListener {
    //File file = new File(getCacheDir()+name);
    public static final String path = Environment.getExternalStorageDirectory().getPath()+"/tarotPlayer";
    public static final String aiCardsPath = path+"/AICards.txt";

    // Used to load the 'native-lib' library on application startup.
    /*
    static {
        if (!OpenCV.initDebug()) {
            Log.e("Error", "Cannot load OpenCV library");
        }
    }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playButton = (Button) findViewById(R.id.mainButtonPlay);
        playButton.setOnTouchListener(this);




        // Example of a call to a native method
        /*
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI()); */
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        createContentDir();

        Intent difficultyActivity = new Intent(MainActivity.this, DifficultyActivity.class);
        startActivity(difficultyActivity);
        return false;
    }

    private void createContentDir() {

        File file = new File(path);
        file.mkdirs();
    }

}
