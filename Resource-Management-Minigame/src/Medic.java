import java.util.Random;

public class Medic extends Soldier{
    public Medic(String name,int intelligence,int strength,int mobility,String flavor){
        super(name,intelligence,strength,mobility,flavor);
    }
    @Override
    public void IncreaseStats(){
        this.intelligence+=1;
        this.mobility+=1;
    }
    @Override
    public void ability(Soldier soldier){
        if(soldier.health<2 && !(soldier instanceof Medic)){
            System.out.println(name + " healed " + soldier.name);
            soldier.heal();
        }
    }
}
