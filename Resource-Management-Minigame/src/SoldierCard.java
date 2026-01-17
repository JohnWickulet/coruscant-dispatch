import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SoldierCard extends JPanel {

    private final Soldier soldier;
    private boolean selected = false;
    private double flowProgress = 0.0; // 0.0 -> 1.0 for resting bar

    public SoldierCard(Soldier soldier) {
        setOpaque(false);
        this.soldier = soldier;
        setPreferredSize(new Dimension(140, 160));
        setLayout(new BorderLayout());
        setBorder(new LineBorder(Color.GRAY, 2));
        setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(soldier.name, SwingConstants.CENTER);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        add(nameLabel, BorderLayout.SOUTH);

        JLabel imageLabel = new JLabel(soldier.portrait, SwingConstants.CENTER);
        Image img = soldier.portrait.getImage();
        Image scaled = img.getScaledInstance(150, 200, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
        add(imageLabel, BorderLayout.CENTER);

        if (soldier.state == Soldier.State.RESTING && soldier.rest_time > 0) {
            flowProgress = 1.0 - (double) soldier.rest_time_remaining / soldier.rest_time;
            repaint();
        }
    }

    public Soldier getSoldier() { return soldier; }
    public boolean isSelected() { return selected; }

    public void toggle() {
        selected = !selected;
        setBorder(new LineBorder(selected ? Color.GREEN : Color.GRAY, 3));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        /* ===== BASE STATE COLOR ===== */
        switch (soldier.state) {
            case DEPLOYED -> {
                g2.setColor(new Color(163, 157, 157));
                g2.fillRect(0, 0, w, h);
            }
            case RESTING -> {
                g2.setColor(new Color(73, 145, 193));
                g2.fillRect(0, 0, w, h);
            }
            case AVAILABLE -> {
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, w, h);
            }
            case DOWNED -> {
                g2.setColor(new Color(84, 91, 96, 255));
                g2.fillRect(0, 0, w, h);
            }
        }

        /* ===== RESTING FLOW OVERLAY ===== */
        if (soldier.state == Soldier.State.RESTING && soldier.rest_time > 0) {
            double progress =
                    1.0 - (double) soldier. rest_time_remaining / soldier.rest_time;

            int barWidth = (int) (w * progress);

            GradientPaint gp = new GradientPaint(
                    w - barWidth, 0, new Color(255, 255, 255, 120),
                    w, 0, new Color(180, 210, 255, 40)
            );

            g2.setPaint(gp);
            g2.fillRect(w - barWidth, 0, barWidth, h);
        }

        /* ===== BORDER ===== */
        g2.setColor(selected ? Color.GREEN : Color.GRAY);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(1, 1, w - 3, h - 3);

        /* ===== STATE TEXT ===== */
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));

        String stateText = switch (soldier.state) {
            case DEPLOYED -> "Deployed";
            case RESTING -> "Resting";
            case DOWNED -> "Downed";
            default -> "";
        };

        if (!stateText.isEmpty()) {
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(stateText)) / 2;
            g2.drawString(stateText, x, 16);
        }

        g2.dispose();
        super.paintComponent(g); // paints child components LAST
    }

}
