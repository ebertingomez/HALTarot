package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game;

import java.io.FileNotFoundException;
import java.io.IOException;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Card;

public abstract class Player {

	public int position;
	public boolean side; //true if the player is taker

	public abstract boolean isOutOf(int couleur);

	public abstract Card play(Game partie, Turn tour) throws IOException, ClassNotFoundException;

	public abstract void bid(Game partie, Bid enchere) throws FileNotFoundException, IOException;

	public Player(int position) {
		this.position = position;
		side = false;
	}

	public int getPosition() {
		return position;
	}

	public boolean isPreneur() {
		return side;
	}

	public void setPreneur() {
		side = true;
	}

	public abstract boolean isAI();

	public boolean equals(Player player) {
		return getPosition() == player.getPosition();
	}
}
