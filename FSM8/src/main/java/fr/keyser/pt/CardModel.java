package fr.keyser.pt;

public final class CardModel {

    public class CardModelInfo {
	private CardModelInfo(int ageToken, BuildingLevel level, MetaCard meta) {
	    this.ageToken = ageToken;
	    this.level = level;
	    this.meta = meta;
	}

	private CardModelInfo(CardModelInfo other) {
	    this(other.ageToken, other.level, other.meta);
	}

	private int ageToken;

	private BuildingLevel level;

	private MetaCard meta;

	public int getAgeToken() {
	    return ageToken;
	}

	public void setAgeToken(int ageToken) {
	    this.ageToken = ageToken;
	}

	public BuildingLevel getLevel() {
	    return level;
	}

	public void setLevel(BuildingLevel level) {
	    this.level = level;
	}

	public MetaCard getMeta() {
	    return meta;
	}

	public void setMeta(MetaCard meta) {
	    this.meta = meta;
	}
    }

    private CardModelInfo info;

    private CardModelInfo previous;

    private int playedTurn;

    private boolean protection;

    public CardModel() {
	this.info = new CardModelInfo(0, null, null);
    }

    public CardModel(MetaCard meta, BuildingLevel level) {
	this.info = new CardModelInfo(0, level, meta);
    }

    public CardModel(MetaCard meta, int ageToken, int playedTurn) {
	this.info = new CardModelInfo(ageToken, null, meta);
	this.playedTurn = playedTurn;
    }

    public int getAgeToken() {
	return info.getAgeToken();
    }

    public BuildingLevel getLevel() {
	return info.getLevel();
    }

    public MetaCard getMeta() {
	return info.getMeta();
    }

    public int getPlayedTurn() {
	return playedTurn;
    }

    public void setAgeToken(int ageToken) {
	info.setAgeToken(ageToken);
    }

    public void setLevel(BuildingLevel level) {
	info.setLevel(level);
    }

    public void setMeta(MetaCard meta) {
	info.setMeta(meta);
    }

    public void setPlayedTurn(int playedTurn) {
	this.playedTurn = playedTurn;
    }

    public boolean isProtection() {
	return protection;
    }

    public void setProtection(boolean protection) {
	this.protection = protection;
    }

    public CardModelInfo getInfo() {
	return info;
    }

    public void updatePrevious() {
	previous = new CardModelInfo(info);
    }

    public CardModelInfo getPrevious() {
	return previous;
    }
}
