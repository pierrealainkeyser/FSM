package fr.keyser.pt2.prop;

import java.util.Collection;

public abstract class ComputedInt extends ComputedProp<Integer> implements IntSupplier {

    public ComputedInt(Collection<? extends DirtyObserver> sources) {
	super(sources);
    }

    public ComputedInt(DirtyObserver... sources) {
	super(sources);
    }

}
