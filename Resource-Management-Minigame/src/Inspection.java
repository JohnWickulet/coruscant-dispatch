import java.util.List;

public class Inspection extends Event {
    public Inspection(Location location,int x,int y) {
        super("Unannounced Inspection","We have planned an unannounced inspection in the " + location.name + ". Send some troopers on it."
                ,location,x,y,15,60,3,2,
                2,0,3,0,3);
        statPref.add("Try to make them talk. Intimidate them if you must.");
        statPref.add("Check every corner for any issues");
    }
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
        for (Soldier s : deployed) {
            s.damage();
        }
    }
}
