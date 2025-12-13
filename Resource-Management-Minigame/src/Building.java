
import java.util.Random;

public class Building extends Location{
    public Building(String name,String flavor,int day){
        super(name,flavor,day);
    }
    @Override
    public Health Check_Result(Soldier soldier){
        Health hp_pack=new Health("Health Kit","An old unused health kit, might be useful",1);
        Random r=new Random();
        int dice_strength=day+r.nextInt(2*day);
        int dice_mobility=day+r.nextInt(2*day);
        int check_hurt=soldier.health;
        if(soldier instanceof Medic){
            System.out.println(soldier.name + " has returned with a " + hp_pack.name + "\n");
            soldier.IncreaseXp(100);
            return hp_pack;
        }
        if(soldier.strength>=dice_strength){
            if(soldier.mobility>=dice_mobility){
                System.out.println(soldier.name + " has returned with a " + hp_pack.name + "\n");
                soldier.IncreaseXp(75);
                return hp_pack;
            }
            else{
                System.out.println(soldier.name + " failed to jump across the hole in the ground and hurt himself...\n");
                soldier.hurt();
                return null;
            }
        }
        else{
            System.out.println(soldier.name + " hurt himself trying to break through the locked door...\n");
            soldier.hurt();
            return null;
        }
    }
}
