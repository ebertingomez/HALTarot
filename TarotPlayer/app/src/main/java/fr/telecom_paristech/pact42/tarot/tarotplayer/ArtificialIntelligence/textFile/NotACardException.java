package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile;

public class NotACardException extends Exception {

	public NotACardException(String message) {
		super("Carte non reconnue: " + message);
	}
}
