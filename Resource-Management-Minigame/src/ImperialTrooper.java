public class ImperialTrooper extends Soldier{
    public ImperialTrooper(String name){
        super(name,1,1,1,1,1,10,"/portraits/mud.png");
        this.troopClass=TroopClass.IMPERIALTROOPER;
    }
    public ImperialTrooper(Soldier base){
        super(base);
        this.troopClass=TroopClass.IMPERIALTROOPER;
        this.rest_time_remaining=base.rest_time_remaining;
    }
    @Override
    public boolean canPromote(TroopClass target) {
        return UpgradeRegistry.getNextTroopClass(troopClass, level) == target;
    }
    @Override
    public Soldier promoteTo(TroopClass target){
        return new StormTrooper(this);
    }
}
