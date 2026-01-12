import java.util.ArrayList;
import java.util.List;

public class Squad {

    public List<Soldier> soldiers;

    public Squad(int capacity) {
        soldiers = new ArrayList<>(capacity);
    }

    public void addSoldier(Soldier s) {
        soldiers.add(s);
    }

    public void replaceSoldier(Soldier old, Soldier upgraded) {
        int index = soldiers.indexOf(old);
        if (index != -1) {
            soldiers.set(index, upgraded);
        }
    }
}
