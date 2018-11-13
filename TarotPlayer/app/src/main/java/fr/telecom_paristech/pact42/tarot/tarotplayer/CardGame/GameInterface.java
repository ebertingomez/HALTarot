package fr.telecom_paristech.pact42.tarot.tarotplayer.CardGame;



interface GameInterface {
    public void setPlayer(String player);
    public void addPlayingCard(String card);
    public void setDifficulty(String difficulty);
    public void changeCard(String oldCard, String newCard);
    public void showCard(String card);

    public void hideCard(String card2Play);
}
