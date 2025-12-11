
public class Scout extends Soldier{
    public Scout(String name,int intelligence,int strength,int mobility,String flavor){
        super(name,intelligence,strength,mobility,flavor);
    }
    @Override
    public void IncreaseStats(){
        this.intelligence+=1;
        this.strength+=0;
        this.mobility+=3;
    }
    @Override
    public void ability(Squad squad){
        if(isExploring){
            this.intelligence++;
        }
    }
}
