package fr.keyser.pt;

public class MetaCardBuilder {

    private int value;

    public MetaCard meta(Card card) {
	return new MetaCard(value++, card);
    }

}
