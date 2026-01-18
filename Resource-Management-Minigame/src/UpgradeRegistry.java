import java.util.ArrayList;
import java.util.List;

public class UpgradeRegistry {

    private static final List<TroopUpgrade> upgrades = new ArrayList<>();

    static {
        upgrades.add(new TroopUpgrade(TroopClass.Imperial_Trooper, TroopClass.Storm_Trooper, 3));
        upgrades.add(new TroopUpgrade(TroopClass.Storm_Trooper, TroopClass.Scout_Trooper, 5));
        upgrades.add(new TroopUpgrade(TroopClass.Storm_Trooper,TroopClass.Purge_Trooper,5));
    }

    public static List<TroopClass> getAvailablePromotions(TroopClass current, int level) {
        List<TroopClass> options = new ArrayList<>();
        for (TroopUpgrade u : upgrades) {
            if (u.current == current && level >= u.requiredLevel && u.next != current) {
                options.add(u.next);
            }
        }
        return options;
    }
}
