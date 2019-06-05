package fr.keyser.pt2;

import org.junit.jupiter.api.Test;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt2.effects.ChoosenTargets;
import fr.keyser.pt2.prop.MutableInt;
import fr.keyser.pt2.units.Alchimist;
import fr.keyser.pt2.units.CaveSpirit;
import fr.keyser.pt2.units.ForestChildren;

public class TestPlayer {
    @Test
    public void test() {

	MutableInt turn = new MutableInt(1);
	LocalBoard lb1 = new LocalBoard(turn);
	LocalPlayer lp1 = new LocalPlayer(null, lb1);

	CardProvider cp = new CardProvider();
	cp.addUnit(CaveSpirit::new);
	cp.addUnit(Alchimist::new);
	cp.addUnit(ForestChildren::new);

	lb1.front(0).play(cp.unit("CaveSpirit"));
	lb1.front(1).play(cp.unit("Alchimist"));
	lb1.back(0).play(cp.unit("ForestChildren"));
	lp1.deployPhase();

	System.out.println("------lb1--------");
	System.out.println(lp1.getPublicView());
	System.out.println(lp1.getPrivateView());
	System.out.println(lp1.deployEffects());

	System.out.println(lp1.activateDeploy(CardPosition.Position.FRONT.index(0), new ChoosenTargets()));

	System.out.println("------activate--------");
	System.out.println(lp1.getPublicView());
	System.out.println(lp1.getPrivateView());
	System.out.println(lp1.deployEffects());

	lp1.endDeployPhase();
	System.out.println(lp1.getPublicView());
	System.out.println(lp1.getPrivateView());

    }
}
