import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.Random;

//遊戲類別
class Competitor extends JPanel {
    Image wallCube;
    Image orangeCube;
    Image redCube;
    Image blueCube;
    Image greenCube;
    Image blackCube;

    Timer timer;

    private int mapRow = 22;                            // 地圖長
    private int mapCol = 12;                            // 地圖寬
    private int mapGame[][] = new int[mapRow][mapCol];  //地圖
    private int score = 0;                              //記錄成績

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

    public Competitor()                                   //建構函式----建立好地圖
    {
        try{
            redCube = ImageIO.read(new File("redCube.png"));
            orangeCube = ImageIO.read(new File("orangeCube.png"));
            blueCube = ImageIO.read(new File("blueCube.png"));
            greenCube = ImageIO.read(new File("greenCube.png"));
            blackCube = ImageIO.read(new File("blackCube.png"));
            wallCube = ImageIO.read(new File("wallCube.png"));
        }catch (Exception e){

        }

        this.addComponentListener(new reSized());
        initMap();      //初始化這個地圖
        SetWall();      //設定牆

        timer = new Timer(500,new TimerListener());
        timer.start();
    }

    class TimerListener implements ActionListener   //監聽Timer
    {
        public void actionPerformed(ActionEvent e)
        {
            repaint();
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
                }
            }
        }

        //座標偏移量
        int x = RectWidth * (mapCol+1) + 10;
        int y = RectWidth;

        g.setFont(new Font( Font.DIALOG, Font.BOLD, RectWidth));
        g.drawString("score = " + score, RectWidth , y * 2);
    }

    public void setMap(int[][] map)
    {
        this.mapGame = map;
    }

    public void setScore(int score)  //設定分數
    {
        this.score = score;
    }

    class reSized extends ComponentAdapter
    {
        @Override
        public void componentResized(ComponentEvent e) {
            int Height = e.getComponent().getHeight() / 22;
            int Width = e.getComponent().getWidth() / 12;

            if(Height > Width){
                RectWidth = Width - 5;
            }else{
                RectWidth = Height - 5;
            }
        }
    }
}