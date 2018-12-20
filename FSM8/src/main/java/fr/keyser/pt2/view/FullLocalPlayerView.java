package fr.keyser.pt2.view;

import java.util.List;

public class FullLocalPlayerView extends PartialLocalPlayerView {

    private int food;

    private int wood;

    private int crystal;

    private int combat;

    private List<CardView> cards;

    public int getFood() {
	return food;
    }

    public void setFood(int food) {
	this.food = food;
    }

    public int getWood() {
	return wood;
    }

    public void setWood(int wood) {
	this.wood = wood;
    }

    public int getCrystal() {
	return crystal;
    }

    public void setCrystal(int crystal) {
	this.crystal = crystal;
    }

    public int getCombat() {
	return combat;
    }

    public void setCombat(int combat) {
	this.combat = combat;
    }

    public List<CardView> getCards() {
	return cards;
    }

    public void setCards(List<CardView> cards) {
	this.cards = cards;
    }
}
