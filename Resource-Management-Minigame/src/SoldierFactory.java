public class SoldierFactory {

    public static Soldier create(String troopClass, String name) {
        return switch (TroopClass.valueOf(troopClass)) {
            case Storm_Trooper -> new StormTrooper(name);
            case Imperial_Trooper -> new ImperialTrooper(name);
            case Scout_Trooper -> new ScoutTrooper(name);
            case Purge_Trooper -> new PurgeTrooper(name);
        };
    }
}
