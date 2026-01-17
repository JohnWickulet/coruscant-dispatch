import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UpgradeWindow extends GameWindow implements UpgradeListener {

    private GameUI gameUi;
    private final Squad squad;
    private Soldier activeSoldier;
    private JPanel centerPanel;
    private JPanel selectorPanel;
    private JPanel portraitContainer;

    public UpgradeWindow(GameUI gameUI, Squad squad) {
        this.gameUi=gameUI;
        super(gameUI, "Upgrade Troopers", 900, 550);
        this.squad = squad;

        if (squad.soldiers.isEmpty()) {
            JOptionPane.showMessageDialog(gameUI, "No soldiers available to upgrade!");
            dispose();
            return;
        }

        this.activeSoldier = squad.soldiers.get(0);

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(30, 30, 30));

        centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        refreshCenter();

        selectorPanel = createSelectorPanel();

        add(centerPanel, BorderLayout.CENTER);
        add(selectorPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    private JComponent buildPortraitComponent(Soldier soldier) {
        Image img = soldier.portrait.getImage();
        Image scaled = img.getScaledInstance(180, 240, Image.SCALE_SMOOTH);

        JLabel portraitLabel = new JLabel(new ImageIcon(scaled));
        portraitLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel nameLabel = new JLabel(
                soldier.name,
                SwingConstants.CENTER
        );
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(portraitLabel, BorderLayout.CENTER);

        return panel;
    }


    private void refreshCenter() {
        centerPanel.removeAll();

        // --- LEFT COLUMN: Info & Promotion Buttons ---
        JPanel infoWithPromotion = new JPanel(new BorderLayout(10, 10));
        portraitContainer = new JPanel(new BorderLayout());
        portraitContainer.add(buildPortraitComponent(activeSoldier), BorderLayout.CENTER);
        infoWithPromotion.add(portraitContainer, BorderLayout.CENTER);

        // Vertical stack for all available promotion branches
        JPanel promotionButtonsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        promotionButtonsPanel.setOpaque(false);

        List<TroopClass> possiblePromotions = UpgradeRegistry.getAvailablePromotions(activeSoldier.troopClass, activeSoldier.level);

        // --- RIGHT COLUMN: StatPentagon ---
        StatPentagonPanel statPanel = new StatPentagonPanel(activeSoldier);

        if (!possiblePromotions.isEmpty() && activeSoldier.isAvailable()) {
            for (TroopClass nextClass : possiblePromotions) {
                JButton promoteBtn = new JButton("Promote to " + nextClass);
                promoteBtn.setFont(promoteBtn.getFont().deriveFont(Font.BOLD, 12f));
                promoteBtn.setBackground(Color.BLACK);
                promoteBtn.setForeground(Color.WHITE);

                // Preview logic (Phantom soldier)
                Soldier previewSoldier = activeSoldier.promoteTo(nextClass);

                promoteBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        statPanel.setPreview(previewSoldier);
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        statPanel.setPreview(null);
                    }
                });

                // INSTANT PROMOTION (No confirmation dialog)
                promoteBtn.addActionListener(e -> {
                    Soldier promoted = activeSoldier.promoteTo(nextClass);
                    squad.replaceSoldier(activeSoldier, promoted);
                    activeSoldier = promoted;

                    // Immediate UI Refresh
                    listenerRefresh();
                    if (gameUi.getSoldierPanel() != null) {
                        gameUi.getSoldierPanel().rebuildCards();
                    }
                });

                promotionButtonsPanel.add(promoteBtn);
            }
        } else {
            // Show status if no promotions are possible
            String reason = (!activeSoldier.isAvailable()&&!possiblePromotions.isEmpty()) ? "Troop is busy" : "No promotion available";
            JButton disabledBtn = new JButton(reason);
            disabledBtn.setEnabled(false);
            promotionButtonsPanel.add(disabledBtn);
        }

        infoWithPromotion.add(promotionButtonsPanel, BorderLayout.SOUTH);

        // --- CENTER COLUMN: Upgrade Panel ---
        UpgradePanel upgradePanel = new UpgradePanel(activeSoldier, this);
        if(!activeSoldier.isAvailable()) upgradePanel.setEnabled(false);

        // Final Assembly
        centerPanel.add(infoWithPromotion);
        centerPanel.add(upgradePanel);
        centerPanel.add(statPanel);

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private JPanel createSelectorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.DARK_GRAY));

        for (Soldier s : squad.soldiers) {
            JButton btn = new JButton(s.name);
            btn.setPreferredSize(new Dimension(90, 60));

            if (s.exp >= s.getExpToLevel()) {
                btn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
            }

            btn.addActionListener(e -> {
                activeSoldier = s;
                refreshCenter();
                refreshSelector();
            });

            panel.add(btn);
        }
        return panel;
    }

    private void refreshSelector() {
        remove(selectorPanel);
        selectorPanel = createSelectorPanel();
        add(selectorPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void listenerRefresh() {
        refreshCenter();
        refreshSelector();
    }

    @Override
    public void onUpgrade() {
        listenerRefresh();
    }
}
