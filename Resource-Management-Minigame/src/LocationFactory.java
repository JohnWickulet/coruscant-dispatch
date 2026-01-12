import java.util.ArrayList;
import java.util.List;

public class LocationFactory {

    // We can define tiers of locations
    public static Location getRandomLocation(int successCount) {
        List<Location> possible = new ArrayList<>();

        // Tier 1: Always available
        possible.add(new Location("Entertainment District", 0.8));
        possible.add(new Location("Commerce District", 1.0));

        // Tier 2: Unlocks at 5 successes
        if (successCount >= 8) {
            possible.add(new Location("Senate District", 1.2));
            possible.add(new Location("Industrial Sector", 1.5));
        }

        // Tier 3: Unlocks at 10 successes
        if (successCount >= 15) {
            possible.add(new Location("Senate Building", 2.0));
            possible.add(new Location("Imperial Palace", 2.5));
        }

        java.util.Collections.shuffle(possible);
        return possible.get(0);
    }
}