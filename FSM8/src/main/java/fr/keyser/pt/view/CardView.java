package fr.keyser.pt.view;

import fr.keyser.pt.BuildingLevel;
import fr.keyser.pt.CardPosition;

public class CardView {

    private final CardPosition position;

    private Integer combat;

    private Boolean mayCombat;

    private String name;

    private Boolean hidden;

    private Integer age;

    private BuildingLevel level;

    private Boolean removed;

    public CardView(CardPosition position) {
	this.position = position;
    }

    public Integer getCombat() {
	return combat;
    }

    public void setCombat(Integer combat) {
	this.combat = combat;
    }

    public Boolean getMayCombat() {
	return mayCombat;
    }

    public void setMayCombat(Boolean mayCombat) {
	this.mayCombat = mayCombat;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Boolean getHidden() {
	return hidden;
    }

    public void setHidden(Boolean hidden) {
	this.hidden = hidden;
    }

    public CardPosition getPosition() {
	return position;
    }

    public Integer getAge() {
	return age;
    }

    public void setAge(Integer age) {
	this.age = age;
    }

    public BuildingLevel getLevel() {
	return level;
    }

    public void setLevel(BuildingLevel level) {
	this.level = level;
    }

    public Boolean getRemoved() {
	return removed;
    }

    public void setRemoved(Boolean removed) {
	this.removed = removed;
    }
}
