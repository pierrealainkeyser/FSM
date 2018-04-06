package fr.keyser.pt;

import static fr.keyser.pt.CardPosition.Position.BACK;
import static fr.keyser.pt.CardPosition.Position.FRONT;

import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import fr.keyser.pt.buildings.Mine;
import fr.keyser.pt.effects.DropAgeTokenEffect;
import fr.keyser.pt.event.PlayerLegendChanged;
import fr.keyser.pt.units.CaveSpirit;
import fr.keyser.pt.units.Farmer;
import fr.keyser.pt.units.Lumberjack;
import fr.keyser.pt.units.Mercenary;
import fr.keyser.pt.units.Necromancer;
import fr.keyser.pt.units.TimeMistress;

public class TestPlayerBoard {

    @Test
    public void testBasicWar() {

	@SuppressWarnings("unchecked")
	Consumer<PlayerLegendChanged> listener = Mockito.mock(Consumer.class);

	BoardBuilder builder = new BoardBuilder();
	builder.listenTo(PlayerLegendChanged.class, listener);

	MetaCard caveSpirit = builder.meta(new CaveSpirit());
	MetaCard mine = builder.meta(new Mine());

	PlayerBoard p0 = builder.player()
	        .front(caveSpirit)
	        .level2(mine)
	        .build();

	p0.computeValues();

	Assertions.assertEquals(4, p0.getCombat());
	Assertions.assertEquals(2, p0.getCrystal());

	p0.setVictoriousWar(2);
	p0.computeWarGain();

	Mockito.verify(listener).accept(Mockito.argThat(plc -> {
	    int base = 3 * 2;
	    int fromMine = 2 * 2;
	    return plc.getLegend() == base + fromMine;
	}));

    }

    @Test
    public void testDeploy() {
	BoardBuilder builder = new BoardBuilder();

	MetaCard caveSpirit = builder.meta(new CaveSpirit());
	MetaCard necromancer = builder.meta(new Necromancer());
	MetaCard lumberjack = builder.meta(new Lumberjack());

	PlayerBoard p0 = builder.player()
	        .toDeploy(caveSpirit)
	        .toDeploy(necromancer)
	        .toDeploy(lumberjack)
	        .addGold(2)
	        .build();

	p0.deployPhase();

	p0.processDeployCardAction(new DoDeployCard(caveSpirit, FRONT.index(0)));
	p0.processDeployCardAction(new DoDeployCard(necromancer, FRONT.index(1)));
	p0.processDeployCardAction(new DoDeployCard(lumberjack, BACK.index(0)));

	DeployedCard u = p0.units().filter(d -> d.getMeta() == caveSpirit).findFirst().get();

	p0.endOfDeployPhase();

	Assertions.assertEquals(4, u.getCombat());
	Assertions.assertEquals(2, u.getAgeToken());

	Assertions.assertEquals(0, p0.getGold());
	Assertions.assertEquals(8, p0.getCombat());
	Assertions.assertEquals(1, p0.getCrystal());
	Assertions.assertEquals(1, p0.getWood());
    }

    @Test
    public void testDeployTarget() {
	BoardBuilder builder = new BoardBuilder();

	MetaCard timeMistress = builder.meta(new TimeMistress());
	MetaCard mercenary = builder.meta(new Mercenary());
	MetaCard farmer = builder.meta(new Farmer());

	PlayerBoard p0 = builder.player()
	        .toDeploy(timeMistress)
	        .toDeploy(mercenary)
	        .toDeploy(farmer)
	        .addGold(2)
	        .build();

	CardPosition front1 = FRONT.index(1);
	CardPosition front0 = FRONT.index(0);
	p0.processDeployCardAction(new DoDeployCard(mercenary, front0));
	p0.processDeployCardAction(new DoDeployCard(timeMistress, front1));
	p0.processDeployCardAction(new DoDeployCard(farmer, BACK.index(0)));

	DeployedCard u = p0.units().filter(d -> d.getMeta() == mercenary).findFirst().get();

	p0.deployPhase();

	Assertions.assertEquals(1, p0.getInputActions().size());
	p0.processCardAction(new CardAction(front1, DropAgeTokenEffect.DROP_AGE, front0));
	Assertions.assertEquals(0, p0.getInputActions().size());
	p0.endOfDeployPhase();

	Assertions.assertEquals(4, u.getCombat());
	Assertions.assertEquals(2, u.getAgeToken());

	Assertions.assertEquals(2, p0.getGold());
	Assertions.assertEquals(5, p0.getCombat());
	Assertions.assertEquals(1, p0.getFood());
    }

}
