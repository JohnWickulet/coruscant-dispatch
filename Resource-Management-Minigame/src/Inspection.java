import java.util.List;

public class Inspection extends Event {
    public Inspection(Location location) {
        super("Unannounced Inspection","We have planned an unannounced inspection in " + location.name + ". Send some troopers on it."
                ,location,15,60,3,2,
                2,0,4,0,3);
    }

    // Called when player deploys soldiers

    @Override
    public void onSuccess(Game game,List<Soldier> deployed) {
        game.addCurrency(25);
        game.gainPatience(5);
        game.increaseSuccess();
        for (Soldier s : deployed) {
            s.exp += 25;
        }
    }
    @Override
    public void onFailure(Game game,List<Soldier> deployed) {
        game.losePatience(10);
    }
}
