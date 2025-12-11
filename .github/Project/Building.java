package Project;

import java.util.Random;

public class Building extends Location{
    public Building(String name,String flavor,int day){
        super(name,flavor,day);
    }
    @Override
    public Health Check_Result(Soldier soldier){
        Health hp_pack=new Health("Health Kit","An old unused health kit, might be useful",5);
        Random r=new Random();
        int dice_strength=r.nextInt(5*day);
        int dice_mobility=r.nextInt(2*day);
        if(soldier.strength>dice_strength){
            if(soldier.mobility>dice_mobility){
                return hp_pack;
            }
        }
        return hp_pack;
    }
}
