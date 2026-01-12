import java.util.ArrayList;
import java.util.List;

public class UpgradeRegistry {

    private static final List<TroopUpgrade> upgrades = new ArrayList<>();

    static {
        // Define your promotions
        upgrades.add(new TroopUpgrade(TroopClass.IMPERIALTROOPER, TroopClass.STORMTROOPER, 3));
        upgrades.add(new TroopUpgrade(TroopClass.STORMTROOPER, TroopClass.SCOUTTROOPER, 5));
    }

    public static TroopClass getNextTroopClass(TroopClass current, int level) {
        for (TroopUpgrade u : upgrades) {
            if (u.current == current && level >= u.requiredLevel) {
                return u.next;
            }
        }
        return null; // No promotion available
    }
}
