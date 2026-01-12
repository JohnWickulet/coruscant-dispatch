import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SoldierSelectDialog extends JDialog {

    private Soldier selected = null;

    public static Soldier chooseSoldier(JFrame parent, Squad squad) {
        SoldierSelectDialog dialog =
                new SoldierSelectDialog(parent, squad);
        return dialog.selected;
    }

    private SoldierSelectDialog(JFrame parent, Squad squad) {
        super(parent, "Select Soldier", true);

        setSize(520, 380);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        List<SoldierCard> cards = new ArrayList<>();
        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        for (Soldier s : squad.soldiers) {
            if (s.health <= 0) continue;

            SoldierCard card = new SoldierCard(s);
            cards.add(card);

            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    for (SoldierCard c : cards)
                        if (c != card && c.isSelected())
                            c.toggle();

                    card.toggle();
                }
            });

            grid.add(card);
        }

        add(new JScrollPane(grid), BorderLayout.CENTER);
        add(buildBottom(cards), BorderLayout.SOUTH);

        setVisible(true);
    }

    /* ================= BOTTOM ================= */

    private JPanel buildBottom(List<SoldierCard> cards) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton select = new JButton("Select");
        JButton cancel = new JButton("Cancel");

        select.addActionListener(e -> {
            for (SoldierCard c : cards)
                if (c.isSelected())
                    selected = c.getSoldier();

            dispose();
        });

        cancel.addActionListener(e -> dispose());

        p.add(select);
        p.add(cancel);
        return p;
    }
}
