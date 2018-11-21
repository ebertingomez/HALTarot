/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.card;

import java.util.Iterator;
import java.util.TreeSet;

public class Stack implements Iterable<Card> {

	// Elements in a SortedSet are always sorted. Remove, add and contains operations are in log(n)
	private TreeSet<Card> cards;
	private int points;

	public Stack() {
		cards = new TreeSet<Card>();
		points = 0;
	}


	//public boolean contains(Card carte) {
	//return cartes.contains(carte);
	//}

	public boolean contains(int value) {
		Iterator<Card> it = this.iterator();
		while(it.hasNext()) {
			if(it.next().getValue() == value) {
				return true;
			}
		}
		return false;
	}
	public boolean contains(Card carte) {
		return cards.contains(carte);
	}


	// This returns the lowest card that is strictly greater to the card given in argument, or null if there are no such cards
	public Card getLowestCardAfter(Card carte) {
		//System.out.println("   --> Play lowest after " + carte);
		if (carte == null)
			return cards.first();
		return cards.higher(carte); }

	public Card getHighestCardAfter(Card card) {
		if(card == null) {
			return cards.last();
		} else {
			return cards.lower(card);
		}
	}

	public Card getHighestCard() {
		//System.out.println("   --> Play highest card");
		return cards.last();
	}

	public Card getLowestCard() {
		//System.out.println("   --> Play lowest card");
		//The petit has to be treated differently

		Card card = cards.first();

		if(card.equals(Atout.getCard(1))) {
			Card c = cards.lower(Atout.getCard(1));
			if(c != null) {
				return c;
			}
		}

		return card;
	}

	public boolean hasHigherCardThan(Card carte) {
		if (carte == null)
			return true;
		return (cards.higher(carte) != null );
	}

	public Card remove(Card carte) {
		points -= carte.getScore();
		cards.remove(carte);
		return carte;
	}

	public void add(Card carte) {
		cards.add(carte);
		points += carte.getScore();
	}

	public int size() {
		return cards.size();
	}

	public int getScore() {
		return points;
	}

	@Override
	public String toString()
	{
		String s = "";
		Iterator<Card> it = cards.iterator();

		while(it.hasNext())
		{
			s = s + it.next().toString() + "\n";
		}

		return s;
	}


	public Iterator<Card> iterator() {
		return cards.iterator();
	}

	public Card getHighestAbsent(Card maxCard) {

		if(cards.isEmpty()) {
			return maxCard;
		}

		Card current = getHighestCard();
		while(current != null && current.getValue() > 1 && maxCard.equals(current)) {
			current = cards.lower(current);
			maxCard = maxCard.getLower();
		}

		return maxCard;
	}	
}
