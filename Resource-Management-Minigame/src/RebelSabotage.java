import java.util.List;

public class RebelSabotage extends Event {
    public RebelSabotage(Location location,int x,int y) {
        super("Rebel Sabotage","We have acquired insider information about a potential rebel sabotage on " + location.name + ". Send some troops to ensure the rebels rethink their plan."
                ,location,x,y,10,20,4,3,
                6,4,3,2,0);
        statPref.add("Bring as much muscle as possible and do it fast.");
        statPref.add("Make sure to secure all entry points");
    }



    @Override
    public void onSuccess(Game game,List<Soldier> deployed) {
        game.addCurrency(100);
        game.gainPatience(10);
        game.increaseSuccess();
        for (Soldier s : deployed) {
            s.exp += 50;
        }
    }
    @Override
    public void onFailure(Game game,List<Soldier> deployed) {
        game.losePatience(20);
        for (Soldier s : deployed) {
            s.damage();
            s.damage();
        }
    }
}
