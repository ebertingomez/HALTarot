package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;

import java.util.Hashtable;
import fr.telecom_paristech.pact42.tarot.tarotplayer.R;

final class EnchereLibrary {
    private final static Hashtable<String,Integer> enchereTable = null;
    static{
        enchereTable.put("GC",Integer.valueOf(R.drawable.enchere_set2));
        enchereTable.put("GS",Integer.valueOf(R.drawable.enchere_set3));
        enchereTable.put("GD",Integer.valueOf(R.drawable.enchere_set4));
        enchereTable.put("PE",Integer.valueOf(R.drawable.enchere_set5));
        enchereTable.put("PA",Integer.valueOf(R.drawable.enchere_set6));
    }
}
