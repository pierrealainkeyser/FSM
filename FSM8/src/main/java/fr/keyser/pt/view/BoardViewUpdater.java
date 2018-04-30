package fr.keyser.pt.view;

import fr.keyser.bus.Bus;
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
	    if (cdc.getPlayer().equals(fsm.getUuid()))
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

    @Override
    public void forward(Object event) {
	bus.forward(event);
    }

    public BoardView getView() {
	return view;
    }
}
