
public class Health extends Resource{
    int value;
    public Health(String name,String flavor,int value){
        super(name,flavor);
        this.value=value;
    }
    @Override
    public void Resource_info(){
        System.out.println("\nName: " + name);
        System.out.println(flavor);
        System.out.println("Heal a soldier for " + value + " state of health");
        System.out.println();
    }
    @Override
    public void Use(Soldier soldier){
        switch(soldier.health){
            case 2:
                System.out.println(soldier.name + " is already healthy");
                break;
            case 1:
                System.out.println("Healing " + soldier.name + "...");
                soldier.heal();
                break;
            case 0: System.out.println("Cannot heal a dead unit!");
        }
    }
    @Override
    public boolean Valid_Use(Soldier soldier){
        return (soldier.health != 2 || !soldier.isAlive());
    }
}
