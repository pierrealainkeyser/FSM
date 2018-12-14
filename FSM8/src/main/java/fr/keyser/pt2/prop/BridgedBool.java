package fr.keyser.pt2.prop;

import java.util.function.Function;

public class BridgedBool<T> extends BridgedProp<T, Boolean> implements BoolSupplier {

    public BridgedBool(DirtySupplier<T> source, Function<T, BoolSupplier> mapper) {
	super(source, mapper);
    }
}
