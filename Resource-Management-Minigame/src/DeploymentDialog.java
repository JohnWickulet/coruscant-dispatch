import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DeploymentDialog extends JDialog {

    private final GameUI ui;
    private final Squad squad;
    private final Event event;

    private final CombinedStatPentagonPanel pentagonPanel =
            new CombinedStatPentagonPanel();

    private final JPanel statCluesPanel = new JPanel();
    private final JPanel troopCardsPanel = new JPanel();

    private final List<Soldier> selectedTroops = new ArrayList<>();

    private final int maxSelectable;
    private final int cardWidth = 60;
    private final int cardHeight = 80;
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

        this.revalidate();
    }

    /* ================= MAIN PANEL ================= */

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));

        JPanel topRow = new JPanel(new GridLayout(1, 3, 10, 0));

        // LEFT: EVENT INFO
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(event.name);
        name.setFont(name.getFont().deriveFont(Font.BOLD, 18f));
        JLabel desc = new JLabel("<html><i>" + event.description + "</i></html>");
        left.add(name);
        left.add(Box.createVerticalStrut(10));
        left.add(desc);

        // MIDDLE: STATS
        JPanel middle = new JPanel(new BorderLayout());
        middle.add(pentagonPanel, BorderLayout.CENTER);

        // RIGHT: CLUES
        statCluesPanel.setLayout(new BoxLayout(statCluesPanel, BoxLayout.Y_AXIS));
        updateStatClues();

        topRow.add(left);
        topRow.add(middle);
        topRow.add(statCluesPanel);

        main.add(topRow, BorderLayout.CENTER);

        // BOTTOM: TROOP CARDS
        troopCardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, gap, 10));
        int width = maxSelectable * (cardWidth + gap) - gap;
        troopCardsPanel.setPreferredSize(new Dimension(width, cardHeight + 20));

        for (Soldier s : squad.soldiers) {

            SoldierCard card = new SoldierCard(s);
            card.setPreferredSize(new Dimension(cardWidth, cardHeight));

            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    toggleTroopSelection(card);
                }
            });

            troopCardsPanel.add(card);
        }

        JScrollPane scroll = new JScrollPane(troopCardsPanel);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(width, cardHeight + 40));

        main.add(scroll, BorderLayout.SOUTH);

        return main;
    }

    /* ================= BUTTONS ================= */

    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new FlowLayout());

        JButton deploy = new JButton("Deploy");
        JButton cancel = new JButton("Cancel");

        deploy.addActionListener(e -> deploy());
        cancel.addActionListener(e -> dispose());

        bottom.add(deploy);
        bottom.add(cancel);
        return bottom;
    }

    /* ================= SELECTION ================= */

    private void toggleTroopSelection(SoldierCard card) {
        Soldier s = card.getSoldier();
        if (!s.isAvailable()) {
            JOptionPane.showMessageDialog(
                    this,
                    s.name + " is busy or resting and cannot be sent.",
                    "Soldier Unavailable",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (!selectedTroops.contains(s)) {
            if (selectedTroops.size() >= maxSelectable) {
                JOptionPane.showMessageDialog(
                        this,
                        "You can deploy at most " + maxSelectable + " troops."
                );
                return;
            }
            selectedTroops.add(s);
            card.toggle();
        } else {
            selectedTroops.remove(s);
            card.toggle();
        }

        pentagonPanel.updateStats(selectedTroops);
        updateStatClues();
    }

    /* ================= CLUES ================= */

    private void updateStatClues() {
        statCluesPanel.removeAll();
        statCluesPanel.add(new JLabel("<html><b>Mission Requirements</b></html>"));
        for(String s:event.statPref) {
            statCluesPanel.add(new JLabel(s));
        }
        statCluesPanel.revalidate();
        statCluesPanel.repaint();
    }

    /* ================= DEPLOY ================= */

    private void deploy() {
        if (selectedTroops.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one soldier.");
            return;
        }
        for (Soldier s : selectedTroops) {
            s.startMission();
        }
        event.deploy(selectedTroops);
        dispose();
    }
}
