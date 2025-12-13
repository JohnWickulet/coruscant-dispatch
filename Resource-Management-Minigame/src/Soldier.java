
public abstract class Soldier {
    String name;
    int level;
    int health;
    int intelligence;
    int strength;
    int mobility;
    double xp;
    String flavor;
    public Soldier(String name,int intelligence,int strength,int mobility,String flavor){
        this.name=name;
        this.health=2;
        this.intelligence=intelligence;
        this.strength=strength;
        this.mobility=mobility;
        this.level=1;
        this.xp=0;
        this.flavor=flavor;
    }
    public abstract void IncreaseStats();
    public void LevelUp(){
        if(xp>=100){
            xp-=100;
            level++;
            IncreaseStats();
            System.out.println(name + " leveled up!\n");
        }
    }
    abstract void ability(Soldier soldier);
    public void GetStatus(){
        if(isAlive()) {
            System.out.println("Name: " + name);
            System.out.println("Level: " + level);
            if(health==2) System.out.println("This soldier is healthy");
            if(health==1) System.out.println("This soldier is hurt");
            System.out.println(xp + "/100 Exp");
            System.out.println(flavor);
            System.out.println("Stats: ");
            System.out.println("Intelligence: " + intelligence);
            System.out.println("Strength: " + strength);
            System.out.println("Mobility: " + mobility +"\n");
        }
        else System.out.println("This unit is dead.\n");
    }
    public boolean isAlive(){
        return health>0;
    }
    public void heal(){
        if(health<2) health++;
    }
    public void hurt(){
        health--;
    }
    public void IncreaseXp(double val){
        xp+=val;
        LevelUp();
    }
}
