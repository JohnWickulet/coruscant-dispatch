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
                soldier.name + "\n" + soldier.troopClass,
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
        // Left: Soldier Info + Promotion Button
        JPanel infoWithPromotion = new JPanel();
        infoWithPromotion.setLayout(new BorderLayout(10, 10));
        JPanel portraitPanel = new JPanel(new BorderLayout(5,5));

        JLabel nameLabel = new JLabel(
                activeSoldier.name + "\n" + activeSoldier.troopClass,
                SwingConstants.CENTER
        );
        portraitContainer = new JPanel(new BorderLayout());
        portraitContainer.add(buildPortraitComponent(activeSoldier), BorderLayout.CENTER);

        infoWithPromotion.add(portraitContainer, BorderLayout.CENTER);


        // Promotion button
        JButton promoteBtn = new JButton();
        promoteBtn.setFont(promoteBtn.getFont().deriveFont(Font.BOLD, 12f));
        promoteBtn.setBackground(new Color(80, 80, 120));
        promoteBtn.setForeground(Color.WHITE);

        TroopClass nextClass = UpgradeRegistry.getNextTroopClass(activeSoldier.troopClass, activeSoldier.level);
        if (nextClass != null && activeSoldier.canPromote(nextClass) && activeSoldier.isAvailable()) {
            promoteBtn.setText("Promote to " + nextClass);
            promoteBtn.setEnabled(true);
            promoteBtn.addActionListener(e -> {
                Soldier promoted = activeSoldier.promoteTo(nextClass);
                squad.replaceSoldier(activeSoldier, promoted);
                activeSoldier = promoted;

                //Update portrait immediately
                portraitContainer.removeAll();
                portraitContainer.add(buildPortraitComponent(activeSoldier), BorderLayout.CENTER);
                portraitContainer.revalidate();
                portraitContainer.repaint();

                //Update rest of UI
                listenerRefresh();
                gameUi.getSoldierPanel().rebuildCards();
            });

        } else {
            if(!activeSoldier.isAvailable()){
                promoteBtn.setText("Busy");
            }
            else {
                promoteBtn.setText("No Promotion Available");
            }
            promoteBtn.setEnabled(false);
        }

        infoWithPromotion.add(promoteBtn, BorderLayout.SOUTH);

        // Center: UpgradePanel
        UpgradePanel upgradePanel = new UpgradePanel(activeSoldier, this);
        if(!activeSoldier.isAvailable()){
            upgradePanel.setEnabled(false);
        }
        // Right: StatPentagon
        StatPentagonPanel statPanel = new StatPentagonPanel(activeSoldier);

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
