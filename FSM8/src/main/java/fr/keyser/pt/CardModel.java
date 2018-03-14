package fr.keyser.pt;

public final class CardModel {

    private String name;

    private int ageToken;

    private int playedTurn;

    private BuildingLevel level;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getAgeToken() {
	return ageToken;
    }

    public void setAgeToken(int ageToken) {
	this.ageToken = ageToken;
    }

    public int getPlayedTurn() {
	return playedTurn;
    }

    public void setPlayedTurn(int playedTurn) {
	this.playedTurn = playedTurn;
    }

    public BuildingLevel getLevel() {
	return level;
    }

    public void setLevel(BuildingLevel level) {
	this.level = level;
    }
}
