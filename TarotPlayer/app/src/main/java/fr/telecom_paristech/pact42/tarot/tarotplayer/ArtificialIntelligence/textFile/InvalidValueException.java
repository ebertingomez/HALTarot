/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile;

public class InvalidValueException extends NotACardException {

	public InvalidValueException(char val) {
		super(val + " ne représente pas une valeur de carte.");
	}

	public InvalidValueException(int val) {
		super(val + " ne représente pas une valeur d'atout.");
	}
}
