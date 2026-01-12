import javax.swing.*;
import java.awt.*;

public class SoldierInfoPanel extends JPanel {

    public SoldierInfoPanel(Soldier soldier) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel image = new JLabel(soldier.portrait, SwingConstants.CENTER);
        Image img = soldier.portrait.getImage();
        Image scaled = img.getScaledInstance(200, 250, Image.SCALE_SMOOTH);
        image.setIcon(new ImageIcon(scaled));
        image.setPreferredSize(new Dimension(200, 250));
        image.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(image, BorderLayout.CENTER);

        JLabel info = new JLabel(
                "<html><center><b>" + soldier.name +
                        "</b><br/>Level " + soldier.level +
                        "</center></html>",
                SwingConstants.CENTER
        );
        add(info, BorderLayout.SOUTH);
    }
}
