import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GalaxyMapPanel extends JPanel {

    private final List<EventCard> eventCards = new ArrayList<>();
    public Game game;
    private Image backgroundImage;

    // Camera State
    private double zoomFactor = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;
    private Point mousePt;

    public GalaxyMapPanel(Game game) {
        this.game = game;
        setLayout(null);
        setBackground(Color.BLACK);

        // Improved Image Loading
        try {
            java.net.URL imgURL = getClass().getResource("/portraits/coruscant.png");
            if (imgURL != null) {
                this.backgroundImage = new ImageIcon(imgURL).getImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading background: " + e.getMessage());
        }

        setupControls();
    }

    private void setupControls() {
        // --- ZOOM LOGIC ---
        addMouseWheelListener(e -> {
            double oldZoom = zoomFactor;
            if (e.getWheelRotation() < 0) zoomFactor *= 1.1; // Zoom In
            else zoomFactor /= 1.1; // Zoom Out

            // Constraints
            zoomFactor = Math.max(0.3, Math.min(zoomFactor, 3.0));

            // Zoom towards mouse cursor
            double zoomDiv = zoomFactor / oldZoom;
            offsetX = e.getX() - zoomDiv * (e.getX() - offsetX);
            offsetY = e.getY() - zoomDiv * (e.getY() - offsetY);

            updateComponentPositions();
            repaint();
        });

        // --- PANNING LOGIC ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePt = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - mousePt.x;
                int dy = e.getY() - mousePt.y;
                offsetX += dx;
                offsetY += dy;
                mousePt = e.getPoint();

                updateComponentPositions();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (backgroundImage != null) {
            // Apply transformation for the background image
            AffineTransform at = new AffineTransform();
            at.translate(offsetX, offsetY);
            at.scale(zoomFactor, zoomFactor);
            g2.drawImage(backgroundImage, at, this);
        }
    }

    public void updateComponentPositions() {
        for (EventCard card : eventCards) {
            // ScreenPos = (WorldPos * Zoom) + Offset
            int nx = (int) (card.event.x * zoomFactor + offsetX);
            int ny = (int) (card.event.y * zoomFactor + offsetY);

            // Adjust to center the card on the coordinate
            card.setLocation(nx - card.getWidth()/2, ny - card.getHeight()/2);
        }
    }

    public void addEvent(EventCard card) {
        eventCards.add(card);
        add(card);

        // Use initial position
        int nx = (int) (card.event.x * zoomFactor + offsetX);
        int ny = (int) (card.event.y * zoomFactor + offsetY);
        card.setLocation(nx, ny);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleEventClick(card);
            }
        });
        repaint();
    }

    private void handleEventClick(EventCard card) {
        Event evt = card.event;
        if (evt.state == Event.State.AVAILABLE) {
            GameUI parent = (GameUI) SwingUtilities.getWindowAncestor(this);
            parent.openDeploymentDialog(evt);
        } else if (evt.state == Event.State.RESOLVED) {
            evt.resolve(game);

            String title = evt.isSuccess() ? "MISSION SUCCESS" : "MISSION FAILURE";
            String message = "Operation: " + evt.name + "\n" + (evt.isSuccess() ? "Success!" : "Failed!");
            JOptionPane.showMessageDialog(this, message, title,
                    evt.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            removeEvent(card);
            game.getActiveEvents().remove(evt);
        }
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
        updateComponentPositions(); // Keep cards synced during logic ticks
        repaint();
    }

    private void removeEvent(EventCard card) {
        eventCards.remove(card);
        remove(card);
        revalidate();
        repaint();
    }

    public void syncWithGame() {
        this.removeAll();
        this.eventCards.clear();
        for (Event evt : game.getActiveEvents()) {
            EventCard card = new EventCard(evt);
            card.setSize(card.getPreferredSize());
            addEvent(card);
        }
        updateComponentPositions();
        revalidate();
        repaint();
    }
}