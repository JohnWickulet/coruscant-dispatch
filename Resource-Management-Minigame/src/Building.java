
import java.util.Random;

public class Building extends Location{
    public Building(String name,String flavor,int day){
        super(name,flavor,day);
    }
    @Override
    public Health Check_Result(Soldier soldier){
        Health hp_pack=new Health("Health Kit","An old unused health kit, might be useful",1);
        Random r=new Random();
        int dice_strength=r.nextInt(2*day);
        int dice_mobility=r.nextInt(2*day);
        int check_hurt=soldier.health;
        if(soldier.strength>dice_strength){
            if(soldier.mobility<dice_mobility){
                soldier.health--;
            }
        }
        else soldier.health--;
        if(soldier.health<=0){
            System.out.println(soldier.name + " died while exploring...");
            return null;
            }
        else {
            if (check_hurt != soldier.health) System.out.println(soldier.name + " was injured!");
            System.out.println(soldier.name + " has returned with a medical kit!");
            return hp_pack;
        }
    }
}
