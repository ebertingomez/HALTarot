/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.File;

import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

/**
 *  This is the main activity. It is used to welcome the players and to let them sign in using their Google Accounts. It has a button, so it implements the interface onclicklistener
 *  @version 1.0
 *  @see GoogleSignIn
 *  @see DifficultyActivity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * This is the path of the directory where all files and pictures of the app will be. It will be used
     *  by other activities.
     */
    public static final String MAIN_PATH = Environment.getExternalStorageDirectory().getPath() + "/tarotPlayer";
    /**
     * This is the name of the file where all cards of the Ai will be stored
     */
    public static final String AI_CARDS_PATH = MainActivity.MAIN_PATH + "/AICards.txt";
    /**
     * This variable will be used to stablish a connection to sign in into a Google Account.
     */
    private GoogleSignInClient mGoogleSignInClient;
    /**
     *  We have to initiate all the buttons, Imageview, and set them as listeners. We need to initiate the sign in client.
     *
     *  @see AppCompatActivity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button playButton = findViewById(R.id.mainButtonPlay);
        playButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    /**
     * We need to verify if the user has already signed in.
     * @see AppCompatActivity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    /**
     * This method update the view if an user has already be logged in.
     * @param account
     *          The account of the user.
     */
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.sign_in_button).setClickable(false);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setClickable(true);
        }
    }

    /**
     * This method is used to create the main directory of the application.
     */
    private void createContentDir() {
        File file = new File(MAIN_PATH);
        file.mkdirs();

    }

    /**
     * Called when a view has been clicked. It is used to sign in or to start a game.
     *
     * @param v The view that was clicked.
     * @see DifficultyActivity
     * @see android.view.View.OnClickListener#onClick(View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.sign_in_button:
            signIn();
            break;
        case R.id.mainButtonPlay:
            createContentDir();
            Intent difficultyActivity = new Intent(MainActivity.this, DifficultyActivity.class);
            startActivity(difficultyActivity);
            break;
        }
    }

    /**
     * This method is used to let the user sign in.
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 100);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        // GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 100) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     *  Called when the sign in process succeded to update the View.
     * @param completedTask
     *             Tells if the sign in process was made correctly
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more
            // information.
            updateUI(null);
        }
    }
}
