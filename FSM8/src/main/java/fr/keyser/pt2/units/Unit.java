package fr.keyser.pt2.units;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.prop.MutableInt;

public abstract class Unit extends Card {

    public void addAge(int delta) {
	MutableInt age = getAge();
	age.setValue(age.getValue() + delta);
    }
}
