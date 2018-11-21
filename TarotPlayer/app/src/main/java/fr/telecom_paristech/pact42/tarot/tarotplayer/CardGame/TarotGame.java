/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.MainActivity;
/**
 *  This class is used to represent a tarot game and store its information like the players, the cards played, the chien cards,
 *  the difficulty of the game,etc.
 *  @version 1.0
 *  @see Parcelable
 *  @see GameInterface
 *  @see Parcel
 */
public class TarotGame implements GameInterface, Parcelable {
    /**
     * The list of the names of all the players
     * @see #setPlayer(String)
     */
    private ArrayList<String> playerList;
    /**
     * The list of the cards which belong to the AI.
     * @see #addPlayingCard(String)
     */
    private ArrayList<TarotCard> cardList;
    /**
     * The list of the chien cards to be changed or taken by the other players.
     * @see #addChienCard(String)
     */
    private ArrayList<TarotCard> chienList;
    /**
     * The list of the cards played after the enchere phase.
     * @see #addPlayedCard(String)
     */
    private static LinkedList<TarotCard> lastPlayedCards; // Liste des cartes joués
    /**
     * The difficilty of the game/AI.
     * @see #setDifficulty(String)
     */
    private String difficulty;

    /**
     * Constructor by default of the game. All the variables are empty and will be fill in during the
     * application flow.
     */
    public TarotGame() {
        this.playerList = new ArrayList<String>();
        this.cardList = new ArrayList<TarotCard>();
        this.chienList = new ArrayList<TarotCard>();
        this.lastPlayedCards = new LinkedList<TarotCard>();
        this.difficulty = null;
    }

    /**
     * Constructor of a game when passed using a Parcel. We have to initiate each variable consecutively.
     * @param in
     *      The parcel which contains all the information of the card.
     * @see Parcel
     */
    protected TarotGame(Parcel in) {
        difficulty = in.readString();
        this.playerList = new ArrayList<String>();
        playerList = in.createStringArrayList();
        cardList = new ArrayList<TarotCard>();
        in.readTypedList(cardList, TarotCard.CREATOR);
        chienList = new ArrayList<TarotCard>();
        in.readTypedList(chienList, TarotCard.CREATOR);
        lastPlayedCards = new LinkedList<TarotCard>();
        in.readTypedList(lastPlayedCards, TarotCard.CREATOR);
    }
    /**
     * This method is called when initiating the object through the parcel in an activity.
     * @see android.os.Parcelable.Creator
     */
    public static final Creator<TarotGame> CREATOR = new Creator<TarotGame>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public TarotGame createFromParcel(Parcel in) {
            return new TarotGame(in);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public TarotGame[] newArray(int size) {
            return new TarotGame[size];
        }
    };
    /**
     * {@inheritDoc}
     */
    @Override
    public void setPlayer(String player) {
        playerList.add(player);
    }

