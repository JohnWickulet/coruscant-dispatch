import javax.swing.*;
import java.awt.*;

public class EventCard extends JComponent {

    public final Event event;
    private double visualProgress = 1.0;
    private double targetProgress = 1.0;

    public EventCard(Event event) {
        this.event = event;
        setSize(64, 64);
        setPreferredSize(new Dimension(64, 64));
        setOpaque(false);
    }

    public void tick(double dt) {
        event.tick(dt);

        switch (event.state) {
            case AVAILABLE -> targetProgress = event.timeDeploy > 0 ?  event.timeLeft / event.timeDeploy : 0;
            case IN_PROGRESS -> targetProgress = event.duration > 0 ? 1 - event.timeLeft / event.duration : 0;
            case RESOLVED, EXPIRED -> targetProgress = 1;
        }
        visualProgress += (targetProgress-visualProgress)*Math.min(1,dt*8);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 8;
        int x = 4, y = 4;

        Color ringColor;
        String symbol;
        switch (event.state) {
            case AVAILABLE -> { ringColor = new Color(200, 50, 50); symbol = "!"; }
            case IN_PROGRESS -> { ringColor = new Color(230, 180, 40); symbol = "!"; }
            case RESOLVED -> { ringColor = new Color(60, 190, 90); symbol = "√"; }
            default -> { ringColor = Color.GRAY; symbol = "X"; }
        }
        // background ring (always visible)
        g2.setStroke(new BasicStroke(6));
        g2.setColor(new Color(80, 80, 80, 120));
        g2.drawOval(x, y, size, size);

        // progress ring (fills / empties)
        int arcAngle;
        g2.setColor(ringColor);
        if(event.state== Event.State.AVAILABLE) arcAngle = (int) (360 * visualProgress);
        else arcAngle = (int) (360 * visualProgress);
        g2.drawArc(x, y, size, size, 90, arcAngle);

        g2.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        int tx = getWidth()/2 - fm.stringWidth(symbol)/2;
        int ty = getHeight()/2 + fm.getAscent()/2 - 3;

        g2.setColor(Color.WHITE);
        g2.drawString(symbol, tx, ty);
    }
}
