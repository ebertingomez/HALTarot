/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

import java.util.ArrayList;
import java.util.Hashtable;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;
/**
 *  This class is used as a library to store all the constant information about the cards.
 *  @version 1.0
 */
public final class TarotCardLibrary {
    /**
     * This variable contains a relation between the name of each card and the ID of the image which represents each.
     */
    public final static Hashtable<String, Integer> cardsTable = new Hashtable<String, Integer>();
    static {
        cardsTable.put("1A", Integer.valueOf(R.drawable.card_1a));
        cardsTable.put("2A", Integer.valueOf(R.drawable.card_2a));
        cardsTable.put("3A", Integer.valueOf(R.drawable.card_3a));
        cardsTable.put("4A", Integer.valueOf(R.drawable.card_4a));
        cardsTable.put("5A", Integer.valueOf(R.drawable.card_5a));
        cardsTable.put("6A", Integer.valueOf(R.drawable.card_6a));
        cardsTable.put("7A", Integer.valueOf(R.drawable.card_7a));
        cardsTable.put("8A", Integer.valueOf(R.drawable.card_8a));
        cardsTable.put("9A", Integer.valueOf(R.drawable.card_9a));
        cardsTable.put("0A", Integer.valueOf(R.drawable.card_0a));
        cardsTable.put("VA", Integer.valueOf(R.drawable.card_va));
        cardsTable.put("CA", Integer.valueOf(R.drawable.card_ca));
        cardsTable.put("DA", Integer.valueOf(R.drawable.card_da));
        cardsTable.put("RA", Integer.valueOf(R.drawable.card_ra));

        cardsTable.put("1O", Integer.valueOf(R.drawable.card_1o));
        cardsTable.put("2O", Integer.valueOf(R.drawable.card_2o));
        cardsTable.put("3O", Integer.valueOf(R.drawable.card_3o));
        cardsTable.put("4O", Integer.valueOf(R.drawable.card_4o));
        cardsTable.put("5O", Integer.valueOf(R.drawable.card_5o));
        cardsTable.put("6O", Integer.valueOf(R.drawable.card_6o));
        cardsTable.put("7O", Integer.valueOf(R.drawable.card_7o));
        cardsTable.put("8O", Integer.valueOf(R.drawable.card_8o));
        cardsTable.put("9O", Integer.valueOf(R.drawable.card_9o));
        cardsTable.put("0O", Integer.valueOf(R.drawable.card_0o));
        cardsTable.put("VO", Integer.valueOf(R.drawable.card_vo));
        cardsTable.put("CO", Integer.valueOf(R.drawable.card_co));
        cardsTable.put("DO", Integer.valueOf(R.drawable.card_do));
        cardsTable.put("RO", Integer.valueOf(R.drawable.card_ro));

        cardsTable.put("1P", Integer.valueOf(R.drawable.card_1p));
        cardsTable.put("2P", Integer.valueOf(R.drawable.card_2p));
        cardsTable.put("3P", Integer.valueOf(R.drawable.card_3p));
        cardsTable.put("4P", Integer.valueOf(R.drawable.card_4p));
        cardsTable.put("5P", Integer.valueOf(R.drawable.card_5p));
        cardsTable.put("6P", Integer.valueOf(R.drawable.card_6p));
        cardsTable.put("7P", Integer.valueOf(R.drawable.card_7p));
        cardsTable.put("8P", Integer.valueOf(R.drawable.card_8p));
        cardsTable.put("9P", Integer.valueOf(R.drawable.card_9p));
        cardsTable.put("0P", Integer.valueOf(R.drawable.card_0p));
        cardsTable.put("VP", Integer.valueOf(R.drawable.card_vp));
        cardsTable.put("CP", Integer.valueOf(R.drawable.card_cp));
        cardsTable.put("DP", Integer.valueOf(R.drawable.card_dp));
        cardsTable.put("RP", Integer.valueOf(R.drawable.card_rp));

        cardsTable.put("1T", Integer.valueOf(R.drawable.card_1t));
        cardsTable.put("2T", Integer.valueOf(R.drawable.card_2t));
        cardsTable.put("3T", Integer.valueOf(R.drawable.card_3t));
        cardsTable.put("4T", Integer.valueOf(R.drawable.card_4t));
        cardsTable.put("5T", Integer.valueOf(R.drawable.card_5t));
        cardsTable.put("6T", Integer.valueOf(R.drawable.card_6t));
        cardsTable.put("7T", Integer.valueOf(R.drawable.card_7t));
        cardsTable.put("8T", Integer.valueOf(R.drawable.card_8t));
        cardsTable.put("9T", Integer.valueOf(R.drawable.card_9t));
        cardsTable.put("0T", Integer.valueOf(R.drawable.card_0t));
        cardsTable.put("VT", Integer.valueOf(R.drawable.card_vt));
        cardsTable.put("CT", Integer.valueOf(R.drawable.card_ct));
        cardsTable.put("DT", Integer.valueOf(R.drawable.card_dt));
        cardsTable.put("RT", Integer.valueOf(R.drawable.card_rt));

        cardsTable.put("EX", Integer.valueOf(R.drawable.card_ex));
        cardsTable.put("01", Integer.valueOf(R.drawable.card_01));
        cardsTable.put("02", Integer.valueOf(R.drawable.card_02));
        cardsTable.put("03", Integer.valueOf(R.drawable.card_03));
        cardsTable.put("04", Integer.valueOf(R.drawable.card_04));
        cardsTable.put("05", Integer.valueOf(R.drawable.card_05));
        cardsTable.put("06", Integer.valueOf(R.drawable.card_06));
        cardsTable.put("07", Integer.valueOf(R.drawable.card_07));
        cardsTable.put("08", Integer.valueOf(R.drawable.card_08));
        cardsTable.put("09", Integer.valueOf(R.drawable.card_09));
        cardsTable.put("10", Integer.valueOf(R.drawable.card_10));
        cardsTable.put("11", Integer.valueOf(R.drawable.card_11));
        cardsTable.put("12", Integer.valueOf(R.drawable.card_12));
        cardsTable.put("13", Integer.valueOf(R.drawable.card_13));
        cardsTable.put("14", Integer.valueOf(R.drawable.card_14));
        cardsTable.put("15", Integer.valueOf(R.drawable.card_15));
        cardsTable.put("16", Integer.valueOf(R.drawable.card_16));
        cardsTable.put("17", Integer.valueOf(R.drawable.card_17));
        cardsTable.put("18", Integer.valueOf(R.drawable.card_18));
        cardsTable.put("19", Integer.valueOf(R.drawable.card_19));
        cardsTable.put("20", Integer.valueOf(R.drawable.card_20));
        cardsTable.put("21", Integer.valueOf(R.drawable.card_21));

        cardsTable.put("question", Integer.valueOf(R.drawable.card_question));
    }

