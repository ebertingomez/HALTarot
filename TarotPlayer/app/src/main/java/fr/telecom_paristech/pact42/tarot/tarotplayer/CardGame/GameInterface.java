/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

/**
 * This interface contains all the basic methods of a basic card game.
 * @version 1.0
 *
 */
interface GameInterface {
    /**
     * This method is used to add a player to the game.
     * @param player
     *      The name of the player
     */
    void setPlayer(String player);

    /**
     * This method is used to add a card to the game
     * @param card
     *      The card which was played.
     */
    void addPlayingCard(String card);

    /**
     * This method is used to set the difficulty of the game.
     * @param difficulty
     *      The chosen diffitulty.
     */
    void setDifficulty(String difficulty);

    /**
     * This method is used to change the position of the cards
     * @param oldCard
     *      The card to be changed
     * @param newCard
     *      The card which will replace the old one.
     */
    void changeCard(String oldCard, String newCard);

    /**
     * Called to show an upside down card
     * @param card
     *      The card to be shown.
     */
    void showCard(String card);

    /**
     * Called to hide an upside up card.
     * @param card2Play
     *      The card to be hidden.
     */
    void hideCard(String card2Play);
}
