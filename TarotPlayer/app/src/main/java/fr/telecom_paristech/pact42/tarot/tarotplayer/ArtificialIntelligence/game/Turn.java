/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game;

import java.io.IOException;
import java.util.ArrayList;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.*;

public class Turn {

	private static final int NUMBER_OF_TURNS = 18;

	//Attributes
	private int couleur = Card.excuse; //leading color, by default excuse because it has to be replaced
	private Player[] order; //play order
	private ArrayList<Card> playedCards; //playedCards[i] contains the (i+1)th played  card
	// For convenience, since a player needs to play an atout greater than the already played atouts
	private Atout highestAtout;

	private Game partie;
	private int number; //number of the turn in the game
	private Player meneur; //Player who currently is winning the "pli"
	private Player excusePlayer;
	private Card leaderCard;

	//Constructors
	public Turn(int number, Game partie, Player firstPlayer) { //generic constructor

		this.number = number;
		this.partie = partie;
		meneur = firstPlayer;
		int firstPlayerPosition = firstPlayer.getPosition();
		order = new Player[4];

		for(int i = 0; i < 4; i ++) {
			order[i] = partie.getPlayer((i + firstPlayerPosition) % 4);
		}

		playedCards = new ArrayList<Card>();
		highestAtout = null;
	}

	public Turn(Game partie) { //Initial tour constructor
		this(0, partie,  partie.getPlayer(0));
	}

	public Turn(Game partie, Turn tour) { //Constructs the next tour
		this(tour.getNumber() + 1, partie, tour.getLeader());
	}

	//Methods
	public int getNumber() {
		return number;
	}

	public Player getLeader() {
		return meneur;
	}

	public ArrayList<Card> getPli() {
		return playedCards;
	}

	public Atout getHighestAtout() {
		return highestAtout;
	}

	public int getCouleur() {
		return couleur;
	}

	public Card getLeaderCard() {
		return leaderCard;
	}

	private void addCard(Player joueur, Card carte) {

		playedCards.add(carte); //Add the card to the array

		if (carte instanceof Atout) {
			if (highestAtout == null || carte.getValue() > highestAtout.getValue())
				highestAtout = (Atout) carte;
		}

		if(carte.isStrongerThan(leaderCard, couleur)) { //meneur update
			meneur = joueur;
			leaderCard = carte;
		}

		if(carte.getCouleur() == Card.excuse) {
			excusePlayer = joueur;
		}

		if(couleur == Card.excuse) {
			couleur = carte.getCouleur();
		}
	}

	public void play() throws IOException, ClassNotFoundException {
		Player joueur = order[playedCards.size()];
		Card carte = order[playedCards.size()].play(partie, this);
		addCard(joueur, carte);
		System.out.println("Le joueur " + joueur.getPosition() + " a joué la carte " + carte);
	}

	public boolean isOver() {
		if(playedCards.size() == 4) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isLast() {
		return number + 1 == NUMBER_OF_TURNS;
	}

	public boolean isAITurn() {
		return(order[playedCards.size()].isAI());
	}

	public boolean isFirstPlayer(Player joueur) {
		return joueur.getPosition() == order[0].getPosition();
	}

	public void addPli() {
		for(Card card : playedCards) {
			if(card.getCouleur() == Card.excuse && !isLast()) {
				partie.addPli(excusePlayer, card);

				//If the pli is earned by someone else than the excuse player, a 0.5-score card has to be exchanged
				if(!meneur.equals(excusePlayer)) {
					partie.cardExchange(excusePlayer);
				}

			} else if(card.getCouleur() == Card.excuse && isLast()) {
				partie.addPli(!excusePlayer.isPreneur(), card); //If played in the last turn, the excuse is taken by the opposite side.

			} else {
				partie.addPli(meneur, card);
			}
		}
	}

	public ArrayList<Card> getPlayedCards() {
		return playedCards;
	}

	public Card getPlayedCard(Player player) {
		int n = (player.getPosition() + order[0].getPosition()) % 4;
		if(n < playedCards.size()) {
			return playedCards.get(n);
		} else {
			return null;
		}
	}

	public int getScorePli() {
		int score = 0;
		for(Card card : playedCards) {
			score += card.getScore();
		}
		return score;
	}

	public int getPosition(Player player) {
		return (order[0].getPosition() + player.getPosition()) % 4;
	}
}
