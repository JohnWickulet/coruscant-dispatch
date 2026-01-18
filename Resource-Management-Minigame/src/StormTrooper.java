import javax.swing.*;

public class StormTrooper extends ImperialTrooper {

    public StormTrooper(String name) {
        super(name);
        this.troopClass=TroopClass.Storm_Trooper;
        this.strength += 1;
        this.mobility += 1;
        this.intelligence += 1;
        this.charisma += 1;
        this.rest_time = 8.0;
        this.portrait = new ImageIcon(getClass().getResource("/portraits/stormtrooper.png"));
    }
    public StormTrooper(Soldier base){
        super(base);
        this.setDatabaseId(base.getDatabaseId());
        this.state=base.state;
        this.troopClass=TroopClass.Storm_Trooper;
        this.strength += 1;
        this.mobility += 1;
        this.intelligence += 1;
        this.charisma += 1;
        this.rest_time = 8.0;
        this.rest_time_remaining=base.rest_time_remaining;
        this.portrait = new ImageIcon(getClass().getResource("/portraits/stormtrooper.png"));
    }
    @Override
    public Soldier promoteTo(TroopClass target) {
        return switch (target) {
            case Scout_Trooper -> new ScoutTrooper(this);
            case Purge_Trooper -> new PurgeTrooper(this);
            default -> this;
        };
    }
}
