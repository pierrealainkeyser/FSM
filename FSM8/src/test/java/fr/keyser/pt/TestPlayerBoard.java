package fr.keyser.pt;

import static fr.keyser.pt.CardPosition.Position.BACK;
import static fr.keyser.pt.CardPosition.Position.FRONT;
import static java.util.Arrays.asList;

import org.junit.Assert;
import org.junit.Test;

import fr.keyser.pt.CardPosition.Position;
import fr.keyser.pt.buildings.Town;
import fr.keyser.pt.effects.DropAgeTokenEffect;
import fr.keyser.pt.units.CaveSpirit;
import fr.keyser.pt.units.Farmer;
import fr.keyser.pt.units.Lumberjack;
import fr.keyser.pt.units.Mercenary;
import fr.keyser.pt.units.Necromancer;
import fr.keyser.pt.units.TimeMistress;

public class TestPlayerBoard {

    @Test
    public void testBasicRestore() {
	Board board = new Board(null);

	PlayerBoardModel model = new PlayerBoardModel();

	MetaCardBuilder b = new MetaCardBuilder();
	MetaCard caveSpirit = b.meta(new CaveSpirit());
	MetaCard town = b.meta(new Town());

	PlayerBoard p0 = board.addPlayer(model,
	        asList(new DeployedCardInfo(Position.FRONT.index(0), new CardModel(caveSpirit, 0, 0)),
	                new DeployedCardInfo(Position.BUILDING.index(0), new CardModel(town, BuildingLevel.LEVEL2))));

	p0.computeValues();
	
	Assert.assertEquals(4, p0.getCombat());
	Assert.assertEquals(2, p0.getCrystal());

    }

    @Test
    public void testDeploy() {
	Board board = new Board(null);

	PlayerBoardModel model = new PlayerBoardModel();
	MetaCardBuilder b = new MetaCardBuilder();
	MetaCard caveSpirit = b.meta(new CaveSpirit());
	MetaCard necromancer = b.meta(new Necromancer());
	MetaCard lumberjack = b.meta(new Lumberjack());
	model.getToDeploy().add(caveSpirit);
	model.getToDeploy().add(necromancer);
	model.getToDeploy().add(lumberjack);
	model.addGold(2);

	PlayerBoard p0 = board.addPlayer(model);
	p0.processDeployCardAction(new DoDeployCard(caveSpirit, FRONT.index(0)));
	p0.processDeployCardAction(new DoDeployCard(necromancer, FRONT.index(1)));
	p0.processDeployCardAction(new DoDeployCard(lumberjack, BACK.index(0)));

	DeployedCard u = p0.units().filter(d -> d.getMeta() == caveSpirit).findFirst().get();

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
	MetaCardBuilder b = new MetaCardBuilder();
	MetaCard timeMistress = b.meta(new TimeMistress());
	MetaCard mercenary = b.meta(new Mercenary());
	MetaCard farmer = b.meta(new Farmer());
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
