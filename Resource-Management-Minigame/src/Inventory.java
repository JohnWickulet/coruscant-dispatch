import java.util.Scanner;

public class Inventory {
    Resource[] inventory=new Resource[5];
    int amount=0;
    public void Check_Inventory(){
        if(amount==0) System.out.println("Inventory empty");
        for(int i=0;i<amount;i++){
            inventory[i].Resource_info();
        }
    }

    public void Discard(int index){
        if(amount>0) {
            amount--;
            for(int i=index;i<amount;i++){
                inventory[i]=inventory[i+1];
            }
        }
    }
    public void Discard_From_Inventory(){
        Scanner in=new Scanner(System.in);
        int sel=-1;
        boolean ok=true;
        while(ok){
            System.out.println("Select an item to discard or exit the menu using 0:");
            sel=in.nextInt();
            if(sel==0){
                System.out.println("Exiting...");
                break;
            }
            if(sel>0 && sel<amount) {
                System.out.println("Discarding " + inventory[sel-1] + "...");
                Discard(sel-1);
                ok=false;
            }
        }
    }
    public void Add_To_Inventory(Resource resource){
        if(amount<10){
            inventory[amount++]=resource;
        }
        else System.out.println("Inventory full! Please discard an item");
    }
    public void Use_From_Inventory(Squad squad){
        Scanner in=new Scanner(System.in);
        int sel=-1;
        int sel_sol=-1;
        while(true){
            System.out.println("Select an item to use or exit the menu using 0:");
            sel=in.nextInt();
            if(sel==0){
                System.out.println("Exiting...");
                break;
            }
            if(sel>0 && sel<=amount) {
                while(true) {
                    System.out.println("Select a soldier to use the item on or go back to item selection using 0:");
                    sel_sol = in.nextInt();
                    if (sel_sol == 0) {
                        System.out.println("Returning...");
                        break;
                    }
                    if (sel_sol > 0 && sel_sol <= squad.current_size && inventory[sel-1].Valid_Use(squad.soldiers[sel_sol-1])) {
                        System.out.println("Using " + inventory[sel-1].name + " on " + squad.soldiers[sel_sol-1].name);
                        Discard(sel-1);
                        break;
                    }
                    else {
                        if(!inventory[sel-1].Valid_Use(squad.soldiers[sel_sol-1])){
                            System.out.println("Cannot use " + inventory[sel-1].name + " on " + squad.soldiers[sel_sol-1]);
                        }
                        else System.out.println("Invalid option!");
                    }
                }
            }
            else System.out.println("Invalid option!");
        }
    }
}
