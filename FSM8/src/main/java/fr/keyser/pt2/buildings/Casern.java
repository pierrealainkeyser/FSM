package fr.keyser.pt2.buildings;

import fr.keyser.pt2.LocalBoard;

public class Casern extends Building {
    public Casern() {
	super(cost().wood(1), cost().wood(1).food(1));
	combat = mapInt(LocalBoard::getWood);
	warLegend = mapInt(LocalBoard::getUnitsStrenghtAbove3).when(getLevel2());
    }
}
