package Project;

public abstract class Resource {
    String name;
    String flavor;
    public Resource(String name,String flavor){
        this.name=name;
        this.flavor=flavor;
    }
    public abstract void Resource_info();
}
