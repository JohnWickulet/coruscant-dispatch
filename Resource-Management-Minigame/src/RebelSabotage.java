import java.util.List;

public class RebelSabotage extends Event {
    public RebelSabotage(Location location) {
        super("Rebel Sabotage","We have acquired insider information about a potential rebel sabotage on " + location.name + ". Send some troops to ensure the rebels rethink their plan."
                ,location,10,20,2,2,
                6,4,3,0,0);
    }

    // Called when player deploys soldiers

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
        }
    }
}
