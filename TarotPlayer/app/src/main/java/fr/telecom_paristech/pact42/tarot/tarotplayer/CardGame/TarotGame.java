package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Hashtable;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.*;

public class TarotGame implements GameInterface,Parcelable {
    private ArrayList<String> playerList;
    private ArrayList<TarotCard> cardList;
    private String difficulty;
    private Game game;

    public TarotGame(){
        this.playerList = new ArrayList<String>();
        this.cardList   = new ArrayList<TarotCard>();
        this.difficulty = null;
        this.game       = null;
    }

    protected TarotGame(Parcel in) {
        difficulty      = in.readString();
        this.playerList = new ArrayList<String>();
        playerList      = in.createStringArrayList();
        cardList        = new ArrayList<TarotCard>();
        in.readTypedList(cardList, TarotCard.CREATOR);
        game            = (Game) in.readValue(Game.class.getClassLoader());
    }

    public static final Creator<TarotGame> CREATOR = new Creator<TarotGame>() {
        @Override
        public TarotGame createFromParcel(Parcel in) {
            return new TarotGame(in);
        }

        @Override
        public TarotGame[] newArray(int size) {
            return new TarotGame[size];
        }
    };

    @Override
    public void setPlayer(String player) {
        playerList.add(player);
    }

    @Override
    public void addPlayingCard(String card) {
        cardList.add(new TarotCard(card));
    }

    @Override
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void changeCard(String oldCard, String newCard) {
        for (TarotCard card : cardList){
            if (card.getName() == oldCard){
                int i = cardList.indexOf(card);
                cardList.remove(i);
                cardList.add(i, new TarotCard(newCard));
            }
        }
    }

    @Override
    public void showCard(String card2Play) {
        for (TarotCard card : cardList){
            if (card2Play.compareTo(card.getName())==0){
                card.showCard();
            }
        }
        
    }

    @Override
    public void hideCard(String card2Play) {
        for (TarotCard card : cardList){
            if (card2Play.compareTo(card.getName())==0){
                card.hideCard();
            }
        }
    }

    public void printCards(TableLayout tableLayout, Context context){
        tableLayout.removeAllViews();

        int layoutHeight = tableLayout.getHeight();
        int layoutWidth = tableLayout.getWidth();

        int cardCounter = 0;
        TableRow row = new TableRow(context);
        TableRow oldRow = null;

        for (TarotCard card : cardList){
            card.setImage(context);
            ImageView currentCard = card.getImage(context);
            currentCard.setMinimumWidth(layoutWidth/6);
            currentCard.setMinimumHeight(layoutHeight/3);

            if (cardCounter%6 == 0 && cardCounter!=0){
                oldRow = row;
                tableLayout.addView(oldRow);
                row = new TableRow(context);
            }

            row.addView(currentCard);
            cardCounter++;
        }
        tableLayout.addView(row);
        tableLayout.requestLayout();
        tableLayout.invalidate();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(difficulty);
        parcel.writeStringList(playerList);
        parcel.writeTypedList(cardList);
        parcel.writeValue(game);
    }

    public String getDifficulty() {
        return difficulty;
    }
}
