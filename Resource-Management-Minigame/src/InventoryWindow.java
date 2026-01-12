import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class InventoryWindow extends GameWindow {

    private final Inventory inventory;
    private final Squad squad;
    private final Map<ItemType, JButton> itemButtons = new EnumMap<>(ItemType.class);

    private ItemType selectedItem = null;
    private Soldier selectedSoldier = null;

    private final JLabel infoLabel = new JLabel("Select an item");

    public InventoryWindow(GameUI ui, Inventory inventory, Squad squad) {
        super(ui, "Inventory", 600, 500);
        this.inventory = inventory;
        this.squad = squad;

        ui.pauseGame();

        setLayout(new BorderLayout(10, 10));
        add(buildItemsPanel(), BorderLayout.NORTH);
        add(buildSoldiersPanel(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        setVisible(true);
        ui.resumeGame();
    }

    /* ================= ITEMS ================= */

    private JPanel buildItemsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));

        for (ItemType type : ItemType.values()) {
            JButton btn = new JButton();
            updateItemButton(btn, type);

            btn.addActionListener(e -> {
                selectedItem = type;
                infoLabel.setText("Selected item: " + type.displayName());
            });

            itemButtons.put(type, btn); // ✅ store reference
            panel.add(btn);
        }

        return panel;
    }


    private void updateItemButton(JButton btn, ItemType type) {
        int qty = inventory.get(type);
        btn.setEnabled(qty > 0);
        btn.setText("<html><center>" + type.displayName() +
                "<br>Qty: " + qty + "</center></html>");
    }


    /* ================= SOLDIERS ================= */

    private JPanel buildSoldiersPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        for (Soldier s : squad.soldiers) {
            JButton btn = new JButton(s.name);
            btn.setPreferredSize(new Dimension(120, 50));

            btn.addActionListener(e -> {
                selectedSoldier = s;
                tryApplyItem();
            });

            panel.add(btn);
        }

        return panel;
    }

    /* ================= APPLY ================= */

    private void tryApplyItem() {
        if (selectedItem == null || selectedSoldier == null) {
            infoLabel.setText("Select item and soldier");
            return;
        }

        if (!inventory.consume(selectedItem)) {
            infoLabel.setText("No " + selectedItem.displayName() + " left!");
            return;
        }

        selectedItem.use(selectedSoldier);

        // 🔄 Update UI
        updateItemButton(itemButtons.get(selectedItem), selectedItem);

        infoLabel.setText(
                "Used " + selectedItem.displayName() + " on " + selectedSoldier.name
        );

        selectedItem = null;
        selectedSoldier = null;
    }


    /* ================= BOTTOM ================= */

    private JPanel buildBottom() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(infoLabel, BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());

        panel.add(close, BorderLayout.EAST);
        return panel;
    }

}
