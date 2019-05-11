package fr.keyser.pt2;

import org.junit.jupiter.api.Test;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.effects.ChoosenTargets;
import fr.keyser.pt2.prop.MutableInt;
import fr.keyser.pt2.units.CaveSpirit;

public class TestPlayer {
    @Test
    public void test() {

	MutableInt turn = new MutableInt(1);
	LocalBoard lb1 = new LocalBoard(turn);
	LocalPlayer lp1 = new LocalPlayer(lb1);

	lb1.front(0).play(new CaveSpirit());

	System.out.println("------lb1--------");
	System.out.println(lb1);
	System.out.println(lp1.deployEffects());

	System.out.println(lp1.activateDeploy(CardPosition.Position.FRONT.index(0), new ChoosenTargets()));

	System.out.println("------activate--------");
	System.out.println(lb1);
	System.out.println(lp1.deployEffects());

    }
}
