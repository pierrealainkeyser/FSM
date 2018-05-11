package fr.keyser.pt.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.keyser.pt.BuildingConstruction;
import fr.keyser.pt.CardPosition;
import fr.keyser.pt.MetaCard;
import fr.keyser.pt.TargetedEffectDescription;

public class BoardView {

    private String appeareance;

    private List<BuildingConstruction> buildPlan;

    private Map<CardPosition, List<TargetedEffectDescription>> inputActions;

    private final UUID local;

    private String phase;

    private Map<UUID, PlayerBoardView> players;

    private List<MetaCard> toDeploy;

    private List<MetaCard> toDraft;

    private Integer turn;

    public BoardView(UUID local) {
	this.local = local;

    }

    public String getAppeareance() {
	return appeareance;
    }

    public List<BuildingConstruction> getBuildPlan() {
	return buildPlan;
    }

    public Map<CardPosition, List<TargetedEffectDescription>> getInputActions() {
	return inputActions;
    }

    public UUID getLocal() {
	return local;
    }

    public String getPhase() {
	return phase;
    }

    public PlayerBoardView getPlayer(UUID uuid) {
	if (players == null)
	    players = new HashMap<>();

	if (players.containsKey(uuid))
	    return players.get(uuid);
	else {
	    PlayerBoardView pbv = new PlayerBoardView();
	    players.put(uuid, pbv);
	    return pbv;
	}
    }

    public Map<UUID, PlayerBoardView> getPlayers() {
	return players;
    }

    public List<MetaCard> getToDeploy() {
	return toDeploy;
    }

    public List<MetaCard> getToDraft() {
	return toDraft;
    }

    public Integer getTurn() {
	return turn;
    }

    public void setAppeareance(String appeareance) {
	this.appeareance = appeareance;
    }

    public void setBuildPlan(List<BuildingConstruction> buildPlan) {
	this.buildPlan = buildPlan;
    }

    public void setInputActions(Map<CardPosition, List<TargetedEffectDescription>> inputActions) {
	this.inputActions = inputActions;
    }

    public void setPhase(String phase) {
	this.phase = phase;
    }

    public void setPlayers(Map<UUID, PlayerBoardView> players) {
	this.players = players;
    }

    public void setToDeploy(List<MetaCard> toDeploy) {
	this.toDeploy = toDeploy;
    }

    public void setToDraft(List<MetaCard> toDraft) {
	this.toDraft = toDraft;
    }

    public void setTurn(Integer turn) {
	this.turn = turn;
    }
}
