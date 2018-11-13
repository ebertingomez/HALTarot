package fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.EnchereActivity;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;


public class PopupEnchereDialog extends Dialog implements View.OnClickListener {
    private EnchereActivity activity;
    public PopupEnchereDialog(@NonNull Context context) {
        super(context);
        activity = (EnchereActivity) context;
        setContentView(R.layout.popup_encheres);
        ImageView enchereType1 = (ImageView) findViewById(R.id.enchereImage1);
        ImageView enchereType2 = (ImageView) findViewById(R.id.enchereImage2);
        ImageView enchereType3 = (ImageView) findViewById(R.id.enchereImage3);
        ImageView enchereType4 = (ImageView) findViewById(R.id.enchereImage4);
        ImageView enchereType5 = (ImageView) findViewById(R.id.enchereImage5);
        ImageView enchereType6 = (ImageView) findViewById(R.id.enchereImage6);

        enchereType1.setOnClickListener(this);
        enchereType2.setOnClickListener(this);
        enchereType3.setOnClickListener(this);
        enchereType4.setOnClickListener(this);
        enchereType5.setOnClickListener(this);
        enchereType6.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        ImageView enchereType = (ImageView) view;
        String tag = enchereType.getTag().toString();
        Drawable image = enchereType.getDrawable();

        activity.setEnchere(tag, image);
        try { Game.bid(); }  catch (Exception e) {e.printStackTrace();}

        this.dismiss();
    }
}
