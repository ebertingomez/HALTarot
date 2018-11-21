/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card;

public class Excuse extends Card{

	private static Excuse excuse = new Excuse();

	private Excuse() {
		super();
	}

	@Override
	public int getScore() {
		return 9;
	}

	@Override
	public int getCouleur() {
		return Card.excuse;
	}

	@Override
	public int getValue() // On retourne une valeur pour des raisons de compatibilité: Dans Stack, on trie les cartes selon la variable valeur.
	{
		return 0;
	}

	@Override
	public boolean isStrongerThan(Card carte, int couleur) {
		if(carte == null) {
			return true;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return "Excuse";
	}

	public static Card getCard() {
		return excuse;
	}

	@Override
	public Card getLower() {
		return null;
	}
}
