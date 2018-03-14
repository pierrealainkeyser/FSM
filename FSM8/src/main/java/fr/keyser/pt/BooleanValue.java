package fr.keyser.pt;

import java.util.function.Predicate;

public interface BooleanValue extends Predicate<DeployedCard> {

    public static final class Test implements BooleanValue {

	private final Predicate<DeployedCard> predicate;

	private Test(Predicate<DeployedCard> predicate) {
	    this.predicate = predicate;
	}

	@Override
	public boolean getValue(DeployedCard ctx) {
	    return predicate.test(ctx);
	}
    }

    public static final class Constant implements BooleanValue {

	private final boolean value;

	private Constant(boolean value) {
	    this.value = value;
	}

	@Override
	public boolean getValue(DeployedCard ctx) {
	    return value;
	}
    }

    public static BooleanValue constant(boolean value) {
	return new Constant(value);
    }

    public static BooleanValue card(Predicate<DeployedCard> predicate) {
	return new Test(predicate);
    }

    public static BooleanValue player(Predicate<PlayerBoard> predicate) {
	return new Test(c -> predicate.test(c.getPlayer()));
    }

    public static final BooleanValue FALSE = constant(false);

    public static final BooleanValue TRUE = constant(true);

    public static final BooleanValue HAS_CRYSTAL = player(p -> p.getCrystal() > 0);

    public boolean getValue(DeployedCard ctx);

    public default boolean test(DeployedCard ctx) {
	return getValue(ctx);
    }
}
