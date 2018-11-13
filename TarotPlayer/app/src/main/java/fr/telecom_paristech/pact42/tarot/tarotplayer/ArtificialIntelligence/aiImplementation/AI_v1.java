package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.aiImplementation;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.binomial.Binomial;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Atout;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Card;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.CardTree;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.ClassicCard;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Stack;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.AIPlayer;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Bid;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Game;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game.Turn;

/*** This is our first IA implementation
 * It doesn't use any machine learning methods
 */

public class AI_v1 implements TarotAI {

	private static final int RDC = 6;
	private static final int RD = 5;
	private static final int RCV = 4;
	private static final int DCV = 3;
	private static final int DC = 2;
	private static final int DV = 1;
	private static final int CV = 0;

	private Game partie;
	private Turn tour;
	private CardTree main;
	private CardTree chien;
	private Card carteMeneur;

	private Stack longue;

	private int couleur; // Just for convience, it is stored in tour

	public AI_v1(AIPlayer player) {
		this.main = player.getMain();
	}

	@Override
	public void init(Game partie, Turn tour) {
		this.partie = partie;
		this.tour = tour;

		this.carteMeneur = tour.getLeaderCard();
		this.couleur = tour.getCouleur();
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
	
	public float cutProbability(int couleur) {
		int nbColorCardsInDefense = 14 - main.getStack(couleur).size() - chien.getStack(couleur).size();
		int nbAtoutsInDefense = 21 - main.getStack(Card.atout).size() - chien.getStack(Card.atout).size();
		float probaFirstPlayerOutOfColor = (float) Math.pow(2f/3f,nbColorCardsInDefense);
		float probaFirstPlayerHasAtout = 1f - (float) Math.pow(2f/3f,nbAtoutsInDefense);
		float probability = 3f*probaFirstPlayerOutOfColor*probaFirstPlayerHasAtout;
		return probability;
	}

	// Plays the lowest card from a random color

	public Card playFirst() {

		if (tour.getNumber() == 0) {

		}

		return getRandomCard();
	}

	// When atout was played first and IA has atout

	public Card playAtoutAfterAtout() {
		return main.getStack(Card.atout).getHighestCard();
	}

	// When atout was played first but IA doesn't have any atout

	public Card playColorAfterAtout() {
		return getRandomCard();
	}

	@Override
	public Card playColor() {
		Stack stack = main.getStack(couleur);
		if (stack.hasHigherCardThan(carteMeneur))
			return stack.getLowestCardAfter(carteMeneur);
		else
			return stack.getLowestCard();
	}

	// If IA has a higher atout than the highest atout played, it will play the lowest of
	// these higher atouts. Else, it will play it's lowest atout

	public Card cut() {
		Stack stack = main.getStack(Card.atout);
		if ( stack.hasHigherCardThan(tour.getHighestAtout()) )
			return main.getStack(Card.atout).getLowestCardAfter(tour.getHighestAtout());
		return main.getStack(Card.atout).getLowestCard();
	}

	// When a couleur was played first, but IA doesn't have the couleur or atouts

	public Card playOtherColorAfterColor() {
		return getRandomCard();
	}
	
	@Override
	public int bid(Bid enchere) {
		int points = main.getScore();
		int nbAtouts = main.getStack(Card.atout).size();
		int nbBouts = main.getNbBouts();
		int estimation = points+50;
		
		switch(nbBouts) {
		case 0:
			estimation-=56*2;
			break;
		case 1:
			estimation-=51*2;
			break;
		case 2:
			estimation-=41*2;
			break;
		case 3:
			estimation-=36*2;
			break;
		}
		estimation+= (nbAtouts - 4)*3;
		String output = "Bouts = " + nbBouts + ", Points = " + points + ", nbAtout = " + nbAtouts + ", Estimation: " + estimation + " - ";
		System.out.println("Encheres restantes: " + enchere.getEncheresLeft());
		if(estimation < 0) {			
			if (enchere.getEncheresLeft() == 1 && enchere.maxEnchere() == Bid.PASSE) {
				System.out.println(output + "Petite au lieu de passer.");
				return Bid.PETITE;
			}
			System.out.println(output + "Passe.");
			return Bid.PASSE;
		}
		if(estimation < 20) {
			if (enchere.maxEnchere() == Bid.PASSE) {
				System.out.println(output + "Petite.");
				return Bid.PETITE;
			}
			System.out.println(output + "Passe au lieu de petite.");
		}
		if (enchere.maxEnchere() == Bid.PASSE || enchere.maxEnchere() == Bid.PETITE) {
			System.out.println(output + "Garde.");
			return Bid.GARDE;
		}
		System.out.println(output + "Passe au lieu de garder");
		return Bid.PASSE;
	}
	
	public String listeTetes(Stack stack) {
		String s = "";
		if (stack.contains(14)) {
			s+="R";	
		}
		if (stack.contains(13)) {
			s+="D";	
		}
		if (stack.contains(12)) {
			s+="C";	
		}
		if (stack.contains(11)) {
			s+="V";	
		}
		return s;
	}
	
	public int faireCoupe(Stack stack, int ecartsLeft) {
		if (stack.contains(14))
			return -1;
		return (stack.size() > ecartsLeft) ? -1 : (stack.size());
	}
	
	public int garderRepriseDouble(Stack stack, int ecartsLeft) {
		String s = listeTetes(stack);

		switch(s) {
		case "RD":
			return (stack.size()-2 > ecartsLeft) ? -1 : RD;
		case "DC":
			return (stack.size()-2 > ecartsLeft) ? -1 : DC;
		case "CV":
			return (stack.size()-2 > ecartsLeft) ? -1 : CV;
		case "DV":
			return (stack.size()-2 > ecartsLeft) ? -1 : DV;
		default:
			return -1;
		}
	}
	
	public int garderRepriseTriple(Stack stack, int ecartsLeft) {
		String s = listeTetes(stack);
				
		switch(s) {
		case "RDC":
			return (stack.size()-3 > ecartsLeft) ? -1 : RDC;
		case "RDCV":
			return (stack.size()-3 > ecartsLeft) ? -1 : RDC;
		case "DCV":
			return (stack.size()-3 > ecartsLeft) ? -1 : DCV;
		default:
			return -1;
		}
	}
	
	public int ecarterHonneur(Stack stack, int ecartsLeft, int nbAtouts, int nbBouts) {
		String s = listeTetes(stack);
		
		switch(s) {
		case "V":
			return (ecartsLeft < 1) ? -1 : 11;
		case "C":
			return (ecartsLeft < 1) ? -1 : 12;
		case "D":
			return (ecartsLeft < 1) ? -1 : 13;
		case "RV":
			return (ecartsLeft < 1) ? -1 : 11;
		case "RC":
			return (ecartsLeft < 1) ? -1 : 12;
		default:
			return -1;
		}
	}
	
	public int estimationCoupe(int nbCoupe, int nbAtouts, int nbBouts) {
		return 1;
	}
	
	public int estimationReprise(int reprise, int nbAtouts, int nbBouts) {
		return 1;
	}
	
	public int estimationEcartHonneur(int honneur, int nbAtouts, int nbBouts) {
		return 1;
	}
	
	public int getGlobalEstimation(CardTree mainAvecChien, int longueColor, int[] decision) {
		int estimationGlobale = 0;
		
		return estimationGlobale;
	}
	
	@Override
	public void chien(CardTree chienCardTree) {
		this.chien = partie.getChien();

		CardTree mainAvecChien = CardTree.add(chien, main);
		Stack atouts = mainAvecChien.getStack(Card.atout);

		// We first determine the longue
		int longueSize = mainAvecChien.getStack(Card.carreau).size();
		int longueColor = Card.carreau;
		int temp;

		temp = mainAvecChien.getStack(Card.coeur).size();
		if (temp > longueSize) {
			longueSize = temp;
			longueColor = Card.coeur;
		}
		temp = mainAvecChien.getStack(Card.pique).size();
		if (temp > longueSize) {
			longueSize = temp;
			longueColor = Card.pique;
		}
		temp = mainAvecChien.getStack(Card.trefle).size();
		if (temp > longueSize) {
			longueSize = temp;
			longueColor = Card.trefle;
		}

		int[] bestDecision = {-1, -1, -1, -1};
		int bestDecisionEstimation = 0;

		for(int firstDecision = 0; firstDecision < 4 ; firstDecision++) {
			for(int secondDecision = 0; secondDecision < 4; secondDecision++) {
				for(int thirdDecision = 0; thirdDecision < 4; thirdDecision++) {
					int[] decision = {firstDecision, secondDecision, thirdDecision};
					int decisionEstimation = getGlobalEstimation(mainAvecChien, longueColor, decision);

					if(decisionEstimation > bestDecisionEstimation) {
						bestDecision = decision;
						bestDecisionEstimation = decisionEstimation;

					}
				}
			}
		}

		String output = "";
		output+= "Decision: ";
		output+= bestDecision[0] + " ";
		output+= bestDecision[1] + " ";
		output+= bestDecision[2] + " ";
		output+= bestDecision[3];
		System.out.println(output);

	}

	public int bid(int maxBid) {


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

			if ( carte.getCouleur() == Card.atout && carte.getValue() == 1)
				petit = 1;

			if ( carte.getCouleur() == Card.atout && carte.getValue() == 21)
				vingt_et_un = 1;

			if ( carte.getCouleur() == Card.excuse )
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
			for ( int i = 0 ; i < atoutsMaitres + 1 ; i ++ )
				scoreAttendu += 6 * Binomial.coefficient(22 - atouts, i) * Binomial.coefficient(56 + atouts, 18 - i) / Binomial.coefficient(78, 18);
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

	@Override
	public void update(Turn turn) {
		// TODO Auto-generated method stub

	}
}
