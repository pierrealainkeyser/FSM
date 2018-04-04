package fr.keyser.pt.fsm;

public class BuildCommand {

    private Integer index;

    public BuildCommand() {
    }

    public BuildCommand(Integer index) {
	this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

}
