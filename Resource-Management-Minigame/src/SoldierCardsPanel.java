import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SoldierCardsPanel extends JPanel {

    public List<SoldierCard> cards = new ArrayList<>();
    private final Squad squad;

    public SoldierCardsPanel(Squad squad) {
        this.squad = squad;
        setLayout(new GridLayout(4, 2, 10, 10));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rebuildCards();
    }

    public void rebuildCards() {
        removeAll();
        cards.clear();
        for (Soldier s : squad.soldiers) {
            SoldierCard card = new SoldierCard(s, 150, 180);
            cards.add(card);
            add(card);
        }
        for (int i = squad.soldiers.size(); i < 8; i++) {
            add(new JPanel() {{ setOpaque(false); }});
        }
        revalidate();
        repaint();
    }
}