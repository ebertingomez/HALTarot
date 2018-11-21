/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

/**
 *  This abstract class represents a basic card and its mouvements.
 *  @version 1.0
 */
abstract class BasicCard {
    /**
     * Name of the card
     */
    protected String name = null;
    /**
     * Value of the card
     */
    protected String value = null;
    /**
     * Description of the card if any
     */
    protected String description = null;
    /**
     * If the card is upside down or not.
     */
    protected boolean show;

    /**
     * Constructor of a normal card. It is upside down by default
     */
    protected BasicCard() {
        this.show = false;
    }

    /**
     * Constructor of a basic card with all its parameters.
     * @param name
     * Its name
     * @param value
     * Its value
     * @param description
     * Its description
     * @param show
     * The card is not shown (upside up) by default.
     */
    protected BasicCard(String name, String value, String description, boolean show) {
        this.name = name;
        this.value = value;
        this.description = description;
        this.show = false;
    }

    /**
     * Method used when a card is turned from upside down to upside up.
     * @see #show
     */
    protected abstract void showCard();

    /**
     * Method used when a card is turned from upside up to upside down.
     * @see #show
     */
    protected abstract void hideCard();
    /**
     * Method used when a card is played.
     * @see #show
     */
    protected abstract void playCard();

    /**
     * Method used to get the name of the card
     * @return
     *      The name of the card
     */
    public String getName() {
        return name;
    }
}
