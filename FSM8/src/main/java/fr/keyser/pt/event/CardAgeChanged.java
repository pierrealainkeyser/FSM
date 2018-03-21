package fr.keyser.pt.event;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.PlayerBoard;

public class CardAgeChanged extends DeployedCardEvent {

    private final int age;

    public CardAgeChanged(DeployedCard card, PlayerBoard board, int age) {
	super(card, board);
	this.age = age;
    }

    public int getAge() {
        return age;
    }

}
