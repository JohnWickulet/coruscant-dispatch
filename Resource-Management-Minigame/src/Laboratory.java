import java.util.Random;

public class Laboratory extends Location{
    public Laboratory(String name, String flavor, int day){
        super(name,flavor,day);
    }
    @Override
    public Buff Check_Result(Soldier soldier){
        Buff buff = new Buff("Research note","A research note on the development of the Virus",1,0,0);
        Random r=new Random();
        int dice_str=r.nextInt(day);
        int dice_intel=r.nextInt(3*day);
        if(soldier.strength>=dice_str){
            if(soldier.intelligence>=dice_intel){
                System.out.println(soldier.name + " has returned with some research notes!");
                return buff;
            }
            else{
                System.out.println(soldier.name + " failed to find anything useful and returned to the camp");
                return null;
            }
        }
        else{
            System.out.println(soldier.name + " hurt himself trying to break through the locked door...");
            soldier.health--;
            return null;
        }
    }
}
