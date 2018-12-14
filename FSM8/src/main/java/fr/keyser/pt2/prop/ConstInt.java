package fr.keyser.pt2.prop;

public class ConstInt extends ConstProp<Integer> implements IntSupplier {

    public static final ConstInt ZERO = new ConstInt(0);
    public static final ConstInt ONE = new ConstInt(1);
    public static final ConstInt TWO = new ConstInt(2);
    public static final ConstInt THREE = new ConstInt(3);
    public static final ConstInt FOUR = new ConstInt(4);
    public static final ConstInt SEVEN = new ConstInt(7);

    public static final ConstInt MINUS_TWO = new ConstInt(-2);

    public ConstInt(int value) {
	super(value);
    }
}
