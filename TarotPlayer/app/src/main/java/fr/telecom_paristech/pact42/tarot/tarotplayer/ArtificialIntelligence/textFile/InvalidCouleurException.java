package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile;

public class InvalidCouleurException extends NotACardException {

	public InvalidCouleurException(char couleur) {
		super(couleur + " ne représente pas une couleur");
	}
}
