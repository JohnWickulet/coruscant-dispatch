import java.util.EnumMap;
import java.util.Map;

public class Inventory {

    private final Map<ItemType, Integer> items =
            new EnumMap<>(ItemType.class);

    public Inventory() {
        for (ItemType t : ItemType.values()) {
            items.put(t, 0);
        }
    }

    public int get(ItemType type) {
        return items.get(type);
    }

    public void add(ItemType type) {
        items.put(type, items.get(type) + 1);
    }

    public boolean consume(ItemType type) {
        if (items.get(type) <= 0) return false;
        items.put(type, items.get(type) - 1);
        return true;
    }

    public Map<ItemType, Integer> getAll() {
        return items;
    }
}
