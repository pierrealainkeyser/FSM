package fr.keyser.pt2;

import java.util.List;

public class PlayerScoreMemento {

    private int gold;
    private int legend;
    private List<CardMemento> cards;

    public PlayerScoreMemento() {
	super();
    }

    public List<CardMemento> getCards() {
        return cards;
    }

    public int getGold() {
        return gold;
    }

    public int getLegend() {
        return legend;
    }

    public void setCards(List<CardMemento> cards) {
        this.cards = cards;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setLegend(int legend) {
        this.legend = legend;
    }

}