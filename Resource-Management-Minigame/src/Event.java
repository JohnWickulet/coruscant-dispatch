import java.util.List;
import java.util.Random;

public abstract class Event {

    public enum State { AVAILABLE, IN_PROGRESS, RESOLVED, EXPIRED }

    public String name, description;
    public Location location;
    public double duration, timeDeploy, timeLeft, linger = 1;
    public boolean remove = false;
    public int maxDeploy, checks_required;
    public int reqStrength, reqMobility, reqIntelligence, reqStealth, reqCharisma;

    protected List<Soldier> deployed;
    protected State state = State.AVAILABLE;
    private static final Random rand = new Random();

    public Event(String name, String description, Location location,
                 double duration, double timeDeploy, int maxDeploy, int checks_required,
                 int STRENGTH, int MOBILITY, int INTELLIGENCE, int STEALTH, int CHARISMA) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.duration = duration;
        this.timeDeploy = timeDeploy;
        this.timeLeft = timeDeploy;
        this.maxDeploy = maxDeploy;
        this.checks_required = checks_required;
        this.reqStrength = (int)(STRENGTH*location.getDiff());
        this.reqMobility = (int)(MOBILITY*location.getDiff());
        this.reqStealth = (int)(STEALTH*location.getDiff());
        this.reqCharisma = (int)(CHARISMA*location.getDiff());
        this.reqIntelligence = (int)(INTELLIGENCE*location.getDiff());
    }

    public void deploy(List<Soldier> soldiers) {
        this.deployed = soldiers;
        this.state = State.IN_PROGRESS;
        this.timeLeft = this.duration;
        for (Soldier s : deployed) s.startMission();
    }

    public void tick(double dt) {
        // Countdown
        if (state == State.AVAILABLE || state == State.IN_PROGRESS) {
            timeLeft -= dt;
            if (timeLeft <= 0) {
                timeLeft = 0;
                if (state == State.AVAILABLE) state = State.EXPIRED;
                else state = State.RESOLVED;
            }
        }

        // Linger for expired events
        if (state == State.EXPIRED) {
            linger -= dt;
            if (linger <= 0) remove = true;
        }
    }

    protected void resolve(Game game) {
        if (state != State.RESOLVED) return;

        int check_success = 0;

        if (reqStrength > 0 && statSum(Stat.STRENGTH) >= rand.nextInt(reqStrength + 1)) check_success++;
        if (reqCharisma > 0 && statSum(Stat.CHARISMA) >= rand.nextInt(reqCharisma + 1)) check_success++;
        if (reqIntelligence > 0 && statSum(Stat.INTELLIGENCE) >= rand.nextInt(reqIntelligence + 1)) check_success++;
        if (reqMobility > 0 && statSum(Stat.MOBILITY) >= rand.nextInt(reqMobility + 1)) check_success++;
        if (reqStealth > 0 && statSum(Stat.STEALTH) >= rand.nextInt(reqStealth + 1)) check_success++;

        boolean success = check_success >= checks_required;

        if (success) onSuccess(game,deployed);
        else onFailure(game,deployed);

        for (Soldier s : deployed) s.endMission();
        state = State.RESOLVED;
    }

    public int statSum(Stat stat) {
        int sum = 0;
        for (Soldier s : deployed) {
            switch (stat) {
                case STRENGTH -> sum += s.strength;
                case MOBILITY -> sum += s.mobility;
                case INTELLIGENCE -> sum += s.intelligence;
                case STEALTH -> sum += s.stealth;
                case CHARISMA -> sum += s.charisma;
            }
        }
        return sum;
    }

    public abstract void onSuccess(Game game,List<Soldier> deployed);
    public abstract void onFailure(Game game,List<Soldier> deployed);
}
