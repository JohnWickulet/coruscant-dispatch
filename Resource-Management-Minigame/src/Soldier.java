import javax.swing.*;

public abstract class Soldier {

    public enum State {AVAILABLE, DEPLOYED, RESTING,DOWNED}

    public String name;

    public int strength;
    public int mobility;
    public int intelligence;
    public int stealth;
    public int charisma;
    public State state=State.AVAILABLE;
    public int health;
    public int maxHealth;
    public int exp;
    public int level;
    public double rest_time;
    public double rest_time_remaining;
    public ImageIcon portrait;
    public TroopClass troopClass;

    public Soldier(
            String name,
            int strength,
            int mobility,
            int intelligence,
            int stealth,
            int charisma,
            double rest_time,
            String imagePath
    ) {
        this.name = name;
        this.strength = clamp(strength);
        this.mobility = clamp(mobility);
        this.intelligence = clamp(intelligence);
        this.stealth = clamp(stealth);
        this.charisma = clamp(charisma);
        this.rest_time = rest_time;
        this.maxHealth = 2;
        this.health = maxHealth;
        this.exp = 0;
        this.level = 1;
        this.portrait = new ImageIcon(getClass().getResource(imagePath));
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(10, v));
    }

    public int getExpToLevel() {
        return 10 + level * 5;
    }

    public void increaseStat(String stat) {
        switch (stat) {
            case "Strength":
                strength++;
                break;
            case "Mobility":
                mobility++;
                break;
            case "Intelligence":
                intelligence++;
                break;
            case "Stealth":
                stealth++;
                break;
            case "Charisma":
                charisma++;
                break;
        }
        level++;
    }
    public boolean isAvailable(){
        return state==State.AVAILABLE;
    }
    public void startMission() {
        state = State.DEPLOYED;
        rest_time_remaining = 0;
    }
    public void endMission() {
        if(state==State.DOWNED) return;
        state = State.RESTING;
        rest_time_remaining = rest_time;
    }
    public void tick(double dt){
        if(state == State.RESTING){
            rest_time_remaining-=dt;
            if(rest_time_remaining<=0){
                state=State.AVAILABLE;
                rest_time_remaining=0;
            }
        }
    }
    public boolean isDead() {
        return health <= 0;
    }
    public void revive() {
        health = 1;
        state = State.AVAILABLE;
    }

    public void makeAvailableInstantly() {
        state = State.AVAILABLE;
        rest_time_remaining = 0;
    }

    public void heal() {
        if(health==1) health++;
    }
    public void damage(){
        health--;
        if(health<=0) state=State.DOWNED;
    }
    public Soldier(Soldier base){
        this.name=base.name;
        this.exp=base.exp;
        this.level=base.level;
        this.health=base.health;
        this.maxHealth=base.maxHealth;
        this.strength=base.strength;
        this.intelligence=base.intelligence;
        this.charisma=base.charisma;
        this.mobility=base.mobility;
        this.stealth=base.stealth;
    }

    public abstract boolean canPromote(TroopClass target);
    public abstract Soldier promoteTo(TroopClass target);

    private int databaseId;

    public void setDatabaseId(int id) {
        this.databaseId = id;
    }

    public int getDatabaseId() {
        return databaseId;
    }

}
