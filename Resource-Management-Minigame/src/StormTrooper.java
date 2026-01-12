import javax.swing.*;

public class StormTrooper extends ImperialTrooper {

    public StormTrooper(String name) {
        super(name);
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
        this.troopClass=TroopClass.STORMTROOPER;
        this.strength += 1;
        this.mobility += 1;
        this.intelligence += 1;
        this.charisma += 1;
        this.rest_time = 8.0;
        this.rest_time_remaining=base.rest_time_remaining;
        this.portrait = new ImageIcon(getClass().getResource("/portraits/stormtrooper.png"));
    }
    @Override
    public boolean canPromote(TroopClass target) {
        return UpgradeRegistry.getNextTroopClass(troopClass, level) == target;
    }
    @Override
    public Soldier promoteTo(TroopClass target){
        return new ScoutTrooper(this);
    }
}
