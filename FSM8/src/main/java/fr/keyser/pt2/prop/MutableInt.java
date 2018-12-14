package fr.keyser.pt2.prop;

public class MutableInt extends MutableProp<Integer> implements IntSupplier {

    public MutableInt() {
    }

    public MutableInt(int value) {
	super(value);
    }

    public void setValue(int value) {
	set(value);
    }

    public void add(int delta) {
	setValue(getValue() + delta);
    }
}
