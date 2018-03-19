package fr.keyser.pt;

public interface BoardVisitor extends PlayerBoardVisitor {
    public void turn(int turn, String phase);
}
