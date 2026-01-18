# **OOP Project** written in Java based on the Star Wars series and the Dispatch Video Game in which you must maintain order in the capital of the Galactic Empire, Coruscant.

+ The project itself is a video game in which you must send troopers to take care of various events on the map.
+ The game features an inventory system, a shop system, an upgrade system and a promotion mechanic.
+ The main goal is to manage to survive for as long as possible as losing too many events will lead to a game over.

## Java classes utilized:
### Game:
#### Variables:
    public Squad squad;
    public Inventory inventory;
    public Shop shop;
    private int currency;
    private int patience;
    private int success_count;
    private final Random random;
    private final List<GameStateListener> listeners;
    private final List<Event> activeEvents;
    private double spawnInterval;
#### Methods 
    public void initialize(): inserts 3 new Imperial Troopers (used only if starting a new game)
    private Soldier generateNewTrooper(): generates the Imperial Troopers in question with a random name
    public void gainSupport(): based on the amount of successful events, will add additional troops to use but also increase the speed at which events are added
    public void tick(): ticks the timers for active events and the troops
    Add/Get/Use/Gain/Lose: self explanitory, used to modify the private variables

### Soldier:
#### Variables:
    public enum State {AVAILABLE, DEPLOYED, RESTING,DOWNED}
    public String name;
    public int strength;
    public int mobility;
    public int intelligence;
    public int stealth;
    public int charisma;
    public State state=State.AVAILABLE;
    public int health;
    public int maxHealth;
    public int exp;
    public int level;
    public double rest_time;
    public double rest_time_remaining;
    public ImageIcon portrait;
    public TroopClass troopClass;
    private int databaseId;
#### Methods
    








