package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;


abstract class BasicCard {
    protected String name = null;
    protected String value = null;
    protected String description = null;
    protected boolean show;

    public BasicCard() {
        this.show = false;
    }

    public BasicCard(String name, String value, String description, boolean show) {
        this.name = name;
        this.value = value;
        this.description = description;
        this.show = false;
    }

    public abstract void showCard();
    public abstract void hideCard();
    public abstract void playCard();

    public String getName(){return name;}
}
