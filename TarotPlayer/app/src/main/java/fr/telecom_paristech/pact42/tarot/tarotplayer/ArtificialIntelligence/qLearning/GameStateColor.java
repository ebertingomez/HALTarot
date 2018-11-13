package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.qLearning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Card;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.CardTree;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.AIPlayer;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Player;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Turn;

public class GameStateColor extends GameState {

	public static final int NB_OF_STATES = (int) (Math.pow(2, 5) * Math.pow(4, 3));
	
	public boolean[] boolFeatures;
	
	private boolean allyOutOfColor; //Is one ally out of this color?
	private boolean allyOutOfAtout; //Are all allies out of atout?
	
	private boolean enemyOutOfColor; //Is an enemy out of color?
	private boolean enemyOutOfAtout; //Are all enemies out of atout?
	
	private boolean isTaker;
	
	private int isCurrentlyWinning; /* All values take the player's best card into account
										0 -> no
	 									1 -> yes 
	 									2 -> we have max color and are winning
	 									3 -> we have max atout */
	
	private int nbOfValuables = 0;
	
	private int position;

	public GameStateColor(AIPlayer player, Game game, Turn turn, int color) {
		
		allyOutOfColor = false;
		allyOutOfAtout = true;
		for(Player p : game.getTeam(player)) {
			allyOutOfColor = allyOutOfColor || p.isOutOf(color);
			allyOutOfAtout = allyOutOfAtout && p.isOutOf(Card.atout);
		}
		
		enemyOutOfColor = false;
		enemyOutOfAtout = true;
		for(Player p : game.getTeam(!player.isPreneur())) {
			enemyOutOfColor = enemyOutOfColor || p.isOutOf(color);
			enemyOutOfAtout = enemyOutOfAtout && p.isOutOf(Card.atout);
		}
		
		isCurrentlyWinning = isCurrentlyWinning(game, turn, color, player);

		if(turn.getCouleur() != Card.atout) {
			for(Card card : turn.getPlayedCards()) {
				if((card.getCouleur() != Card.atout && card.getValue() >= 11) || (card.equals(Card.getCard(Card.atout, 1)))) {
					nbOfValuables ++;
				}
			}
		}
		
		isTaker = player.isPreneur();
		
		position = turn.getPosition(player);
		
		boolean[] boolFeatures = {allyOutOfColor, allyOutOfAtout, enemyOutOfColor, enemyOutOfAtout, isTaker};
		this.boolFeatures = boolFeatures;
		
		/*System.out.println(Arrays.toString(boolFeatures));
		System.out.println("isCurrentlyWinning = " + isCurrentlyWinning);
		System.out.println("nbOfValuables = " + nbOfValuables);*/
	}
	
	public int stateIndex() {
		 
		int index = isCurrentlyWinning
				+ 4 * nbOfValuables
				+ 4 * position;
		for(boolean b : boolFeatures) {
			index  = 2*index + booleanToBit(b);
		}
		
		return index;
	}
	
	private static int booleanToBit(boolean b) {
		if(b) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public int hashCode() {
		return stateIndex();
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			return stateIndex() == ((GameState) o).stateIndex();
		} catch(Exception e) {
			return false;
		}
	}
}
