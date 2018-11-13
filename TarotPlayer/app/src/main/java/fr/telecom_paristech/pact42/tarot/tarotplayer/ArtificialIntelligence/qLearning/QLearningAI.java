package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.qLearning;

import java.io.IOException;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.aiImplementation.AI_v1;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.aiImplementation.TarotAI;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.binomial.Binomial;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Atout;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Card;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.CardTree;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.ClassicCard;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Excuse;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Stack;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.AIPlayer;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Bid;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Player;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Turn;

public class QLearningAI implements TarotAI {

	public static final int NB_OF_OUTPUTS = 2;
	public static final int LOWEST_CARD = 0;
	public static final int HIGHEST_CARD = 1;
	public static final int PETIT = 2;
	
	/*The AI will randomly choose a sub-optimal action in order to explore
	 * all solutions.
	 * A random rate of 0 makes the AI deterministic, a random rate of 0.5
	 * gives even chances to choose the best estimated action or an action
	 * amongst the other ones (better for learning but stupid AI). */
	public static final float RANDOM_RATE = (float) 0.1;
	
	private AIPlayer player;
	private Game game;
	private Turn turn;
	private CardTree main;
	private int color;
	
	private static ExpectedValue expectedValueColor;
	private static ExpectedValue expectedValueCut;
	
	private GameState state;
	private int action;
	
	public QLearningAI(AIPlayer player) throws ClassNotFoundException, IOException {
		this.player = player;
		this.main = player.getMain();
		expectedValueColor = new ExpectedValueColor();
		expectedValueCut = new ExpectedValueCut();
	}
	
	@Override
	public void init(Game partie, Turn tour) throws ClassNotFoundException, IOException {
		
		this.game = partie;
		this.turn = tour;
		this.color = tour.getCouleur();
		
	}
	
	public Card getRandomCard() {
		
		if (main.hasCouleur(Card.carreau)) {
			return main.getStack(Card.carreau).getLowestCard();
		} else if (main.hasCouleur(Card.pique)) {
			return main.getStack(Card.pique).getLowestCard();
		} else if (main.hasCouleur(Card.excuse)) {
			return main.getStack(Card.excuse).getLowestCard();
		} else if (main.hasCouleur(Card.trefle)) {
			return main.getStack(Card.trefle).getLowestCard();
		} else if (main.hasCouleur(Card.coeur)) {
			return main.getStack(Card.coeur).getLowestCard();
		} else {
			return main.getStack(Card.atout).getLowestCard();
		}
	}

	@Override
	public Card playFirst() {
		
		float bestValue = (float) 0.;
		color = -1;
		
		//Choix de la couleur donnant la plus grande espérance estimée
		for(int c : Card.colors) {
			if(!player.isOutOf(c)) {
				GameStateColor state = new GameStateColor(player, game, turn, c);
				float value = expectedValueColor.getValue(state, expectedValueColor.bestAction(state));
				if(color == -1 || value > bestValue) {
					bestValue = value;
					color = c;
				}
			}
		}
		
		System.out.println(" > Couleur " + color + " choisie.");
		return playColor();
	}

	@Override
	public Card playAtoutAfterAtout() {
		color = Card.atout;
		return playColor();
	}

	@Override
	public Card playColorAfterAtout() {
		return getRandomCard();
	}

	@Override
	public Card playColor() {
		
		System.out.println(" == QLearning : play color");
		state = new GameStateColor(player, game, turn, color);
		action = expectedValueColor.bestAction(state);
		
		//Randomness to explore all the solutions
		if(Math.random() <= RANDOM_RATE) {
			if(action == HIGHEST_CARD) {
				action = LOWEST_CARD;
			} else {
				action = HIGHEST_CARD;
			}
		}
		
		return playCard();
	}

	@Override
	public Card cut() {
		
		System.out.println(" == QLearning : cut");
		state = new GameStateCut(player, game, turn, color);
		action = expectedValueCut.bestAction(state);
		
		//Randomness to explore all the solutions
		if(Math.random() <= RANDOM_RATE) {
			if(action == HIGHEST_CARD) {
				if(Math.random() <= 0.5) {
					action = LOWEST_CARD;
				} else {
					action = PETIT;
				}
			} else if(action == LOWEST_CARD){
				if(Math.random() <= 0.5) {
					action = HIGHEST_CARD;
				} else {
					action = PETIT;
				}
			} else {
				if(Math.random() <= 0.5) {
					action = LOWEST_CARD;
				} else {
					action = HIGHEST_CARD;
				}
			}
		}
		
		Card c = playCard();
		return c;
	}
	
