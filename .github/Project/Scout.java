package Project;

public class Scout extends Soldier{
    public Scout(String name,int health,int intelligence,int strength,int mobility,String flavor){
        super(name,health,intelligence,strength,mobility,flavor);
    }
    @Override
    public void IncreaseStats(){
        this.health+=3;
        this.intelligence+=1;
        this.strength+=1;
        this.mobility+=4;
    }
    @Override
    public void ability(){
        if(isExploring){
            this.intelligence++;
        }
    }
}
