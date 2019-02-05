/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

import java.util.Hashtable;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

/**
 *  This class is used as a library to store all the constant information about the encheres.
 *  @version 1.0
 */
public final class EnchereLibrary {
    /**
     * This variable contains a relation between the name of the encheres and the ID of the image which represents each.
     */
    public final static Hashtable<String, Integer> enchereTable = new Hashtable<String, Integer>();
    static {
        enchereTable.put("CO", R.drawable.enchere_set2);
        enchereTable.put("SC", R.drawable.enchere_set3);
        enchereTable.put("GA", R.drawable.enchere_set4);
        enchereTable.put("PE", R.drawable.enchere_set5);
        enchereTable.put("PA", R.drawable.enchere_set6);
    }

    /**
     * This variable is used to assign a value(ponderation) to each enchere.
     */
    public final static Hashtable<String, Integer> enchereTableValue = new Hashtable<String, Integer>();
    static {
        enchereTableValue.put("Chelem", 6);
        enchereTableValue.put("CO", 5);
        enchereTableValue.put("SC", 4);
        enchereTableValue.put("GA", 3);
        enchereTableValue.put("PE", 2);
        enchereTableValue.put("PA", 1);
    }
}