    /**
     * Called when an AI's playing card has been added to the game. It writes its value in the right file.
     * @param card
     *      The name of the card
     */
    @Override
    public void addPlayingCard(String card) {

        TarotCard tarotCard = new TarotCard(card);
        cardList.add(tarotCard);
        String name = "AIcards.txt";
        Log.e("addPlayingCard", String.valueOf(cardList));
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(MainActivity.MAIN_PATH, name), true));
            bw.write(card);
            bw.newLine();
        } catch (Exception e) {
            Log.e("WritingFile", "The file could not be written: " + e.getLocalizedMessage());
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Called when a chien card has been added to the game. It writes its value in the right file.
     * @param card
     *      The name of the card
     */
    public void addChienCard(String card) {

        TarotCard chienCard = new TarotCard(card);
        chienCard.showCard();
        chienList.add(chienCard);

        String name = "chien.txt";
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(MainActivity.MAIN_PATH, name), true));
            bw.write(card);
            bw.newLine();
        } catch (Exception e) {
            Log.e("WritingFile", "The file could not be written: " + e.getLocalizedMessage());
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Called when a card has been played. It writes its value in the right file.
     * @param card
     *      The name of the card
     */
    public void addPlayedCard(String card) {

        if (lastPlayedCards.size() < 4) {
            TarotCard playedCard = new TarotCard(card);
            playedCard.showCard();
            lastPlayedCards.addFirst(playedCard);
        } else {
            lastPlayedCards.removeLast();
            TarotCard playedCard = new TarotCard(card);
            playedCard.showCard();
            lastPlayedCards.addFirst(playedCard);
        }
        String name = "jeu.txt";
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(MainActivity.MAIN_PATH, name), true));
            bw.write(card);
            bw.newLine();
        } catch (Exception e) {
            Log.e("WritingFile", "The file could not be written: " + e.getLocalizedMessage());
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * This method is called to change a card from the AI's game. It is used when the Ai has to take
     * the chien.
     * @param oldCard
     *      The card to be changed
     * @param newCard
     *      The card which will replace the old one.
     */
    @Override
    public void changeCard(String oldCard, String newCard) {
        for (int j = 0; j < cardList.size(); j++) {
            TarotCard card = cardList.get(j);
            if (card.getName().compareTo(oldCard) == 0) {
                int i = cardList.indexOf(card);
                cardList.remove(i);
                cardList.add(i, new TarotCard(newCard));
            }
        }
    }

    /**
     * This method is used to show an AI's card when it was played.
     * @param card2Play
     *      Card to be shown
     * @see TarotCard#showCard()
     */
    @Override
    public void showCard(String card2Play) {
        for (TarotCard card : cardList) {
            if (card2Play.compareTo(card.getName()) == 0) {
                card.showCard();
            }
        }
    }

    /**
     * This method is used to hide an AI's card when it was already dropped on the played card stack (on the table).
     * @param card2Play
     *      Card to be hidden
     * @see TarotCard#hideCard()
     */
    @Override
    public void hideCard(String card2Play) {
        for (TarotCard card : cardList) {
            if (card2Play.compareTo(card.getName()) == 0) {
                card.hideCard();
            }
        }
    }

    /**
     * Getter of chien card from the list
     * @param i
     *      The index of the card to get
     * @return
     *      The selected card
     * @see #chienList
     */
    public TarotCard getChienCard(int i) {
        Log.e("vla", chienList.toString());
        return chienList.get(i);
    }

    /**
     * This method is called to print all the visible AI's cards on an activity.
     * @param tableLayout
     *         The array which will contain all the imageviews of the cards
     * @param context
     *          The context of the application.
     *  @see #cardList
     */
    public void printGameCards(TableLayout tableLayout, Context context) {
        this.printCards(tableLayout, context, cardList, 3, 6);
    }
    /**
     * This method is called to print all the chien cards on an activity.
     * @param tableLayout
     *         The array which will contain all the imageviews of the cards
     * @param context
     *          The context of the application.
     *      @see #chienList
     */
    public void printChienCards(TableLayout tableLayout, Context context) {
        this.printCards(tableLayout, context, chienList, 1, 6);
    }
    /**
     * This method is called to print all the last cards played on an activity.
     * @param tableLayout
     *         The array which will contain all the imageviews of the cards
     * @param context
     *          The context of the application.
     *          @see #lastPlayedCards
     */
    public void printLastCards(TableLayout tableLayout, Context context) {
        this.printCards(tableLayout, context, lastPlayedCards, 1, 4);
    }
    /**
     * This method is called to print the cards of a list in a array of images. It updates the activity
     * which contains the cards.
     * @param tableLayout
     *         The array which will contain all the imageviews of the cards
     * @param context
     *          The context of the application.
     * @param cardList
     *          The list of cards of tarot to be printed
     * @param height
     *          The number of cards vertically arranged in the array
     * @param width
     *         The number of cards horizontally arranged in the array
     *     @see TableLayout
     */
    private void printCards(TableLayout tableLayout, Context context, List<TarotCard> cardList, int height, int width) {
        tableLayout.removeAllViews();
        int layoutHeight = tableLayout.getHeight();
        int layoutWidth = tableLayout.getWidth();
        int cardCounter = 0;
        TableRow row = new TableRow(context);
        TableRow oldRow;
        for (TarotCard card : cardList) {
            card.setImage(context);
            ImageView currentCard = card.getImage(context);
            currentCard.setMinimumWidth(layoutWidth / width);
            currentCard.setMinimumHeight(layoutHeight / height);
            if (cardCounter % 6 == 0 && cardCounter != 0) {
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
        parcel.writeString(difficulty);
        parcel.writeStringList(playerList);
        parcel.writeTypedList(cardList);
        parcel.writeTypedList(chienList);
        parcel.writeTypedList(lastPlayedCards);
    }

    /**
     * Getter of the index of an AI's card
     * @param card2change
     *      The card whose index is requested.
     * @return
     *      The index of the requested card
     */
    public int getIndexOf(String card2change) {
        for (TarotCard card : cardList) {
            if (card.getName().equals(card2change)) {
                return cardList.indexOf(card) + 1;
            }
        }
        return 0;
    }

    /**
     * Getter of the list of players
     * @return
     *      An arrays containing the list of players
     *     @see #playerList
     */
    public ArrayList<String> getPlayerList() {
        return playerList;
    }

    /**
     * Getter of the card to change in function of the AI decision
     * @return
     *      The name of the card to change
     * @see fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game
     * @see #cardList
     */
    public String getCard2Change() {
        return cardList.get(1).toString();
    }

    /**
     * Getter of the list of the AI's cards.
     * @return
     *      The list of the AI's cards
     *    @see #cardList
     */
    public ArrayList<TarotCard> getCardList() {
        return cardList;
    }
    /**
     * Getter of the list of the chien cards.
     * @return
     *      The list of the chien cards
     *    @see #chienList
     */
    public ArrayList<TarotCard> getChienList() {
        return chienList;
    }
}
