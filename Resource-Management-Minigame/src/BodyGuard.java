import java.util.List;

public class BodyGuard extends Event {
    public BodyGuard(Location location,int x,int y) {
        super("Bodyguard duty","A VIP has requested 2 troopers to act as bodyguards for his visit to the " + location.name
                ,location,x,y,20,30,2,3,
                3,1,2,0,2);
        statPref.add("Send someone capable of protecting the VIP");
        statPref.add("The VIP is a very talkative kind. Make sure to satisfy his need for conversation.");
    }

    @Override
    public void onSuccess(Game game,List<Soldier> deployed) {
        game.addCurrency(100);
        game.gainPatience(5);
        game.increaseSuccess();
        for (Soldier s : deployed) {
            s.exp += 50;
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
