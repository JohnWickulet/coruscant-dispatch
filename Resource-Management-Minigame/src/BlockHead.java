
public class BlockHead extends Soldier {
    public BlockHead(String name,int intelligence,int strength,int mobility,String flavor){
        super(name,intelligence,strength,mobility,flavor);
    }
    @Override
    public void IncreaseStats(){
        this.strength+=2;
        this.mobility+=2;
    }
    @Override
    public void ability(Soldier soldier){
        if(soldier instanceof BlockHead){
            System.out.println(name + " is pumped up!");
            this.strength++;
        }
    }
}
