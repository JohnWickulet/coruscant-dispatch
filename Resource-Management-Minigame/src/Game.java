import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {

    public Squad squad;
    public Inventory inventory=new Inventory();
    public Shop shop=new Shop();
    private int currency = 100;
    private int patience=50;
    private int succes_count=0;

    private final List<Event> activeEvents;

    public Game() {
        squad = new Squad(10);
        activeEvents = new ArrayList<>();
    }

    public void initialize() {
        squad.addSoldier(new ImperialTrooper("TK-421"));
        squad.addSoldier(new ImperialTrooper("FN-2187"));
        squad.addSoldier(new ImperialTrooper("CT-9904"));
        squad.addSoldier(new ImperialTrooper("CT-555"));
    }

    // Unified tick
    public void tick(double dt) {
        // Tick events
        Iterator<Event> it = activeEvents.iterator();
        while (it.hasNext()) {
            Event e = it.next();
            e.tick(dt);
            if (e.remove) it.remove(); // Remove expired/resolved
        }

        // Tick soldiers
        for (Soldier s : squad.soldiers) {
            s.tick(dt);
        }
    }
    public void addEvent(Event event) {
        activeEvents.add(event);
    }

    public int getCurrency(){
        return currency;
    }

    public void addCurrency(int amount){
        currency+=amount;
    }

    public void useCurrency(int amount){
        currency-=amount;
    }

    public int getPatience(){
        return patience;
    }

    public void gainPatience(int amount){
        patience+=amount;
    }
    public void losePatience(int amount){
        patience-=amount;
    }
    public void increaseSuccess(){
        this.succes_count++;
    }
    public int getSuccess(){
        return succes_count;
    }

    public List<Event> getActiveEvents(){
        return activeEvents;
    }
}
