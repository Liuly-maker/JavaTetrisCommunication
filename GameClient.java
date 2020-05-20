import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Random;

//總客戶端
public class GameClient{
    public static void main(String [] args){
        new GameFrame();
    }
}

//遊戲視窗內嵌通訊欄
class GameFrame extends JFrame{
    /**
     * 布局元件
     */
    private JPanel MainPanel;
    private JPanel bottom;
    private JPanel top;
    private JPanel right;
    private JButton sendBtn;
    private JTextField sendMsg;
    private JPanel center;
    private JButton ContinueGame;
    private JButton StopGame;
    private JButton NewGame;
    private JButton button2;
    private JPanel left;
    private JTextArea talk;
    private JTextField textIP;
    private JTextField TextPort;
    private JButton connectBtn;
    private JButton clearBtn;
    private JPanel GamePanel;
    private JLabel sendLabel;
    private JLabel stateLabel;

    Socket socket;
    BufferedReader input;
    PrintWriter out;

    final int _SEND_MSG = 0;
    final int _GET_MSG = 1;
    final int _SET_NAME = 2;

    ButtonTrack connectListener;

    Game game;

    //視窗長寬設定
    private int window_Width = 1000;
    private int window_Height = 600;

    private void setupUi()                                      //設定介面
    {
        try{  //設定Style為nimbus
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }
        catch (UnsupportedLookAndFeelException | InstantiationException | ClassNotFoundException | IllegalAccessException ignored) {}

        MainPanel = new JPanel();
        MainPanel.setLayout(new BorderLayout(0, 0));
        Font MainPanelFont = UIManager.getFont("DesktopIcon.font");
        if (MainPanelFont != null) MainPanel.setFont(MainPanelFont);
        bottom = new JPanel();
        bottom.setLayout(new GridBagLayout());
        MainPanel.add(bottom, BorderLayout.SOUTH);
        sendMsg = new JTextField();
        sendMsg.setColumns(30);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottom.add(sendMsg, gbc);
        sendBtn = new JButton();
        sendBtn.setText("Send");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottom.add(sendBtn, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 30;
        bottom.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 30;
        bottom.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 30;
        bottom.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        bottom.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        bottom.add(spacer5, gbc);
        sendLabel = new JLabel();
        sendLabel.setText("Send : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        bottom.add(sendLabel, gbc);
        top = new JPanel();
        top.setLayout(new GridBagLayout());
        MainPanel.add(top, BorderLayout.NORTH);
        textIP = new JTextField();
        textIP.setText("127.0.0.1");
        textIP.setToolTipText("輸入IP");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        top.add(textIP, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("輸入IP : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        top.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("輸入Port : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        top.add(label2, gbc);
        TextPort = new JTextField();
        TextPort.setText("6600");
        TextPort.setToolTipText("輸入Port");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        top.add(TextPort, gbc);
        connectBtn = new JButton();
        connectBtn.setText("Connect");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        top.add(connectBtn, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        top.add(spacer6, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        top.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 5;
        top.add(spacer8, gbc);
        stateLabel = new JLabel();
        stateLabel.setText("初始化成功...尚未連線");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        top.add(stateLabel, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 30;
        top.add(spacer9, gbc);
        right = new JPanel();
        right.setLayout(new GridBagLayout());
        MainPanel.add(right, BorderLayout.EAST);
        button2 = new JButton();
        button2.setText("Button");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        right.add(button2, gbc);
        NewGame = new JButton();
        NewGame.setText("NewGame");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        right.add(NewGame, gbc);
        StopGame = new JButton();
        StopGame.setText("StopGame");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        right.add(StopGame, gbc);
        ContinueGame = new JButton();
        ContinueGame.setText("ContinueGame");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        right.add(ContinueGame, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 50;
        right.add(spacer10, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 50;
        right.add(spacer11, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 50;
        right.add(spacer12, gbc);
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 50;
        right.add(spacer13, gbc);
        final JPanel spacer14 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 50;
        right.add(spacer14, gbc);
        final JPanel spacer15 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 15;
        right.add(spacer15, gbc);
        final JPanel spacer16 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        right.add(spacer16, gbc);
        center = new JPanel();
        center.setLayout(new GridBagLayout());
        MainPanel.add(center, BorderLayout.CENTER);
        GamePanel = new JPanel();
        GamePanel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 500;
        center.add(GamePanel, gbc);
        left = new JPanel();
        left.setLayout(new GridBagLayout());
        MainPanel.add(left, BorderLayout.WEST);
        final JPanel spacer17 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        left.add(spacer17, gbc);
        clearBtn = new JButton();
        clearBtn.setText("Clear");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        left.add(clearBtn, gbc);
        final JPanel spacer18 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        left.add(spacer18, gbc);
        final JPanel spacer19 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        left.add(spacer19, gbc);
        final JPanel spacer20 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 15;
        left.add(spacer20, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 125;
        left.add(scrollPane1, gbc);
        talk = new JTextArea();
        talk.setEditable(false);
        Font talkFont = UIManager.getFont("TextArea.font");
        if (talkFont != null) talk.setFont(talkFont);
        talk.setText("嘗試加入聊天室...");
        scrollPane1.setViewportView(talk);

        /**
         * 將遊戲添加於GamePanel
         */
        game = new Game();
        GamePanel.add(game);

        this.add(MainPanel);    //添加主要Panel
    }

    class ButtonTrack implements MouseListener                  //按鈕的滑鼠監聽
    {
        private String buttonName = "";
        ButtonTrack(String name){
            this.buttonName = name;
        }
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if(buttonName.equals("Connect")) talkConnect();
            if(buttonName.equals("ContinueGame"))  game.ContinueGame();
            if(buttonName.equals("StopGame"))  game.StopGame();
            if(buttonName.equals("NewGame"))  game.NewGame();
            if(buttonName.equals("Clear")) clearTalk();
        }
        @Override
        public void mousePressed(MouseEvent mouseEvent) {}
        @Override
        public void mouseReleased(MouseEvent mouseEvent) {}
        @Override
        public void mouseEntered(MouseEvent mouseEvent) {}
        @Override
        public void mouseExited(MouseEvent mouseEvent) {}
    }

    private void talkConnect()                                  //連接至伺服器
    {
        String IP = textIP.getText();
        int Port = Integer.parseInt(TextPort.getText());

        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    socket = new Socket(IP, Port);

                    connectBtn.removeMouseListener(connectListener);
                    connectBtn.setEnabled(false);
                    stateLabel.setText("已連線至聊天伺服器");

                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                while(true) {
                                    String inMsg = input.readLine();
                                    System.out.println(inMsg);
                                    talk.append("\n" + inMsg);
                                }
                            }catch (IOException e){
                                System.err.println(e);
                            }
                        }
                    }.start();

                    /**
                     * 寄送UserName
                     */
                    out = new PrintWriter(socket.getOutputStream());
                    String userName = JOptionPane.showInputDialog("輸入希望的用戶名稱 : ");
                    if(userName == null) {
                        talk.append("\n用戶取消連線");
                        socket.close();
                    }else if(userName.isEmpty()){
                        userName = "Client";
                    }
                    JSONObject json = new JSONObject();
                    json.put("action", _SET_NAME);
                    json.put("name", userName);

                    out.println(json.toString());
                    out.flush();
                }catch(IOException e){
                    System.err.println(e);
                }
            }
        }.start();
    }

    private void clearTalk()                                    //清理對話框
    {
        talk.setText("已清空對話...");
    }

    private void addListener()                                  //添加監聽
    {
        /**
         * 監聽全域鍵盤按鍵並傳送給game的鍵盤偵測
         */
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            //遊戲繼續時的按鍵判定
            if(!game.isGameStop()){
                if (event.getID() == KeyEvent.KEY_PRESSED){
                    // 獲取按鍵的code
                    switch (((KeyEvent) event).getKeyCode()){
                        case KeyEvent.VK_UP:
                            game.Turn();
                            break;
                        case KeyEvent.VK_DOWN:
                            game.MoveDown();
                            break;
                        case KeyEvent.VK_LEFT:
                            game.MoveLeft();
                            break;
                        case KeyEvent.VK_RIGHT:
                            game.MoveRight();
                            break;
                        case KeyEvent.VK_P:
                            game.StopGame();
                            break;
                        case KeyEvent.VK_SPACE:
                            game.MoveBottom();
                            break;
                    }
                }
            }else{ //遊戲暫停時的按鍵判定
                if (event.getID() == KeyEvent.KEY_PRESSED) {
                    // 獲取按鍵的code
                    switch (((KeyEvent) event).getKeyCode()) {
                        case KeyEvent.VK_P:
                            game.ContinueGame();
                            break;
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);

        sendBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                sendMsg();
            }
        });

        sendMsg.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
                    sendMsg();
            }
        });

        ContinueGame.addMouseListener(new ButtonTrack(ContinueGame.getText()));
        StopGame.addMouseListener(new ButtonTrack(StopGame.getText()));
        NewGame.addMouseListener(new ButtonTrack(NewGame.getText()));
        connectListener = new ButtonTrack(connectBtn.getText());
        connectBtn.addMouseListener(connectListener);
        clearBtn.addMouseListener(new ButtonTrack(clearBtn.getText()));

        this.addWindowStateListener(new WindowTrack());

    }

