package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game;

import java.io.IOException;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.aiImplementation.AI_v1;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.aiImplementation.TarotAI;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Card;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.CardTree;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.Stack;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.qLearning.QLearningAI;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile.CardFile;

public class AIPlayer extends Player {

	private CardTree mainIA;
	private TarotAI ai;

	public AIPlayer(int position, CardFile mainFile) throws ClassNotFoundException, IOException {
		super(position);
		mainIA = mainFile.toCardTree();
		this.ai = new QLearningAI(this);

		//System.out.println("\nLe joueur " + position + " a pour jeu :" + mainIA );
	}

	public void setAI(TarotAI ai) {
		this.ai = ai;
	}

	@Override
	public boolean isAI() {
		return true;
	}

	@Override
	public Card play(Game partie, Turn tour) throws IOException, ClassNotFoundException {

		int couleur = tour.getCouleur();
		Card carte;

		ai.init(partie, tour); // This is so we don't have to give partie and tour as arguments for the following calls

		// If the IA must play first, it can play any card it wants (except special cases, e.g. the excuse can't be
		// played at the end)
		if (tour.isFirstPlayer(this) || couleur == Card.excuse) {
			carte = ai.playFirst();
		}

		else {
			// If the first played card is an atout
			if (couleur == Card.atout) {
				// If the IA has no atout, it can play any card it wants
				if (isOutOf(Card.atout)) {
					carte = ai.playColorAfterAtout();
				}
				// If the IA has atout, it should either play the highest atout it has, or the lowest but still
				// greater atout than the already played atout cards
				else
					carte = ai.playAtoutAfterAtout();
			}
			// If the first played card is a coeur / carreau / pique / trefle
			else { // TODO : Deal with the case where excuse is played first
				// If the IA has no couleur
				if (isOutOf(couleur)) {
					// If the IA has no atout, it can play any card it wants
					if (isOutOf(Card.atout))
						carte = ai.playOtherColorAfterColor();
					else {
						// If the IA has atout, it should "cut" with an atout
						// It can either cut with the petit, cut with the highest atout it has, or cut with the lowest but still
						// greater atout than the already played atout cards
						carte = ai.cut();
					}
				}
				else {
					carte = ai.playColor();
				}
			}
		}

		partie.addPlayedCard(carte);
		mainIA.remove(carte);
		return carte;
	}

	@Override
	public boolean isOutOf(int couleur) {
		return(!mainIA.hasCouleur(couleur));
	}

	@Override
	public void bid(Game partie, Bid enchere) throws IOException {
		// TODO Auto-generated method stub
		enchere.setEnchere(position, ai.bid(enchere));
		System.out.println("Le joueur " + position + " a encheri");
	}

	public void chien(Game partie, CardTree chienCardTree) {
		ai.chien(chienCardTree);
	}

	public void bid(Bid enchere) throws IOException {
		int e = ai.bid(enchere);
		System.out.println(e);
		enchere.setEnchere(position, e);
		System.out.println("Le joueur " + position + " a encheri ");
	}

	public void chien(CardTree chienCardTree) {
		ai.chien(chienCardTree);
	}

	public CardTree getMain() {
		return mainIA;
	}

	public void updateAI(Turn turn) throws ClassNotFoundException, IOException {
		ai.update(turn);
	}
}