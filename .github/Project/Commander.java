package Project;

public class Commander {
    String name;
    String flavor;
    int level;
    double xp;
    boolean isDead;
    public Commander(String name,String flavor){
        this.name=name;
        this.flavor=flavor;
        this.level=1;
        this.xp=0;
    }
    public void GetStatus(){
        if(!isDead) {
            System.out.println("Name: " + name);
            System.out.println("Level: " + level);
            System.out.println(xp + "/300 Exp");
            System.out.println(flavor + "\n");
        }
        else System.out.println("The commander is dead. The rest of the squad is soon to follow...");
    }
    public void LevelUp(){
        if(xp>=300){
            xp-=300;
            level++;
            System.out.println(name + " leveled up!");
        }
        else System.out.println("Insufficient xp");
    }
}
