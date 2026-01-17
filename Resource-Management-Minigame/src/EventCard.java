import javax.swing.*;
import java.awt.*;

public class EventCard extends JComponent {

    public final Event event;
    private double visualProgress = 1.0;
    private double targetProgress = 1.0;
    private double flashTimer = 0;
    private boolean ok=true;

    public EventCard(Event event) {
        this.event = event;
        setSize(64, 64);
        setPreferredSize(new Dimension(64, 64));
        setOpaque(false);
    }

    public void tick(double dt) {
        event.tick(dt);
        flashTimer += dt;

        switch (event.state) {
            // For AVAILABLE, we want the ring to go from 1.0 down to 0.0
            case AVAILABLE -> targetProgress = event.timeDeploy > 0 ? event.timeLeft / event.timeDeploy : 0;
            // For IN_PROGRESS, target shows completion progress (0 up to 1)
            case IN_PROGRESS -> targetProgress = event.duration > 0 ? 1 - (event.timeLeft / event.duration) : 1;
            case RESOLVED, EXPIRED -> targetProgress = 1;
        }

        // Smooth interpolation for the visual ring
        visualProgress += (targetProgress - visualProgress) * Math.min(1, dt * 8);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 8;
        int x = 4, y = 4;

        // 1. Draw Interior (Solid White)
        g2.setColor(Color.BLACK);
        g2.fillOval(x, y, size, size);

        // 2. Draw Blinking Effect (ONLY when AVAILABLE and < 25% time)
        if (event.state == Event.State.AVAILABLE && targetProgress < 0.25) {
            float pulse = (float) (Math.sin(flashTimer * 15) * 0.5 + 0.5);
            g2.setColor(new Color(255, 0, 0, (int)(pulse * 120)));
            g2.fillOval(x, y, size, size);
            if(ok){
                SoundManager.playSound("event_expire.wav");
                ok=false;
            }
        }

        // 3. Determine Colors and Symbols
        Color ringColor;
        String symbol;
        switch (event.state) {
            case AVAILABLE -> { ringColor = new Color(200, 50, 50); symbol = "!"; }
            case IN_PROGRESS -> { ringColor = new Color(230, 180, 40); symbol = "..."; }
            case RESOLVED -> { ringColor = new Color(60, 190, 90); symbol = "√"; }
            default -> { ringColor = Color.GRAY; symbol = "X"; }
        }

        // 4. Draw Static Background Ring
        g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(50, 50, 50, 80));
        g2.drawOval(x, y, size, size);

        // 5. Draw Depleting Progress Ring
        g2.setColor(ringColor);
        // Start at 90 degrees (Top), draw arc based on visualProgress
        int arcAngle = (int) (360 * visualProgress);
        g2.drawArc(x, y, size, size, 90, arcAngle);

        // 6. Draw Interior Text (Black)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (getWidth() - fm.stringWidth(symbol)) / 2;
        int ty = (getHeight() + fm.getAscent()) / 2 - 4;

        g2.setColor(Color.WHITE);
        g2.drawString(symbol, tx, ty);
    }
}