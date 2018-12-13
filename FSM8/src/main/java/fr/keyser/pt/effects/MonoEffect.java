package fr.keyser.pt.effects;

import java.util.Optional;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.PlayerBoard;

public interface MonoEffect extends TargetableEffect {

    public void apply(DeployedCard source, DeployedCard target);

    @Override
    public default void apply(DeployedCard source, ChoosenTargets targets) {
	PlayerBoard player = source.getPlayer();
	Optional<DeployedCard> cardAt = player.cardAt(targets.getDefault());
	cardAt.ifPresent(at -> apply(source, at));
    }
}
