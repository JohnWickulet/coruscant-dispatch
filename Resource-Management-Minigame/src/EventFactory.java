public class EventFactory {

    public static Event create(String type, Location location) {
        // Normalize type string to handle different save formats
        String eventType = type.toUpperCase().trim();

        switch (eventType) {
            case "INSPECTION":
                return new Inspection(location);

            case "ASSASSINATION":
                return new Assassination(location);
            case "REBELSABOTAGE":
                return new RebelSabotage(location);

            default:
                System.err.println("Unknown event type: " + type + ". Defaulting to Inspection.");
                return new Inspection(location);
        }
    }
}