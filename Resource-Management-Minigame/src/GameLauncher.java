import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GameLauncher extends JFrame {
    private final saveGame saveManager = new saveGame();
    private JList<String> saveList;
    private DefaultListModel<String> listModel;

    public GameLauncher() {
        setTitle("Totally not Dispatch - A Star Wars Game");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // Header
        JLabel title = new JLabel("Coruscant Dispatch", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Save List
        listModel = new DefaultListModel<>();
        saveList = new JList<>(listModel);
        saveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        saveList.setFont(new Font("Monospaced", Font.PLAIN, 14));

        refreshSaveList();

        JScrollPane scrollPane = new JScrollPane(saveList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Save Files"));
        add(scrollPane, BorderLayout.CENTER);

        saveList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SoundManager.select();
            }
        });

        // Buttons Panel
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

        // Start New Game
        btnNew.addActionListener(e -> {
            SoundManager.select();
            String name = JOptionPane.showInputDialog(this, "Enter Save File Name:");
            if (name != null && !name.trim().isEmpty()) {
                SoundManager.select();
                Game game = new Game();
                game.initialize();
                startGame(game, name.trim());
            }
        });

        // Continue Existing Game
        btnContinue.addActionListener(e -> {
            SoundManager.select();
            String selected = saveList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a save from the list.");
                return;
            }
            try {
                Game loadedGame = saveManager.fullLoad(selected);
                startGame(loadedGame,selected);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load save: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Delete Save
        btnDelete.addActionListener(e -> {
            SoundManager.select();
            String selected = saveList.getSelectedValue();
            if (selected == null) return;

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + selected + "'?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    SoundManager.select();
                    saveManager.deleteProfile(selected);
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
        SwingUtilities.invokeLater(() -> new GameUI(game, profileName).setVisible(true));
        GameLauncher.this.dispose();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new GameLauncher().setVisible(true));
    }
}