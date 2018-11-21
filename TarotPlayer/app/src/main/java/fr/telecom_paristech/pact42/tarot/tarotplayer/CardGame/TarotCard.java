/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
/**
 *  This class is used to represent a tarot card and store all its main characteristics.
 *  @version 1.0
 *  @see Parcelable
 *  @see BasicCard
 *  @see Parcel
 */
public class TarotCard extends BasicCard implements Parcelable {
    /**
     * This variable stores the view of the card to be shown in the activities.
     * @see ImageView
     */
    private ImageView image = null;
    /**
     * This is the ID of the resource which contains the graphical representation of the card.
     */
    private int imageID;

    /**
     * Constructor of a tarot card
     * @param name
     *      Name and value of the card.
     * @see TarotCardLibrary#cardsTable
     */
    public TarotCard(String name) {
        super();
        this.name = name;
        this.value = name;
        this.show = true;
        imageID = TarotCardLibrary.cardsTable.get("question");
        image = null;
    }

    /**
     * Constructor of a card when passed using a Parcel. We have to initiate each variable consecutively.
     * @param in
     *      The parcel which contains all the information of the card.
     * @see Parcel
     */
    protected TarotCard(Parcel in) {

        imageID = in.readInt();
        name = in.readString();
        value = in.readString();
        description = in.readString();
        show = (in.readInt() == 1) ? true : false;
        image = null;
    }

    /**
     * This method is called when initiating the object through the parcel in an activity.
     * @see android.os.Parcelable.Creator
     */
    public static final Creator<TarotCard> CREATOR = new Creator<TarotCard>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public TarotCard createFromParcel(Parcel in) {
            return new TarotCard(in);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public TarotCard[] newArray(int size) {
            return new TarotCard[size];
        }
    };

    /**
     * This method is used to set the ID of the image of the card..
     * @see TarotCardLibrary#cardsTable
     */
    @Override
    public void showCard() {
        imageID = TarotCardLibrary.cardsTable.get(name);
    }

    /**
     * This method is used to mark the card as a hidden card.
     * @see #show
     */
    @Override
    public void hideCard() {
        show = false;
    }

    /**
     * This method is used to assign the cards image and give it the right configurations.
     */
    @Override
    public void playCard() {
        image.setImageResource(imageID);
        image.setPadding(2, 2, 2, 2);
        image.setBackgroundColor(Color.RED);
    }

    /**
     * Getter of the ImageID of the card
     * @return
     *      the imageID of resource of the card
     */
    public int getImageID() {
        return imageID;
    }

    /**
     * Setter of the image of each card to the ImageView which will be in the activities.
     * @param context
     *      The context of the activity
     */
    public void setImage(Context context) {
        image = new ImageView(context);
        image.setImageResource(imageID);
    }

    /**
     * Getter of the ImageView which will represent the card in an activity
     * @param context
     *      The context of the application
     * @return
     *      The ImageView which will represent the card.
     */
    public ImageView getImage(Context context) {
        if (show == false) {
            return new ImageView(context);
        }
        return image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(imageID);
        parcel.writeString(name);
        parcel.writeString(value);
        parcel.writeString(description);
        parcel.writeInt(show ? 1 : 0);
    }
}
