package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import fr.telecom_paristech.pact42.tarot.tarotplayer.R;


public class TarotCard extends BasicCard implements Parcelable{
    private ImageView image = null;
    private int imageID;

    public TarotCard(String name){
        super();
        this.name   = name;
        this.value  = name;
        this.show   = true;
        imageID     = TarotCardLibrary.cardsTable.get("question");
        image       = null;
    }

    protected TarotCard(Parcel in) {

        imageID = in.readInt();
        name    = in.readString();
        value   = in.readString();
        description = in.readString();
        show    = (in.readInt() == 1 ) ? true : false;
        image   = null;
    }

    public static final Creator<TarotCard> CREATOR = new Creator<TarotCard>() {
        @Override
        public TarotCard createFromParcel(Parcel in) {
            return new TarotCard(in);
        }

        @Override
        public TarotCard[] newArray(int size) {
            return new TarotCard[size];
        }
    };

    @Override
    public void showCard() {
        imageID     = TarotCardLibrary.cardsTable.get(name);
    }

    @Override
    public void hideCard() {
        show = false;
    }

    @Override
    public void playCard() {
        image.setImageResource(imageID);
        image.setPadding(2,2,2,2);
        image.setBackgroundColor(Color.RED);
    }

    public void setImage(Context context) {
        image   = new ImageView(context);
        image.setImageResource(imageID);
    }

    public ImageView getImage(Context context){
        if (show==false) {
            return new ImageView(context);}
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(imageID);
        parcel.writeString(name);
        parcel.writeString(value);
        parcel.writeString(description);
        parcel.writeInt(show ? 1 : 0);
    }
}
