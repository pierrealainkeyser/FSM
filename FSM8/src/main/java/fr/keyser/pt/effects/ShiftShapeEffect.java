package fr.keyser.pt.effects;

import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.MetaCard;
import fr.keyser.pt.PlayerBoard;
import fr.keyser.pt.SpecialEffect;

public class ShiftShapeEffect implements SpecialEffect {

    public static final SpecialEffect INSTANCE = new ShiftShapeEffect();

    private ShiftShapeEffect() {

    }

    @Override
    public void apply(DeployedCard card) {

	PlayerBoard player = card.getPlayer();
	MetaCard to = player.getBoard().pickTopCard();

	player.redeploy(to, card.getPosition());
    }
}
