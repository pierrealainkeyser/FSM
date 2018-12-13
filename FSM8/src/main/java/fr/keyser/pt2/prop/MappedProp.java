package fr.keyser.pt2.prop;

import java.util.function.Function;

public class MappedProp<T, R> extends ComputedProp<R> {

    private final Function<T, R> mapper;

    private final DirtySupplier<T> supplier;

    public MappedProp(Function<T, R> mapper, DirtySupplier<T> supplier) {
	super(supplier);
	this.mapper = mapper;
	this.supplier = supplier;
    }

    @Override
    protected R compute() {
	return mapper.apply(supplier.get());
    }
}
