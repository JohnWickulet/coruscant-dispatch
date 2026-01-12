public class TroopUpgrade {
    public final TroopClass current;
    public final TroopClass next;
    public final int requiredLevel;

    public TroopUpgrade(TroopClass current, TroopClass next, int requiredLevel) {
        this.current = current;
        this.next = next;
        this.requiredLevel = requiredLevel;
    }
}
