package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.*;

public class CardFile {

	private String fileName;
	private File file;

	public CardFile(String fileName) {
		this.fileName = fileName;
		file = new File(fileName);
	}

	public void init() throws IOException {

		//If the directory containing the file does not exist, it has to be created
		file.getParentFile().mkdirs();

		//Creating a new emty text file
		PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		wr.print("");

		try {
			wr.close();
		} catch(Exception e) {}
	}

	public CardTree toCardTree() {

		CardTree cardTree = new CardTree();
		Scanner sc = null;

		try {

			sc = new Scanner(new BufferedReader(new FileReader(file)));

			while (sc.hasNextLine()) {
				String s = sc.nextLine();
				Card carte = stringToCarte(s);
				cardTree.add(carte);
			}

		} catch(NotACardException e) {
			System.err.println("Le fichier " + fileName + " ne correspond pas à un ensemble de cartes. \n" + e.getMessage());
			return new CardTree();
		} catch(FileNotFoundException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				sc.close();
			} catch(Exception e) {} //making sure sc is closed
		}
		return cardTree;
	}

	public Card lastCard() throws NotACardException, FileNotFoundException { //Returns the last card of the file, or none if it is empty.

		String line = null;
		Scanner sc = new Scanner(new BufferedReader(new FileReader(file)));

		try {

			while (sc.hasNextLine()) {
				line = sc.nextLine();
			}

		} catch(Exception e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				sc.close();
			} catch(Exception e) {} //making sure sc is closed
		}

		if(line != null) {
			return stringToCarte(line);
		} else {
			return null;
		}
	}

	public void addCard(Card carte) throws IOException { //Adds a card in a new line at the end of the file.

		PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

		wr.println(carteToString(carte));

		try {
			wr.close();
		} catch(Exception e) {}
	}

	private static int charToValue(char c) throws InvalidValueException {
		switch(c) {
			case '1' : return 1;
			case '2' : return 2;
			case '3' : return 3;
			case '4' : return 4;
			case '5' : return 5;
			case '6' : return 6;
			case '7' : return 7;
			case '8' : return 8;
			case '9' : return 9;
			case '0' : return 10;
			case 'V' : return 11;
			case 'C' : return 12;
			case 'D' : return 13;
			case 'R' : return 14;
			case 'E' : return 0; //cas de l'excuse
			default:
				throw new InvalidValueException(c);
		}
	}

	private static int charToCouleur(char c) throws InvalidCouleurException {
		switch(c) {
			case 'A' : return Card.carreau;
			case 'O' : return Card.coeur;
			case 'P' : return Card.pique;
			case 'T' : return Card.trefle;
			case '0' :
			case '1' :
			case '2' :
			case '3' :
			case '4' :
			case '5' :
			case '6' :
			case '7' :
			case '8' :
			case '9' : return Card.atout;
			case 'X' : return Card.excuse;
			default :
				throw new InvalidCouleurException(c);
		}
	}

	private static int stringToValue(String s) throws InvalidValueException {
		int value = 0;
		for(int i = 0; i < 2; i ++) {
			switch(s.charAt(i)) {
				case '0' :
					value = value * 10 + 0;
					break;
				case '1' :
					value = value * 10 + 1;
					break;
				case '2' :
					value = value * 10 + 2;
					break;
				case '3' :
					value = value * 10 + 3;
					break;
				case '4' :
					value = value * 10 + 4;
					break;
				case '5' :
					value = value * 10 + 5;
					break;
				case '6' :
					value = value * 10 + 6;
					break;
				case '7' :
					value = value * 10 + 7;
					break;
				case '8' :
					value = value * 10 + 8;
					break;
				case '9' :
					value = value * 10 + 9;
					break;
				default :
					throw new InvalidValueException(s.charAt(i));
			}
		}
		if(value <= 21) {
			return(value);
		} else {
			throw new InvalidValueException(value);
		}
	}

	private static Card stringToCarte(String s) throws NotACardException {

		int color;
		int value;

		if(s.length() != 2) {
			throw new NotACardException("Longueur incorrecte");
		}

		color = charToCouleur(s.charAt(1));
		if(color == Card.atout) {
			value = stringToValue(s);
		} else {
			value = charToValue(s.charAt(0));
		}

		return Card.getCard(color, value);
	}

	private static String carteToString(Card carte) {

		int couleur = carte.getCouleur();
		int valeur = carte.getValue();
		String str = null;

		if(couleur == Card.atout) {
			if(valeur < 10)
				return "0" + valeur;
			else
				return valeur + "";
		} else if(couleur == Card.excuse) {
			return "EX";
		} else if(couleur == Card.coeur) {
			str = "O";
		} else if(couleur == Card.carreau) {
			str = "A";
		} else if(couleur == Card.pique) {
			str = "P";
		} else if(couleur == Card.trefle) {
			str = "T";
		}
		switch(valeur) {
			case 10 : return "0" + str;
			case 11 : return "V" + str;
			case 12 : return "C" + str;
			case 13 : return "D" + str;
			case 14 : return "R" + str;
			default : return valeur + str;
		}
	}
	/*
	public static void test() {

		CardFile test = new CardFile("IA/data/test.txt");

		try {

			test.init();
			test.addCard(new Atout(21));
			test.addCard(new ClassicCard(Card.coeur, 10));
			test.addCard(new Excuse());
			test.addCard(new ClassicCard(Card.pique, 13));

			CardTree testTree = test.toCardTree();
			System.out.println("Test CardTree contains:\n" + testTree);

			System.out.println("\nLast Line represents " + test.lastCard());

		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}*/
}