	private Card playCard() {
		
		int neededColor = color;
		
		if(state instanceof GameStateCut) {
			neededColor = Card.atout;
		}
		
		if(action == HIGHEST_CARD) {
			System.out.println(" ===> Highest card");
			return player.getMain().getStack(neededColor).getHighestCard();
		}
		
		if(action == PETIT && main.contains(Atout.getCard(1))) {
			System.out.println(" ===> Petit");
			return Atout.getCard(1);
		}
		
		if(neededColor == Card.atout) {
			Card c = turn.getHighestAtout();
			c = main.getStack(Card.atout).getLowestCardAfter(c);
			
			if(c != null) {
				return c;
			}
		}
		
		return main.getStack(neededColor).getLowestCard();
	}

	@Override
	public Card playOtherColorAfterColor() {
		return getRandomCard();
	}

	@Override
	public void chien(CardTree chienCardTree) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Turn turn) throws ClassNotFoundException, IOException {
		if(state != null) {
			
			if(player.isPreneur()) {
				if(state instanceof GameStateColor) {
					expectedValueColor.updateValue(state, action, turn.getScorePli());
				} else if(state instanceof GameStateCut) {
					expectedValueCut.updateValue(state, action, turn.getScorePli());
				}
				
			} else {
				if(state instanceof GameStateColor) {
					expectedValueColor.updateValue(state, action, - turn.getScorePli());
				} else if(state instanceof GameStateCut) {
					expectedValueCut.updateValue(state, action, - turn.getScorePli());
				}
			}
		}
		
		if(turn.isLast()) {
			expectedValueColor.write();
			expectedValueCut.write();
		}
	}
	
	@Override
	public int bid(Bid bid) {
		
		int maxBid = bid.maxEnchere();
		
		int rois = 0;
		int dames = 0;
		int cavaliers = 0;
		int valets = 0;
		int atoutsMaitres = main.getAtoutsMaitres();
		float petit = 0;
		int excuse = 0;
		int vingt_et_un = 0;
		int atouts = main.getStack(Card.atout).size() + main.getStack(Card.excuse).size() + 1;	// le +1 est l'espérance du nombre d'atouts du chien.
		
		
		for ( Card carte : main)
		{
			System.out.println(carte);
			
			if (carte.equals(Atout.getCard(1)))
				petit = 1;
			
			if (carte.equals(Atout.getCard(21)))
				vingt_et_un = 1;
			
			if (carte.equals(Excuse.getCard()))
				excuse = 1;
			
			if ( carte instanceof ClassicCard )
			{
				if ( carte.getValue() == 14 )
					rois ++;
				
				if ( carte.getValue() == 13 )
					dames ++;
				
				if ( carte.getValue() == 12 )
					cavaliers ++;
				
				if ( carte.getValue() == 11)
					valets ++;
			}
		}
		
		float scoreAttendu = 
				(float) (	rois			* 6 	+
							dames			* 5		+
							cavaliers		* 3.8	+
							valets			* 2.4 	+
							atouts			* 3 	+
							atoutsMaitres	* 4		+
							4);
		
		if ( petit == 1 && atouts < 6 )				//risque capture du petit
			scoreAttendu -= ( 7 - atouts ) / 3;
		
		if ( petit == 0 && atoutsMaitres > 2 )		// chasse au petit
		{
			for ( int i = 0 ; i < atoutsMaitres + 1 ; i ++ ) {
				scoreAttendu += 6 * Binomial.coefficient(22 - atouts, i) * Binomial.coefficient(56 + atouts, 18 - i) / Binomial.coefficient(78, 18);
			}
		}
		
		if ( petit + excuse + vingt_et_un == 1)
			scoreAttendu += 10;
		else if ( petit + excuse + vingt_et_un == 2)
			scoreAttendu += 25;
		else if ( petit + excuse + vingt_et_un == 3)
			scoreAttendu += 35;
		
		if ( scoreAttendu > 70 && maxBid == Bid.GARDE_SANS)
			return Bid.GARDE_CONTRE;
		if ( scoreAttendu > 70 && maxBid == Bid.GARDE)
			return Bid.GARDE_SANS;
		if ( scoreAttendu > 62 && maxBid < Bid.GARDE)
			return Bid.GARDE;
		if ( scoreAttendu > 56 && maxBid < Bid.PETITE)
			return Bid.PETITE;
		
		return Bid.PASSE;
	}
}
