import java.util.List;

public class Assassination extends Event {
    public Assassination(Location location,int x,int y) {
        super("Target Assassination","Imperial intelligence has found a traitor located in the " + location.name + ". Send someone to execute them as a reminder that the Empire sees all."
                ,location,x,y,10,20,1,3,
                2,3,2,3,0);
        statPref.add("Send someone to who can eliminate the target swiftly while also remaining unseen");
        statPref.add("Make sure they can handle tha target in case it resists");
    }

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
