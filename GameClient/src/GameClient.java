import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

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
    private JButton InviteBtn;
    private JButton StopGame;
    private JButton ContinueGame;
    private JPanel left;
    private JTextField textIP;
    private JTextField TextPort;
    private JButton connectBtn;
    private JButton clearBtn;
    private JPanel GamePanel;
    private JButton NewGame;
    private JLabel stateLabel;
    private JTabbedPane tab;
    private JTextArea talk;
    private JTable onlineTable;
    private JPanel talkPanel;
    private JPanel onlinePanel;
    private JPanel AnotherGame;
    private JLabel IPLabel;
    private JLabel PortLabel;
    private DefaultTableModel TableModel = new DefaultTableModel();

    Socket socket;
    BufferedReader input;
    PrintWriter out;

    final int _SEND_MSG = 0;
    final int _GET_MSG = 1;
    final int _SET_NAME = 2;
    final int _SEND_GAMEMAP = 3;
    final int _GET_GAMEMAP = 4;
    final int _GET_COMPETITOR = 5;
    final int _SEND_COMPETITOR = 6;
    final int _SET_ID = 7;
    final int _SET_ONLINEMEMBER = 8;
    final int _SEND_ITEM = 9;
    final int _GET_ITEM = 10;

    ButtonTrack connectListener;

    Game game;
    Competitor competitor;

    String toUserAdress;

    int clientID;
    String userName;

    Thread send = null;

    //視窗長寬設定
    private int window_Width = 1500;
    private int window_Height = 700;

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
        final JPanel spacer1 = new JPanel();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        bottom.add(spacer1, gbc);
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
        IPLabel = new JLabel();
        IPLabel.setText("輸入IP : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        top.add(IPLabel, gbc);
        PortLabel = new JLabel();
        PortLabel.setText("輸入Port : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        top.add(PortLabel, gbc);
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
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        top.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        top.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 5;
        top.add(spacer4, gbc);
        stateLabel = new JLabel();
        stateLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        top.add(stateLabel, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 30;
        top.add(spacer5, gbc);
        right = new JPanel();
        right.setLayout(new GridBagLayout());
        MainPanel.add(right, BorderLayout.EAST);
        InviteBtn = new JButton();
        InviteBtn.setText("SendInvite");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        right.add(InviteBtn, gbc);
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
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 50;
        right.add(spacer6, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 50;
        right.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 50;
        right.add(spacer8, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 50;
        right.add(spacer9, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 50;
        right.add(spacer10, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 15;
        right.add(spacer11, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        right.add(spacer12, gbc);
        center = new JPanel();
        center.setLayout(new GridBagLayout());
        MainPanel.add(center, BorderLayout.CENTER);
        GamePanel = new JPanel();
        GamePanel.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        center.add(GamePanel, gbc);
        AnotherGame = new JPanel();
        AnotherGame.setLayout(new BorderLayout(0, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        center.add(AnotherGame, gbc);
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        center.add(spacer13, gbc);
        left = new JPanel();
        left.setLayout(new GridBagLayout());
        MainPanel.add(left, BorderLayout.WEST);
        clearBtn = new JButton();
        clearBtn.setText("Clear");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        left.add(clearBtn, gbc);
        final JPanel spacer14 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        left.add(spacer14, gbc);
        final JPanel spacer15 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        left.add(spacer15, gbc);
        sendMsg = new JTextField();
        sendMsg.setColumns(30);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        left.add(sendMsg, gbc);
        final JPanel spacer16 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipady = 10;
        left.add(spacer16, gbc);
        tab = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 75;
        left.add(tab, gbc);
        talkPanel = new JPanel();
        talkPanel.setLayout(new GridBagLayout());
        tab.addTab("聊天室", talkPanel);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 125;
        talkPanel.add(scrollPane1, gbc);
        talk = new JTextArea();
        talk.setEditable(false);
        Font talkFont = UIManager.getFont("TextArea.font");
        if (talkFont != null) talk.setFont(talkFont);
        talk.setText("嘗試加入聊天室...");
        scrollPane1.setViewportView(talk);
        onlinePanel = new JPanel();
        onlinePanel.setLayout(new GridBagLayout());
        tab.addTab("在線成員", onlinePanel);
        final JScrollPane scrollPane2 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        onlinePanel.add(scrollPane2, gbc);
        onlineTable = new JTable(TableModel);
        scrollPane2.setViewportView(onlineTable);
        final JPanel spacer17 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        left.add(spacer17, gbc);
        final JPanel spacer18 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 20;
        left.add(spacer18, gbc);
        sendBtn = new JButton();
        sendBtn.setText("Send");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        left.add(sendBtn, gbc);

        talk.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        /**
         * 遊戲畫面添加
         */
        game = new Game();
        GamePanel.add(game);
        /**
         * 對手畫面添加
         */
        competitor = new Competitor();
        AnotherGame.add(competitor);

        this.add(MainPanel);    //添加主要Panel
    }

    class ButtonTrack extends MouseAdapter                      //按鈕的滑鼠監聽
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
            if(buttonName.equals("SendInvite")) sendCompetitor();
        }
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
                    userName = JOptionPane.showInputDialog("輸入希望的用戶名稱 : ");
                    if(userName == null) {
                        talk.append("\n用戶取消連線");
                        return;
                    }

                    socket = new Socket(IP, Port);

                    addListenerAfterConnect();
                    connectBtn.removeMouseListener(connectListener);
                    connectBtn.setEnabled(false);
                    stateLabel.setText("已連線至聊天伺服器");

                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    new Thread(){
                        @Override
                        public void run() {
                            try
                            {
                                while(true)
                                {
                                    String inMsg = input.readLine();

                                    JSONObject Json = new JSONObject(inMsg);

                                    switch (Json.getInt("action"))
                                    {
                                        case _GET_MSG:
                                            talk.append("\n" + Json.getString("msg"));
                                            break;
                                        case _SET_ID:
                                            clientID = Json.getInt("id");
                                            talk.append("\n" + "您的使用者ID為 = " + clientID);
                                            break;
                                        case _GET_COMPETITOR:
                                            toUserAdress = Json.getString("useraddress");
                                            JOptionPane.showMessageDialog(null, "取得對手資訊" + toUserAdress + "開始寄送地圖");
                                            System.out.println("取得對手資訊" + toUserAdress + "開始寄送地圖");
                                            sendMap();
                                            break;
                                        case _GET_GAMEMAP:
                                            toUserAdress = Json.getString("useraddress");
                                            getMap(Json);
                                            sendMap();
                                            break;
                                        case _SET_ONLINEMEMBER:
                                            setMember(Json);
                                            break;
                                        case _GET_ITEM:
                                            getItem(Json);
                                            break;
                                        default:
                                            System.out.println(inMsg);
                                            talk.append("\n" + inMsg);
                                            break;
                                    }
                                }
                            }
                            catch (IOException e)
                            {
                                connectBtn.addMouseListener(connectListener);
                                connectBtn.setEnabled(true);
                                stateLabel.setText("伺服器斷線...");
                                System.err.println(e);
                            }
                            catch (Exception e)
                            {
                                System.err.println(e);
                            }
                        }
                    }.start();

                    out = new PrintWriter(socket.getOutputStream());

                    if(userName.isEmpty())
                    {
                        userName = "Client";
                    }

                    JSONObject json = new JSONObject();
                    json.put("action", _SET_NAME);
                    json.put("name", userName);
                    out.println(json.toString());
                    out.flush();
                }
                catch(IOException e)
                {
                    JOptionPane.showMessageDialog(null, "伺服器連線失敗");
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
                        case KeyEvent.VK_X:
                            game.HoldShape();
                            break;
                        case KeyEvent.VK_C:
                            if(socket != null)
                                sendItem();
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

        ContinueGame.addMouseListener(new ButtonTrack(ContinueGame.getText()));
        StopGame.addMouseListener(new ButtonTrack(StopGame.getText()));
        NewGame.addMouseListener(new ButtonTrack(NewGame.getText()));
        connectListener = new ButtonTrack(connectBtn.getText());
        connectBtn.addMouseListener(connectListener);
        clearBtn.addMouseListener(new ButtonTrack(clearBtn.getText()));

        this.addWindowStateListener(new WindowTrack());
    }

    private void addListenerAfterConnect()                      //在連線以後添加監聽
    {
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

        InviteBtn.addMouseListener(new ButtonTrack(InviteBtn.getText()));
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

    private void sendCompetitor()                               //寄送連線邀請
    {
        String toUserAddress = JOptionPane.showInputDialog("輸入希望連線的用戶Address : \n" +
                                                           "範例:\\127.0.0.1:6600");
        if(toUserAddress == null || toUserAddress.isEmpty()){
            return;
        }
        JSONObject Json = new JSONObject();
        Json.put("action",_SEND_COMPETITOR);
        Json.put("useraddress",socket.getLocalSocketAddress().toString());
        Json.put("username", userName);
        Json.put("touseraddress", toUserAddress);

        out.println(Json.toString());
        out.flush();
        System.out.println(Json.toString());
    }

    private void sendMap()
    {
        if(send == null){
            send = new Thread(() -> {
                while(true) {
                    JSONObject json = new JSONObject();
                    json.put("action", _SEND_GAMEMAP);
                    json.put("map", game.getMap());
                    json.put("score",game.getScore());
                    json.put("useraddress",socket.getLocalSocketAddress());
                    json.put("touseraddress", toUserAdress);

                    out.println(json.toString());
                    out.flush();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            send.start();
        }
    }

    private void getMap(JSONObject Json)
    {
        int length1 = Json.getJSONArray("map").length();
        int length2 = Json.getJSONArray("map").getJSONArray(0).length();
        int [][] map = new int [length1][length2];

        for(int i = 0;i < length1;i++){
            for(int o = 0;o < length2;o++){
                map[i][o] = Json.getJSONArray("map").getJSONArray(i).getInt(o);
            }
        }

        competitor.setScore(Json.getInt("score"));
        competitor.setMap(map);
    }

    private void getCompetitor(JSONObject Json)                 //接收連線邀請
    {

    }

    private void sendItem()                                     //寄送使用道具
    {
        if(toUserAdress == null) return;
        JSONObject json = new JSONObject();
        json.put("action", _SEND_ITEM);
        json.put("item", game.useItem());
        json.put("touseraddress", toUserAdress);

        out.println(json.toString());
        out.flush();
    }

    private void getItem(JSONObject json)                                      //使用道具效果
    {
        int item = json.getInt("item");

        switch (item)
        {
            case 1:
                game.AddLine();
                break;
            case 2:
                game.DeleteLine();
                break;
            case 3:
                game.speedUp();
                break;
            case 4:
                game.Block();
                break;
        }
    }

    private class WindowTrack implements WindowStateListener    //視窗狀態監聽
    {
        @Override
        public void windowStateChanged(WindowEvent windowEvent) {
            if(windowEvent.getNewState() == 1) game.StopGame();
        }
    }

    private void setMember(JSONObject Json)                     //設定在線成員
    {
        Vector names = new Vector();            // 列名向量，使用它的add()方法添加列名
        names.add("名稱");
        names.add("位置");

        int memberLen = Json.getJSONArray("member").length();

        Vector data = new Vector();              // 数据行向量集，因为列表不止一行，往里面添加数据行向量，添加方法add(row)
        Vector row = new Vector();              // 数据行向量，使用它的add()添加元素，比如整数、String、Object等，有几行就new几个行向量
        for(int i = 0;i < memberLen;i++)
        {
            row.add(Json.getJSONArray("member").getString(i));
            row.add(Json.getJSONArray("address").getString(i));

            data.add(row.clone());

            row = new Vector();
        }

        TableModel.setDataVector(data, names);   // 设置模型中的元素，它会自动显示在列表中
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
         * 設定視窗標題
         */
        this.setTitle("Tiramisu Tetris");
        /**
         * 設定視窗可見
         */
        this.setVisible(true);

    }
}