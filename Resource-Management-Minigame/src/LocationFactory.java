import java.util.ArrayList;
import java.util.List;

public class LocationFactory {
    public static Location getRandomLocation(int successCount) {
        List<Location> possible = new ArrayList<>();

        // Tier 1: Always available
        possible.add(new Location("Entertainment District", 0.8,100, 200));
        possible.add(new Location("Commerce District", 1.0,800,200));

        // Tier 2: Unlocks at 8 successes
        if (successCount >= 8) {
            possible.add(new Location("Senate District", 1.2,400,400));
            possible.add(new Location("Industrial Sector", 1.5,200,800));
        }

        // Tier 3: Unlocks at 15 successes
        if (successCount >= 15) {
            possible.add(new Location("Senate Building", 2.0,200,500));
            possible.add(new Location("Imperial Palace", 2.5,800,800));
        }

        java.util.Collections.shuffle(possible);
        return possible.get(0);
    }
}