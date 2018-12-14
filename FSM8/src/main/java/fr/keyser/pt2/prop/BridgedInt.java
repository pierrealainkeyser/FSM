package fr.keyser.pt2.prop;

import java.util.function.Function;

public class BridgedInt<T> extends BridgedProp<T, Integer> implements IntSupplier {

    public BridgedInt(DirtySupplier<T> source, Function<T, IntSupplier> mapper) {
	super(source, mapper);
    }
}
