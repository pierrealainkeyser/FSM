package fr.keyser.n.fsm.automat;

import fr.keyser.n.fsm.Event;

public class Started extends Event {

    public final static Started INSTANCE = new Started();

    private Started() {
	super("<started>");
    }
}
