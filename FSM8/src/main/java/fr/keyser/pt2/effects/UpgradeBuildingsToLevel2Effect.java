package fr.keyser.pt2.effects;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.pt2.Card;
import fr.keyser.pt2.EffectLog;
import fr.keyser.pt2.Slot;

public class UpgradeBuildingsToLevel2Effect implements SelfEffect {

    public static final UpgradeBuildingsToLevel2Effect INSTANCE = new UpgradeBuildingsToLevel2Effect();

    private UpgradeBuildingsToLevel2Effect() {

    }

    @Override
    public List<EffectLog> apply(Slot source) {
	Card card = source.getCard().get();
	Stream<EffectLog> e = source.getBoard().getBuildings()
	        .filter(b -> b.getBuildLevel().getValue() == 1)
	        .map(b -> {
	            b.setBuildingLevel(2);
	            return EffectLog.upgrade(card, b);
	        });
	return e.collect(Collectors.toList());
    }
}
