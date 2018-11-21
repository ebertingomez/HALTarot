/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card;

import java.util.ArrayList;
import java.util.Iterator;

import fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.textFile.CardFile;

public class CardTree implements Iterable<Card>{

	private Stack coeur;
	private Stack carreau;
	private Stack pique;
	private Stack trefle;
	private Stack atout;
	private Stack excuse;

	private int offset;

	public CardTree()
	{
		coeur = new Stack();
		carreau = new Stack();
		pique = new Stack();
		trefle = new Stack();
		atout = new Stack();
		excuse = new Stack();
	}

	public int getScore() {
		return coeur.getScore() + carreau.getScore() + pique.getScore() +
				trefle.getScore() + atout.getScore() + excuse.getScore() + offset;
	}

	public int getNbBouts() {
		int nbBouts = 0;
		Stack atouts = this.getStack(Card.atout);
		if(atouts.contains(21))
			nbBouts+=1;
		if(atouts.contains(1))
			nbBouts+=1;
		if(this.getStack(Card.excuse).size()>0)
			nbBouts+=1;
		return nbBouts;
	}

	//Adds an offset in the score if a card has to be exchanged with the excuse. Is ifRemoved = true, the offset is -1.
	//Else, the offset is 1. Since the score is doubled, it corresponds to +/- 0.5 points.
	public void cardExchange(boolean isRemoved) {
		if(isRemoved) {
			offset = -1;
		} else {
			offset = 1;
		}
	}
	
	public int getAtoutsMaitres()
	{
		for ( int i = 21 ; i > 0 ; i-- )
		{
			if ( ! atout.contains(Atout.getCard(i)))
				return 21 - i;
		}
		return 21;
	}

	public void add(Card carte) {
		switch(carte.getCouleur()) {
			case Card.coeur: coeur.add(carte);
				break;
			case Card.carreau: carreau.add(carte);
				break;
			case Card.pique: pique.add(carte);
				break;
			case Card.trefle: trefle.add(carte);
				break;
			case Card.atout: atout.add(carte);
				break;
			case Card.excuse: excuse.add(carte);
				break;

		}
	}

	public static CardTree add(CardTree a, CardTree b) {
		CardTree r = new CardTree();

		Iterator<Card> aIt = a.iterator();
		Iterator<Card> bIt = b.iterator();

		while(aIt.hasNext())
			r.add(aIt.next());
		while(bIt.hasNext())
			r.add(bIt.next());

		return r;
	}


	public Stack getStack(int couleur)
	{
		Stack couleurStack = null;
		switch(couleur)
		{
			case Card.coeur: couleurStack =  coeur;
				break;
			case Card.carreau: couleurStack =  carreau;
				break;
			case Card.pique: couleurStack =  pique;
				break;
			case Card.trefle: couleurStack =  trefle;
				break;
			case Card.atout: couleurStack =  atout;
				break;
			case Card.excuse: couleurStack =  excuse;
				break;
		}
		return couleurStack;
	}

	public boolean hasCouleur(int couleur)
	{
		return !(getStack(couleur).size() == 0);
	}

	public String complete(String m) {
		String longestString = "Cavalier de Carreau";
		while (m.length() < longestString.length()) {
			m+= " ";
		}
		return m;
	}

	@Override
	public String toString()
	{
		String s = "" ;
		String blank = "    ";
		s+= "\n\n";
		s+= complete("== Coeur ==") + blank;
		s+= complete("== Carreau ==") + blank;
		s+= complete("== Pique ==") + blank;
		s+= complete("== Trefle ==") + blank;
		s+= complete("== Atout ==");
		s+= "\n\n";

		Iterator<Card> coeurIt = coeur.iterator();
		Iterator<Card> carreauIt = carreau.iterator();
		Iterator<Card> piqueIt = pique.iterator();
		Iterator<Card> trefleIt = trefle.iterator();
		Iterator<Card> atoutIt = atout.iterator();

		boolean continuer = true;

		while(continuer) {
			continuer = false;
			if (coeurIt.hasNext()) {
				s+= complete(coeurIt.next().toString());
				continuer = true;
			}
			else {
				s+= complete("");
			}
			s+= blank;
			if (carreauIt.hasNext()) {
				s+= complete(carreauIt.next().toString());
				continuer = true;
			}
			else {
				s+= complete("");
			}
			s+= blank;
			if (piqueIt.hasNext()) {
				s+= complete(piqueIt.next().toString());
				continuer = true;
			}
			else {
				s+= complete("");
			}
			s+= blank;
			if (trefleIt.hasNext()) {
				s+= complete(trefleIt.next().toString());
				continuer = true;
			}
			else {
				s+= complete("");
			}
			s+= blank;
			if (atoutIt.hasNext()) {
				s+= complete(atoutIt.next().toString());
				continuer = true;
			}
			else {
				s+= complete("");
			}
			s+= "\n";
		}
		if (excuse.size()!=0)
			s+= "== Excuse ==";
		if (s=="")
			s= "Aucune carte presente";
		return s;
	}

	public Card remove(Card carte) {
		getStack(carte.getCouleur()).remove(carte);
		return carte;
	}

	public Iterator<Card> iterator() {
		Stack[] couleurs = {coeur, carreau, pique, trefle, atout, excuse};
		ArrayList<Card> cards = new ArrayList<Card>();

		for(Stack couleur : couleurs) {
			for(Card card : couleur) {
				cards.add(card);
			}
		}

		return cards.iterator();
	}

	public boolean contains(Card card) {
		return getStack(card.getCouleur()).contains(card);
	}

	public int bouts() {
		int n = 0;

		if(contains(Atout.getCard(1)))
			n++;

		if(contains(Atout.getCard(21)))
			n++;

		if(contains(Excuse.getCard()))
			n++;

		return n;
	}
}
