import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class GameUI extends JFrame implements GameStateListener {
    private String profileName;
    private final Game game;
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

        // REGISTER AS LISTENER
        this.game.addGameStateListener(this);

        setTitle("Star Wars Dispatch - Profile: " + profileName);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        if(game.squad.soldiers.isEmpty()) {
            game.initialize();
        }
        setupMenuBar(profileName);

        map = new GalaxyMapPanel(game);
        add(map, BorderLayout.CENTER);

        // Troop panel setup
        soldierPanel = new SoldierCardsPanel(game.squad);
        JScrollPane soldierScroll = new JScrollPane(soldierPanel);
        soldierScroll.setPreferredSize(new Dimension(340, getHeight()));
        soldierScroll.setBorder(BorderFactory.createEmptyBorder());
        add(soldierScroll, BorderLayout.EAST);

        bottomPanel = new BottomPanel(game, this);
        add(bottomPanel, BorderLayout.SOUTH);

        eventManager = new EventManager(game, map,game.getSpawnInterval());
        startTicking();
        setVisible(true);
    }

    @Override
    public void onSquadChanged() {
        soldierPanel.rebuildCards();
    }

    private void startTicking() {
        tickTimer = new Timer(TICK_MS, e -> {
            game.tick(DT);
            map.tick(DT);
            eventManager.tick(DT);
            soldierPanel.repaint();
            bottomPanel.repaint();
            map.repaint();
            if(game.getPatience() <= 0) {
                triggerGameOver();
            }
        });
        tickTimer.start();
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

        //Menu
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
    private void triggerGameOver() {
        pauseGame();
         SoundManager.playSound("lose.wav");

        String message = "The ISB has had enough of your incompetence.\n" +
                "You have been relieved of your duty and will be sent to Narkina 5 detention facility.\n\n" +
                "Final Score: " + game.getSuccess();

        Object[] options = {"Return to Menu", "Exit to Desktop"};
        int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "GAME OVER",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]
        );

        this.dispose();
        if (choice == JOptionPane.YES_OPTION) {
            new GameLauncher().setVisible(true);
        } else {
            System.exit(0);
        }
    }
}