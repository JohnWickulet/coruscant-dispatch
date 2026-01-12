import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GalaxyMapPanel extends JPanel {

    private final List<EventCard> eventCards = new ArrayList<>();
    public Game game;

    public GalaxyMapPanel(Game game) {
        this.game=game;
        setLayout(null);
        setBackground(new Color(5, 5, 30));
    }

    public void addEvent(EventCard card) {
        card.setLocation((int)(Math.random()*600), (int)(Math.random()*300));
        eventCards.add(card);
        add(card);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Event evt = card.event;
                if (evt.state == Event.State.AVAILABLE) {
                    GameUI parent = (GameUI) SwingUtilities.getWindowAncestor(GalaxyMapPanel.this);
                    parent.openDeploymentDialog(evt);
                } else if (evt.state == Event.State.RESOLVED) {
                    evt.resolve(game);
                    removeEvent(card);
                    game.getActiveEvents().remove(card.event);
                }
            }
        });

        repaint();
    }

    public void tick(double dt) {
        Iterator<EventCard> it = eventCards.iterator();
        while (it.hasNext()) {
            EventCard card = it.next();
            card.tick(dt);
            if (card.event.remove) {
                it.remove();
                remove(card);
                game.getActiveEvents().remove(card.event);
            }
        }
        revalidate();
        repaint();
    }

    private void removeEvent(EventCard card) {
        eventCards.remove(card);
        remove(card);
        revalidate();
        repaint();
    }
    public void syncWithGame() {
        // Clear existing UI components
        this.removeAll();
        this.eventCards.clear();

        // Create a new card for every event currently in the game data
        for (Event evt : game.getActiveEvents()) {
            EventCard card = new EventCard(evt);

            // Use random positioning or save/load coordinates if you want them to stay put
            card.setLocation((int)(Math.random() * 600), (int)(Math.random() * 300));
            card.setSize(card.getPreferredSize()); // Ensure card has size

            eventCards.add(card);
            add(card);

            // Re-attach the click listener
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (evt.state == Event.State.AVAILABLE) {
                        GameUI parent = (GameUI) SwingUtilities.getWindowAncestor(GalaxyMapPanel.this);
                        parent.openDeploymentDialog(evt);
                    } else if (evt.state == Event.State.RESOLVED) {
                        evt.resolve(game);
                        removeEvent(card);
                        // No need to remove from game here, tick() or resolve() should handle it
                    }
                }
            });
        }
        revalidate();
        repaint();
    }
}
