
public abstract class Soldier {
    String name;
    int level;
    int health;
    int intelligence;
    int strength;
    int mobility;
    double xp;
    boolean isDead;
    boolean isExploring;
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
            System.out.println(name + " leveled up!");
        }
        else System.out.println("Insufficient xp!");
    }
    abstract void ability(Squad squad);
    public void GetStatus(){
        if(!isDead) {
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
}
