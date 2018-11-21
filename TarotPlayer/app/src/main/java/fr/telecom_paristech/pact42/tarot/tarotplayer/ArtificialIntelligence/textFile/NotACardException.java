/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile;

public class NotACardException extends Exception {

	public NotACardException(String message) {
		super("Carte non reconnue: " + message);
	}
}
