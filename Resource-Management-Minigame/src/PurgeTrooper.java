import javax.swing.*;

public class PurgeTrooper extends StormTrooper{
    public PurgeTrooper(String name){
        super(name);
        this.strength++;
        this.stealth++;
        this.troopClass=TroopClass.Purge_Trooper;
        this.rest_time=12;
        this.portrait = new ImageIcon(getClass().getResource("/portraits/purgetrooper.png"));
    }
    public PurgeTrooper(Soldier base){
        super(base);
        this.setDatabaseId(base.getDatabaseId());
        this.state=base.state;
        this.strength++;
        this.stealth++;
        this.troopClass=TroopClass.Purge_Trooper;
        this.rest_time=12;
        this.rest_time_remaining=base.rest_time_remaining;
        this.portrait = new ImageIcon(getClass().getResource("/portraits/purgetrooper.png"));
    }
}
