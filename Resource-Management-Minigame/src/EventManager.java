import java.util.Random;

public class EventManager {

    private final Game game;
    private final GalaxyMapPanel map;
    private final double spawnInterval;
    private double spawnTimer;
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
            java.awt.Point p = loc.getRandomPointInside();
            Event newEvent = generateProgressiveEvent(loc,p.x,p.y);
            game.addEvent(newEvent);
            EventCard card = new EventCard(newEvent);
            map.addEvent(card);
            SoundManager.event();
        }
    }
    private Event generateProgressiveEvent(Location loc,int x,int y) {
        int s = game.getSuccess();
        int choice = rng.nextInt(100);
        if (s < 5) {
            if(choice<50) return new Inspection(loc,x,y);
            return new BodyGuard(loc,x,y);
        }
        else if (s < 15) {
            if (choice < 40) return new Inspection(loc,x,y);
            if(choice < 80) return new BodyGuard(loc,x,y);
            else return new Assassination(loc,x,y);
        }
        else if(s<20){
            if (choice < 30) return new Inspection(loc,x,y);
            if (choice < 60) return new BodyGuard(loc,x,y);
            if (choice < 80) return new Assassination(loc,x,y);
            return new RebelSabotage(loc,x,y);
        }
        else{
            if (choice < 30) return new BodyGuard(loc,x,y);
            if (choice < 60) return new Assassination(loc,x,y);
            return new RebelSabotage(loc,x,y);
        }
    }
}
