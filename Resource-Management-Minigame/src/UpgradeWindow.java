import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class UpgradeWindow extends GameWindow implements UpgradeListener {

    private GameUI gameUi;
    private final Squad squad;
    private Soldier activeSoldier;
    private JPanel centerPanel;
    private JPanel selectorPanel;

    // Theme Colors
    private final Color BG_COLOR = new Color(245, 245, 245);
    private final Color GLOW_BLUE = new Color(0, 120, 215);
    private final Color GLOW_UPGRADE = new Color(246, 208, 63); // Orange for Level Up
    private final Color GLOW_RED = new Color(200, 0, 0);

    public UpgradeWindow(GameUI gameUI, Squad squad) {
        super(gameUI, "Upgrade Menu", 950, 600);
        this.gameUi = gameUI;
        this.squad = squad;

        if (squad.soldiers.isEmpty()) {
            dispose();
            return;
        }

        this.activeSoldier = squad.soldiers.get(0);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(10, 10));

        centerPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        refreshCenter();
        selectorPanel = createSelectorPanel();

        add(centerPanel, BorderLayout.CENTER);
        add(selectorPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void refreshCenter() {
        centerPanel.removeAll();

        StatPentagonPanel statPanel = new StatPentagonPanel(activeSoldier);
        UpgradePanel upgradePanel = new UpgradePanel(activeSoldier, this);
        upgradePanel.setOpaque(false);

        // LEFT COLUMN setup
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setOpaque(false);

        // Soldier Name
        JLabel nameLabel = new JLabel(activeSoldier.name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        SoldierCard portraitCard = new SoldierCard(activeSoldier, 200, 250);
        portraitCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel promoPanel = new JPanel();
        promoPanel.setLayout(new BoxLayout(promoPanel, BoxLayout.Y_AXIS));
        promoPanel.setOpaque(false);

        promoPanel.setPreferredSize(new Dimension(250, 120));
        promoPanel.setMinimumSize(new Dimension(250, 120));
        promoPanel.setMaximumSize(new Dimension(250, 120));
        promoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        List<TroopClass> possiblePromotions = UpgradeRegistry.getAvailablePromotions(activeSoldier.troopClass, activeSoldier.level);

        if (!possiblePromotions.isEmpty() && (activeSoldier.state== Soldier.State.AVAILABLE || activeSoldier.state== Soldier.State.RESTING)){
            JLabel promoTitle = new JLabel("AVAILABLE PROMOTIONS");
            promoTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
            promoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            promoPanel.add(promoTitle);
            promoPanel.add(Box.createVerticalStrut(5));

            for (TroopClass tc : possiblePromotions) {
                JButton b = createGlowButton("Promote to " + tc.toString().replace("_", " "), GLOW_RED);
                b.setAlignmentX(Component.CENTER_ALIGNMENT);
                b.setMaximumSize(new Dimension(220, 40));

                Soldier ghostPreview = activeSoldier.promoteTo(tc);
                b.addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { statPanel.setPreview(ghostPreview); }
                    @Override public void mouseExited(MouseEvent e) { statPanel.setPreview(null); }
                });

                b.addActionListener(e -> {
                    SoundManager.upgradeBig();
                    Soldier promoted = activeSoldier.promoteTo(tc);
                    squad.replaceSoldier(activeSoldier, promoted);
                    activeSoldier = promoted;
                    listenerRefresh();
                });

                promoPanel.add(b);
                promoPanel.add(Box.createVerticalStrut(5));
            }
        } else {
            JLabel status = new JLabel(activeSoldier.isAvailable() ? "NO PROMOTION AVAILABLE" : "UNIT ON MISSION");
            status.setForeground(Color.GRAY);
            status.setAlignmentX(Component.CENTER_ALIGNMENT);
            promoPanel.add(Box.createVerticalGlue());
            promoPanel.add(status);
            promoPanel.add(Box.createVerticalGlue());
        }

        leftColumn.add(nameLabel);
        leftColumn.add(Box.createVerticalStrut(10));
        leftColumn.add(portraitCard);
        leftColumn.add(Box.createVerticalStrut(10));
        leftColumn.add(promoPanel);

        centerPanel.add(leftColumn);
        centerPanel.add(upgradePanel);
        centerPanel.add(statPanel);

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private JPanel createSelectorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(230, 230, 230));

        for (Soldier s : squad.soldiers) {
            JButton btn = new JButton(s.name);
            btn.setPreferredSize(new Dimension(110, 50));
            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);

            //GLOW LOGIC
            if (s == activeSoldier) {
                //Active soldier
                btn.setBorder(BorderFactory.createLineBorder(GLOW_BLUE, 3));
            } else if (s.exp >= s.getExpToLevel()) {
                //Soldier can lvl up
                btn.setBorder(BorderFactory.createLineBorder(GLOW_UPGRADE, 2));
            } else {
                btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }

            btn.addActionListener(e -> {
                activeSoldier = s;
                refreshCenter();
                refreshSelector();
                SoundManager.select();
            });
            panel.add(btn);
        }
        return panel;
    }

    private JButton createGlowButton(String text, Color glowColor) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(BorderFactory.createLineBorder(glowColor, 2));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        });
        return btn;
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
        if (gameUi.getSoldierPanel() != null) gameUi.getSoldierPanel().rebuildCards();
    }

    @Override public void onUpgrade() { listenerRefresh(); }
}