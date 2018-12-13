package fr.keyser.pt2.prop;

public interface BoolSupplier extends DirtySupplier<Boolean> {

    public default boolean getValue() {
	Boolean b = get();
	if (b != null)
	    return b;
	else
	    return false;
    }

    public default BoolSupplier not() {
	BoolSupplier me = this;
	return new ComputedBool(me) {

	    @Override
	    protected Boolean compute() {
		return !me.getValue();
	    }
	};
    }

    public default BoolSupplier or(BoolSupplier or) {
	BoolSupplier me = this;
	return new ComputedBool(me, or) {

	    @Override
	    protected Boolean compute() {
		return me.getValue() || or.getValue();
	    }
	};
    }

    public default BoolSupplier and(BoolSupplier and) {
	BoolSupplier me = this;
	return new ComputedBool(me, and) {

	    @Override
	    protected Boolean compute() {
		return me.getValue() && and.getValue();
	    }
	};
    }
}
