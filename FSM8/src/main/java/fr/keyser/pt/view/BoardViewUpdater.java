package fr.keyser.pt.view;

import fr.keyser.bus.Bus;
import fr.keyser.bus.SynchronousBus;
import fr.keyser.pt.DeployedCard;
import fr.keyser.pt.event.CardAgeChanged;
import fr.keyser.pt.event.CardBuildingLevelChanged;
import fr.keyser.pt.event.CardDeploymentChanged;
import fr.keyser.pt.event.CardRefreshInfo;
import fr.keyser.pt.event.DeployedCardEvent;
import fr.keyser.pt.event.PlayerEvent;
import fr.keyser.pt.event.PlayerGoldChanged;
import fr.keyser.pt.event.PlayerLegendChanged;
import fr.keyser.pt.fsm.PlayerBoardFSM;

class BoardViewUpdater implements Bus {

    private final SynchronousBus bus = new SynchronousBus();

    private final BoardView view;

    private final PlayerBoardFSM fsm;

    public BoardViewUpdater(PlayerBoardFSM fsm) {
	this.fsm = fsm;
	this.view = new BoardView(this.fsm.getUuid());
	initBus();
    }

    public void done() {
	view.setAppeareance(fsm.getAppearance());
	view.setInputActions(fsm.getInputActions());
    }

    private void initBus() {
	bus.listenTo(PlayerGoldChanged.class, this::playerGoldChanged);
	bus.listenTo(PlayerLegendChanged.class, this::playerLegendChanged);
	bus.listenTo(CardAgeChanged.class, this::cardAgeChanged);
	bus.listenTo(CardDeploymentChanged.class, this::cardDeployementChanged);
	bus.listenTo(CardBuildingLevelChanged.class, this::cardBuildingLevelChanged);
	bus.listenTo(CardRefreshInfo.class, this::cardRefreshInfo);
    }

    private void cardRefreshInfo(CardRefreshInfo cari) {
	DeployedCard dc = cari.getCard();

	CardView card = card(cari);

	if (dc.isInitialDeploy())
	    registerName(cari, card);

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
	if (cdc.isDeployed()) {
	    boolean samePlayer = cdc.getPlayer().equals(fsm.getUuid());
	    if (samePlayer)
		registerName(cdc, card);

	    card.setHidden(true);
	} else
	    card.setRemoved(true);
    }

    private void registerName(DeployedCardEvent dce, CardView card) {
	card.setName(dce.getCard().getMeta().getName());
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

    @Override
    public void forward(Object event) {
	bus.forward(event);
    }

    public BoardView getView() {
	return view;
    }
}
