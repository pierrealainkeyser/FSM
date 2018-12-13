package fr.keyser.pt2.prop;

public class PlugableBool extends PlugableProp<Boolean> implements BoolSupplier {

    public PlugableBool() {
	super(false);
    }

    public PlugableBool(DirtySupplier<Boolean> supplier) {
	super(false, supplier);
    }
}
