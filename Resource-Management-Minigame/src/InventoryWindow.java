import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.HashMap;

public class InventoryWindow extends JDialog { // Changed to JDialog for modal behavior

    private final Inventory inventory;
    private final Squad squad;
    private final GameUI ui;
    private final Map<ItemType, JButton> itemButtons = new EnumMap<>(ItemType.class);
    private final Map<Soldier, JPanel> soldierCards = new HashMap<>();

    private ItemType selectedItem = null;
    private final JLabel infoLabel = new JLabel("Select an item to use", SwingConstants.CENTER);

    public InventoryWindow(GameUI ui, Inventory inventory, Squad squad) {
        super(ui, "Armory & Inventory", true); // Modal prevents background clicking
        this.ui = ui;
        this.inventory = inventory;
        this.squad = squad;

        setSize(700, 600);
        setLocationRelativeTo(ui);
        setLayout(new BorderLayout(15, 15));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildItemsPanel(), BorderLayout.NORTH);
        add(new JScrollPane(buildSoldiersPanel()), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        // UI looks better with a dark theme or specific colors
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        setVisible(true);
    }

    /* ================= ITEMS ================= */

    private JPanel buildItemsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, ItemType.values().length, 10, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Available Supplies"));

        for (ItemType type : ItemType.values()) {
            JButton btn = new JButton();
            updateItemButton(btn, type);

            btn.addActionListener(e -> {
                selectedItem = type;
                highlightSelectedItem(type);
                infoLabel.setText("Using " + type.displayName() + ": Select a soldier to apply.");
            });

            itemButtons.put(type, btn);
            panel.add(btn);
        }
        return panel;
    }

    private void updateItemButton(JButton btn, ItemType type) {
        int qty = inventory.get(type);
        btn.setEnabled(qty > 0);
        btn.setText("<html><center><b>" + type.displayName() + "</b><br><font color='blue'>Qty: " + qty + "</font></center></html>");
    }

    private void highlightSelectedItem(ItemType selected) {
        itemButtons.forEach((type, btn) -> {
            if (type == selected) {
                btn.setBackground(new Color(180, 200, 255));
            } else {
                btn.setBackground(null);
            }
        });
    }

    /* ================= SOLDIERS ================= */

    private JPanel buildSoldiersPanel() {
        // Grid layout makes it look like a collection of dossiers
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        for (Soldier s : squad.soldiers) {
            JPanel card = createSoldierMiniCard(s);
            soldierCards.put(s, card);
            panel.add(card);
        }
        return panel;
    }

    private JPanel createSoldierMiniCard(Soldier s) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createEtchedBorder());

        JButton nameBtn = new JButton(s.name + " (" + s.troopClass + ")");

        // Show current stats so player knows who needs healing
        JLabel stats = new JLabel(String.format("HP: %d/%d | STR: %d | INT: %d",
                s.health, s.maxHealth, s.strength, s.intelligence));
        stats.setHorizontalAlignment(SwingConstants.CENTER);
        stats.setFont(new Font("Monospaced", Font.PLAIN, 11));

        nameBtn.addActionListener(e -> tryApplyItem(s, stats));

        card.add(nameBtn, BorderLayout.CENTER);
        card.add(stats, BorderLayout.SOUTH);

        return card;
    }

    /* ================= APPLY LOGIC ================= */

    private void tryApplyItem(Soldier s, JLabel statsLabel) {
        if (selectedItem == null) {
            infoLabel.setText("<html><font color='red'>Please select an item first!</font></html>");
            return;
        }

        if (inventory.consume(selectedItem)) {
            selectedItem.use(s); // Apply the logic (e.g., s.health += 20)

            // Refresh visuals
            updateItemButton(itemButtons.get(selectedItem), selectedItem);
            statsLabel.setText(String.format("HP: %d/%d | STR: %d | INT: %d",
                    s.health, s.maxHealth, s.strength, s.intelligence));

            infoLabel.setText("Applied " + selectedItem.displayName() + " to " + s.name);

            // Update the main game UI soldier list
            ui.getSoldierPanel().repaint();

            // If item ran out, deselect
            if (inventory.get(selectedItem) <= 0) {
                selectedItem = null;
            }
        }
    }

    private JPanel buildBottom() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(infoLabel, BorderLayout.CENTER);

        JButton close = new JButton("Finish");
        close.addActionListener(e -> dispose());
        panel.add(close, BorderLayout.EAST);
        return panel;
    }
}