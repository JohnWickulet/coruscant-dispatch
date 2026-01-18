import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.EnumMap;
import java.util.Map;

public class InventoryWindow extends GameWindow {

    private final Inventory inventory;
    private final Squad squad;
    private final GameUI ui;
    private final Map<ItemType, JButton> itemButtons = new EnumMap<>(ItemType.class);
    private ItemType selectedItem = null;
    private final JLabel infoLabel = new JLabel("SELECT AN ITEM", SwingConstants.CENTER);
    private final Color BG_WHITE = new Color(240, 240, 240);
    private final Color CARD_WHITE = Color.WHITE;
    private final Color GLOW_HEAL = new Color(0, 180, 70); // Strong green
    private final Color GLOW_SELECT = new Color(0, 120, 215); // Strong blue

    public InventoryWindow(GameUI ui, Inventory inventory, Squad squad) {
        super(ui, "Inventory Menu", 750, 650);
        this.ui = ui;
        this.inventory = inventory;
        this.squad = squad;

        getContentPane().setBackground(BG_WHITE);
        setLayout(new BorderLayout(15, 15));
        ((JPanel)getContentPane()).setOpaque(false);
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildItemsPanel(), BorderLayout.NORTH);

        JPanel soldierGrid = buildSoldiersPanel();
        soldierGrid.setOpaque(false);
        JScrollPane scroll = new JScrollPane(soldierGrid);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        add(buildBottom(), BorderLayout.SOUTH);
        setVisible(true);
    }

    private JPanel buildItemsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, ItemType.values().length, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY), "RESOURCES", 0, 0, null, Color.DARK_GRAY));

        for (ItemType type : ItemType.values()) {
            JButton btn = createGlowButton(type);
            itemButtons.put(type, btn);
            panel.add(btn);
        }
        return panel;
    }

    private JButton createGlowButton(ItemType type) {
        JButton btn = new JButton();
        btn.setBackground(CARD_WHITE);
        btn.setFocusPainted(false);
        updateItemButton(btn, type);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if(btn.isEnabled()) btn.setBorder(BorderFactory.createLineBorder(GLOW_SELECT, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if(selectedItem != type) btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        });

        btn.addActionListener(e -> {
            selectedItem = type;
            highlightSelectedItem(type);
            infoLabel.setText("READY TO USE: " + type.displayName());
            SoundManager.select();
        });
        return btn;
    }

    private void updateItemButton(JButton btn, ItemType type) {
        int qty = inventory.get(type);
        btn.setEnabled(qty > 0);
        btn.setText("<html><center><b>" + type.displayName() + "</b><br>" +
                "<font color='#0078D7'>Qty: " + qty + "</font></center></html>");
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    private void highlightSelectedItem(ItemType selected) {
        itemButtons.forEach((type, btn) -> {
            btn.setBorder(BorderFactory.createLineBorder(type == selected ? GLOW_SELECT : Color.LIGHT_GRAY, 2));
            btn.setBackground(type == selected ? new Color(240, 250, 255) : CARD_WHITE);
        });
    }

    private JPanel buildSoldiersPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setOpaque(false);
        int count = 0;
        for (Soldier s : squad.soldiers) {
            if (count >= 8) break;
            panel.add(createSoldierCard(s));
            count++;
        }
        for (int i = count; i < 8; i++) {
            panel.add(new JPanel() {{ setOpaque(false); }});
        }
        return panel;
    }

    private JPanel createSoldierCard(Soldier s) {
        SoldierCard card = new SoldierCard(s, 150, 180); // Maintains large size
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(GLOW_HEAL, 2));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(null);
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                tryApplyItem(s);
                card.repaint();
            }
        });
        return card;
    }

    // Inside your Item usage logic (InventoryWindow or ItemType)
    private void tryApplyItem(Soldier s) {
        if (selectedItem == null) return;

        boolean success = selectedItem.use(s);
        if (success) {
            inventory.consume(selectedItem);
            SoundManager.heal();
            updateItemButton(itemButtons.get(selectedItem), selectedItem);
            ui.getSoldierPanel().repaint();
            infoLabel.setText("Used " + selectedItem.displayName() + " on " + s.name);
            infoLabel.setForeground(Color.BLACK);
        } else {
            SoundManager.error();
            infoLabel.setText("Cannot use " + selectedItem.displayName() + " on " + s.name);
            infoLabel.setForeground(Color.RED);
        }
    }

    private JPanel buildBottom() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JButton close = new JButton("Close");
        close.addActionListener(e -> {
            SoundManager.select();
            dispose();
        });
        p.add(infoLabel, BorderLayout.WEST);
        p.add(close, BorderLayout.CENTER);
        return p;
    }
}