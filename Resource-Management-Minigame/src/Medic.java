import java.util.Random;

public class Medic extends Soldier{
    public Medic(String name,int intelligence,int strength,int mobility,String flavor){
        super(name,intelligence,strength,mobility,flavor);
    }
    @Override
    public void IncreaseStats(){
        this.intelligence+=1;
        this.strength+=0;
        this.mobility+=1;
    }
    @Override
    public void ability(Squad squad){
        if(isExploring){
            Random r=new Random();
            int select=r.nextInt()%squad.current_size-1;
            int medicindex=0;
            for(int i=0;i<squad.current_size;i++){
                if(squad.soldiers[i] instanceof Medic){
                    medicindex=i;
                }
            }
            while(select==medicindex){
                medicindex=r.nextInt()%squad.current_size-1;
            }
        }
    }
}
