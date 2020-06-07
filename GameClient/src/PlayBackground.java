import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * 背景撥放音樂類別
 * 在此可以呼叫介面調整音量大小
 */
public class PlayBackground extends JFrame{
    //視窗元件類別
    private JPanel panel1;
    private JSlider slider1;
    private JCheckBox muteCheckBox;
    private JButton closeButton1;

    //音訊控制類別
    private AudioInputStream audioIn;
    private Clip clip;
    private FloatControl gainControl;

    private void setupUI()
    {
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        slider1 = new JSlider();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(slider1, gbc);
        muteCheckBox = new JCheckBox();
        muteCheckBox.setText("Mute");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(muteCheckBox, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        panel1.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 20;
        panel1.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer3, gbc);
        closeButton1 = new JButton();
        closeButton1.setText("");
        closeButton1.setBorderPainted(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(closeButton1, gbc);

        slider1.setInverted(true);

        this.add(panel1);


        spacer1.setOpaque(false);
        spacer2.setOpaque(false);
        spacer3.setOpaque(false);
        closeButton1.setOpaque(false);


        slider1.setBackground(new Color(0x9882AC));
        muteCheckBox.setBackground(new Color(0x9882AC));
        panel1.setBackground(new Color(0x73648A));

        closeButton1.setIcon(new ImageIcon("./images/close.png"));

    }

    //視窗初始化
    PlayBackground()
    {
        try{
            Image icon = ImageIO.read(new File("./images/icon.png"));
            this.setIconImage(icon);
        }catch (IOException e){
            System.err.println(e);
        }
        setupUI();
        this.setUndecorated(true);
        this.setSize(400,150);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addListener();

        try{
            //獲取音源串流輸入
            audioIn = AudioSystem.getAudioInputStream(new File("music.wav").toURI().toURL());
            // Get a sound clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);

            //獲取控制類別
            gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);

            //設定音量大小 依照分貝數調整
            // -10.0f為減弱10分貝
            gainControl.setValue(-20.0f);

            //循環次數
            clip.loop(Integer.MAX_VALUE);

            //開始撥放
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e){
            e.printStackTrace();
        }
    }

    //添加事件監聽
    private void addListener()
    {
        closeButton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                setVisible(false);
            }
        });

        slider1.addChangeListener(e -> {
            if(!muteCheckBox.isSelected())
            {
                float interval = gainControl.getMaximum() - gainControl.getMinimum();
                float vol = 0 - ((float) (interval * slider1.getValue() * 0.01) - gainControl.getMaximum());
                gainControl.setValue(vol);
            }
        });

        muteCheckBox.addChangeListener(e -> {
            if(muteCheckBox.isSelected())
            {
                gainControl.setValue(gainControl.getMinimum());
            }else{
                float interval = gainControl.getMaximum() - gainControl.getMinimum();
                float vol = 0 - ((float) (interval * slider1.getValue() * 0.01) - gainControl.getMaximum());
                gainControl.setValue(vol);
            }
        });
    }

    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
    }
}

