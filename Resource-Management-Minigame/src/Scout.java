
public class Scout extends Soldier{
    public Scout(String name,int intelligence,int strength,int mobility,String flavor){
        super(name,intelligence,strength,mobility,flavor);
    }
    @Override
    public void IncreaseStats(){
        this.intelligence+=1;
        this.mobility+=3;
    }
    @Override
    public void ability(Soldier soldier){
        if(!(soldier instanceof Scout)) {
            System.out.println(name + " guides " + soldier.name + " from afar...");
            soldier.intelligence++;
        }
    }
}
