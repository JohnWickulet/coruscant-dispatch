
public class Health extends Resource{
    int value;
    public Health(String name,String flavor,int value){
        super(name,flavor);
        this.value=value;
    }
    @Override
    public void Resource_info(){
        System.out.println("Name: " + name);
        System.out.println("Heal a soldier for " + value + " state of health");
        System.out.println(flavor);
    }
    @Override
    public void Use(Soldier soldier){
        if(soldier.health<2){
            soldier.health++;
        }
        else System.out.println(soldier.name + " is already healthy!");
    }
    @Override
    public boolean Valid_Use(Soldier soldier){
        return soldier.health != 2;
    }
}
