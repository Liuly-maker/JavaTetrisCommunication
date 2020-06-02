import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainPanel extends JPanel {
    Image background;
    public MainPanel(){
        try {
            background = ImageIO.read(new File("./images/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background,0,0,this.getWidth(),this.getHeight(),null);
    }
}
