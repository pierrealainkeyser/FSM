package fr.keyser.pt2.prop;

public class ConstBool extends ConstProp<Boolean> implements BoolSupplier {

    public static final ConstBool FALSE = new ConstBool(false);
    public static final ConstBool TRUE = new ConstBool(true);

    public ConstBool(boolean value) {
	super(value);
    }
}
