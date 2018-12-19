package fr.keyser.pt2;

import java.util.List;
import java.util.Map;

import fr.keyser.pt.CardPosition;

public class LocalPlayer {

    private LocalBoard localBoard;

    private int gold;

    private int legend;

    private List<Card> hand;

    private Map<CardPosition, CardMemento> atStartOfTurn;
}
