import javax.swing.*;
import java.awt.*;

public class StatPentagonPanel extends JPanel {

    private final Soldier soldier;
    private Soldier previewSoldier;

    private final double[] displayStats = new double[5];
    private final int[] targetStats = new int[5];

    // New variables for animated preview
    private final double[] previewDisplayStats = new double[5];
    private final int[] previewTargetStats = new int[5];
    private float previewAlpha = 0f; // To fade the preview in/out

    private Timer animationTimer;

    private static final int MAX_STAT = 10;
    private static final int ANIM_DURATION = 350; // Slightly longer for smoother feel
    private static final int FPS = 60;

    public StatPentagonPanel(Soldier soldier) {
        this.soldier = soldier;
        setPreferredSize(new Dimension(280, 280));
        setOpaque(false); // Clean for white theme
        syncWithSoldier();
    }

    public void syncWithSoldier() {
        targetStats[0] = soldier.strength;
        targetStats[1] = soldier.mobility;
        targetStats[2] = soldier.intelligence;
        targetStats[3] = soldier.stealth;
        targetStats[4] = soldier.charisma;
        startAnimation();
    }

    public void setPreview(Soldier s) {
        this.previewSoldier = s;
        if (s != null) {
            previewTargetStats[0] = s.strength;
            previewTargetStats[1] = s.mobility;
            previewTargetStats[2] = s.intelligence;
            previewTargetStats[3] = s.stealth;
            previewTargetStats[4] = s.charisma;
        } else {
            // When preview is null, target the current soldier's stats to "shrink" back
            System.arraycopy(targetStats, 0, previewTargetStats, 0, 5);
        }
        startAnimation();
    }

    private void startAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        final double[] startBase = displayStats.clone();
        final double[] startPreview = previewDisplayStats.clone();
        final float startAlpha = previewAlpha;

        final int frames = ANIM_DURATION * FPS / 1000;
        final int[] frame = {0};

        animationTimer = new Timer(1000 / FPS, e -> {
            frame[0]++;
            double t = Math.min(1.0, frame[0] / (double) frames);
            double ease = 1 - Math.pow(1 - t, 3); // Cubic ease out

            // Animate Base Stats
            for (int i = 0; i < 5; i++) {
                displayStats[i] = startBase[i] + (targetStats[i] - startBase[i]) * ease;
            }

            // Animate Preview Stats
            for (int i = 0; i < 5; i++) {
                previewDisplayStats[i] = startPreview[i] + (previewTargetStats[i] - startPreview[i]) * ease;
            }

            // Animate Alpha (Fade in if previewSoldier exists, else fade out)
            float targetAlpha = (previewSoldier != null) ? 1.0f : 0.0f;
            previewAlpha = (float) (startAlpha + (targetAlpha - startAlpha) * ease);

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
        int maxRadius = 90;

        // Draw Grid and Web (Standard logic)
        drawGrid(g2, cx, cy, maxRadius);

        // --- DRAW ANIMATED PREVIEW STATS ---
        if (previewAlpha > 0.01f) {
            int[] px = new int[5];
            int[] py = new int[5];
            for (int i = 0; i < 5; i++) {
                double angle = Math.toRadians(-90 + i * 72);
                double ratio = previewDisplayStats[i] / MAX_STAT;
                px[i] = cx + (int) (Math.cos(angle) * maxRadius * ratio);
                py[i] = cy + (int) (Math.sin(angle) * maxRadius * ratio);
            }

            // Apply animated alpha to colors
            g2.setColor(new Color(246, 208, 63, (int) (80 * previewAlpha)));
            g2.fillPolygon(px, py, 5);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g2.setColor(new Color(210, 160, 0, (int) (200 * previewAlpha)));
            g2.drawPolygon(px, py, 5);
            g2.setStroke(new BasicStroke(1f));
        }

        // --- DRAW CURRENT STATS ---
        int[] x = new int[5];
        int[] y = new int[5];
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(-90 + i * 72);
            double ratio = displayStats[i] / MAX_STAT;
            x[i] = cx + (int) (Math.cos(angle) * maxRadius * ratio);
            y[i] = cy + (int) (Math.sin(angle) * maxRadius * ratio);
        }

        g2.setColor(new Color(0, 120, 215, 140));
        g2.fillPolygon(x, y, 5);
        g2.setColor(new Color(0, 100, 200));
        g2.drawPolygon(x, y, 5);

        drawLabels(g2, cx, cy, maxRadius);
    }

    private void drawGrid(Graphics2D g2, int cx, int cy, int maxRadius) {
        g2.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(-90 + i * 72);
            g2.drawLine(cx, cy, cx + (int) (Math.cos(angle) * maxRadius), cy + (int) (Math.sin(angle) * maxRadius));
        }
        for (int level = 2; level <= MAX_STAT; level += 2) {
            double ratio = level / (double) MAX_STAT;
            int[] gx = new int[5]; int[] gy = new int[5];
            for (int i = 0; i < 5; i++) {
                double angle = Math.toRadians(-90 + i * 72);
                gx[i] = cx + (int) (Math.cos(angle) * maxRadius * ratio);
                gy[i] = cy + (int) (Math.sin(angle) * maxRadius * ratio);
            }
            g2.drawPolygon(gx, gy, 5);
        }
    }

    private void drawLabels(Graphics2D g2, int cx, int cy, int maxRadius) {
        String[] labels = {"STR", "MOB", "INT", "STL", "CHA"};
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(new Color(60, 60, 60));
        FontMetrics fm = g2.getFontMetrics();
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(-90 + i * 72);
            int lx = cx + (int) (Math.cos(angle) * (maxRadius + 25));
            int ly = cy + (int) (Math.sin(angle) * (maxRadius + 25));
            g2.drawString(labels[i], lx - fm.stringWidth(labels[i]) / 2, ly + fm.getAscent() / 2);
        }
    }
}