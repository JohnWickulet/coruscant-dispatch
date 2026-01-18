import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JPanel {

    public BottomPanel(Game game, GameUI ui) {
        setLayout(new BorderLayout());

        // Menu buttons
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton inventoryBtn = new JButton("Inventory");
        JButton shopBtn = new JButton("Shop");
        JButton upgradeBtn = new JButton("Upgrades");

        inventoryBtn.addActionListener(e ->{
            ui.pauseGame();
            SoundManager.select();
            new InventoryWindow(ui, game.inventory,game.squad);
            ui.resumeGame();
        });

        shopBtn.addActionListener(e -> {
            ui.pauseGame();
            SoundManager.select();
            new ShopWindow(ui, game.shop,game);
            ui.resumeGame();
        });

        upgradeBtn.addActionListener(e ->{
            ui.pauseGame();
            SoundManager.select();
            new UpgradeWindow(ui, game.squad);
            ui.resumeGame();
        });

        menuPanel.add(inventoryBtn);
        menuPanel.add(shopBtn);
        menuPanel.add(upgradeBtn);

        add(menuPanel, BorderLayout.CENTER);
    }
}
