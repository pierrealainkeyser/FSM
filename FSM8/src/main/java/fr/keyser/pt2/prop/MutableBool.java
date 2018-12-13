package fr.keyser.pt2.prop;

public class MutableBool extends MutableProp<Boolean> implements BoolSupplier {

    public MutableBool() {
	super(false);
    }

    public MutableBool(boolean value) {
	super(value);
    }

    public void setValue(boolean value) {
	set(value);
    }

}
