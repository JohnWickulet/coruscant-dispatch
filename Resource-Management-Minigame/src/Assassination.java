import java.util.List;

public class Assassination extends Event {
    public Assassination(Location location) {
        super("Target Assassination","Imperial intelligence has found a traitor in " + location.name + ". Send someone to execute them swiftly and unseen"
                ,location,10,20,1,3,
                2,3,2,3,0);
    }

    // Called when player deploys soldiers

    @Override
    public void onSuccess(Game game,List<Soldier> deployed) {
        game.addCurrency(50);
        game.gainPatience(10);
        game.increaseSuccess();
        for (Soldier s : deployed) {
            s.exp += 100;
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
