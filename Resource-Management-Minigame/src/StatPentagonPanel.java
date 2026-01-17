import javax.swing.*;
import java.awt.*;

public class StatPentagonPanel extends JPanel {

    private final Soldier soldier;
    private Soldier previewSoldier; // NEW: The potential promoted soldier

    private final double[] displayStats = new double[5];
    private final int[] targetStats = new int[5];

    private Timer animationTimer;

    private static final int MAX_STAT = 10;
    private static final int ANIM_DURATION = 250;
    private static final int FPS = 60;

    public StatPentagonPanel(Soldier soldier) {
        this.soldier = soldier;
        setPreferredSize(new Dimension(260, 260));
        setBackground(new Color(30, 30, 30)); // Dark background for contrast
        syncWithSoldier();
    }

    /** Set the promoted soldier to show the ghost outline */
    public void setPreview(Soldier preview) {
        this.previewSoldier = preview;
        repaint();
    }

    private void syncWithSoldier() {
        targetStats[0] = soldier.strength;
        targetStats[1] = soldier.mobility;
        targetStats[2] = soldier.intelligence;
        targetStats[3] = soldier.stealth;
        targetStats[4] = soldier.charisma;

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        final double[] startStats = displayStats.clone();
        final int frames = ANIM_DURATION * FPS / 1000;
        final int[] frame = {0};

        animationTimer = new Timer(1000/FPS, e -> {
            frame[0]++;
            double t = Math.min(1.0, frame[0] / (double) frames);
            double ease = 1 - Math.pow(1 - t, 3);

            for (int i = 0; i < 5; i++) {
                displayStats[i] = startStats[i] + (targetStats[i] - startStats[i]) * ease;
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int maxRadius = 95;

        // --- 1. RADIAL LINES ---
        g2.setColor(new Color(100, 100, 100));
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(-90 + i * 72);
            int x = cx + (int) (Math.cos(angle) * maxRadius);
            int y = cy + (int) (Math.sin(angle) * maxRadius);
            g2.drawLine(cx, cy, x, y);
        }

        // --- 2. CONCENTRIC PENTAGONS (The Grid) ---
        g2.setColor(new Color(70, 70, 70));
        for (int level = 2; level <= MAX_STAT; level += 2) {
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

        // --- 3. PREVIEW STATS (Dashed Gold Outline) ---
        if (previewSoldier != null) {
            int[] px = new int[5];
            int[] py = new int[5];
            int[] pVals = {previewSoldier.strength, previewSoldier.mobility,
                    previewSoldier.intelligence, previewSoldier.stealth, previewSoldier.charisma};

            for (int i = 0; i < 5; i++) {
                double angle = Math.toRadians(-90 + i * 72);
                double ratio = pVals[i] / (double) MAX_STAT;
                px[i] = cx + (int) (Math.cos(angle) * maxRadius * ratio);
                py[i] = cy + (int) (Math.sin(angle) * maxRadius * ratio);
            }

            g2.setColor(new Color(255, 215, 0, 60)); // Translucent gold fill
            g2.fillPolygon(px, py, 5);

            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            g2.setColor(new Color(255, 215, 0, 200)); // Gold dashed border
            g2.drawPolygon(px, py, 5);
            g2.setStroke(new BasicStroke(1f)); // Reset stroke
        }

        // --- 4. CURRENT STATS (Animated Blue) ---
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
        g2.setColor(new Color(150, 180, 255));
        g2.drawPolygon(x, y, 5);

        // --- 5. LABELS ---
        String[] labels = {"STR", "MOB", "INT", "STL", "CHA"};
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(-90 + i * 72);
            int lx = cx + (int) (Math.cos(angle) * (maxRadius + 20));
            int ly = cy + (int) (Math.sin(angle) * (maxRadius + 20));
            g2.drawString(labels[i], lx - 10, ly + 5);
        }
    }
}