package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.*;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile.CardFile;

public class GameGenerator {

	private static final CardFile J0_FILE = new CardFile("IA/data/main0.txt");
	private static final CardFile J1_FILE = new CardFile("IA/data/main1.txt");
	private static final CardFile J2_FILE = new CardFile("IA/data/main2.txt");
	private static final CardFile J3_FILE = new CardFile("IA/data/main3.txt");
	private static final CardFile CHIEN_FILE = new CardFile(Game.CHIEN_FILE_NAME);


	public static void RandomDistribution() throws IOException {

		ArrayList<Card> cards = getAllCards();
		Collections.shuffle(cards);
		CardFile[] files = {J0_FILE, J1_FILE, J2_FILE, J3_FILE};

		for(int i = 0; i < 4; i++) {
			generateFile(files[i], cards, i * 18, 18);
		}

		generateFile(CHIEN_FILE, cards,  18 * 4, 6);
	}

	//intializes a file from a arraylist of cards, stating from index start and with the length
	private static void generateFile(CardFile file, ArrayList<Card> list, int start, int length) throws IOException {
		file.init();
		for(int i = 0; i < length; i++) {
			file.addCard(list.get(start + i));
		}
	}

	private static ArrayList<Card> getAllCards() {
		ArrayList<Card> list = new ArrayList<Card>();
		int[] couleurs = {Card.carreau, Card.coeur, Card.pique, Card.trefle};

		for(int couleur : couleurs) {
			for(int valeur = 1; valeur <= 14; valeur ++) {
				list.add(ClassicCard.getCard(couleur, valeur));
			}
		}

		for(int valeur = 1; valeur <= 21; valeur ++) {
			list.add(Atout.getCard(valeur));
		}

		list.add(Excuse.getCard());

		return list;
	}
}
