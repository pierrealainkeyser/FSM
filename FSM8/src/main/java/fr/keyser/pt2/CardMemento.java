package fr.keyser.pt2;

import fr.keyser.pt.CardPosition;

public class CardMemento {

    private String name;

    private int age;

    private int buildLevel;

    private int deployedTurn;

    private boolean simpleDyingProtection;

    private CardPosition position;

    public int getAge() {
	return age;
    }

    public void setAge(int age) {
	this.age = age;
    }

    public int getBuildLevel() {
	return buildLevel;
    }

    public void setBuildLevel(int buildLevel) {
	this.buildLevel = buildLevel;
    }

    public int getDeployedTurn() {
	return deployedTurn;
    }

    public void setDeployedTurn(int deployedTurn) {
	this.deployedTurn = deployedTurn;
    }

    public boolean isSimpleDyingProtection() {
	return simpleDyingProtection;
    }

    public void setSimpleDyingProtection(boolean simpleDyingProtection) {
	this.simpleDyingProtection = simpleDyingProtection;
    }

    public CardPosition getPosition() {
	return position;
    }

    public void setPosition(CardPosition position) {
	this.position = position;
    }

    @Override
    public String toString() {
	return "CardMemento [age=" + age + ", buildLevel=" + buildLevel + ", deployedTurn=" + deployedTurn + ", simpleDyingProtection="
	        + simpleDyingProtection + ", position=" + position + "]";
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

}
