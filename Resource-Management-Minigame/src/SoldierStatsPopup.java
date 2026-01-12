import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SoldierStatsPopup extends JWindow {

    public SoldierStatsPopup(Window owner, Soldier s) {
        super(owner);

        // Main panel with border
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new LineBorder(Color.DARK_GRAY, 2));
        mainPanel.setBackground(new Color(255, 255, 240));

        // Pentagon stat panel
        StatPentagonPanel pentagonPanel = new StatPentagonPanel(s);
        mainPanel.add(pentagonPanel, BorderLayout.CENTER);

        // Optional: text labels below the pentagon

        add(mainPanel);
        pack();
        setLocationRelativeTo(owner);
    }
}
