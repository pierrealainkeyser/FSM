package fr.keyser.pt;

import java.util.function.Predicate;

public class MetaCard {

    private Card card;

    private int id;

    public MetaCard() {
    }

    public MetaCard(int id, Card card) {
	this.id = id;
	this.card = card;
    }

    public Card getCard() {
	return card;
    }

    public static Predicate<? super MetaCard> sameId(int id) {
	return m -> m.getId() == id;
    }

    public int getId() {
	return id;
    }

    public void setCard(Card card) {
	this.card = card;
    }

    public void setId(int id) {
	this.id = id;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + id;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	MetaCard other = (MetaCard) obj;
	if (id != other.id)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return card.getName() + "#" + id;
    }
}