    private void sendMsg()                                      //寄送訊息
    {
        String msg = sendMsg.getText();
        sendMsg.setText("");

        if(msg.isEmpty()) return;

        JSONObject Jmsg = new JSONObject();

        Jmsg.put("action", _SEND_MSG);
        Jmsg.put("msg", msg);

        out.println(Jmsg.toString());
        out.flush();
        System.out.println(Jmsg.toString());
    }

    private class WindowTrack implements WindowStateListener    //視窗狀態監聽
    {
        @Override
        public void windowStateChanged(WindowEvent windowEvent) {
            if(windowEvent.getNewState() == 1) game.StopGame();
        }
    }

    GameFrame()                                                 //建構子設定視窗
    {
        /**
         * 元件布局
         * setupUi()
         * 並設定事件監聽
         * addListener()
         */
        this.setupUi();
        this.addListener();
        /**
         * 設定視窗大小
         */
        this.setSize( window_Width, window_Height);
        /**
         * 設定視窗置中
         */
        this.setLocationRelativeTo(null);
        /**
         * 設定關閉時直接結束程序
         */
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        /**
         * 設定視窗可見
         */
        this.setVisible(true);
    }
}

//遊戲類別
class Game extends JPanel{
    Image image;

    private int mapRow = 22;                            // 地圖長
    private int mapCol = 12;                            // 地圖寬
    private int mapGame[][] = new int[mapRow][mapCol];  //地圖
    private Timer timer;                                //計時器
    private int score = 0;                              //記錄成績
    Random random = new Random();                       //隨機產生

