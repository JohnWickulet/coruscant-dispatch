import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SoldierCardsPanel extends JPanel {

    public List<SoldierCard> cards = new ArrayList<>();
    private final Squad squad;

    public SoldierCardsPanel(Squad squad) {
        this.squad=squad;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (Soldier s : squad.soldiers) {
            SoldierCard card = new SoldierCard(s);
            card.setAlignmentX(Component.CENTER_ALIGNMENT);
            cards.add(card);
            add(card);
            add(Box.createVerticalStrut(10)); // spacing between cards
        }
    }
    public void rebuildCards() {
        removeAll();
        for (Soldier s : squad.soldiers) {
            add(new SoldierCard(s));
        }
        revalidate();
        repaint();
    }
}
