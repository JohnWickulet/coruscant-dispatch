import javax.swing.*;
import java.awt.*;

class CombinedStatPentagonPanel extends JPanel {
    private double[] displayStats = new double[5];
    private int[] targetStats = new int[5];
    private Timer animationTimer;

    private static final int MAX_STAT = 10;
    private static final int ANIM_DURATION = 250; // ms
    private static final int FPS = 60;

    public CombinedStatPentagonPanel() {
        setPreferredSize(new Dimension(260, 260));
        setBackground(Color.WHITE);
    }

    public void updateStats(java.util.List<Soldier> soldiers) {
        int[] summed = new int[5];
        for (Soldier s : soldiers) {
            summed[0] += s.strength;
            summed[1] += s.mobility;
            summed[2] += s.intelligence;
            summed[3] += s.stealth;
            summed[4] += s.charisma;
        }
        for(int i=0;i<5;i++)
            summed[i]=clamp(summed[i]);
        System.arraycopy(summed, 0, targetStats, 0, 5);
        animateToTarget();
    }

    private void animateToTarget() {
        if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();

        final double[] startStats = displayStats.clone();
        final int frames = ANIM_DURATION * FPS / 1000;
        final int[] frame = {0};

        animationTimer = new Timer(1000 / FPS, e -> {
            frame[0]++;
            double t = Math.min(1.0, frame[0] / (double) frames);
            double ease = 1 - Math.pow(1 - t, 3);

            for (int i = 0; i < 5; i++) {
                displayStats[i] = startStats[i] + (targetStats[i] - startStats[i]) * ease;
                displayStats[i] = clamp(displayStats[i]);
            }
            repaint();
            if (t >= 1.0) animationTimer.stop();
        });
        animationTimer.start();
    }
     @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int maxRadius = 95;

        String[] labels = {"STR", "MOB", "INT", "STL", "CHA"};

        g2.setColor(new Color(210, 210, 210));

        // Radial lines
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(-90 + i * 72);
            int x = cx + (int) (Math.cos(angle) * maxRadius);
            int y = cy + (int) (Math.sin(angle) * maxRadius);
            g2.drawLine(cx, cy, x, y);
        }

        // Concentric pentagons (1–10)
        for (int level = 2; level < MAX_STAT; level+=2) {
            double ratio = level / (double) MAX_STAT;

            int[] gx = new int[5];
            int[] gy = new int[5];

            for (int i = 0; i < 5; i++) {
                double angle = Math.toRadians(-90 + i * 72);
                gx[i] = cx + (int) (Math.cos(angle) * maxRadius * ratio);
                gy[i] = cy + (int) (Math.sin(angle) * maxRadius * ratio);
            }

            g2.drawPolygon(gx, gy, 5);
        }

        /* ===== MAX OUTLINE ===== */
        int[] maxX = new int[5];
        int[] maxY = new int[5];

        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(-90 + i * 72);
            maxX[i] = cx + (int) (Math.cos(angle) * maxRadius);
            maxY[i] = cy + (int) (Math.sin(angle) * maxRadius);
        }

        g2.setColor(new Color(200, 200, 200));
        g2.drawPolygon(maxX, maxY, 5);

        /* ===== CURRENT STATS ===== */
        int[] x = new int[5];
        int[] y = new int[5];

        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(-90 + i * 72);
            double ratio = displayStats[i] / MAX_STAT;

            x[i] = cx + (int) (Math.cos(angle) * maxRadius * ratio);
            y[i] = cy + (int) (Math.sin(angle) * maxRadius * ratio);
        }

        g2.setColor(new Color(80, 130, 255, 160));
        g2.fillPolygon(x, y, 5);

        g2.setColor(new Color(30, 60, 150));
        g2.drawPolygon(x, y, 5);

        /* ===== LABELS ===== */
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(Color.BLACK);

        for (int i = 0; i < 5; i++) {
            int lx = cx + (int) (Math.cos(Math.toRadians(-90 + i * 72)) * (maxRadius + 18));
            int ly = cy + (int) (Math.sin(Math.toRadians(-90 + i * 72)) * (maxRadius + 18));
            g2.drawString(labels[i], lx - 10, ly + 5);
        }
    }
    private int clamp(int v) {
        return Math.min(10, v);
    }
    private double clamp(double v) {
        return Math.min(10, v);
    }
}