    private int curShapeType = -1;
    private int curShapeState = -1;                     //設定當前的形狀型別和當前的形狀狀態
    private int nextShapeType = -1;
    private int nextShapeState = -1;                    //設定下一次出現的方塊的型別和狀態
    private int holdShapeType = -1;
    private int holdShapeState = -1;                    //設定保留的方塊的型別和狀態
    private boolean ishold = false;

    private int posx = 0;                               //當前方塊位置
    private int posy = 0;                               //當前方塊位置

    private int Pposx = 0;
    private int Pposy = 0;

    private final int shapes[][][] = new int[][][]{
            //T字形按逆時針的順序儲存
            {
                    {0,1,0,0, 1,1,1,0, 0,0,0,0, 0,0,0,0},
                    {0,1,0,0, 1,1,0,0, 0,1,0,0, 0,0,0,0},
                    {1,1,1,0, 0,1,0,0, 0,0,0,0, 0,0,0,0},
                    {0,1,0,0, 0,1,1,0, 0,1,0,0, 0,0,0,0}
            },
            //I字形按逆時針的順序儲存
            {
                    {0,0,0,0, 3,3,3,3, 0,0,0,0, 0,0,0,0},
                    {0,3,0,0, 0,3,0,0, 0,3,0,0, 0,3,0,0},
                    {0,0,0,0, 3,3,3,3, 0,0,0,0, 0,0,0,0},
                    {0,3,0,0, 0,3,0,0, 0,3,0,0, 0,3,0,0}
            },
            //倒Z形按逆時針的順序儲存
            {
                    {0,4,4,0, 4,4,0,0, 0,0,0,0, 0,0,0,0},
                    {4,0,0,0, 4,4,0,0, 0,4,0,0, 0,0,0,0},
                    {0,4,4,0, 4,4,0,0, 0,0,0,0, 0,0,0,0},
                    {4,0,0,0, 4,4,0,0, 0,4,0,0, 0,0,0,0}
            },
            //Z形按逆時針的順序儲存
            {
                    {1,1,0,0, 0,1,1,0, 0,0,0,0, 0,0,0,0},
                    {0,1,0,0, 1,1,0,0, 1,0,0,0, 0,0,0,0},
                    {1,1,0,0, 0,1,1,0, 0,0,0,0, 0,0,0,0},
                    {0,1,0,0, 1,1,0,0, 1,0,0,0, 0,0,0,0}
            },
            //J字形按逆時針的順序儲存
            {
                    {0,1,0,0, 0,1,0,0, 1,1,0,0, 0,0,0,0},
                    {1,1,1,0, 0,0,1,0, 0,0,0,0, 0,0,0,0},
                    {1,1,0,0, 1,0,0,0, 1,0,0,0, 0,0,0,0},
                    {1,0,0,0, 1,1,1,0, 0,0,0,0, 0,0,0,0}
            },
            //L字形按逆時針的順序儲存
            {
                    {1,0,0,0, 1,0,0,0, 1,1,0,0, 0,0,0,0},
                    {0,0,1,0, 1,1,1,0, 0,0,0,0, 0,0,0,0},
                    {1,1,0,0, 0,1,0,0, 0,1,0,0, 0,0,0,0},
                    {1,1,1,0, 1,0,0,0, 0,0,0,0, 0,0,0,0}
            },
            //田字形按逆時針的順序儲存
            {
                    {1,1,0,0, 1,1,0,0, 0,0,0,0, 0,0,0,0},
                    {1,1,0,0, 1,1,0,0, 0,0,0,0, 0,0,0,0},
                    {1,1,0,0, 1,1,0,0, 0,0,0,0, 0,0,0,0},
                    {1,1,0,0, 1,1,0,0, 0,0,0,0, 0,0,0,0}
            }
    };

