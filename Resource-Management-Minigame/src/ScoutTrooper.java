import javax.swing.*;

public class ScoutTrooper extends StormTrooper{
    public ScoutTrooper(String name){
        super(name);
        this.mobility+=2;
        this.troopClass=TroopClass.Scout_Trooper;
        this.rest_time=6.0;
        this.portrait = new ImageIcon(getClass().getResource("/portraits/scouttrooper.png"));
    }
    public ScoutTrooper(Soldier base){
        super(base);
        this.setDatabaseId(base.getDatabaseId());
        this.state=base.state;
        this.mobility+=2;
        this.troopClass=TroopClass.Scout_Trooper;
        this.rest_time_remaining=base.rest_time_remaining;
        this.rest_time=6.0;
        this.portrait = new ImageIcon(getClass().getResource("/portraits/scouttrooper.png"));
    }
}
