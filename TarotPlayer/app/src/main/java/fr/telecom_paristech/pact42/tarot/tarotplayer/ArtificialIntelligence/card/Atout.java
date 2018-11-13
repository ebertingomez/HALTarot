package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card;

public class Atout extends Card {

	private static Atout[] atouts = new Atout[21];
	private int valeur;

	private Atout(int valeur)
	{
		this.valeur = valeur;
	}

	public static Card getCard(int value) {
		if(atouts[value-1] == null) {
			atouts[value-1] = new Atout(value);
		}
		return atouts[value-1];
	}

	public int getValue() {
		return valeur;
	}

	@Override
	public int getCouleur() {
		return Card.atout;
	}

	@Override
	public int getScore() {
		if(valeur == 1 || valeur == 21) {
			return 9;
		} else {
			return 1;
		}
	}

	@Override
	public boolean isStrongerThan(Card carte, int couleur) {
		if(carte == null) {
			return true;
		} if(!carte.hasCouleur(Card.atout)) {
			return true;
		} else {
			return ((Atout)carte).getValue() < valeur;
		}
	}

	@Override
	public String toString()
	{
		return valeur + " d'Atout";
	}

	@Override
	public Card getLower() {
		if(valeur == 1) {
			return null;
		} else {
			return getCard(valeur - 1);
		}
	}
}
