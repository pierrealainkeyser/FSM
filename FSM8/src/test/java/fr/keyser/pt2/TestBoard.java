package fr.keyser.pt2;

import org.junit.jupiter.api.Test;

import fr.keyser.pt2.buildings.Tavern;
import fr.keyser.pt2.buildings.Town;
import fr.keyser.pt2.prop.MutableInt;
import fr.keyser.pt2.units.Chief;
import fr.keyser.pt2.units.Chiromancian;
import fr.keyser.pt2.units.Farmer;
import fr.keyser.pt2.units.Knight;
import fr.keyser.pt2.units.Leviathan;
import fr.keyser.pt2.units.Looter;
import fr.keyser.pt2.units.Manticore;
import fr.keyser.pt2.units.Princess;
import fr.keyser.pt2.units.Sculptrice;

public class TestBoard {
    @Test
    public void test() {

	MutableInt turn = new MutableInt(1);

	LocalBoard lb1 = new LocalBoard(turn);
	LocalBoard lb2 = new LocalBoard(turn);

	lb1.setNeighbour(lb2);
	lb2.setNeighbour(lb1);

	lb1.front(0).play(new Knight());
	Leviathan leviathan = new Leviathan();
	lb1.front(1).play(leviathan);
	lb1.front(2).play(new Looter());
	lb1.back(0).play(new Princess());

	Town town = new Town();
	town.setBuildingLevel(2);
	lb1.building(0).play(town);

	Tavern tavern = new Tavern();
	tavern.setBuildingLevel(2);
	lb1.building(1).play(tavern);

	Sculptrice sculptrice = new Sculptrice();

	lb2.front(0).play(new Manticore());
	lb2.front(1).play(sculptrice);
	lb2.front(2).play(new Chief());
	lb2.back(0).play(new Farmer());
	lb2.back(1).play(new Chiromancian());
	sculptrice.addAge(2);

	System.out.println("------lb1--------");
	System.out.println(lb1);

	System.out.println("------lb1 aged--------");
	leviathan.addAge(1);
	System.out.println(lb1);

	System.out.println("------lb2--------");
	System.out.println(lb2);
    }
}
