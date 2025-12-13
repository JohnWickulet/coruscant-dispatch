
import java.time.format.ResolverStyle;
import java.util.Random;
import java.util.Scanner;

public class Game {
    int day=0;
    Squad squad=new Squad();
    Location[] location=new Location[1];
    public void main(String[] args){
        Soldier brick=new BlockHead("Watanabe",2,4,1,"Not the brightest bulb and slow as molasses but his strength more than makes up for it");
        Soldier scout=new Scout("John",2,1,3,"Could've won the gold medal for 100 metres had the war not started");
        Soldier medic=new Medic("Wanshi",3,1,1,"Sleepy head");
        Soldier[] pool=new Soldier[]{brick,scout,medic};
        Scanner in=new Scanner(System.in);
        Random r=new Random();
        System.out.println("Starting a new round");
        Commander commander=new Commander("Joe Biden","die");
        System.out.println("Input the size you want to start the game with: ");
        int starting_size=in.nextInt();
        int[] chosen=new int[4];
        for(int j=0;j<4;j++){
            chosen[j]=-1;
        }
        int i=0;
        while(i<starting_size) {
            int select = r.nextInt(3);
            if (chosen[select] == -1) {
                chosen[select] = 1;
                squad.Add_Soldier(pool[select]);
                i++;
            }
        }
        squad.Add_Commander(commander);

        boolean ok=true;
        boolean sent=false;
        int index_sent=0;
        Resource[] collected = new Resource[1];
        while(!squad.status()){
            squad.change_status();
            int random_location_generate=r.nextInt(2);
            switch (random_location_generate){
                case 0: location[0]=new Building("Abandoned hospital","An abandoned hospital, might be able to find some medical kits",day);
                        break;
                case 1: location[0]=new Laboratory("Abandoned Research Base","A research base clearly abandoned in a rush",day);
                        break;
                default: //how did this even happen
                        location[0]=new Building("Abandoned building","An abandoned building...",day);
            }
            if(sent) {
                collected[0] = location[0].Check_Result(squad.soldiers[index_sent]);
                if(collected[0]!=null) squad.inventory.Add_To_Inventory(collected[0]);
            }
            sent = false;
            ok=true;
            while(ok) {
                System.out.println("Select what you want to do: ");
                System.out.println("1) Check the status of your squad");
                System.out.println("2) Check your inventory");
                System.out.println("3) Check the current location");
                if (!sent) {
                    System.out.println("4) Send a soldier to collect resources");
                    System.out.println("5) End day");
                } else System.out.println("4) End day");
                switch (in.nextInt()) {
                    case 1:
                        squad.GetStatus();
                        break;
                    case 2:
                        int option=0;
                        squad.Check_Inventory();
                        while(squad.inventory.amount!=0 && option!=3){
                            System.out.println("1) Use an item");
                            System.out.println("2) Discard an item");
                            System.out.println("3) Exit inventory");
                            option=in.nextInt();
                            switch(option){
                                case 1:
                                    squad.inventory.Use_From_Inventory(squad);
                                    break;
                                case 2:
                                    squad.inventory.Discard_From_Inventory();
                                    break;
                            }
                        }
                        break;
                    case 3:
                        location[0].Location_Info();
                        break;
                    case 4:
                        if (!sent) {
                            boolean ValidOption=true;
                            while(ValidOption) {
                                System.out.println("Select the soldier to send");
                                int selected=in.nextInt();
                                if(selected<0 || selected>squad.current_size){
                                    System.out.println("Invalid option!\n");
                                }
                                else {
                                    squad.soldiers[selected-1].isExploring = true;
                                    sent = true;
                                    ValidOption = false;
                                    index_sent=selected-1;
                                }
                            }
                        } else {
                            System.out.println("Ending day...");
                            ok = false;
                            day++;
                        }
                        break;
                    case 5:
                        if (!sent) {
                            System.out.println("Ending day...");
                            ok = false;
                            day++;
                            break;
                        }
                    default:
                        System.out.println("Invalid option! Don't be a retard and select the choices given!");
                }
                System.out.println();
            }
        }
    }
}
