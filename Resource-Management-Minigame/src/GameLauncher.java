import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GameLauncher extends JFrame {
    // Ensure this matches your class name (SaveManager or saveGame)
    private final saveGame saveManager = new saveGame();
    private JList<String> saveList;
    private DefaultListModel<String> listModel;

    public GameLauncher() {
        setTitle("Empire Command - Main Menu");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // Header
        JLabel title = new JLabel("EMPIRE COMMAND", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Save List Center
        listModel = new DefaultListModel<>();
        saveList = new JList<>(listModel);
        saveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        saveList.setFont(new Font("Monospaced", Font.PLAIN, 14));

        refreshSaveList(); // Load initial data from DB

        JScrollPane scrollPane = new JScrollPane(saveList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Save Files"));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel (Bottom)
        JPanel southPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel actionButtons = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton btnNew = new JButton("New Game");
        JButton btnContinue = new JButton("Continue Save");
        actionButtons.add(btnNew);
        actionButtons.add(btnContinue);

        JButton btnDelete = new JButton("Delete Selected Save");
        btnDelete.setForeground(Color.RED);

        southPanel.add(actionButtons);
        southPanel.add(btnDelete);
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        add(southPanel, BorderLayout.SOUTH);

        // --- Event Listeners ---

        // 1. Start New Game
        btnNew.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter Commander Name:");
            if (name != null && !name.trim().isEmpty()) {
                Game game = new Game();
                // initialize() adds the default soldiers
                game.initialize();
                startGame(game, name.trim());
            }
        });

        // 2. Continue Existing Game
        btnContinue.addActionListener(e -> {
            String selected = saveList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a save from the list.");
                return;
            }
            try {
                Game loadedGame = saveManager.fullLoad(selected);
                GameUI ui = new GameUI(loadedGame,selected);
                ui.getMap().syncWithGame();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load save: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // 3. Delete Save
        btnDelete.addActionListener(e -> {
            String selected = saveList.getSelectedValue();
            if (selected == null) return;

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + selected + "'?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    saveManager.deleteProfile(selected); // You need to add this to SaveManager
                    refreshSaveList();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting save: " + ex.getMessage());
                }
            }
        });
    }

    private void refreshSaveList() {
        listModel.clear();
        List<String> profiles = saveManager.getAllProfileNames();
        for (String s : profiles) {
            listModel.addElement(s);
        }
    }

    private void startGame(Game game, String profileName) {
        // Launches the main game UI and kills the launcher
        SwingUtilities.invokeLater(() -> new GameUI(game, profileName));
        this.dispose();
    }

    public static void main(String[] args) {
        // This is now the entry point for your entire application
        try {
            // Optional: Makes it look like a native Windows/Mac app
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new GameLauncher().setVisible(true));
    }
}