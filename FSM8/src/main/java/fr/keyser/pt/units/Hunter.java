package fr.keyser.pt.units;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.IntValue;
import fr.keyser.pt.Unit;

public final class Hunter extends Unit {

    public Hunter() {
	super(essence(0)
	        .food(IntValue.choice(DeployedCard::isOnFrontLine, IntValue.ONE, IntValue.ZERO))
	        .combat(IntValue.constant(2)));
    }

}
