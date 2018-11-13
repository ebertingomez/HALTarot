package fr.telecom_paristech.pact42.tarot.tarotplayer.Widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.EnchereActivity;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;


public class SuccessfulScanDialog extends Dialog {
    public SuccessfulScanDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.activity_successful_scan);
    }
}
