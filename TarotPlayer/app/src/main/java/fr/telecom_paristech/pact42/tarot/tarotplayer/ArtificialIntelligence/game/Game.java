package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.game;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card.*;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile.BidFile;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile.CardFile;
import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile.NotACardException;


public class Game {

	private static Game game;

	// *** Constants ***

	// File directories
	public static final String CHIEN_FILE_NAME = "IA/data/chien.txt";
	public static final String GAME_FILE_NAME = "IA/data/jeu.txt";
	public static final String BID_FILE_NAME = "IA/data/encheres.txt";

	// Code for the return of bid() and play()
	public static final int OVER = 0;
	public static final int AI_PLAYER = 1;
	public static final int REAL_PLAYER = 2;


	// *** Attributes ***

	private Player[] players; //an array containing the four player in their order
	private Player preneur;

	private Bid enchere;
	private int enchereNb = 0;

	private CardFile gameCardFile = new CardFile(GAME_FILE_NAME);
	private CardTree chien = new CardFile(CHIEN_FILE_NAME).toCardTree();
	private CardTree plisPreneur;
	private CardTree plisAdverses;
	private CardTree playedCards;

	private Turn turn;


	// *** Constructor ***

	private Game(Player J0, Player J1, Player J2, Player J3) throws IOException {

		//Generating the players array with the right order
		players = new Player[4];
		players[J0.getPosition()] = J0;
		players[J1.getPosition()] = J1;
		players[J2.getPosition()] = J2;
		players[J3.getPosition()] = J3;

		chien = new CardFile(CHIEN_FILE_NAME).toCardTree();
		System.out.println("\nLe chien est constitué de:" + chien );

		plisPreneur = new CardTree();
		plisAdverses = new CardTree();
		playedCards = new CardTree();

		chien = new CardFile(CHIEN_FILE_NAME).toCardTree();
		//System.out.println("Le chien est : " +chien);

		try {
			gameCardFile.init(); //new blank file
		} catch(IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void initGame(Player J0, Player J1, Player J2, Player J3) throws IOException {
		game = new Game(J0, J1, J2, J3);
	}

	public Player getPlayer(int position) {
		return players[position];
	}

	public CardTree getChien() {
		return chien;
	}
	public ArrayList<Player> getTeam(boolean isPreneur) {
		ArrayList<Player> team = new ArrayList<Player>();
		for(Player player : players) {
			if(player.isPreneur() == isPreneur) {
				team.add(player);
			}
		}
		return team;
	}

	public ArrayList<Player> getTeam(Player player) {
		return getTeam(player.isPreneur());
	}


	// --- addPli methods are used to add gained cards to the right cardFile ---
	// Adds a card to the plis specified by a boolean boolean (true if the card is for the preneur)
	public void addPli(boolean isForPreneur, Card card) {
		if(isForPreneur) {
			plisPreneur.add(card);
		} else {
			plisAdverses.add(card);
		}
		playedCards.add(card);
	}

	// Adds a card to a player's team plis (useful to make the code easier to read)
	public void addPli(Player joueur, Card card) {
		addPli(joueur.isPreneur(), card);
	}

	// Adds all the cards from a CardTree to the plis specified by a boolean (true if the cards are for the preneur)
	public void addPli(boolean isForPreneur, CardTree cards) {
		for(Card card : cards) {
			addPli(isForPreneur, card);
		}
	}

	/* In complement to addPli, the case of the excuse has to be treated.
	 * If the excuse is kept by a player who loses the pli, a 0.5-points card has to be traded
	 * with the excuse. To simplify this, we simply remove 0.5 points to one team an add
	 * it to the other.
	 * The cardExchange method takes the player who keeps the excuse and does this.*/
	public void cardExchange(Player excusePlayer) {
		boolean sideOfExcuse = excusePlayer.isPreneur();
		plisPreneur.cardExchange(sideOfExcuse);
		plisAdverses.cardExchange(!sideOfExcuse);
	}

	public static int play() throws ClassNotFoundException, IOException {
		if(game == null) {
			return OVER;
		}
		return game.playGame();
	}

	/* Called before and after every play
	 * Takes the previously played card into account (making the AI think if necessary) and
	 * returns AI_PLAYER, REAL_PLAYER or OVER if the next player is an AI, a real
	 * player or if the game is over respectively */
	private int playGame() throws IOException, ClassNotFoundException {

		//First turn
		if(turn == null) {

			//If no one chose to be the preneur
			if(preneur == null) {
				endOfGame();
				return OVER;
			}

			turn = new Turn(this);
			System.out.println("\n  ###### Tour " + turn.getNumber() + " ######  \n");

			if(turn.isAITurn()) {
				return AI_PLAYER;
			} else {
				return REAL_PLAYER;
			}
		}

		turn.play();

		//Generating the next turn
		if(turn.isOver()) {

			for(Player p : players) {
				if(p.isAI()) {
					((AIPlayer) p).updateAI(turn);
				}
			}
			turn.addPli();

			if(turn.isLast()) {
				endOfGame();
				return OVER;

			} else {

				turn = new Turn(this, turn);
				System.out.println("\n  ###### Tour " + turn.getNumber() + " ######  \n");

			}
		}

		if(turn.isAITurn()) {
			return AI_PLAYER;
		} else {
			return REAL_PLAYER;
		}
	}

	public static int bid() throws IOException, ClassNotFoundException {

		if(game == null) {
			initGame(new RealPlayer(0), new RealPlayer(1),
					new RealPlayer(2), new AIPlayer(3, new CardFile("IA/data/main.txt")));
		}

		return game.bidGame();
	}

	/* Called before and after every play
	 * Takes the previous bidding into account (making the AI think if necessary) and
	 * returns AI_PLAYER, REAL_PLAYER or OVER if the next player is an AI, a real
	 * player or if everyone bid respectively */
	private int bidGame() throws IOException {

		if(enchere == null) {

			enchere = new Bid(this, new BidFile(BID_FILE_NAME));
			enchereNb = 0;

			if(getPlayer(0).isAI()) {
				return AI_PLAYER;
			} else {
				return REAL_PLAYER;
			}
		}

		getPlayer(enchereNb).bid(this, enchere);
		enchereNb ++;

		if(enchereNb >= 4) {

			preneur = enchere.preneur();

			if ( preneur == null )
			{
				endOfGame();
				return OVER;
			}

			preneur.setPreneur();
			System.out.println("\n Le joueur " + preneur.getPosition() + " a pris");

			if(preneur.isAI() && enchere.maxEnchere() <= Bid.GARDE) {
				((AIPlayer) preneur).chien(chien); //If the bid of the AI preneur is lower than garde he has to make the "écart"
			}

			if(enchere.maxEnchere() <= Bid.GARDE_SANS) {
				addPli(true, chien); //If the bid of the preneur is lower than garde sans, he keeps the chien
			} else {
				addPli(false, chien); //Otherwise he looses it.
			}

			if(preneur.isAI() && enchere.maxEnchere() <= Bid.GARDE) {
				((AIPlayer) preneur).chien(this, chien); //If the bid of the AI preneur is lower than garde he has to make the "écart"
			}

			return OVER;

		} else {

			if(getPlayer(0).isAI()) {
				return AI_PLAYER;
			} else {
				return REAL_PLAYER;
			}
		}
	}

	//TODO write in a file the results
	// -> see with the Android module the appropriate format
	private void endOfGame() {

		float score = plisPreneur.getScore();
		int bouts = plisPreneur.bouts();
		boolean victory = false;

		if(bouts == 0) {

		}

		System.out.println("\nJeu Terminé.");
		if(preneur == null) {
			System.out.println("Personne n'a pris.");
		} else {
			System.out.println("Le preneur (joueur " + preneur.getPosition() + ") a marque " + plisPreneur.getScore()/2. +
					" points.\nLes autres ont marque " + plisAdverses.getScore()/2. + " points."
					+ "\nPlis du preneur :" + plisPreneur
					+ "\nPlis des autres :" + plisAdverses);
		}

		game = null;
	}

	public void addPlayedCardGame(Card carte) throws IOException {
		gameCardFile.addCard(carte);
	}

	public static void addPlayedCard(Card carte) throws IOException {
		game.addPlayedCardGame(carte);
	}

	public CardTree getPlayedCards() {
		return playedCards;
	}


	public Card lastPlayedCard() throws FileNotFoundException, NotACardException {
		return gameCardFile.lastCard();
	}
}
