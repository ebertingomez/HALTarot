/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

/**
 *  This activity is used to inform the players the image recognition succeed.
 *  @version 1.0
 *  @see ScanTableActivity
 *  @see ScanHandActivity
 *  @see ScanChienActivity
 *  @see UnsuccessfulScanHandActivity
 */
public class SuccessfulScanActivity extends AppCompatActivity {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_scan);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 500);
    }
}
