package Project;

public abstract class Location {
    int day;
    String name;
    String flavor;
    public Location(String name,String flavor,int day){
        this.name=name;
        this.flavor=flavor;
        this.day=day;
    }
    public void Location_Info(){
        System.out.println("Name: " + name);
        System.out.println(flavor);
    }
    public abstract Resource Check_Result(Soldier soldier);
}