    /**
     * This array contains all the cards in the game. To be used along with the spinners of each UnsuccessfulScan
     * @see android.widget.Spinner
     * @see fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.UnsuccessfulScanHandActivity
     * @see fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.UnsuccessfulScanChienActivity
     * @see fr.telecom_paristech.pact42.tarot.tarotplayer.Activities.UnsuccessfulScanTableActivity
     */
    public final static ArrayList<String> cards = new ArrayList<String>();
    static {
        cards.add("1A");
        cards.add("2A");
        cards.add("3A");
        cards.add("4A");
        cards.add("5A");
        cards.add("6A");
        cards.add("7A");
        cards.add("8A");
        cards.add("9A");
        cards.add("0A");
        cards.add("VA");
        cards.add("CA");
        cards.add("DA");
        cards.add("RA");
        cards.add("1O");
        cards.add("2O");
        cards.add("3O");
        cards.add("4O");
        cards.add("5O");
        cards.add("6O");
        cards.add("7O");
        cards.add("8O");
        cards.add("9O");
        cards.add("0O");
        cards.add("VO");
        cards.add("CO");
        cards.add("DO");
        cards.add("RO");
        cards.add("1P");
        cards.add("2P");
        cards.add("3P");
        cards.add("4P");
        cards.add("5P");
        cards.add("6P");
        cards.add("7P");
        cards.add("8P");
        cards.add("9P");
        cards.add("0P");
        cards.add("VP");
        cards.add("CP");
        cards.add("DP");
        cards.add("RP");
        cards.add("1T");
        cards.add("2T");
        cards.add("3T");
        cards.add("4T");
        cards.add("5T");
        cards.add("6T");
        cards.add("7T");
        cards.add("8T");
        cards.add("9T");
        cards.add("0T");
        cards.add("VT");
        cards.add("CT");
        cards.add("DT");
        cards.add("RT");

        cards.add("EX");
        cards.add("01");
        cards.add("02");
        cards.add("03");
        cards.add("04");
        cards.add("05");
        cards.add("06");
        cards.add("07");
        cards.add("08");
        cards.add("09");
        cards.add("10");
        cards.add("11");
        cards.add("12");
        cards.add("13");
        cards.add("14");
        cards.add("15");
        cards.add("16");
        cards.add("17");
        cards.add("18");
        cards.add("19");
        cards.add("20");
        cards.add("21");
    }
}
