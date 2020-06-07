import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class mainPanel extends JPanel {
    Image background;
    mainPanel(String str)
    {
        try {
            background = ImageIO.read(new File(str));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(background,0,0,this.getWidth(),this.getHeight(),null);
        super.paint(g);
    }
}
