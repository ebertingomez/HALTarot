/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotCardLibrary;
import fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TarotGame;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
/**
 *  This activity is used to inform the players the image recognition did not succeed. It allows you to rescan the card
 *  or to choose it manually.
 *  @version 1.0
 *  @see ScanTableActivity
 *  @see SuccessfulScanActivity
 */
public class UnsuccessfulScanTableActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * The list of all the cards to be selected
     * @see TarotCardLibrary#cards
     */
    private Spinner spinner;
    /**
     *  This is the current game. It contains all the information about the cards, the players,etc.
     *  @see #onCreate(Bundle)
     *  @see TarotGame
     */
    private TarotGame currentGame;
    /**
     *  Called to instantiate all the elements of the view and the spinner
     * @param savedInstanceState
     *       Parameters stored after calling onStop()
     *  @see AppCompatActivity#onCreate(Bundle)
     *  @see #spinner
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentGame = getIntent()
                .getParcelableExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME");
        setContentView(R.layout.activity_unsuccessful_scan);
        ImageView rescanButton = findViewById(R.id.rescan);
        ImageView goButton = findViewById(R.id.unsucess_scanGo);
        final TextView selectCard = findViewById(R.id.unsucess_selectCard);
        spinner = findViewById(R.id.spinner);
        rescanButton.setOnClickListener(this);
        goButton.setOnClickListener(this);

        AdapterView.OnItemSelectedListener itemSelect = new AdapterView.OnItemSelectedListener() {

            /**
             * <p>
             * Callback method to be invoked when an item in this view has been selected.
             * This callback is invoked only when the newly selected position is different
             * from the previously selected position or if there was no selected item.
             * </p>
             * <p>
             * Implementers can call getItemAtPosition(position) if they need to access the
             * data associated with the selected item.
             *
             * @param parent   The AdapterView where the selection happened
             * @param view     The view within the AdapterView that was clicked
             * @param position The position of the view in the adapter
             * @param id       The row id of the item that is selected
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectCard.setText(spinner.getSelectedItem().toString());
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                TarotCardLibrary.cards);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(itemSelect);
    }

    /**
     * Called when a view has been clicked. It is used to distinguish if the user wants to rescan a
     * card or choose it manually
     * @param v The view that was clicked.
     * @see ScanTableActivity
     * @see android.view.View.OnClickListener#onClick(View)
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.rescan:
            break;
        case R.id.unsucess_scanGo:
            ScanTableActivity.cardsScanned++;
            currentGame.addPlayedCard(spinner.getSelectedItem().toString());
            break;

        }
        Intent scanTableActivity = new Intent(UnsuccessfulScanTableActivity.this,
                fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.ScanTableActivity.class);
        scanTableActivity.putExtra("fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame.TAROTGAME", currentGame);
        startActivity(scanTableActivity);
    }
}
