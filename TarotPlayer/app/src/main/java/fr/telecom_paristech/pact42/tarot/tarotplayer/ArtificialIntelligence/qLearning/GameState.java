package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.qLearning;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Card;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.CardTree;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.AIPlayer;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Player;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Turn;

public abstract class GameState {
	
	public static int NB_OF_STATES = 0;
	
	public abstract int stateIndex();
	
	protected int isCurrentlyWinning(Game game, Turn turn, int couleur, AIPlayer player) {
		
		boolean haveMaxAtout = false;
		Card highestUnplayedAtout = game.getPlayedCards().getStack(Card.atout).getHighestAbsent(Card.getCard(Card.atout, 21));
		
		for(Player p : game.getTeam(player)) {
			if(turn.getPlayedCard(p) != null) {
				haveMaxAtout = haveMaxAtout || turn.getPlayedCard(p).equals(highestUnplayedAtout);
			}
		}
		
		if(haveMaxAtout) {
			return 3;
		}
		
		CardTree main = player.getMain();
		Player leader = turn.getLeader();
		int maxColorValue;
		if(couleur == Card.atout) {
			maxColorValue = 21;
		} else {
			maxColorValue = 14;
		}
		
		if(leader.isPreneur() == player.isPreneur() 
				|| main.getStack(couleur).hasHigherCardThan(turn.getPlayedCard(leader))) {
			
			boolean haveMaxColor = false;
			
			Card highestUnplayed = game.getPlayedCards().getStack(couleur).getHighestAbsent(Card.getCard(couleur, maxColorValue));
			haveMaxColor = main.contains(highestUnplayed);
			for(Player p : game.getTeam(player)) {
				if(turn.getPlayedCard(p) != null) {
					haveMaxColor = haveMaxColor || turn.getPlayedCard(p).equals(highestUnplayed);
				}
			}
			
			if(haveMaxColor) {
				return 2;
			} else {
				return 1;
			}
		}
		
		return 0;
	}
	
	protected static boolean teamOutOfColor(boolean side, int color, Game game, Turn turn) {
		boolean outOfColor = false;
		for(Player p : game.getTeam(side)) {
			outOfColor = outOfColor || p.isOutOf(color);
		}
		return outOfColor;
	}
	
	protected static boolean teamOutOfAtout(boolean side, Game game, Turn turn) {
		boolean outOfAtout = true;
		for(Player p : game.getTeam(side)) {
			outOfAtout = outOfAtout && p.isOutOf(Card.atout);
		}
		return outOfAtout;
	}

}
