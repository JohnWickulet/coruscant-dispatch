import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class GameUI extends JFrame {
    private String profileName;
    private final Game game;
    private final saveGame saveManager = new saveGame(); // Cleaned up variable name
    private final GalaxyMapPanel map;
    private final BottomPanel bottomPanel;
    private final SoldierCardsPanel soldierPanel;
    private final EventManager eventManager;

    private Timer tickTimer;
    private static final int TICK_MS = 100;
    private static final double DT = 0.1;

    public GameUI(Game game, String profileName) {
        this.game = game;
        this.profileName = profileName;

        setTitle("Star Wars Dispatch - Profile: " + profileName);

        // Set to a large size but not necessarily full screen to allow window dragging
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize game only if it's brand new
        if(game.squad.soldiers.isEmpty()) {
            game.initialize();
        }

        setupMenuBar(profileName);

        // UI Components
        map = new GalaxyMapPanel(game);
        add(map, BorderLayout.CENTER); // Map stays in the center

        // Ensure map is synced with current game state
        if (!game.getActiveEvents().isEmpty()) {
            map.syncWithGame();
        }

        // Side Panel for Soldiers
        soldierPanel = new SoldierCardsPanel(game.squad);
        JScrollPane soldierScroll = new JScrollPane(soldierPanel);
        soldierScroll.setPreferredSize(new Dimension(200, getHeight()));
        soldierScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(soldierScroll, BorderLayout.EAST);

        // Bottom UI for Currency/Stats
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
            game.tick(DT);
            map.tick(DT);
            eventManager.tick(DT);

            // Repaint components to show updated timers/progress bars
            soldierPanel.repaint();
            bottomPanel.repaint();
            map.repaint();
        });
        resumeGame();
    }

    public void openDeploymentDialog(Event event) {
        pauseGame();
        DeploymentDialog dialog = new DeploymentDialog(this, game.squad, event);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        map.updateComponentPositions();
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

        // --- SYSTEM MENU ---
        JMenu gameMenu = new JMenu("System");

        JMenuItem saveItem = new JMenuItem("Save Game");
        saveItem.addActionListener(e -> {
            try {
                saveGame.fullSave(game, profileName);
                JOptionPane.showMessageDialog(this, "Progress saved for " + profileName);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Save Error: " + ex.getMessage());
            }
        });

        JMenuItem mainMenuItem = new JMenuItem("Exit to Main Menu");
        mainMenuItem.addActionListener(e -> {
            // Specify JOptionPane.YES_NO_OPTION to remove the "Cancel" button
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Return to Main Menu? (Unsaved progress will be lost)",
                    "Exit to Menu",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                pauseGame();
                this.dispose();
                new GameLauncher().setVisible(true);
            }
        });
        JMenuItem exitItem = new JMenuItem("Quit to Desktop");
        exitItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Quit the application?");
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        gameMenu.add(saveItem);
        gameMenu.add(mainMenuItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }
}