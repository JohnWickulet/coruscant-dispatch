import java.sql.*;
import java.util.*;

public class saveGame {

    // --- SAVING LOGIC ---
    public static void fullSave(Game game, String profileName) throws SQLException {
        try (Connection conn = DB_Connection.get()) {
            conn.setAutoCommit(false);
            int profileId = getOrCreateProfile(conn, profileName);

            saveGameStats(conn, profileId, game);
            saveInventory(conn, profileId, game.inventory);

            saveEvents(conn, profileId, game.getActiveEvents());
            saveSoldiers(conn, profileId, game.squad.soldiers);

            conn.commit();
        } catch (SQLException e) {
            System.err.println("Save failed! Rolling back.");
            throw e;
        }
    }

    // --- LOADING LOGIC ---
    public Game fullLoad(String profileName) throws SQLException {
        Game game = new Game();
        Map<Integer, Soldier> idToSoldierMap = new HashMap<>();

        try (Connection conn = DB_Connection.get()) {
            int profileId = getProfileId(conn, profileName);

            loadGameStats(conn, profileId, game);
            loadInventory(conn, profileId, game.inventory);
            loadSoldiers(conn, profileId, game, idToSoldierMap);
            loadEvents(conn, profileId, game, idToSoldierMap);

            return game;
        }
    }

    // --- HELPER METHODS (SAVE) ---

