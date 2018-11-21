/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game;

import java.io.FileNotFoundException;
import java.io.IOException;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile.BidFile;

public class Bid {

	private Game partie;

	public static final int PASSE = 0;
	public static final int PETITE = 1;
	public static final int GARDE = 2;
	public static final int GARDE_SANS = 3;
	public static final int GARDE_CONTRE = 4;

	private BidFile bidFile;
	private int[] bids = {-1, -1, -1, -1};
	private int preneurPosition = 0;

	public Bid(Game partie, BidFile enchereFile) throws IOException {
		enchereFile.init();
		this.partie = partie;
		this.bidFile = enchereFile;
	}

	public void readEnchere(int position) throws IOException {
		if(bids[position] == -1) {
			bidFile.update(this);
		}
	}

	public void setEnchere(int position, int value) throws IOException {

		if(value > bids[preneurPosition]) {
			preneurPosition = position;
		}

		bids[position] = value;

		if(partie.getPlayer(position).isAI()) {
			bidFile.addEnchere(value);
		}
	}

	public Player preneur() {

		if(bids[preneurPosition] <= 0) {
			return null;
		}

		return partie.getPlayer(preneurPosition);
	}

	public int maxEnchere() {

		if(bids[preneurPosition] == -1) {
			return Bid.PASSE;
		}
		return bids[preneurPosition];
	}

	public int getEncheresLeft() {
		int k = 3;
		while(k >= 0 && bids[k] == -1) {
			k--;
		}
		return 4-k-1;
	}
}
