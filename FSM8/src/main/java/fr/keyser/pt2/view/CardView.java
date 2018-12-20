package fr.keyser.pt2.view;

import fr.keyser.pt.CardPosition;

public class CardView {

    private String name;

    private int age;

    private int level;

    private int combat;

    private CardPosition position;

    public int getAge() {
        return age;
    }

    public int getCombat() {
        return combat;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public CardPosition getPosition() {
        return position;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCombat(int combat) {
        this.combat = combat;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(CardPosition position) {
        this.position = position;
    }

  
}
