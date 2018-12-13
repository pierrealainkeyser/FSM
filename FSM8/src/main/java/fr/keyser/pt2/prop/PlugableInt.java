package fr.keyser.pt2.prop;

public class PlugableInt extends PlugableProp<Integer> implements IntSupplier {

    public PlugableInt() {
	super(0);
    }

    public PlugableInt(IntSupplier supplier) {
	super(0, supplier);
    }
}
