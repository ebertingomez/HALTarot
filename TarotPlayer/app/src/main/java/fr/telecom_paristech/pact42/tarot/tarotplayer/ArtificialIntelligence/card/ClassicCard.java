/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card;

public class ClassicCard extends Card {

	//Attributes
	private static ClassicCard[][] classicCards = new ClassicCard[4][14]; //to prevent a card from being instanciated twice

	private int couleur;
	private int valeur;

	//Constructors
	private ClassicCard(int couleur, int valeur) {
		this.couleur = couleur;
		this.valeur = valeur;
	}

	public static Card getCard(int color, int value) {
		if(classicCards[color][value-1] == null) {
			classicCards[color][value-1] = new ClassicCard(color, value);
		}
		return classicCards[color][value-1];
	}

	public int getValue() {
		return valeur;
	}

	@Override
	public int getCouleur() {
		return couleur;
	}

	@Override
	public int getScore() {
		switch(valeur) {
			case 11: return 3;
			case 12: return 5;
			case 13: return 7;
			case 14: return 9;
			default: return 1;
		}
	}

	@Override
	public boolean isStrongerThan(Card carte, int couleur) {

		if(carte == null) {
			return true;
		}

		// une carte classique perd toujours face à un atoutou si elle n'est pas de la couleur jouee
		if(carte instanceof Atout || !this.hasCouleur(couleur))
			return false;

		// une carte gagne toujours face a une carte qui n'est ni atout,ni de la couleur jouee ou l'excuse
		if(!carte.hasCouleur(couleur) || carte.hasCouleur(Card.excuse))
			return true;

		return (((ClassicCard)carte).getValue() < valeur);
	}

	@Override
	public String toString()
	{
		String couleurString = "";

		switch(couleur) {
			case Card.coeur: couleurString = "Coeur";
				break;
			case Card.carreau: couleurString = "Carreau";
				break;
			case 2: couleurString = "Pique";
				break;
			case 3: couleurString = "Trefle";
				break;
			default: couleurString = "Inconnue";
		}

		String valeurString = "";

		switch(valeur) {
			case 11: valeurString = "Valet";
				break;
			case 12: valeurString = "Cavalier";
				break;
			case 13: valeurString = "Dame";
				break;
			case 14: valeurString = "Roi";
				break;
			default: valeurString = valeur + "";
		}

		return valeurString + " de " + couleurString;
	}

	@Override
	public Card getLower() {
		if(valeur == 1) {
			return null;
		} else {
			return getCard(couleur, valeur - 1);
		}
	}
}
