import javax.swing.*;
import java.awt.*;

public abstract class GameWindow extends JDialog {

    public GameWindow(Window owner, String title, int w, int h) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setSize(w, h);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
