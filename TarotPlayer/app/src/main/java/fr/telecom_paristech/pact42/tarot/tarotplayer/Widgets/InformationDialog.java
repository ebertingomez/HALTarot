/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

/**
 * This class is used to give general information about the application.
 * @version 1.0
 * @see Dialog
 * @see View.OnClickListener
 */
public class InformationDialog extends Dialog implements View.OnClickListener {
    /**
     * This constructor instantiates all the elements of the window of the dialog.
     * @param context
     *      The context of the activity from where it is called
     * @param description
     *      The information to be transmitted by this dialog.
     */
    public InformationDialog(@NonNull Context context, String description) {
        super(context);
        setContentView(R.layout.information);
        Button okButton = findViewById(R.id.information_go);
        TextView title = findViewById(R.id.information_title);
        TextView message = findViewById(R.id.information_message);

        title.setText("Information");
        message.setText(description);
        okButton.setOnClickListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view) {
        this.dismiss();
    }
}