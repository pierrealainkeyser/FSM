package fr.keyser.pt2.prop;

import java.util.function.Function;

public class MappedInt<T> extends MappedProp<T, Integer> implements IntSupplier {

    public MappedInt(Function<T, Integer> mapper, DirtySupplier<T> supplier) {
	super(mapper, supplier);
    }
}
