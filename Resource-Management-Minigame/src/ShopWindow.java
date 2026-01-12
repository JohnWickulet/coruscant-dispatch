import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ShopWindow extends GameWindow {

    public ShopWindow(GameUI ui, Shop shop, Game game) {
        super(ui, "Shop", 900, 500);
        setLayout(new BorderLayout(10, 10));

        // ================= HEADER =================
        JLabel moneyLabel = new JLabel("Credits: " + game.getCurrency());
        moneyLabel.setFont(moneyLabel.getFont().deriveFont(Font.BOLD, 16f));
        moneyLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(moneyLabel, BorderLayout.NORTH);

        // ================= ITEM GRID =================
        JPanel grid = new JPanel(new GridLayout(0, 3, 15, 15));
        grid.setBorder(new EmptyBorder(15, 15, 15, 15));

        for (ItemType item : ItemType.values()) {
            grid.add(createItemCard(item, shop, game, moneyLabel));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // ================= FOOTER =================
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());

        JPanel bottom = new JPanel();
        bottom.add(close);
        add(bottom, BorderLayout.SOUTH);
        setVisible(true);
    }

    // =========================================================
    // ITEM CARD
    // =========================================================
    private JPanel createItemCard(
            ItemType item,
            Shop shop,
            Game game,
            JLabel moneyLabel
    ) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBorder(new LineBorder(Color.DARK_GRAY, 2));
        card.setBackground(new Color(25, 25, 40));
        card.setPreferredSize(new Dimension(240, 320));

        // ---------- PORTRAIT ----------
        JPanel portrait = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(60, 60, 90));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                g.setFont(getFont().deriveFont(Font.BOLD, 24f));
                g.drawString(item.displayName().substring(0, 1), getWidth() / 2 - 8, getHeight() / 2 + 8);
            }
        };
        portrait.setPreferredSize(new Dimension(200, 120));
        card.add(portrait, BorderLayout.NORTH);

        // ---------- DESCRIPTION ----------
        JTextArea desc = new JTextArea(item.description);
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setForeground(Color.WHITE);
        desc.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane descScroll = new JScrollPane(desc);
        descScroll.setBorder(null);
        descScroll.setOpaque(false);
        descScroll.getViewport().setOpaque(false);

        card.add(descScroll, BorderLayout.CENTER);

        // ---------- BUY AREA ----------
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel price = new JLabel(item.price + " credits");
        price.setForeground(Color.WHITE);

        JButton buy = new JButton("Buy");
        buy.addActionListener(e -> {
            if (shop.buy(item, game)) {
                moneyLabel.setText("Credits: " + game.getCurrency());
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Not enough credits!",
                        "Purchase Failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        bottom.add(price, BorderLayout.WEST);
        bottom.add(buy, BorderLayout.EAST);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }
}
