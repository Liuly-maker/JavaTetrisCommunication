import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ServerFrame extends JFrame {
    private mainPanel mainPanel;
    private JTextArea consoleArea;
    private JTextField sendField;
    private JButton sendBtn;
    private JButton startBtn;
    private JTextField portField;
    private JLabel portLabel;
    private JLabel picLabel;

    //Adapter
    MouseAdapter adapter;

    ServerFrame(){
        setupUI();
        addListener();
        this.setSize(new Dimension(1000,700));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("PopKart Server");
        picLabel.setIcon(new ImageIcon("./images/pic.gif"));
        this.setVisible(true);
        printConsole("輸入Port按下開始連線執行伺服器");
        this.setIconImage(new ImageIcon("./images/icon.png").getImage());
        this.setResizable(false);
    }

    private void addListener()
    {
        adapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try{
                    int port = Integer.parseInt(portField.getText());
                    new Thread(()->{
                        try {
                            printConsole("Server is Started at port " + port);
                            Server.startServer(port);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }).start();
                    afterAddListener();
                }catch (Exception exception){
                    consoleArea.append("Seems like you input the wrong port.\n");
                    System.err.println(exception);
                }finally {
                    portField.setText("");
                }
            }
        };
        startBtn.addMouseListener(adapter);
    }

    private void afterAddListener()
    {
        startBtn.removeMouseListener(adapter);
        sendBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(sendField.getText().isEmpty()) return;
                String txt = sendField.getText();
                sendField.setText("");
                parserCommand(txt);
            }
        });
    }

    public String parserCommand(String command)
    {

        String com[] = command.split(" ");

        switch (com[0]){
            case "say":{
                try{
                    String msg = command.substring(4,command.length()-1);
                    if(msg.isEmpty()) return null;
                    printConsole("Server say : " + command.substring(3,command.length()));
                    Server.printMsg(msg);
                }catch (IOException e){
                    printConsole("Some problem in say msg.");
                    System.err.println(e);
                }catch (Exception e){
                    System.err.println(e);
                }
                break;
            }
            case "exit":{
                JOptionPane.showConfirmDialog(null,"Close the Server","Server Close",JOptionPane.OK_OPTION);
                System.exit(0);
                break;
            }
            case "list":{
                break;
            }
            case "help":{
                printConsole("1. say -msg : Print -msg to every Client.");
                printConsole("2. exit : Exit the server.");
                printConsole("3. list : See all the client now.");
                break;
            }
            default:{
                printConsole("Unknown Command " + command);
                printConsole("Try typing help to see all command.");
                break;
            }
        }
        return command;
    }

    public void printConsole(String str){
        consoleArea.append(str + "\n");
    }

    private void setupUI()
    {
        mainPanel = new mainPanel("./images/background.jpg");
        mainPanel.setLayout(new GridBagLayout());
        consoleArea = new JTextArea();
        consoleArea.setBackground(new Color(-4868683));
        consoleArea.setCaretColor(new Color(-1));
        consoleArea.setEditable(false);
        consoleArea.setFocusable(false);
        consoleArea.setForeground(new Color(-16777216));
        consoleArea.setText("");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(consoleArea, gbc);
        sendField = new JTextField();
        sendField.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sendField, gbc);
        sendBtn = new JButton();
        sendBtn.setFocusable(false);
        sendBtn.setText("Send");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(sendBtn, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        mainPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 20;
        mainPanel.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 20;
        mainPanel.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        mainPanel.add(spacer4, gbc);
        picLabel = new JLabel();
        picLabel.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridheight = 2;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(picLabel, gbc);
        startBtn = new JButton();
        startBtn.setText("Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(startBtn, gbc);
        portField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(portField, gbc);
        portLabel = new JLabel();
        portLabel.setFont(new Font(Font.DIALOG,Font.BOLD,14));
        portLabel.setText("Port");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 20;
        mainPanel.add(portLabel, gbc);

        spacer1.setOpaque(false);
        spacer2.setOpaque(false);
        spacer3.setOpaque(false);
        spacer4.setOpaque(false);

        consoleArea.setFont(new Font(Font.DIALOG,Font.PLAIN,18));
        mainPanel.setOpaque(false);

        this.getContentPane().add(mainPanel);
    }
}
