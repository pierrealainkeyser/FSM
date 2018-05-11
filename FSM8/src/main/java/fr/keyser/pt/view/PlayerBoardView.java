package fr.keyser.pt.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.keyser.pt.CardPosition;

public class PlayerBoardView {

    private List<CardView> cards;

    private Integer score;

    private Integer gold;

    private Boolean idle;

    public List<CardView> getCards() {
	return cards;
    }

    public CardView getCard(CardPosition position) {
	if (cards == null)
	    cards = new ArrayList<>();

	Optional<CardView> first = cards.stream().filter(cv -> cv.getPosition().equals(position)).findFirst();
	if (first.isPresent())
	    return first.get();
	else {
	    CardView cv = new CardView(position);
	    cards.add(cv);
	    return cv;
	}
    }

    public Integer getScore() {
	return score;
    }

    public void setScore(Integer score) {
	this.score = score;
    }

    public Integer getGold() {
	return gold;
    }

    public void setGold(Integer gold) {
	this.gold = gold;
    }

    public Boolean getIdle() {
	return idle;
    }

    public void setIdle(Boolean idle) {
	this.idle = idle;
    }

}
