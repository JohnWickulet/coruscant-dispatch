# **OOP Project** written in Java based on the Star Wars series and the Dispatch Video Game in which you must maintain order in the capital of the Galactic Empire, Coruscant.

+ The project itself is a video game in which you must send troopers to take care of various events on the map.
+ The game features an inventory system, a shop system, an upgrade system and a promotion mechanic.
+ The main goal is to manage to survive for as long as possible as losing too many events will lead to a game over.

# Java classes utilized:
## For the Technical side:
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
    public void tick(double dt): ticks the timers for active events and the troops
    Add/Get/Use/Gain/Lose: self explanitory, used to modify the private variables

### (Abstract) Soldier:
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
    public Soldier(String name,int strength,int mobility,int intelligence,int stealth,int charisma,double rest_time,String imagePath): Constructor for Soldier Objects
    private int clamp(int v): used to make sure stats dont go over 10
    public int getExpToLevel(): returns the exp needed to level up the troop (increases with each level)
    public void increaseStat(String stat): increases one of the stats of the troop
    public boolean isAvailable(): checks if the troop is AVAILABLE
    public void startMission(): changes the state of the troop to DEPLOYED
    public void endMission(): changes the state of the troop to DOWNED (if they were hurt due to failing the event) or RESTING and starts the rest timer
    public void tick(double dt): ticks down the timer for a RESTING troop and changes their state to AVAILABLE if it reaches 0
    public boolean revive(): if the troop is DOWNED, heal them once and make them AVAILABLE
    public boolean makeAvailableInstantly(): if the troop is RESTING, finish their resting timer and make them AVAILABLE
    public boolean heal(): if the troop is hurt but not DOWNED, heal them once
    public void damage(): lower a troop's health and change their state to DOWNED if they have 0 health left
    public abstract Soldier promoteTo(TroopClass target): TroopClass is an Enumeration that contains the name for all subclasses of Soldier (don't ask why)

#### Subclasses
    ImperialTrooper: the base class for troopers, all stats are 1, can promote to a StormTrooper
    StormTrooper: has all the stats but stealth of ImperialTrooper increased by 1, can promote to a ScoutTrooper or PurgeTrooper
    ScoutTrooper: has increased mobility compared to a StormTrooper, end of the promotion "tree"
    PurgeTrooper: has increased strength and stealth compared to a StormTrooper but a longer rest time, end of the promotion "tree"

### (Abstract) Event
#### Variables:
    public enum State { AVAILABLE, IN_PROGRESS, RESOLVED, EXPIRED }
    public String name, description;
    public int x,y;
    public Location location;
    public double duration, timeDeploy, timeLeft, linger;
    public boolean remove = false;
    public int maxDeploy, checks_required;
    public int reqStrength, reqMobility, reqIntelligence, reqStealth, reqCharisma;
    protected List<String> statPref= new ArrayList<String>();
    protected List<Soldier> deployed;
    protected State state = State.AVAILABLE;
    private static final Random rand = new Random();
    protected boolean success = false;
#### Methods: 
    public Event(...): constructor for Event, the required stats' true value is based on the input from the constructor and the location's difficulty multiplier
    public void deploy(List<Soldiers>): saves the deployed troops for later and sets them to DEPLOYED, changes state to IN PROGRESS and starts the duration timer\
    public void tick(double dt): if the event is AVAILABLE/IN PROGRESS, ticks down the timer and if it reaches 0, sets the state to EXPIRED/RESOLVED, for EXPIRED events, starts the linger timer
    protected void resolve(Game game): rng based chance of succeding or failing an event based on the required stat value and the sum of the stats of the deployed troops
    public int statSum(Stat stat): does the sum of the soldiers' stats
    public abstract void onSuccess(Game game,List<Soldiers>): called when the resolve method passes the nr of checks required
    public abstract void onFailure(Game game,List<Soldiers>): called when the resolve method doesn't pass the nr of checks required

#### Subclasses (They all override the onSuccess and onFailure):
    Assassination,BodyGuard,Inspection,RebelSabotage

### Location
#### Variables
    public String name;
    private double difficulty_multiplier;
    public int centerX, centerY;
    public int radius;
#### Methods
    public Location(String name, double diff, int x, int y): standard constructor
    public Point getRandomPointInside(): generates a random point inside the circle of the location used for setting the coordinates of the event

### Inventory
#### Variables
    private final Map<ItemType, Integer> items: Map of the 3 item types (Bacta spray, Adrenaline Shot, Bacta Tank)
#### Methods
    public Inventory(): constructor for inventory, sets the 3 item types
    public void add(itemType type): add one to the quantity of an item type
    public boolean consume(itemType type): if it can, it will reduce the quantity of an item type

### Shop (it's a relic of the early code, that's why it's so empty here)
#### Method
    public boolean buy(ItemType item, Game game): if there is enough currency, will buy an item 

### TroopUpgrade
#### Variables
    public final TroopClass current;
    public final TroopClass next;
    public final int requiredLevel;
#### Method
    public TroopUpgrade(): standard constructor

### Upgrade registry
#### Variable
    private static final List<TroopUpgrade> upgrades; <- this is where the promotions for troops are actually stored
#### Method
    public static List<TroopClass> getAvailablePromotions(Soldier soldier): returns all possible promotions for a troop at its current level

## Database related stuff:
### DB_Connection (Establishes the connection to the database)
### saveGame
#### Methods
    public static void fullSave: saves all data for a profile
    public static void fullLoad: loads the data for a profile
    private static void saveGameStats: saves the game class' data
    private static int getOrInsertLocation: saves the location's data
    private static void saveSoldiers: saves all the troops' data
    private static void saveEvents: saves all the active events' data
    private static void saveSoldierLinks: saves the connection between the deployed soldiers and their events
    private void loadSoldiers: loads all the soldiers
    private void loadEvents: loads all the active events
    private void loadGameStats: loads the game class data
    private static int getOrCreateProfile: saves/generates a profile
    private int getProfileId: loads the data for a profile
    private void loadInventory: loads the inventory data
    public List<String> getAllProfileNames(): returns all profiles
    public void deleteProfile: deletes a profile and all data related to it
    private static void executeDelete: helper method for deleteProfile

###
    

    
    








