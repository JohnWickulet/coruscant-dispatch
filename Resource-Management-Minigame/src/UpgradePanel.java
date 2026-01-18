import javax.swing.*;
import java.awt.*;

public class UpgradePanel extends JPanel {

    private final Soldier soldier;
    private final UpgradeListener listener;

    public UpgradePanel(Soldier soldier, UpgradeListener listener) {
        this.soldier = soldier;
        this.listener = listener;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel expLabel = new JLabel(
                "EXP: " + soldier.exp + " / " + soldier.getExpToLevel(),
                SwingConstants.CENTER
        );
        expLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(expLabel, BorderLayout.NORTH);

        // Panel with stat rows
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        addStatRow(statsPanel, "Strength", () -> soldier.strength);
        addStatRow(statsPanel, "Mobility", () -> soldier.mobility);
        addStatRow(statsPanel, "Intelligence", () -> soldier.intelligence);
        addStatRow(statsPanel, "Stealth", () -> soldier.stealth);
        addStatRow(statsPanel, "Charisma", () -> soldier.charisma);

        add(statsPanel, BorderLayout.CENTER);
    }

    private void addStatRow(JPanel parent, String statName, StatGetter getter) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel nameLabel = new JLabel(statName + ":");
        nameLabel.setPreferredSize(new Dimension(80, 25));

        JLabel valueLabel = new JLabel(String.valueOf(getter.get()));
        valueLabel.setPreferredSize(new Dimension(30, 25));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton minusBtn = new JButton("-");
        JButton plusBtn = new JButton("+");

        // Initialize button states
        minusBtn.setEnabled(false);
        plusBtn.setEnabled(soldier.exp >= soldier.getExpToLevel());

        // Increase stat
        plusBtn.addActionListener(e -> {
            if (soldier.exp < soldier.getExpToLevel()) return;
            SoundManager.upgradeSmall();
            soldier.exp -= soldier.getExpToLevel();
            soldier.increaseStat(statName);
            valueLabel.setText(String.valueOf(getter.get()));
            plusBtn.setEnabled(soldier.exp >= soldier.getExpToLevel());
            listener.onUpgrade();
        });
        row.add(nameLabel);
        row.add(valueLabel);
        row.add(minusBtn);
        row.add(plusBtn);
        parent.add(row);
    }

    @FunctionalInterface
    interface StatGetter {
        int get();
    }
}
