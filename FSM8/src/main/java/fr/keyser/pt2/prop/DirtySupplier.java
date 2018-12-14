package fr.keyser.pt2.prop;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DirtySupplier<T> extends Supplier<T>, DirtyObserver {

    public default <R> DirtySupplier<R> map(Function<T, DirtySupplier<R>> mapper) {
	return new BridgedProp<>(this, mapper);
    }

    public default IntSupplier mapInt(Function<T, IntSupplier> mapper) {
	return new BridgedInt<>(this, mapper);
    }

    public default BoolSupplier mapBool(Function<T, BoolSupplier> mapper) {
	return new BridgedBool<>(this, mapper);
    }

    public static <T> IntSupplier count(Stream<DirtySupplier<T>> mapped, Predicate<T> predicate) {
	List<DirtySupplier<T>> suppliers = mapped.collect(Collectors.toList());
	return new ComputedInt(suppliers) {
	    @Override
	    protected Integer compute() {
		return (int) suppliers.stream().filter(d -> predicate.test(d.get())).count();
	    }
	};
    }

    public default BoolSupplier match(Predicate<T> predicate) {
	DirtySupplier<T> me = this;
	return new ComputedBool(me) {
	    @Override
	    protected Boolean compute() {
		return predicate.test(me.get());
	    }
	};
    }
}
