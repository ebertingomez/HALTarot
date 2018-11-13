package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card;

public abstract class Card implements Comparable<Card> {

	public static final int coeur = 0;
	public static final int carreau = 1;
	public static final int pique = 2;
	public static final int trefle = 3;
	public static final int atout = 4;
	public static final int excuse = 5;
	public static final int[] colors = {coeur, carreau, pique, trefle, atout, excuse};
	public static final int[] classicColors = {coeur, carreau, pique, trefle};

	public abstract boolean isStrongerThan(Card carte, int couleur);

	public abstract int getScore();

	public abstract int getCouleur();

	public abstract int getValue();

	public boolean hasCouleur(int couleur) {
		return getCouleur() == couleur;
	}

	@Override
	public String toString() {
		return "Carte";
	}

	public int compareTo(Card carte) {
		if(equals(carte)) {
			return 0;
		}
		if (isStrongerThan(carte, getCouleur())) {
			return 1;
		} else {
			return - 1;
		}
	}

	public static Card getCard(int color, int value) {
		if(color == atout) {
			return Atout.getCard(value);
		} else if(color == excuse) {
			return Excuse.getCard();
		} else {
			return ClassicCard.getCard(color, value);
		}
	}

	public abstract Card getLower();

	public static String getColorLabel(int color) {
		if(color == coeur) {
			return "coeur";
		} else if(color == carreau) {
			return "carreau";
		} else if(color == pique) {
			return "pique";
		} else if(color == trefle) {
			return "trefle";
		} else if(color == atout) {
			return "atout";
		} else {
			return "excuse";
		}
	}
}
