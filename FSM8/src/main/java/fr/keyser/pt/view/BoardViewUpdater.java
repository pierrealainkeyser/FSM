package fr.keyser.pt.view;

import java.util.UUID;

import fr.keyser.bus.SynchronousBus;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.event.CardAgeChanged;
import fr.keyser.pt.event.CardAgeRefreshInfo;
import fr.keyser.pt.event.CardBuildingLevelChanged;
import fr.keyser.pt.event.CardDeploymentChanged;
import fr.keyser.pt.event.DeployedCardEvent;
import fr.keyser.pt.event.PlayerEvent;
import fr.keyser.pt.event.PlayerGoldChanged;
import fr.keyser.pt.event.PlayerLegendChanged;
import fr.keyser.pt.fsm.PlayerBoardFSM;

public class BoardViewUpdater {

    private final SynchronousBus bus = new SynchronousBus();

    private final UUID local;

    private final BoardView view;

    public BoardViewUpdater(UUID local, BoardView view) {
	this.local = local;
	this.view = view;
	initBus();
    }

    public void done(PlayerBoardFSM fsm) {
	view.setAppeareance(fsm.getAppearance());
	view.setInputActions(fsm.getInputActions());
    }

    private void initBus() {
	bus.listenTo(PlayerGoldChanged.class, this::playerGoldChanged);
	bus.listenTo(PlayerLegendChanged.class, this::playerLegendChanged);
	bus.listenTo(CardAgeChanged.class, this::cardAgeChanged);
	bus.listenTo(CardDeploymentChanged.class, this::cardDeployementChanged);
	bus.listenTo(CardBuildingLevelChanged.class, this::cardBuildingLevelChanged);
	bus.listenTo(CardAgeRefreshInfo.class, this::cardAgeRefreshInfo);
    }

    private void cardAgeRefreshInfo(CardAgeRefreshInfo cari) {
	CardView card = card(cari);
	DeployedCard dc = cari.getCard();
	card.setCombat(dc.getCombat());
	card.setMayCombat(dc.isMayCombat());
    }

    private void cardBuildingLevelChanged(CardBuildingLevelChanged cblc) {
	card(cblc).setLevel(cblc.getLevel());
    }

    private void cardAgeChanged(CardAgeChanged cac) {
	card(cac).setAge(cac.getAge());
    }

    private void cardDeployementChanged(CardDeploymentChanged cdc) {
	CardView card = card(cdc);
	if (cdc.isDeployed())
	    registerNewDeployedCard(card, cdc.getCard());
	else
	    onRemoval(cdc, card);
    }

    private void onRemoval(CardDeploymentChanged cdc, CardView card) {
	DeployedCard newCard = cdc.getNewCard();
	if (newCard == null)
	    card.setRemoved(true);
	else {
	    // swapp and remove
	    if (cdc.getPlayer().equals(local))
		registerNewDeployedCard(card, newCard);
	    else
		card.setHidden(true);
	}
    }

    private void registerNewDeployedCard(CardView card, DeployedCard dc) {
	card.setName(dc.getMeta().getName());
	card.setRemoved(null);
	card.setHidden(null);
    }

    private CardView card(DeployedCardEvent dce) {
	return player(dce).getCard(dce.getPosition());
    }

    private void playerLegendChanged(PlayerLegendChanged plc) {
	player(plc).setGold(plc.getLegend());
    }

    private PlayerBoardView player(PlayerEvent plc) {
	return view.getPlayer(plc.getPlayer());
    }

    private void playerGoldChanged(PlayerGoldChanged pgc) {
	view.getPlayer(pgc.getPlayer()).setGold(pgc.getGold());
    }

}
