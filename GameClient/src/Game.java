import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.Random;
import java.util.TimerTask;

//遊戲類別
class Game extends JPanel {
    /*加載方塊圖片檔案*/
    Image wallCube;
    Image orangeCube;
    Image redCube;
    Image blueCube;
    Image greenCube;
    Image blackCube;
    Image iceCube;
    Image grayCube;

    /*加載道具圖片檔案*/
    Image IceItem;
    Image BombItem;
    Image SpeedItem;
    Image BlockItem;

    //持有道具
    //道具1 冰封
    //道具2 炸彈
    //道具3 加速
    //道具4 阻擋視野
    private int item1 = 0;
    private int item2 = 0;


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

    private int itemScore = 1;

    private boolean block = false;

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
                    {4,4,0,0, 0,4,4,0, 0,0,0,0, 0,0,0,0},
                    {0,4,0,0, 4,4,0,0, 4,0,0,0, 0,0,0,0},
                    {4,4,0,0, 0,4,4,0, 0,0,0,0, 0,0,0,0},
                    {0,4,0,0, 4,4,0,0, 4,0,0,0, 0,0,0,0}
            },
            //J字形按逆時針的順序儲存
            {
                    {0,5,0,0, 0,5,0,0, 5,5,0,0, 0,0,0,0},
                    {5,5,5,0, 0,0,5,0, 0,0,0,0, 0,0,0,0},
                    {5,5,0,0, 5,0,0,0, 5,0,0,0, 0,0,0,0},
                    {5,0,0,0, 5,5,5,0, 0,0,0,0, 0,0,0,0}
            },
            //L字形按逆時針的順序儲存
            {
                    {5,0,0,0, 5,0,0,0, 5,5,0,0, 0,0,0,0},
                    {0,0,5,0, 5,5,5,0, 0,0,0,0, 0,0,0,0},
                    {5,5,0,0, 0,5,0,0, 0,5,0,0, 0,0,0,0},
                    {5,5,5,0, 5,0,0,0, 0,0,0,0, 0,0,0,0}
            },
            //田字形按逆時針的順序儲存
            {
                    {6,6,0,0, 6,6,0,0, 0,0,0,0, 0,0,0,0},
                    {6,6,0,0, 6,6,0,0, 0,0,0,0, 0,0,0,0},
                    {6,6,0,0, 6,6,0,0, 0,0,0,0, 0,0,0,0},
                    {6,6,0,0, 6,6,0,0, 0,0,0,0, 0,0,0,0}
            }
    };

    private int rowRect = 4;        //方塊陣列長
    private int colRect = 4;        //方塊陣列寬
    int RectWidth = 15;     //預設方塊大小

    public Game()                                   //建構函式----建立好地圖
    {
        try{
            redCube = ImageIO.read(new File("./images/redCube.png"));
            orangeCube = ImageIO.read(new File("./images/orangeCube.png"));
            blueCube = ImageIO.read(new File("./images/blueCube.png"));
            greenCube = ImageIO.read(new File("./images/greenCube.png"));
            blackCube = ImageIO.read(new File("./images/blackCube.png"));
            wallCube = ImageIO.read(new File("./images/wallCube.png"));
            iceCube = ImageIO.read(new File("./images/iceCube.png"));
            grayCube = ImageIO.read(new File("./images/grayCube.png"));

            IceItem = ImageIO.read(new File("./images/ice.png"));
            BombItem = ImageIO.read(new File("./images/bomb.png"));
            BlockItem = ImageIO.read(new File("./images/block.png"));
            SpeedItem = ImageIO.read(new File("./images/speed.png"));
        }catch (Exception e){
            System.err.println(e);
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

        ishold = false;

        //如果產生出來的方塊與地圖當前方塊重疊為遊戲結束
        if(GameOver(posx,posy,curShapeType,curShapeState))
        {
            timer.stop();
            int select = JOptionPane.showConfirmDialog(null, "是否繼續?", "遊戲結束", JOptionPane.OK_OPTION);
            if(select == 1){
                System.exit(0);
            }else{
                NewGame();
            }
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

    public void HoldShape()         //Hold功能
    {
        if(holdShapeState == -1  && holdShapeType == -1){
            //將當前方塊型態保存
            holdShapeState = curShapeState;
            holdShapeType = curShapeType;

            //切換下次的方塊
            curShapeType = nextShapeType;
            curShapeState = nextShapeState;

            //產生下次方塊
            nextShapeType = random.nextInt(shapes.length);
            nextShapeState = random.nextInt(shapes[0].length);

            //產生方塊於地圖中間頂部
            posx = 0;
            posy = (mapCol / 2) - 1;

            ishold = true;

            //如果產生出來的方塊與地圖當前方塊重疊為遊戲結束
            if(GameOver(posx,posy,curShapeType,curShapeState))
            {
                timer.stop();
                JOptionPane.showConfirmDialog(null, "遊戲結束！", "提示", JOptionPane.OK_OPTION);
                System.exit(0);
            }
        }else if(!ishold){
            int tmpState = curShapeState;
            int tmpType = curShapeType;

            curShapeState = holdShapeState;
            curShapeType = holdShapeType;

            holdShapeState = tmpState;
            holdShapeType = tmpType;

            posx = 0;
            posy = (mapCol / 2) - 1;

            ishold = true;
        }
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
        int ScoreBooster = 1;
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
                score += (ScoreBooster*10);
                i++;
                ScoreBooster++;
            }
        }
        if(itemScore <= score/100){
            int item = random.nextInt(4)+1;
            if(item1 == 0){
                item1 = item;
            }else if(item2 == 0){
                item2 = item;
            }
            itemScore++;
            System.out.println("獲得" + item + "道具");
        }
    }

    @Override
    public void paint(Graphics g)   //重新繪製視窗
    {
        super.paint(g);
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.DIALOG,Font.BOLD,24));

        for(int i = 0; i < mapRow; i++)         //刷上底層背景
        {
            for(int j = 0; j < mapCol; j++)
            {
                int x = (j+1)*RectWidth;
                int y = (i+1)*RectWidth + RectWidth * 2;
                g.setColor(Color.GRAY);
                g.fillRect(x, y, RectWidth, RectWidth);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, RectWidth, RectWidth);
                g.setColor(Color.BLACK);
            }
        }

        for(int i = 0; i < mapRow; i++)         //繪製地圖上面已經固定好的方塊資訊
        {
            for(int j = 0; j < mapCol; j++)
            {
                int x = (j+1)*RectWidth;
                int y = (i+1)*RectWidth + RectWidth * 2;
                switch (mapGame[i][j]){
                    case 1:     //預設方格
                        g.drawImage(blackCube,x, y, RectWidth, RectWidth,null);
                        break;
                    case 2:     //牆
                        g.drawImage(wallCube,x, y, RectWidth, RectWidth,null);
                        break;
                    case 3:     //長條
                        g.drawImage(orangeCube,x, y, RectWidth, RectWidth,null);
                        break;
                    case 4:     //Z型
                        g.drawImage(redCube,x, y, RectWidth, RectWidth,null);
                        break;
                    case 5:     //L型
                        g.drawImage(greenCube,x, y, RectWidth, RectWidth,null);
                        break;
                    case 6:     //田型
                        g.drawImage(blueCube,x, y, RectWidth, RectWidth,null);
                        break;
                    case 7:     //道具磚塊
                        g.drawImage(grayCube,x, y, RectWidth, RectWidth,null);
                        break;
                }
            }
        }

        Pridiction();
        g.setColor(Color.DARK_GRAY);
        for(int i = 0; i < rowRect; i++)        //繪製預測的方塊
        {
            for(int j = 0; j < colRect; j++)
            {
                if(shapes[curShapeType][curShapeState][i*colRect+j] != 0)
                {
                    int x = (Pposy+j+1)*RectWidth;
                    int y = (Pposx+i+1)*RectWidth + RectWidth * 2;
                    g.fillRect(x, y, RectWidth, RectWidth);
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
                    int x = (posy+j+1)*RectWidth;
                    int y = (posx+i+1)*RectWidth + RectWidth * 2;
                    switch (shapes[curShapeType][curShapeState][i*colRect+j]){
                        case 1:     //預設方格
                            g.drawImage(blackCube,x, y, RectWidth, RectWidth,null);
                            break;
                        case 3:     //長條
                            g.drawImage(orangeCube,x, y, RectWidth, RectWidth,null);
                            break;
                        case 4:     //Z型
                            g.drawImage(redCube,x, y, RectWidth, RectWidth,null);
                            break;
                        case 5:     //L型
                            g.drawImage(greenCube,x, y, RectWidth, RectWidth,null);
                            break;
                        case 6:     //田型
                            g.drawImage(blueCube,x, y, RectWidth, RectWidth,null);
                            break;
                        default:    //預設
                            g.setColor(Color.CYAN);
                            g.fillRect(x, y, RectWidth, RectWidth);
                            g.setColor(Color.BLACK);
                            break;
                    }
                }
            }
        }

        //座標偏移量
        int x = RectWidth * (mapCol+1) + 10;
        int y = RectWidth;

        g.setFont(new Font( Font.DIALOG, Font.BOLD, RectWidth));
        g.drawString("score = " + score, RectWidth , y * 2);
        g.drawString("Next：", x, y * 3);
        for(int i = 0; i < rowRect; i++)
        {
            for(int j = 0; j < colRect; j++)
            {
                if(shapes[nextShapeType][nextShapeState][i*colRect+j] != 0)
                {
                    switch (shapes[nextShapeType][nextShapeState][i*colRect+j]){
                        case 1:     //預設方格
                            g.drawImage(blackCube,x + (j * RectWidth), (y * 4) + (i * RectWidth), RectWidth, RectWidth,null);
                            break;
                        case 3:     //長條
                            g.drawImage(orangeCube,x + (j * RectWidth), (y * 4) + (i * RectWidth), RectWidth, RectWidth,null);
                            break;
                        case 4:     //Z型
                            g.drawImage(redCube,x + (j * RectWidth), (y * 4) + (i * RectWidth), RectWidth, RectWidth,null);
                            break;
                        case 5:     //L型
                            g.drawImage(greenCube,x + (j * RectWidth), (y * 4) + (i * RectWidth), RectWidth, RectWidth,null);
                            break;
                        case 6:     //田型
                            g.drawImage(blueCube,x + (j * RectWidth), (y * 4) + (i * RectWidth), RectWidth, RectWidth,null);
                            break;
                        default:    //預設
                            g.setColor(Color.CYAN);
                            g.fillRect(x + (j * RectWidth), (y * 4) + (i * RectWidth), RectWidth, RectWidth);
                            g.setColor(Color.BLACK);
                            break;
                    }
                }
            }
        }

        g.drawString("Hold：", x, y * 9);
        if(holdShapeType != -1 && holdShapeState != -1)
        {
            for (int i = 0; i < rowRect; i++) {
                for (int j = 0; j < colRect; j++) {
                    if (shapes[holdShapeType][holdShapeState][i * colRect + j] != 0) {
                        switch (shapes[holdShapeType][holdShapeState][i * colRect + j]) {
                            case 1:     //預設方格
                                g.drawImage(blackCube, x + (j * RectWidth), (y * 10) + (i * RectWidth), RectWidth, RectWidth, null);
                                break;
                            case 3:     //長條
                                g.drawImage(orangeCube, x + (j * RectWidth), (y * 10) + (i * RectWidth), RectWidth, RectWidth, null);
                                break;
                            case 4:     //Z型
                                g.drawImage(redCube, x + (j * RectWidth), (y * 10) + (i * RectWidth), RectWidth, RectWidth, null);
                                break;
                            case 5:     //L型
                                g.drawImage(greenCube, x + (j * RectWidth), (y * 10) + (i * RectWidth), RectWidth, RectWidth, null);
                                break;
                            case 6:     //田型
                                g.drawImage(blueCube, x + (j * RectWidth), (y * 10) + (i * RectWidth), RectWidth, RectWidth, null);
                                break;
                            default:    //預設
                                g.setColor(Color.CYAN);
                                g.fillRect(x + (j * RectWidth), (y * 10) + (i * RectWidth), RectWidth, RectWidth);
                                g.setColor(Color.BLACK);
                                break;
                        }
                    }
                }
            }
        }

        g.drawString("Item：", x, y * 14);
        switch (item1) {
            case 1:     //冰封道具圖案
                g.drawImage(IceItem, x, (y * 15), RectWidth*3, RectWidth*3, null);
                break;
            case 2:     //炸彈道具圖案
                g.drawImage(BombItem, x, (y * 15), RectWidth*3, RectWidth*3, null);
                break;
            case 3:     //加速道具圖案
                g.drawImage(SpeedItem, x, (y * 15), RectWidth*3, RectWidth*3, null);
                break;
            case 4:     //阻擋道具圖案
                g.drawImage(BlockItem, x, (y * 15), RectWidth*3, RectWidth*3, null);
                break;
        }

        g.setFont(new Font( Font.DIALOG, Font.BOLD, RectWidth-5));
        g.drawString("Item2：", x, y * 19 - 5);
        g.setFont(new Font( Font.DIALOG, Font.BOLD, RectWidth));
        switch (item2) {
            case 1:     //冰封道具圖案
                g.drawImage(IceItem, x+RectWidth, (y * 19), RectWidth*2, RectWidth*2, null);
                break;
            case 2:     //炸彈道具圖案
                g.drawImage(BombItem, x+RectWidth, (y * 19), RectWidth*2, RectWidth*2, null);
                break;
            case 3:     //加速道具圖案
                g.drawImage(SpeedItem, x+RectWidth, (y * 19), RectWidth*2, RectWidth*2, null);
                break;
            case 4:     //阻擋道具圖案
                g.drawImage(BlockItem, x+RectWidth, (y * 19), RectWidth*2, RectWidth*2, null);
                break;
        }

        if(block == true)
            g.fillRect(RectWidth , RectWidth,RectWidth * mapCol,RectWidth*(mapRow-6));


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

    public int[][] getMap()
    {
        return mapGame;
    }

    public int getScore()
    {
        return score;
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

    public void speedUp()           //遊戲加速10秒
    {
        timer.setDelay(100);
        new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timer.setDelay(500);
        }).start();
    }

    public void AddLine()           //添加一行
    {
        for(int k = 1; k < mapRow-2; k++)
        {
            for(int p = 1; p < mapCol-1; p++)
            {
                mapGame[k][p] = mapGame[k+1][p];
            }
        }
        int space = random.nextInt(mapCol-2)+1;
        for(int k = mapRow-2; k > mapRow-3; k--)
        {
            for(int p = 1; p < mapCol-1; p++)
            {
                mapGame[k][p] = 7;
            }
            mapGame[k][space] = 0;
        }

    }

    public void DeleteLine()       //強制消除幾行
    {
        for(int i = 0;i < 2;i++){
            for(int k = mapRow-2; k > 0; k--)
            {
                for(int p = 1; p < mapCol-1; p++)
                {
                    mapGame[k][p] = mapGame[k-1][p];
                }
            }
        }
    }

    public void Block()           //阻擋畫面
    {
        block = true;
        new Thread(()->{
            try {
                Thread.sleep(3000);
                block = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int useItem()          //使用道具
    {
        int tmp = item1;
        item1 = item2;
        item2 = 0;
        if(tmp == 2)
        {
            this.DeleteLine();
            tmp = 0;
        }
        System.out.println("你使用道具" + tmp);
        return tmp;
    }

    class reSized extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            int Height = e.getComponent().getHeight() / 22;
            int Width = e.getComponent().getWidth() / 12;

            if(Height > Width){
                RectWidth = Width - 5;
            }else{
                RectWidth = Height - 5;
            }
            repaint();
        }
    }
}