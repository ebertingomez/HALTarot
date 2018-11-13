package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game;

import java.io.FileNotFoundException;
import java.io.IOException;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Card;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile.CardFile;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile.NotACardException;

public class RealPlayer extends Player {

	private boolean[] isOutOf = {false, false, false, false, false, false};

	public RealPlayer(int position) {
		super(position);
	}

	@Override
	public boolean isAI() {
		return false;
	}

	@Override
	public boolean isOutOf(int couleur) {
		return isOutOf[couleur];
	}

	@Override
	public Card play(Game partie, Turn tour) throws IOException {

		Card carte = null;

		try {
			carte = partie.lastPlayedCard();
		} catch (FileNotFoundException | NotACardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int couleur = carte.getCouleur();
		int couleurTour = tour.getCouleur();

		if(!isOutOf[couleur] && couleur != Card.excuse && couleurTour != Card.excuse && couleur != couleurTour) {
			//if the played card is not the required color, we know that the player is out of it
			isOutOf[couleur] = true;
		}

		return carte;
	}

	@Override
	public void bid(Game partie, Bid enchere) throws IOException {
		enchere.readEnchere(position);
		System.out.println("Le joueur " + position + " a encheri");
	}
}