    private int rowRect = 4;        //方塊陣列長
    private int colRect = 4;        //方塊陣列寬
    int RectWidth = 15;     //預設方塊大小

    public Game()                                   //建構函式----建立好地圖
    {
        try{
            image = ImageIO.read(new File("Cube.png"));
        }catch (Exception e){

        }
        this.addComponentListener(new reSized());
        CreateRect();   //創建當前方塊
        initMap();      //初始化這個地圖
        SetWall();      //設定牆

        timer = new Timer(500,new TimerListener());
    }

    class TimerListener implements ActionListener   //監聽Timer設定每秒落下
    {
        public void actionPerformed(ActionEvent e)
        {
            MoveDown();
        }
    }

    public void SetWall()                           //第0列和第11列都是牆，第20行也是牆
    {
        for(int i = 0; i < mapRow; i++)             //先畫列
        {
            mapGame[i][0] = 2;
            mapGame[i][mapCol-1] = 2;
        }
        for(int j = 1; j < mapCol-1; j++)           //畫最後一行
        {
            mapGame[mapRow-1][j] = 2;
        }
    }

    public void initMap()                           //初始化地圖、牆是2、空格是0、方塊是1
    {
        for(int i = 0; i < mapRow; i++)
        {
            for(int j = 0; j < mapCol; j++)
            {
                mapGame[i][j] = 0;
            }
        }
    }

    public void CreateRect()                        //建立方塊
    {
        //如果當前的方塊型別和狀態都存在就設定下一次的，如果不存在就設定當前的並且設定下一次的狀態和型別
        if(curShapeType == -1 && curShapeState == -1)//當前的方塊狀態都為1，表示遊戲才開始
        {
            curShapeType = random.nextInt(shapes.length);
            curShapeState = random.nextInt(shapes[0].length);
        }
        else
        {
            curShapeType = nextShapeType;
            curShapeState = nextShapeState;
        }

        //產生下次方塊
        nextShapeType = random.nextInt(shapes.length);
        nextShapeState = random.nextInt(shapes[0].length);

        //產生方塊於地圖中間頂部
        posx = 0;
        posy = (mapCol / 2) - 1;

        //如果產生出來的方塊與地圖當前方塊重疊為遊戲結束
        if(GameOver(posx,posy,curShapeType,curShapeState))
        {
            timer.stop();
            JOptionPane.showConfirmDialog(null, "遊戲結束！", "提示", JOptionPane.OK_OPTION);
            System.exit(0);
        }
    }

