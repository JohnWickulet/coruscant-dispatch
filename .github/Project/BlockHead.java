package Project;

public class BlockHead extends Soldier {
    public BlockHead(String name,int health,int intelligence,int strength,int mobility,String flavor){
        super(name,health,intelligence,strength,mobility,flavor);
    }
    @Override
    public void IncreaseStats(){
        this.health+=5;
        this.intelligence+=1;
        this.strength+=3;
        this.mobility+=2;
    }
    @Override
    public void ability(){
        if(isExploring){
            this.strength++;
        }
    }
}
