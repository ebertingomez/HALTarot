package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.qLearning;

import java.util.Arrays;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Card;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.AIPlayer;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Player;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Turn;

public class GameStateCut extends GameState {

	public static final int NB_OF_STATES = (int) (Math.pow(2, 3) * Math.pow(4, 3));
	
	public boolean[] boolFeatures;
	
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

	public GameStateCut(AIPlayer player, Game game, Turn turn, int color) {
		
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
		
		boolean[] boolFeatures = {enemyOutOfColor, enemyOutOfAtout, isTaker};
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
}
