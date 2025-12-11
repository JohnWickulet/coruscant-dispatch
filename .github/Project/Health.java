package Project;

public class Health extends Resource{
    int value;
    public Health(String name,String flavor,int value){
        super(name,flavor);
        this.value=value;
    }
    @Override
    public void Resource_info(){
        System.out.println("Name: " + name);
        System.out.println("Heal a soldier for " + value);
        System.out.println(flavor);
    }
}
