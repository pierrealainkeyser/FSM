package fr.keyser.pt2;

import java.util.List;
import java.util.stream.Collectors;

import fr.keyser.pt2.view.FullLocalPlayerView;

public class PlayerStats extends ResourcesStats {

    private int gold;

    private int legend;

    private List<CardMemento> cards;

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

    public FullLocalPlayerView view() {
	FullLocalPlayerView v = new FullLocalPlayerView();
	v.setCombat(getCombat());
	v.setGold(getGold());
	v.setLegend(getLegend());
	v.setFood(getFood());
	v.setWood(getWood());
	v.setCrystal(getCrystal());
	v.setCards(cards.stream().map(CardMemento::view).collect(Collectors.toList()));
	return v;
    }
}
