import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DeploymentDialog extends JDialog {

    private final GameUI ui;
    private final Squad squad;
    private final Event event;

    private final CombinedStatPentagonPanel pentagonPanel = new CombinedStatPentagonPanel();
    private final JPanel statCluesPanel = new JPanel();
    private final JPanel troopCardsPanel = new JPanel();
    private final List<Soldier> selectedTroops = new ArrayList<>();

    private final int maxSelectable;
    private final int cardWidth = 120;
    private final int cardHeight = 150;
    private final int gap = 10;

    public DeploymentDialog(GameUI ui, Squad squad, Event event) {
        super(ui, "Deploy Troopers", true);
        this.ui = ui;
        this.squad = squad;
        this.event = event;
        this.maxSelectable = event.maxDeploy;

        setSize(900, 600);
        setLocationRelativeTo(ui);
        setLayout(new BorderLayout(10, 10));

        add(buildMainPanel(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // TOP SECTION: Intel and Pentagon
        JPanel briefingPanel = buildBriefingPanel();
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(pentagonPanel, BorderLayout.CENTER);
        centerPanel.setBorder(BorderFactory.createTitledBorder("Performance Projection"));

        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.add(briefingPanel, BorderLayout.WEST);
        topRow.add(centerPanel, BorderLayout.CENTER);
        main.add(topRow, BorderLayout.CENTER);

        // BOTTOM SECTION: Centered Troop Cards
        // FlowLayout.CENTER ensures they stay together in the middle
        troopCardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, gap, 5));
        troopCardsPanel.setOpaque(false);

        for (Soldier s : squad.soldiers) {
            SoldierCard card = new SoldierCard(s, 120, 150);
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    toggleTroopSelection(card);
                }
            });
            troopCardsPanel.add(card);
        }

        int preferredWidth = (cardWidth + gap) * squad.soldiers.size() + gap;
        troopCardsPanel.setPreferredSize(new Dimension(preferredWidth, cardHeight + 10));

        JScrollPane scroll = new JScrollPane(troopCardsPanel);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Height allows for card + scrollbar space
        scroll.setPreferredSize(new Dimension(850, cardHeight + 35));
        scroll.setBorder(BorderFactory.createTitledBorder("Select Personnel"));

        main.add(scroll, BorderLayout.SOUTH);
        return main;
    }

    private JPanel buildBriefingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(280, 300));
        panel.setBorder(BorderFactory.createTitledBorder("Mission Intel"));

        JLabel nameLabel = new JLabel(event.name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel desc = new JLabel("<html><body style='width: 200px;'>" + event.description + "</body></html>");
        JLabel maxLabel = new JLabel("Deployment Limit: " + maxSelectable);

        statCluesPanel.setLayout(new BoxLayout(statCluesPanel, BoxLayout.Y_AXIS));
        updateStatClues();

        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(desc);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(10));
        panel.add(maxLabel);
        panel.add(statCluesPanel);

        return panel;
    }

    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton deploy = new JButton("DEPLOY");
        JButton cancel = new JButton("CANCEL");
        deploy.addActionListener(e -> deploy());
        cancel.addActionListener(e -> dispose());
        bottom.add(deploy);
        bottom.add(cancel);
        return bottom;
    }

    private void toggleTroopSelection(SoldierCard card) {
        Soldier s = card.getSoldier();
        if (!s.isAvailable()) { SoundManager.error(); return; }

        if (!selectedTroops.contains(s)) {
            if (selectedTroops.size() >= maxSelectable) { SoundManager.error(); return; }
            selectedTroops.add(s);
            card.toggle();
            SoundManager.selectTroop();
        } else {
            selectedTroops.remove(s);
            card.toggle();
            SoundManager.selectTroop();
        }
        pentagonPanel.updateStats(selectedTroops);
        updateStatClues();
    }

    private void updateStatClues() {
        statCluesPanel.removeAll();
        for (String s : event.statPref) {
            statCluesPanel.add(new JLabel("• " + s));
        }
        statCluesPanel.revalidate();
        statCluesPanel.repaint();
    }

    private void deploy() {
        if (selectedTroops.isEmpty()) return;
        for (Soldier s : selectedTroops) s.startMission();
        event.deploy(selectedTroops);
        dispose();
    }
}