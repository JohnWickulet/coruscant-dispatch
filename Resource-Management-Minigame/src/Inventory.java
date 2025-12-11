
public class Inventory {
    Resource[] inventory=new Resource[10];
    int amount=0;
    public void Check_Inventory(){
        if(amount==0) System.out.println("Inventory empty");
        for(int i=0;i<amount;i++){
            inventory[i].Resource_info();
        }
    }
    public void Discard_From_Inventory(){

    }
    public void Add_To_Inventory(Resource resource){
        if(amount<10){
            inventory[amount]=resource;
        }
        else System.out.println("Inventory full! Please discard an item");
    }

}
