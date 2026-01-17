import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class GameLauncher extends JFrame {
    private final saveGame saveManager = new saveGame();
    private JList<String> saveList;
    private DefaultListModel<String> listModel;
    private static String[] sfx = {"select1.wav","select2.wav","select3.wav","select4.wav","select5.wav"};
    private static final Random rng = new Random();

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
            String name = JOptionPane.showInputDialog(this, "Enter Save File Name:");
            if (name != null && !name.trim().isEmpty()) {
                SoundManager.playSound(sfx[rng.nextInt(sfx.length)]);
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
                startGame(loadedGame,selected);
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
                    SoundManager.playSound(sfx[rng.nextInt(sfx.length)]);
                    saveManager.deleteProfile(selected);
                    refreshSaveList();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting save: " + ex.getMessage());
                }
            }
        });

    }

    public void initGlobalClickSound() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                    SoundManager.playSound(sfx[rng.nextInt(sfx.length)]);
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
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
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) event;
                if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                    SoundManager.playSound(sfx[rng.nextInt(sfx.length)]);
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
        SwingUtilities.invokeLater(() -> new GameLauncher().setVisible(true));
    }
}