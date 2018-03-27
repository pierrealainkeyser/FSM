package fr.keyser.pt;

import fr.keyser.pt.buildings.Casern;
import fr.keyser.pt.buildings.Mine;
import fr.keyser.pt.buildings.Tavern;
import fr.keyser.pt.buildings.Temple;
import fr.keyser.pt.buildings.Town;
import fr.keyser.pt.units.Adventurer;
import fr.keyser.pt.units.Alchimist;
import fr.keyser.pt.units.Archer;
import fr.keyser.pt.units.CaveSpirit;
import fr.keyser.pt.units.Chapman;
import fr.keyser.pt.units.Chief;
import fr.keyser.pt.units.Chiromancian;
import fr.keyser.pt.units.Colossus;
import fr.keyser.pt.units.Daemon;
import fr.keyser.pt.units.Dragon;
import fr.keyser.pt.units.Farmer;
import fr.keyser.pt.units.ForestChildren;
import fr.keyser.pt.units.ForestSpirit;
import fr.keyser.pt.units.General;
import fr.keyser.pt.units.Golem;
import fr.keyser.pt.units.GrandArchitect;
import fr.keyser.pt.units.GreatSnake;
import fr.keyser.pt.units.Hunter;
import fr.keyser.pt.units.Knigth;
import fr.keyser.pt.units.Kraken;
import fr.keyser.pt.units.Leviathan;
import fr.keyser.pt.units.Looter;
import fr.keyser.pt.units.Lumberjack;
import fr.keyser.pt.units.Manticore;
import fr.keyser.pt.units.Mercenary;
import fr.keyser.pt.units.Miner;
import fr.keyser.pt.units.Monkey;
import fr.keyser.pt.units.Mystic;
import fr.keyser.pt.units.Necromancer;
import fr.keyser.pt.units.Notable;
import fr.keyser.pt.units.Salamander;
import fr.keyser.pt.units.Sculptrice;
import fr.keyser.pt.units.ShapeShifter;
import fr.keyser.pt.units.Tactician;
import fr.keyser.pt.units.TimeMistress;
import fr.keyser.pt.units.Treeman;
import fr.keyser.pt.units.Troll;
import fr.keyser.pt.units.Veteran;
import fr.keyser.pt.units.WoodNegociant;

public class DefaultDeckBuilder {

    private DefaultDeckBuilder() {
    };

    public final static DefaultDeckBuilder DEFAULT = new DefaultDeckBuilder();

    final Town town = new Town();
    final Casern casern = new Casern();
    final Temple temple = new Temple();
    final Mine mine = new Mine();
    final Tavern tavern = new Tavern();

    final Adventurer adventurer = new Adventurer();
    final Alchimist alchimist = new Alchimist();
    final Archer archer = new Archer();
    final CaveSpirit caveSpirit = new CaveSpirit();
    final Chapman chapman = new Chapman();
    final Chief chief = new Chief();
    final Chiromancian chiromancian = new Chiromancian();
    final Colossus colossus = new Colossus();
    final Daemon daemon = new Daemon();
    final Dragon dragon = new Dragon();
    final Farmer farmer = new Farmer();
    final ForestChildren forestChildren = new ForestChildren();
    final ForestSpirit forestSpirit = new ForestSpirit();
    final General general = new General();
    final Golem golem = new Golem();
    final GrandArchitect grandArchitect = new GrandArchitect();
    final GreatSnake greatSnake = new GreatSnake();
    final Hunter hunter = new Hunter();
    final Knigth knigth = new Knigth();
    final Kraken kraken = new Kraken();
    final Leviathan leviathan = new Leviathan();
    final Looter looter = new Looter();
    final Lumberjack lumberjack = new Lumberjack();
    final Manticore manticore = new Manticore();
    final Mercenary mercenary = new Mercenary();
    final Miner miner = new Miner();
    final Monkey monkey = new Monkey();
    final Mystic mystic = new Mystic();
    final Necromancer necromancer = new Necromancer();
    final Notable notable = new Notable();
    final Salamander salamander = new Salamander();
    final Sculptrice sculptrice = new Sculptrice();
    final ShapeShifter shapeShifter = new ShapeShifter();
    final Tactician tactician = new Tactician();
    final TimeMistress timeMistress = new TimeMistress();
    final Treeman treeman = new Treeman();
    final Troll troll = new Troll();
    final Veteran veteran = new Veteran();
    final WoodNegociant woodNegociant = new WoodNegociant();

    public void loadUnits(MetaDeck deck) {

	deck.load(adventurer, 2);
	deck.load(alchimist, 1);
	deck.load(archer, 2);
	deck.load(caveSpirit, 2);
	deck.load(chapman, 3);
	deck.load(chief, 2);
	deck.load(chiromancian, 1);
	deck.load(colossus, 1);
	deck.load(daemon, 1);
	deck.load(dragon, 1);
	deck.load(farmer, 3);
	deck.load(forestChildren, 3);
	deck.load(forestSpirit, 1);
	deck.load(general, 1);
	deck.load(golem, 2);
	deck.load(grandArchitect, 1);
	deck.load(greatSnake, 1);
	deck.load(hunter, 3);
	deck.load(knigth, 3);
	deck.load(kraken, 1);
	deck.load(leviathan, 1);
	deck.load(looter, 2);
	deck.load(lumberjack, 3);
	deck.load(manticore, 3);
	deck.load(mercenary, 3);
	deck.load(miner, 3);
	deck.load(monkey, 2);
	deck.load(mystic, 3);
	deck.load(necromancer, 1);
	deck.load(notable, 1);
	deck.load(salamander, 1);
	deck.load(sculptrice, 2);
	deck.load(shapeShifter, 1);
	deck.load(tactician, 1);
	deck.load(timeMistress, 3);
	deck.load(treeman, 2);
	deck.load(troll, 1);
	deck.load(veteran, 3);
	deck.load(woodNegociant, 2);
    }

    public void loadBuilding(MetaDeck deck) {
	deck.load(town);
	deck.load(temple);
	deck.load(mine);
	deck.load(tavern);
	deck.load(casern);

    }

}
