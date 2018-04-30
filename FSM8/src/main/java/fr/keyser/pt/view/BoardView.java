package fr.keyser.pt.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.keyser.pt.CardPosition;
import fr.keyser.pt.TargetedEffectDescription;

public class BoardView {

    private String appeareance;

    private Map<CardPosition, List<TargetedEffectDescription>> inputActions;

    private String phase;

    private Map<UUID, PlayerBoardView> players;

    private Integer turn;

    public BoardView() {

    }

    public String getAppeareance() {
	return appeareance;
    }

    public Map<CardPosition, List<TargetedEffectDescription>> getInputActions() {
	return inputActions;
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
	    PlayerBoardView pbv = new PlayerBoardView(uuid);
	    players.put(uuid, pbv);
	    return pbv;
	}
    }

    public Map<UUID, PlayerBoardView> getPlayers() {
	return players;
    }

    public Integer getTurn() {
	return turn;
    }

    public void setAppeareance(String appeareance) {
	this.appeareance = appeareance;
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

    public void setTurn(Integer turn) {
	this.turn = turn;
    }
}
