
public class BlockHead extends Soldier {
    public BlockHead(String name,int intelligence,int strength,int mobility,String flavor){
        super(name,intelligence,strength,mobility,flavor);
    }
    @Override
    public void IncreaseStats(){
        this.intelligence+=0;
        this.strength+=2;
        this.mobility+=2;
    }
    @Override
    public void ability(Squad Squad){
        if(isExploring){
            this.strength++;
        }
    }
}
