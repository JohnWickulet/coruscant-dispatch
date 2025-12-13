public class Buff extends Resource{
    int intelligence;
    int strength;
    int mobility;
    public Buff(String name, String flavor, int intel,int str,int mob){
        super(name,flavor);
        this.intelligence=intel;
        this.strength=str;
        this.mobility=mob;
    }
    @Override
    public void Resource_info(){
        System.out.println("\nName: " + name);
        System.out.println(flavor);
        if(intelligence>0) System.out.println("Increase a soldier's intelligence by " + intelligence);
        if(strength>0) System.out.println("Increase a soldier's strength by " + strength);
        if(mobility>0) System.out.println("Increase a soldier's mobility by " + mobility);
        System.out.println();
    }
    @Override
    public void Use(Soldier soldier){
        if(intelligence>0) soldier.intelligence+=intelligence;
        if(strength>0) soldier.strength+=strength;
        if(mobility>0) soldier.mobility+=mobility;
    }
    @Override
    public boolean Valid_Use(Soldier soldier){
        return true;
    }
}
