public class SoldierFactory {

    public static Soldier create(String troopClass, String name) {
        return switch (TroopClass.valueOf(troopClass)) {
            case STORMTROOPER -> new StormTrooper(name);
            case IMPERIALTROOPER -> new ImperialTrooper(name);
            case SCOUTTROOPER -> new ScoutTrooper(name);
        };
    }
}