    private static void saveGameStats(Connection conn, int pId, Game g) throws SQLException {
        // Added succes_count to the state save
        String sql = "INSERT INTO game_state (profile_id, currency, patience, success_count) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (profile_id) DO UPDATE SET currency=EXCLUDED.currency, patience=EXCLUDED.patience, success_count=EXCLUDED.success_count";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pId);
            ps.setInt(2, g.getCurrency());
            ps.setInt(3, g.getPatience());
            ps.setInt(4, g.getSuccess()); // New field
            ps.executeUpdate();
        }
    }

    private static int getOrInsertLocation(Connection conn, Location loc) throws SQLException {
        // 1. Check if location already exists in Table 5
        String select = "SELECT location_id FROM locations WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setString(1, loc.name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }

        // 2. If not, insert it (Static data for the universe)
        String insert = "INSERT INTO locations (name, danger_level) VALUES (?, ?) RETURNING location_id";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, loc.name);
            ps.setDouble(2, loc.getDiff());
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    private static void saveSoldiers(Connection conn, int pId, List<Soldier> soldiers) throws SQLException {
        // Delete existing so we don't duplicate when resaving
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM soldiers WHERE profile_id = ?")) {
            del.setInt(1, pId);
            del.executeUpdate();
        }

        String sql = "INSERT INTO soldiers (profile_id, name, troop_class, strength, mobility, intelligence, stealth, charisma, " +
                "health, max_health, level, exp, state, rest_time_remaining) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        for (Soldier s : soldiers) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, pId);
                ps.setString(2, s.name);
                ps.setString(3, s.troopClass.name());
                ps.setInt(4, s.strength);
                ps.setInt(5, s.mobility);
                ps.setInt(6, s.intelligence);
                ps.setInt(7, s.stealth);
                ps.setInt(8, s.charisma);
                ps.setInt(9, s.health);
                ps.setInt(10, s.maxHealth);
                ps.setInt(11, s.level);
                ps.setInt(12, s.exp);
                ps.setString(13, s.state.name());
                ps.setDouble(14, s.rest_time_remaining);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) s.setDatabaseId(rs.getInt(1));
            }
        }
    }

    private static void saveEvents(Connection conn, int pId, List<Event> events) throws SQLException {
        // Clear old deployments and events
        executeDelete(conn, "DELETE FROM event_deployments WHERE event_id IN (SELECT id FROM active_events WHERE profile_id = ?)", pId);
        executeDelete(conn, "DELETE FROM active_events WHERE profile_id = ?", pId);

        String sqlEv = "INSERT INTO active_events (profile_id, event_type, event_name, location_id, time_left, state) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        String sqlDep = "INSERT INTO event_deployments (event_id, soldier_id) VALUES (?, ?)";

        for (Event e : events) {
            // Look up the ID for the location object
            int locId = getOrInsertLocation(conn, e.location);

            int eventId;
            try (PreparedStatement ps = conn.prepareStatement(sqlEv)) {
                ps.setInt(1, pId);
                // Saves: Inspection, Assassination, or Sabotage
                ps.setString(2, e.getClass().getSimpleName().replace("Rebel", "").replace("Event", ""));
                ps.setString(3, e.name);
                ps.setInt(4, locId); // FK to locations table
                ps.setDouble(5, e.timeLeft);
                ps.setString(6, e.state.name());

                ResultSet rs = ps.executeQuery();
                rs.next();
                eventId = rs.getInt(1);
            }

            if (e.deployed != null) {
                for (Soldier s : e.deployed) {
                    if (s.getDatabaseId() > 0) { // Safety check for promoted soldiers
                        try (PreparedStatement psDep = conn.prepareStatement(sqlDep)) {
                            psDep.setInt(1, eventId);
                            psDep.setInt(2, s.getDatabaseId());
                            psDep.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    // --- HELPER METHODS (LOAD) ---

    private void loadSoldiers(Connection conn, int pId, Game g, Map<Integer, Soldier> map) throws SQLException {
        String sql = "SELECT * FROM soldiers WHERE profile_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pId);
            try (ResultSet rs = ps.executeQuery()) {
                g.squad.soldiers.clear(); // Remove default initialization soldiers

                while (rs.next()) {
                    // 1. Use your factory to create the specific subclass instance
                    Soldier s = SoldierFactory.create(rs.getString("troop_class"), rs.getString("name"));

                    // 2. Map standard stats
                    s.setDatabaseId(rs.getInt("id"));
                    s.strength = rs.getInt("strength");
                    s.mobility = rs.getInt("mobility");
                    s.intelligence = rs.getInt("intelligence");
                    s.stealth = rs.getInt("stealth");
                    s.charisma = rs.getInt("charisma");

                    // 3. Map status and health
                    s.health = rs.getInt("health");
                    s.maxHealth = rs.getInt("max_health");
                    s.level = rs.getInt("level");
                    s.exp = rs.getInt("exp");
                    s.state = Soldier.State.valueOf(rs.getString("state"));
                    s.rest_time_remaining = rs.getDouble("rest_time_remaining");

                    // 4. Add to Game and the Lookup Map (for Event reconstruction)
                    g.squad.addSoldier(s);
                    map.put(s.getDatabaseId(), s);
                }
            }
        }
    }

    private void loadEvents(Connection conn, int pId, Game g, Map<Integer, Soldier> lookup) throws SQLException {
        // 1. Get all events for this profile
        String sqlEvent = "SELECT e.*, l.name as loc_name, l.danger_level " +
                "FROM active_events e " +
                "JOIN locations l ON e.location_id = l.location_id " +
                "WHERE e.profile_id = ?";

        try (PreparedStatement stmtEv = conn.prepareStatement(sqlEvent)) {
            stmtEv.setInt(1, pId);
            ResultSet rsEv = stmtEv.executeQuery();

            while (rsEv.next()) {
                Location loc = new Location(rsEv.getString("loc_name"), rsEv.getDouble("danger_level"));
                Event e = EventFactory.create(
                        rsEv.getString("event_type"),
                        loc
                );

                e.timeLeft = rsEv.getDouble("time_left");
                int eventId = rsEv.getInt("id");

                // 3. Fetch the ID of every soldier assigned to THIS specific event
                List<Soldier> deployedForThisEvent = new ArrayList<>();
                String sqlDep = "SELECT soldier_id FROM event_deployments WHERE event_id = ?";

                try (PreparedStatement stmtDep = conn.prepareStatement(sqlDep)) {
                    stmtDep.setInt(1, eventId);
                    ResultSet rsDep = stmtDep.executeQuery();

                    while (rsDep.next()) {
                        int sId = rsDep.getInt("soldier_id");
                        // 4. IMPORTANT: Grab the ALREADY LOADED soldier from the map
                        Soldier s = lookup.get(sId);
                        if (s != null) {
                            deployedForThisEvent.add(s);
                        }
                    }
                }

                // 5. Re-attach the soldiers to the event
                if (!deployedForThisEvent.isEmpty()) {
                    e.deploy(deployedForThisEvent);
                    // Manually fix the state if the deploy() method resets the timer
                    e.timeLeft = rsEv.getDouble("time_left");
                }

                // 6. Add the fully reconstructed event to the game
                g.addEvent(e);
            }
        }
    }

    private void loadGameStats(Connection conn, int pId, Game game) throws SQLException {
        String sql = "SELECT currency, patience FROM game_state WHERE profile_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Since Game starts with 100/50, we reset and set exact values
                    game.useCurrency(game.getCurrency()); // set to 0
                    game.addCurrency(rs.getInt("currency"));

                    game.losePatience(game.getPatience()); // set to 0
                    game.gainPatience(rs.getInt("patience"));
                }
            }
        }
    }

    // Boilerplate ID lookups
    private static int getOrCreateProfile(Connection conn, String name) throws SQLException {
        String sql = "INSERT INTO profiles (name) VALUES (?) ON CONFLICT (name) DO UPDATE SET name=EXCLUDED.name RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    private int getProfileId(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM profiles WHERE name = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Profile not found");
        }
    }

    private static void saveInventory(Connection conn, int pId, Inventory inv) throws SQLException {
        String sql = "INSERT INTO inventory (profile_id, item_name, quantity) VALUES (?, ?, ?) " +
                "ON CONFLICT (profile_id, item_name) DO UPDATE SET quantity = EXCLUDED.quantity";
        for (Map.Entry<ItemType, Integer> entry : inv.getAll().entrySet()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, pId);
                ps.setString(2, entry.getKey().name());
                ps.setInt(3, entry.getValue());
                ps.executeUpdate();
            }
        }
    }

    private void loadInventory(Connection conn, int pId, Inventory inv) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM inventory WHERE profile_id = ?")) {
            ps.setInt(1, pId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ItemType type = ItemType.valueOf(rs.getString("item_name"));
                int qty = rs.getInt("quantity");
                for(int i=0; i<qty; i++) inv.add(type);
            }
        }
    }
    public List<String> getAllProfileNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM profiles ORDER BY name ASC";
        try (Connection conn = DB_Connection.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }
    public void deleteProfile(String profileName) throws SQLException {
        String getPId = "SELECT id FROM profiles WHERE name = ?";
        String delGameState = "DELETE FROM game_state WHERE profile_id = ?";
        String delInventory = "DELETE FROM inventory WHERE profile_id = ?";
        String delDeployments = "DELETE FROM event_deployments WHERE event_id IN (SELECT id FROM active_events WHERE profile_id = ?)";
        String delEvents = "DELETE FROM active_events WHERE profile_id = ?";
        String delSoldiers = "DELETE FROM soldiers WHERE profile_id = ?";
        String delProfile = "DELETE FROM profiles WHERE id = ?";

        try (Connection conn = DB_Connection.get()) {
            conn.setAutoCommit(false); // Use a transaction for safety
            try {
                int pId = -1;
                try (PreparedStatement ps = conn.prepareStatement(getPId)) {
                    ps.setString(1, profileName);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) pId = rs.getInt(1);
                }

                if (pId != -1) {
                    // Delete in order: Children first, then Parent
                    executeDelete(conn, delGameState, pId);
                    executeDelete(conn, delInventory, pId);
                    executeDelete(conn, delDeployments, pId);
                    executeDelete(conn, delEvents, pId);
                    executeDelete(conn, delSoldiers, pId);
                    executeDelete(conn, delProfile, pId);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback(); // Undo everything if one part fails
                throw e;
            }
        }
    }

    private static void executeDelete(Connection conn, String sql, int pId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pId);
            ps.executeUpdate();
        }
    }
}