    public boolean GameOver(int x, int y, int ShapeType, int ShapeState)    //判斷遊戲是否結束
    {
        if(IsOrNoMove(x,y,ShapeType,ShapeState))
        {
            return false;
        }
        return true;
    }

    public boolean IsOrNoMove(int x, int y, int ShapeType, int ShapeState)  //判斷xy可否移動、依照方塊種類以及當前型態
    {
        //判斷當前的這個圖形是否可以移動,這裡重點強調x,y的座標是指4*4的二維陣列（描述圖形的那個陣列）的左上角目標
        for(int i = 0; i < rowRect ; i++)
        {
            for(int j = 0; j < colRect; j++)
            {
                if(shapes[ShapeType][ShapeState][i*colRect+j] != 0 && mapGame[x+i][y+j] != 0
                        || shapes[ShapeType][ShapeState][i*colRect+j] != 0 && mapGame[x+i][y+j] == 2)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public void Pridiction()        //預測當前下落方塊位置
    {
        Pposy = posy;
        Pposx = posx;
        while(IsOrNoMove(Pposx+1,Pposy,curShapeType,curShapeState)){
            Pposx++;
        }
    }

    public void Turn()              //旋轉
    {
        int temp = curShapeState;
        curShapeState = (curShapeState+1) % shapes[0].length;
        if(IsOrNoMove(posx,posy,curShapeType,curShapeState))
        {
        }
        else
        {
            curShapeState = temp;
        }
        repaint();
    }

    public void MoveDown()          //向下移動
    {
        if(IsOrNoMove(posx+1,posy,curShapeType,curShapeState))
        {
            posx++;
        }
        else
        {
            AddToMap();//將此行固定在地圖中
            CheckLine();
            CreateRect();//重新建立一個新的方塊
        }
        repaint();
    }

    public void MoveBottom()        //向底部移動
    {
        while(IsOrNoMove(posx+1,posy,curShapeType,curShapeState)){
            posx++;
        }
        AddToMap();//將此行固定在地圖中
        CheckLine();
        CreateRect();//重新建立一個新的方塊

        repaint();
    }

    public void MoveLeft()          //向左移動
    {
        if(IsOrNoMove(posx,posy-1,curShapeType,curShapeState))
        {
            posy--;
        }
        repaint();
    }

    public void MoveRight()         //向右移動
    {
        if(IsOrNoMove(posx,posy+1,curShapeType,curShapeState))
        {
            posy++;
        }
        repaint();
    }

    public void HoldShape()         //保留當前方塊
    {
        if(ishold == false){
            //將當前方塊型態保存
            holdShapeState = curShapeState;
            holdShapeType = curShapeType;
            ishold = true;
        }
        CreateRect();
    }

    public void AddToMap()          //固定掉下來的這一影象到地圖中
    {
        for(int i = 0; i < rowRect; i++)
        {
            for(int j = 0; j < colRect; j++)
            {
                if(shapes[curShapeType][curShapeState][i*colRect+j] != 0)
                {
                    mapGame[posx+i][posy+j] = shapes[curShapeType][curShapeState][i*colRect+j];
                }
            }
        }
    }

    public void CheckLine()         //檢查一下這些行中是否有滿行的
    {
        int count = 0;
        for(int i = mapRow-2; i >= 0; i--)
        {
            count = 0;
            for(int j = 1; j < mapCol-1; j++)
            {
                if(mapGame[i][j] != 0 && mapGame[i][j] != 2)
                {
                    count++;
                }
                else
                    break;
            }
            if(count >= mapCol-2)
            {
                for(int k = i; k > 0; k--)
                {
                    for(int p = 1; p < mapCol-1; p++)
                    {
                        mapGame[k][p] = mapGame[k-1][p];
                    }
                }
                score += 10;
                i++;
            }
        }
    }

    @Override
    public void paint(Graphics g)   //重新繪製視窗
    {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.DIALOG,Font.BOLD,24));

        Pridiction();
        g.setColor(Color.GRAY);
        for(int i = 0; i < rowRect; i++)        //繪製預測的方塊
        {
            for(int j = 0; j < colRect; j++)
            {
                if(shapes[curShapeType][curShapeState][i*colRect+j] != 0)
                {

                    g.fillRect((Pposy+j+1)*RectWidth, (Pposx+i+1)*RectWidth, RectWidth, RectWidth);

                }
            }
        }
        g.setColor(Color.BLACK);

        for(int i = 0; i < rowRect; i++)        //繪製正在下落的方塊
        {
            for(int j = 0; j < colRect; j++)
            {
                if(shapes[curShapeType][curShapeState][i*colRect+j] != 0)
                {
                    g.fillRect((posy+j+1)*RectWidth, (posx+i+1)*RectWidth, RectWidth, RectWidth);
                }
            }
        }

        for(int i = 0; i < mapRow; i++)         //繪製地圖上面已經固定好的方塊資訊
        {
            for(int j = 0; j < mapCol; j++)
            {
                switch (mapGame[i][j]){
                    case 0:     //空白
                        g.setColor(Color.GRAY);
                        g.drawRect((j+1)*RectWidth, (i+1)*RectWidth, RectWidth, RectWidth);
                        g.setColor(Color.BLACK);
                        break;
                    case 1:     //預設方格
                        g.fillRect((j+1)*RectWidth, (i+1)*RectWidth, RectWidth, RectWidth);
                        break;
                    case 2:     //牆
                        g.setColor(Color.WHITE);
                        g.fillRect((j+1)*RectWidth, (i+1)*RectWidth, RectWidth, RectWidth);
                        g.setColor(Color.BLACK);
                        g.drawRect((j+1)*RectWidth, (i+1)*RectWidth, RectWidth, RectWidth);
                        break;
                    case 3:     //長條
                        g.setColor(Color.BLUE);
                        g.drawImage(image,(j+1)*RectWidth, (i+1)*RectWidth, RectWidth, RectWidth,null);
                        //g.fillRect((j+1)*RectWidth, (i+1)*RectWidth, RectWidth, RectWidth);
                        g.setColor(Color.BLACK);
                        break;
                    case 4:     //倒Z
                        g.setColor(Color.ORANGE);
                        g.fillRect((j+1)*RectWidth, (i+1)*RectWidth, RectWidth, RectWidth);
                        g.setColor(Color.BLACK);
                        break;
                    default:    //預設
                        g.setColor(Color.CYAN);
                        g.fillRect((j+1)*RectWidth, (i+1)*RectWidth, RectWidth, RectWidth);
                        g.setColor(Color.BLACK);
                        break;
                }
            }
        }

        //座標偏移量
        int x = RectWidth * (mapCol + 2);
        int y = RectWidth;

        g.drawString("score = " + score, x , y * 2);
        g.drawString("下一個方塊：", x, y * 4);
        for(int i = 0; i < rowRect; i++)
        {
            for(int j = 0; j < colRect; j++)
            {
                if(shapes[nextShapeType][nextShapeState][i*colRect+j] != 0)
                {
                    g.fillRect(x + (j * RectWidth), (y * 6) + (i * RectWidth), RectWidth, RectWidth);
                }
            }
        }

        if(!timer.isRunning()){
            g.drawString("遊戲暫停中", RectWidth * 4,RectWidth * (mapRow / 2));
            g.drawString("請按下繼續", RectWidth * 4,RectWidth * (mapRow / 2 + 2));
        }
    }

    public void NewGame()           //遊戲重新開始
    {
        score = 0;
        initMap();
        SetWall();
        CreateRect();
        repaint();
        StopGame();
    }

    public void StopGame()          //遊戲暫停
    {
        timer.stop();
        repaint();
    }

    public void ContinueGame()      //繼續遊戲
    {
        timer.start();
    }

    public boolean isGameStop()     //遊戲是否暫停
    {
        if(timer.isRunning()){
            return false;
        }else{
            return true;
        }
    }

    class reSized extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            int Height = e.getComponent().getHeight() / 22;
            int Width = e.getComponent().getWidth() / 12;

            if(Height > Width){
                RectWidth = Width - 2;
            }else{
                RectWidth = Height - 2;
            }
        }
    }
}

