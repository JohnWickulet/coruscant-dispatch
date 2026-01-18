public enum ItemType {
    MEDKIT("Bacta Spray", 100,"Get a hurt troop back in top shape"),
    INSTANT_AVAILABLE("Adrenaline Shot", 100,"Instantly finish a troop's resting time"),
    REVIVE("Bacta Tank", 200,"Put a downed troop back into action");

    public final String name;
    public final int price;
    public String description;

    ItemType(String name, int price,String description) {
        this.name = name;
        this.price = price;
        this.description=description;
    }

    public boolean use(Soldier soldier) {
        return switch (this) {
            case MEDKIT -> soldier.heal();
            case INSTANT_AVAILABLE -> soldier.makeAvailableInstantly();
            case REVIVE -> soldier.revive();
        };
    }
    public String displayName(){
        return name;
    }
}
