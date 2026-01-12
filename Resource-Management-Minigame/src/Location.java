
public class Location {
    public String name;
    private double difficulty_multiplier;
    public Location(String name, double diff){
        this.name=name;
        this.difficulty_multiplier=diff;
    }
    public double getDiff(){
        return difficulty_multiplier;
    }
}
