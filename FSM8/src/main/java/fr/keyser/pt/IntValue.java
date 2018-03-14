package fr.keyser.pt;

import java.util.function.Function;
import java.util.function.Predicate;

public interface IntValue {

    public static final class Choice implements IntValue {

	private final IntValue ifFalse;

	private final IntValue ifTrue;

	private final Predicate<DeployedCard> predicate;

	private Choice(Predicate<DeployedCard> predicate, IntValue ifTrue, IntValue ifFalse) {
	    this.predicate = predicate;
	    this.ifTrue = ifTrue;
	    this.ifFalse = ifFalse;
	}

	@Override
	public int getValue(DeployedCard ctx) {
	    if (predicate.test(ctx))
		return ifTrue.getValue(ctx);
	    else
		return ifFalse.getValue(ctx);
	}
    }

    public static final class Constant implements IntValue {

	private final int value;

	private Constant(int value) {
	    this.value = value;
	}

	@Override
	public int getValue(DeployedCard ctx) {
	    return value;
	}
    }

    public static IntValue constant(int value) {
	return new Constant(value);
    }

    public static IntValue choice(Predicate<DeployedCard> predicate, IntValue ifTrue, IntValue ifFalse) {
	return new Choice(predicate, ifTrue, ifFalse);
    }

    public static IntValue card(Function<DeployedCard, Integer> func) {
	return c -> func.apply(c);
    }

    public static IntValue player(Function<PlayerBoard, Integer> func) {
	return c -> func.apply(c.getPlayer());
    }

    public static final IntValue ZERO = constant(0);

    public static final IntValue ONE = constant(1);

    public static final IntValue AGE = card(DeployedCard::getAgeToken);

    public static final IntValue VICTORIOUS_WAR = player(PlayerBoard::getVictoriousWar);

    public static final IntValue GOLD_GAIN = player(PlayerBoard::getGoldGain);

    public static final IntValue ALL_AGE_TOKEN = player(p -> p.units().mapToInt(DeployedCard::getAgeToken).sum());

    public static final IntValue DYING_AGE_TOKEN = player(p -> p.dyings().mapToInt(DeployedCard::getAgeToken).sum());

    public static final IntValue LEVEL2_BUILDING = player(p -> (int) p.buildings().filter(DeployedCard::isLevel2).count());

    public static final IntValue FOOD = player(PlayerBoard::getFood);

    public static final IntValue WOOD = player(PlayerBoard::getWood);

    public static final IntValue CRYSTAL = player(PlayerBoard::getCrystal);

    public int getValue(DeployedCard ctx);

    public default IntValue plus(IntValue value) {
	return ctx -> getValue(ctx) + value.getValue(ctx);
    }

    public default IntValue mult(IntValue value) {
	return ctx -> getValue(ctx) * value.getValue(ctx);
    }
}
