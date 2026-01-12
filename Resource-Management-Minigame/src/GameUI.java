import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class GameUI extends JFrame {
    private String profileName;
    private final Game game;
    private final saveGame SaveGame=new saveGame();
    private final GalaxyMapPanel map;
    private final BottomPanel bottomPanel;
    private final SoldierCardsPanel soldierPanel;
    private final EventManager eventManager;

    private Timer tickTimer;
    private static final int TICK_MS = 100;
    private static final double DT = 0.1;

    public GameUI(Game game, String profileName) {
        this.game = game;
        this.profileName = profileName; // CRITICAL: Assign the profile name

        setTitle("Star Wars Dispatch - Profile: " + profileName);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize game only if it's brand new
        if(game.squad.soldiers.isEmpty()) {
            game.initialize();
        }

        // Setup the Menu Bar so the player can actually save
        setupMenuBar(profileName);

        // UI Components
        map = new GalaxyMapPanel(game);
        add(map, BorderLayout.CENTER);
        if (!game.getActiveEvents().isEmpty()) {
            map.syncWithGame();
        }

        soldierPanel = new SoldierCardsPanel(game.squad);
        JScrollPane soldierScroll = new JScrollPane(soldierPanel);
        soldierScroll.setPreferredSize(new Dimension(180, getHeight()));
        add(soldierScroll, BorderLayout.EAST);

        bottomPanel = new BottomPanel(game, this);
        add(bottomPanel, BorderLayout.SOUTH);

        eventManager = new EventManager(game, map, 8);

        startTicking();
        setVisible(true);
    }

    public GalaxyMapPanel getMap(){
        return map;
    }

    private void startTicking() {
        tickTimer = new Timer(TICK_MS, e -> {
            game.tick(DT);        // Update game logic
            map.tick(DT);         // Update event cards
            eventManager.tick(DT);
            soldierPanel.repaint();// Refresh soldiers panel
            map.repaint();            // Refresh UI
        });
        resumeGame();
    }

    public void openDeploymentDialog(Event event) {
        pauseGame();
        DeploymentDialog dialog = new DeploymentDialog(this, game.squad, event);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        resumeGame();
    }
    public void pauseGame(){
        tickTimer.stop();
    }
    public void resumeGame(){
        tickTimer.start();
    }
    public SoldierCardsPanel getSoldierPanel() {
        return soldierPanel;
    }
    private void setupMenuBar(String profileName) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Game");
        JMenuItem saveItem = new JMenuItem("Save Progress");

        saveItem.addActionListener(e -> {
            try {
                // Changed 'saveGame' to 'saveManager' to match the field name
                saveGame.fullSave(game, profileName);
                JOptionPane.showMessageDialog(this, "Game Saved Successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
}
