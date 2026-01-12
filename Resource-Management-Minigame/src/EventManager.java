import java.util.Random;

public class EventManager {

    private final Game game;
    private final GalaxyMapPanel map;
    private final double spawnInterval; // seconds between spawns
    private double spawnTimer;          // countdown until next spawn
    private final Random rng;

    public EventManager(Game game, GalaxyMapPanel map, double spawnIntervalSeconds) {
        this.game = game;
        this.map = map;
        this.spawnInterval = spawnIntervalSeconds;
        this.spawnTimer = spawnIntervalSeconds;
        this.rng = new Random();
    }

    public void tick(double dt) {
        spawnTimer -= dt;
        if (spawnTimer <= 0) {
            spawnTimer = spawnInterval;
            Location loc = LocationFactory.getRandomLocation(game.getSuccess());
            // Spawn a new event
            Event newEvent = generateProgressiveEvent(loc); // or random event
            game.addEvent(newEvent);

            EventCard card = new EventCard(newEvent);
            map.addEvent(card);
        }
    }
    private Event generateProgressiveEvent(Location loc) {
        int s = game.getSuccess();
        int choice = rng.nextInt(100);


        if (s < 5) {
            return new Inspection(loc);
        }

        else if (s < 15) {
            if (choice < 60) return new Inspection(loc);
            else return new Assassination(loc);
        }
        // Late-game: High-stakes events
        else {
            if (choice < 30) return new Inspection(loc);
            if (choice < 70) return new Assassination(loc);
            return new RebelSabotage(loc); // Hardest event
        }
    }
}
