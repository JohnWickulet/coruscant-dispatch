import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Game {
    public Squad squad = new Squad(8);
    public Inventory inventory = new Inventory();
    public Shop shop = new Shop();
    private int currency = 100;
    private int patience = 50;
    private int success_count = 0;
    private final Random random = new Random();

    private final List<GameStateListener> listeners = new ArrayList<>();
    private final List<Event> activeEvents = new ArrayList<>();
    private double spawnInterval = 12.0;

    public void addGameStateListener(GameStateListener l) { listeners.add(l); }

    private void notifySquadChanged() {
        for (GameStateListener l : listeners) l.onSquadChanged();
    }

    public void initialize() {
        squad.addSoldier(generateNewTrooper());
        squad.addSoldier(generateNewTrooper());
        squad.addSoldier(generateNewTrooper());
    }

    private Soldier generateNewTrooper() {
        String newID = "TK-" + (1000 + random.nextInt(9000));
        return new ImperialTrooper(newID);
    }

    public void gainSupport() {
        int initialSize = squad.soldiers.size();
        switch (success_count) {
            case 5-> {
                squad.addSoldier(generateNewTrooper());
                spawnInterval = 11.0;
            }
            case 10-> {
                squad.addSoldier(generateNewTrooper());
                spawnInterval = 8.0;
            }
            case 15-> {
                squad.addSoldier(new StormTrooper(generateNewTrooper()));
                spawnInterval = 6.0;
            }
            case 20-> {
                squad.addSoldier(new StormTrooper(generateNewTrooper()));
                spawnInterval = 4.0;
            }
        }
        if (squad.soldiers.size() > initialSize) {
            notifySquadChanged();
        }
    }

    public double getSpawnInterval(){
        return spawnInterval;
    }

    public void tick(double dt) {
        Iterator<Event> it = activeEvents.iterator();
        while (it.hasNext()) {
            Event e = it.next();
            e.tick(dt);
            if (e.remove) it.remove();
        }
        for (Soldier s : squad.soldiers) {
            s.tick(dt);
        }
    }

    public void increaseSuccess() {
        this.success_count++;
        gainSupport();
    }

    public void addEvent(Event event) { activeEvents.add(event); }
    public int getCurrency() { return currency; }
    public void addCurrency(int amount) { currency += amount; }
    public void useCurrency(int amount) { currency -= amount; }
    public int getPatience() { return patience; }
    public void gainPatience(int amount) { patience += amount; }
    public void losePatience(int amount) { patience -= amount; }
    public int getSuccess() { return success_count; }
    public List<Event> getActiveEvents() { return activeEvents; }
}