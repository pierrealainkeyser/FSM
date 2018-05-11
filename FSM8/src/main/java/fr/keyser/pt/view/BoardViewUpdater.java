package fr.keyser.pt.view;

import fr.keyser.bus.Bus;
import fr.keyser.bus.SynchronousBus;
import fr.keyser.pt.event.CardAgeChanged;
import fr.keyser.pt.event.CardBuildingLevelChanged;
import fr.keyser.pt.event.CardDeploymentChanged;
import fr.keyser.pt.event.CardRefreshInfo;
import fr.keyser.pt.event.DeployedCardEvent;
import fr.keyser.pt.event.PlayerBuildPlanEvent;
import fr.keyser.pt.event.PlayerDoDeployEvent;
import fr.keyser.pt.event.PlayerDoDraftEvent;
import fr.keyser.pt.event.PlayerEvent;
import fr.keyser.pt.event.PlayerGoldChanged;
import fr.keyser.pt.event.PlayerIdleEvent;
import fr.keyser.pt.event.PlayerLegendChanged;
import fr.keyser.pt.fsm.PlayerBoardAcces;

class BoardViewUpdater implements Bus {

    private final SynchronousBus bus = new SynchronousBus();

    private final BoardView view;

    private final PlayerBoardAcces player;

    private boolean globalDone = false;

    public BoardViewUpdater(PlayerBoardAcces player) {
	this.player = player;
	this.view = new BoardView(this.player.getUUID());
	initBus();
    }

    private void initBus() {
	bus.listenTo(PlayerIdleEvent.class, this::playerIdleChanged);
	bus.listenTo(PlayerBuildPlanEvent.class, this::playerBuildPlanChanged);
	bus.listenTo(PlayerDoDeployEvent.class, this::playerDoDeployChanged);
	bus.listenTo(PlayerDoDraftEvent.class, this::playerDoDraftChanged);

	bus.listenTo(PlayerGoldChanged.class, this::playerGoldChanged);
	bus.listenTo(PlayerLegendChanged.class, this::playerLegendChanged);
	bus.listenTo(CardAgeChanged.class, this::cardAgeChanged);
	bus.listenTo(CardDeploymentChanged.class, this::cardDeployementChanged);
	bus.listenTo(CardBuildingLevelChanged.class, this::cardBuildingLevelChanged);
	bus.listenTo(CardRefreshInfo.class, this::cardRefreshInfo);
    }

    private void playerBuildPlanChanged(PlayerBuildPlanEvent evt) {
	if (samePlayer(evt))
	    view.setBuildPlan(evt.getBuildPlan());
    }

    private void playerDoDeployChanged(PlayerDoDeployEvent evt) {
	if (samePlayer(evt))
	    view.setToDeploy(evt.getToDeploy());
    }

    private void playerDoDraftChanged(PlayerDoDraftEvent evt) {
	if (samePlayer(evt))
	    view.setToDraft(evt.getToDraft());
    }

    private void cardRefreshInfo(CardRefreshInfo cari) {

	CardView card = card(cari);

	if (cari.isInitialDeploy())
	    registerName(cari, card);

	card.setCombat(cari.getCombat());
	card.setMayCombat(cari.isMayCombat());
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
	    if (samePlayer(cdc))
		registerName(cdc, card);

	    card.setHidden(true);
	} else
	    card.setRemoved(true);
    }

    private boolean samePlayer(PlayerEvent evt) {
	return evt.getPlayer().equals(player.getUUID());
    }

    private void registerName(DeployedCardEvent dce, CardView card) {
	card.setName(dce.getCardName());
    }

    private CardView card(DeployedCardEvent dce) {
	return player(dce).getCard(dce.getPosition());
    }

    private void playerIdleChanged(PlayerIdleEvent pie) {
	player(pie).setIdle(pie.isIdle());
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
	if (!globalDone) {
	    syncGlobal();
	    globalDone = true;
	}

	return view;
    }

    private void syncGlobal() {
	view.setAppeareance(player.getAppearance());

	// on change only ??
	view.setInputActions(player.getInputActions());

	view.setTurn(player.getTurn());
	view.setPhase(player.getPhase());
    }
}
