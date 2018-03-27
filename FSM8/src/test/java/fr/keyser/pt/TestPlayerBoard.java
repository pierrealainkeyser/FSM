package fr.keyser.pt;

import static fr.keyser.pt.CardPosition.Position.BACK;
import static fr.keyser.pt.CardPosition.Position.FRONT;

import org.junit.Assert;
import org.junit.Test;

import fr.keyser.pt.effects.DropAgeTokenEffect;
import fr.keyser.pt.units.CaveSpirit;
import fr.keyser.pt.units.Farmer;
import fr.keyser.pt.units.Lumberjack;
import fr.keyser.pt.units.Mercenary;
import fr.keyser.pt.units.Necromancer;
import fr.keyser.pt.units.TimeMistress;

public class TestPlayerBoard {

    @Test
    public void testDeploy() {
	Board board = new Board(null);

	PlayerBoardModel model = new PlayerBoardModel();
	MetaCard caveSpirit0 = new MetaCard(0, new CaveSpirit());
	MetaCard necromancer = new MetaCard(1, new Necromancer());
	MetaCard lumberjack = new MetaCard(2, new Lumberjack());
	model.getToDeploy().add(caveSpirit0);
	model.getToDeploy().add(necromancer);
	model.getToDeploy().add(lumberjack);
	model.addGold(2);

	PlayerBoard p0 = board.addPlayer(model);
	p0.processDeployCardAction(new DoDeployCard(caveSpirit0, FRONT.index(0)));
	p0.processDeployCardAction(new DoDeployCard(necromancer, FRONT.index(1)));
	p0.processDeployCardAction(new DoDeployCard(lumberjack, BACK.index(0)));

	DeployedCard u = p0.units().filter(d -> d.getMeta() == caveSpirit0).findFirst().get();

	p0.deployPhase();
	p0.endOfDeployPhase();

	Assert.assertEquals(4, u.getCombat());
	Assert.assertEquals(2, u.getAgeToken());

	Assert.assertEquals(0, model.getGold());
	Assert.assertEquals(8, p0.getCombat());
	Assert.assertEquals(1, p0.getCrystal());
	Assert.assertEquals(1, p0.getWood());
    }

    @Test
    public void testDeployTarget() {
	Board board = new Board(null);

	PlayerBoardModel model = new PlayerBoardModel();
	MetaCard timeMistress = new MetaCard(0, new TimeMistress());
	MetaCard mercenary = new MetaCard(1, new Mercenary());
	MetaCard farmer = new MetaCard(2, new Farmer());
	model.getToDeploy().add(timeMistress);
	model.getToDeploy().add(mercenary);
	model.getToDeploy().add(farmer);
	model.addGold(2);

	CardPosition front1 = FRONT.index(1);
	CardPosition front0 = FRONT.index(0);
	PlayerBoard p0 = board.addPlayer(model);
	p0.processDeployCardAction(new DoDeployCard(mercenary, front0));
	p0.processDeployCardAction(new DoDeployCard(timeMistress, front1));
	p0.processDeployCardAction(new DoDeployCard(farmer, BACK.index(0)));

	DeployedCard u = p0.units().filter(d -> d.getMeta() == mercenary).findFirst().get();

	p0.deployPhase();

	Assert.assertEquals(1, model.getInputActions().size());
	p0.processCardAction(new CardAction(front1, DropAgeTokenEffect.DROP_AGE, front0));
	Assert.assertEquals(0, model.getInputActions().size());
	p0.endOfDeployPhase();

	Assert.assertEquals(4, u.getCombat());
	Assert.assertEquals(2, u.getAgeToken());

	Assert.assertEquals(2, model.getGold());
	Assert.assertEquals(5, p0.getCombat());
	Assert.assertEquals(1, p0.getFood());
    }

}
