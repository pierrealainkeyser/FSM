package fr.keyser.evolutions;

public class PlayerAndStatus {

    private final Player player;

    private final PlayerStatus status;

    public PlayerAndStatus(Player player, PlayerStatus status) {
	this.player = player;
	this.status = status;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerStatus getStatus() {
        return status;
    }

}
