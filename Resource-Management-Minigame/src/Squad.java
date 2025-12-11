
public class Squad {
    Soldier[] soldiers=new Soldier[4];
    Commander commander=new Commander(null,null);
    Inventory inventory=new Inventory();
    int current_size;
    public Squad(){
        this.current_size=0;
    }
    public void GetStatus(){
        commander.GetStatus();
        for(int i=0;i<current_size;i++){
            soldiers[i].GetStatus();
        }
    }
    public void Add_Commander(Commander commander){
        this.commander = commander;
    }
    public void Add_Soldier(Soldier new_member){
        if(current_size<4){
            soldiers[current_size++]=new_member;
        }
        else System.out.println("The Squad is full!");
    }
    public boolean status(){
        return (commander.isDead || current_size==0);
    }
    public void Check_Inventory(){
        inventory.Check_Inventory();
    }
    public void change_status(){
        for(Soldier soldier:soldiers){
            if(soldier!=null && !soldier.isDead){
                if(soldier.isExploring){
                    soldier.isExploring=false;}
                if(soldier.health<0){
                    soldier.isDead=true;}
            }
        }
    }
}
