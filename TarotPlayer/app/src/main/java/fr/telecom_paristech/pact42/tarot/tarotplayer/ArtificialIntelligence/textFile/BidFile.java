/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

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

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Bid;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Player;

public class BidFile {

	private File file;

	public BidFile(String fileName) {
		this.file = new File(fileName);
	}

	public void update(Bid enchere) throws IOException {

		Scanner sc = new Scanner(new BufferedReader(new FileReader(file)));
		String line;
		int i = 0;

		while(sc.hasNextLine() && i < 3) {
			line = sc.nextLine();
			enchere.setEnchere(i, toEnchere(line));
			i ++;
		}

		try {
			sc.close();
		} catch(Exception e) {}
	}

	public void init() throws IOException {
		PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		try {
			wr.close();
		} catch(Exception e) {}
	}

	public void addEnchere(int enchere) throws IOException {
		PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		wr.println(EnchereToString(enchere));
		try {
			wr.close();
		} catch(Exception e) {}
	}
	
	/*PA -> Passe
	 * PE -> Petite
	 * GD -> Garde
	 * GS -> Garde sans
	 * GC -> Garde contre
	 */

	private String EnchereToString(int enchere) {
		switch(enchere) {
			case Bid.PETITE : return "PE";
			case Bid.GARDE : return "GD";
			case Bid.GARDE_CONTRE : return "GC";
			case Bid.GARDE_SANS : return "GS";
			default : return "PA";
		}
	}

	private int toEnchere(String line) {
		switch(line.charAt(1)) {
			case 'E' : return Bid.PETITE;
			case 'D' : return Bid.GARDE;
			case 'S' : return Bid.GARDE_SANS;
			case 'C' : return Bid.GARDE_CONTRE;
			default : return Bid.PASSE;
		}
	}
}
