public abstract class Resource {

    public String name;
    public String description;
    public int quantity = 1;

    public abstract void use(Soldier s);
}
