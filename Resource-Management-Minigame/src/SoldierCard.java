import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class SoldierCard extends JPanel {

    private final Soldier soldier;
    private boolean selected = false;
    private int cardW, cardH;

    public SoldierCard(Soldier soldier) {
        this(soldier, 150, 180); // Default size for main panels
    }

    public SoldierCard(Soldier soldier, int width, int height) {
        this.soldier = soldier;
        this.cardW = width;
        this.cardH = height;
        setPreferredSize(new Dimension(cardW, cardH));
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    public Soldier getSoldier() { return soldier; }

    public void toggle() {
        selected = !selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        //BACKGROUND STATE COLOR
        switch (soldier.state) {
            case DEPLOYED -> g2.setColor(new Color(160, 160, 160));
            case RESTING -> g2.setColor(new Color(73, 145, 193));
            case DOWNED -> g2.setColor(new Color(50, 50, 50));
            default -> g2.setColor(Color.WHITE);
        }
        g2.fillRect(0, 0, w, h);

        //PORTRAIT SCALING
        if (soldier.portrait != null) {
            Image img = soldier.portrait.getImage();
            int nameBarHeight = (h > 100) ? 25 : 14;
            int maxImgW = w;
            int maxImgH = h - nameBarHeight;
            double scale = Math.min((double) maxImgW / 450, (double) maxImgH / 500);
            int imgW = (int) (450 * scale);
            int imgH = (int) (500 * scale);
            int imgX = (w - imgW) / 2;
            int imgY = 0;

            BufferedImage bi = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gbi = bi.createGraphics();
            gbi.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            gbi.drawImage(img, 0, 0, imgW, imgH, null);
            gbi.dispose();

            if (soldier.health < soldier.maxHealth) {
                RescaleOp rop = new RescaleOp(
                        new float[]{1.3f, 0.7f, 0.7f, 1.0f},
                        new float[]{35f, 0f, 0f, 0f},
                        null
                );
                bi = rop.filter(bi, null);
            }
            g2.drawImage(bi, imgX, imgY, null);
        }

        //RESTING FLOW
        if (soldier.state == Soldier.State.RESTING && soldier.rest_time > 0) {
            double ratio = soldier.rest_time_remaining / soldier.rest_time;
            int barW = (int) (w * ratio);
            g2.setPaint(new GradientPaint(0, 0, new Color(80, 140, 246, 200), barW, 0, new Color(180, 210, 255, 40)));
            g2.fillRect(0, 0, barW, h);
        }

        //NAME OVERLAY
        int barH = (h > 100) ? 25 : 14;
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRect(0, h - barH, w, barH);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, (h > 100) ? 11 : 9));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(soldier.name, (w - fm.stringWidth(soldier.name)) / 2, h - (barH/3));

        //SELECTION BORDER
        g2.setStroke(new BasicStroke(3));
        g2.setColor(selected ? Color.GREEN : Color.GRAY);
        g2.drawRect(1, 1, w - 3, h - 3);
        g2.dispose();
    }
}