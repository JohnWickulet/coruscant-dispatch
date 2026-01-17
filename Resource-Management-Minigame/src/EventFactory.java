public class EventFactory {

    public static Event create(String type, Location location,int x,int y) {
        // Normalize type string to handle different save formats
        String eventType = type.toUpperCase().trim();

        switch (eventType) {
            case "INSPECTION":
                return new Inspection(location,x,y);
            case "ASSASSINATION":
                return new Assassination(location,x,y);
            case "REBELSABOTAGE":
                return new RebelSabotage(location,x,y);
            case "BODYGUARD":
                return new BodyGuard(location,x,y);
            default:
                System.err.println("Unknown event type: " + type + ". Defaulting to Inspection.");
                return new Inspection(location,x,y);
        }
    }
}