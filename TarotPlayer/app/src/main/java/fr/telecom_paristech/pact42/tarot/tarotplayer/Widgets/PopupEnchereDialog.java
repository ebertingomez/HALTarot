/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.EnchereActivity;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
/**
 * This class is used to choose the other players' encheres.
 * @version 1.0
 * @see EnchereActivity
 * @see Dialog
 * @see View.OnClickListener
 */
public class PopupEnchereDialog extends Dialog implements View.OnClickListener {
    /**
     * The activity from where this method is called.
     */
    private EnchereActivity activity;

    /**
     * This contructor instantiates all the elements of the Dialog windows and set the listeners.
     * @param context
     *  The context from which this dialog is called.
     */
    public PopupEnchereDialog(@NonNull Context context) {
        super(context);
        activity = (EnchereActivity) context;
        setContentView(R.layout.popup_encheres);
        ImageView enchereType1 = findViewById(R.id.enchereImage1);
        ImageView enchereType2 = findViewById(R.id.enchereImage2);
        ImageView enchereType3 = findViewById(R.id.enchereImage3);
        ImageView enchereType4 = findViewById(R.id.enchereImage4);
        ImageView enchereType5 = findViewById(R.id.enchereImage5);
        ImageView enchereType6 = findViewById(R.id.enchereImage6);

        enchereType1.setOnClickListener(this);
        enchereType2.setOnClickListener(this);
        enchereType3.setOnClickListener(this);
        enchereType4.setOnClickListener(this);
        enchereType5.setOnClickListener(this);
        enchereType6.setOnClickListener(this);
    }
    /**
     *  This method is used to store the player's choice of his enchere.
     * @param view
     *  The listener
     * @see View.OnClickListener#onClick(View)
     * @see Game#bid()
     * @see EnchereActivity#setEnchere(String, Drawable)
     */
    @Override
    public void onClick(View view) {
        try {
            int i = Game.bid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int id = view.getId();
        ImageView enchereType = (ImageView) view;
        String tag = enchereType.getTag().toString();
        Drawable image = enchereType.getDrawable();
        String tagShort = "PA";
        switch (tag) {
        case "Chelem":
            tagShort = "Chelem";
            break;
        case "Contre le Chien":
            tagShort = "CO";
            break;
        case "Sans le Chien":
            tagShort = "SC";
            break;
        case "Garde":
            tagShort = "GA";
            break;
        case "Petite":
            tagShort = "PE";
            break;
        case "Passe":
            tagShort = "PA";
            break;
        }
        activity.setEnchere(tagShort, image);
        this.dismiss();
    }
}
