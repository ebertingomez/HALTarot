/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.aiImplementation;

import java.io.IOException;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Card;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.CardTree;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Bid;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Turn;

public interface TarotAI {

	// Constants for cut
	public static final int PETIT = 0;
	public static final int HIGHEST_ATOUT = 1;
	public static final int LOWEST_ATOUT = 2;

	public void init(Game partie, Turn tour) throws ClassNotFoundException, IOException;

	public Card playFirst();
	public Card playAtoutAfterAtout(); // When atout was played and IA has atout
	public Card playColorAfterAtout(); // When atout was played first but IA doesn't have any atout
	public Card playColor(); // When a color was played, and IA has the color
	public Card cut(); // When a color was played, but IA doesn't have that color and has atout
	public Card playOtherColorAfterColor(); // When a color was played, but IA doesn't have that color or atout

	public int bid(Bid enchere);
	public void chien(CardTree chienCardTree);

	public void update(Turn turn) throws ClassNotFoundException, IOException; //called in the end of every turn

}
