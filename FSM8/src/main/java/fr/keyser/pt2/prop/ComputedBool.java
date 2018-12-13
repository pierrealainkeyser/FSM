package fr.keyser.pt2.prop;

import java.util.Collection;

public abstract class ComputedBool extends ComputedProp<Boolean> implements BoolSupplier {

    public ComputedBool(Collection<? extends DirtyObserver> sources) {
	super(sources);
    }

    public ComputedBool(DirtyObserver... sources) {
	super(sources);
    }

}
