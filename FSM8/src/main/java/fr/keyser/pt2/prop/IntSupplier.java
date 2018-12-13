package fr.keyser.pt2.prop;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IntSupplier extends DirtySupplier<Integer> {

    public default int getValue() {
	Integer i = get();
	if (i != null)
	    return i;
	else
	    return 0;
    }

    public default IntSupplier add(IntSupplier right) {
	IntSupplier me = this;
	return new ComputedInt(me, right) {
	    @Override
	    protected Integer compute() {
		return me.getValue() + right.getValue();
	    }
	};
    }

    public static IntSupplier accumulate(Stream<IntSupplier> mapped, BinaryOperator<Integer> accumulator) {
	List<IntSupplier> suppliers = mapped.collect(Collectors.toList());
	return new ComputedInt(suppliers) {
	    @Override
	    protected Integer compute() {
		Stream<Integer> intstream = suppliers.stream().map(IntSupplier::getValue);
		return intstream.reduce(accumulator).orElse(0);
	    }
	};
    }

    public default IntSupplier mult(IntSupplier right) {
	IntSupplier me = this;
	return new ComputedInt(me, right) {
	    @Override
	    protected Integer compute() {
		return me.getValue() * right.getValue();
	    }
	};
    }

    public default BoolSupplier eq(IntSupplier right) {
	IntSupplier me = this;
	return new ComputedBool(me, right) {
	    @Override
	    protected Boolean compute() {
		return me.getValue() == right.getValue();
	    }
	};
    }
    
    public default BoolSupplier lt(IntSupplier right) {
  	IntSupplier me = this;
  	return new ComputedBool(me, right) {
  	    @Override
  	    protected Boolean compute() {
  		return me.getValue() < right.getValue();
  	    }
  	};
      }

    public default BoolSupplier gte(IntSupplier right) {
	IntSupplier me = this;
	return new ComputedBool(me, right) {
	    @Override
	    protected Boolean compute() {
		return me.getValue() >= right.getValue();
	    }
	};
    }

    public default IntSupplier when(BoolSupplier predicate) {
	IntSupplier me = this;
	return new ComputedInt(me, predicate) {
	    @Override
	    protected Integer compute() {
		return predicate.getValue() ? me.getValue() : 0;
	    }
	};
    }

}
