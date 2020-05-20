/**
 * @file Game.java
 * @brief Java Swing視窗測試
 *
 * @author bbb233456@gmail.com
 * @date 2020/05/13
 * */
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Vector;

public class GameServer {
    public static void main(String [] args) throws IOException {
        Server server = new Server();
        server.startServer();
    }
}

class Server {
    protected static Vector<Socket> sockets = new Vector<>();

    Server(){
    }

    void startServer() throws IOException {
        //建立服務端
        ServerSocket server = new ServerSocket(6600);
        boolean flag = true;

        System.out.println("開始監聽，等待用戶連線");
        System.out.println("IP Address : " + server.getLocalSocketAddress());
        System.out.println("PORT : " + server.getLocalPort());

        //接受客戶端請求
        while (flag){
            try {
                //阻塞等待客戶端的連線
                Socket accept = server.accept();
                synchronized (sockets){
                    sockets.add(accept);
                }
                //多個伺服器執行緒進行對客戶端的響應
                Thread thread = new Thread(new ServerThread(accept));
                thread.start();
                //捕獲異常。
            }catch (Exception e){
                flag = false;
                e.printStackTrace();
            }
        }
        //關閉伺服器
        server.close();
    }
}

//每個連線的Socket
class ServerThread extends Server implements Runnable{
    Socket socket;
    String socketName;
    String userName;

    final int _SEND_MSG = 0;
    final int _GET_MSG = 1;
    final int _SET_NAME = 2;

    ServerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //設定該客戶端的端點地址
            socketName = socket.getRemoteSocketAddress().toString();

            //客戶端設定用戶名稱
            userName = new JSONObject(reader.readLine()).getString("name");

            //廣播用戶加入連線
            System.out.println("[" + socketName + "] " + userName + " 已加入聊天");
            print("[" + socketName + "] " + userName + " 已加入聊天");

            boolean flag = true;
            while (flag)
            {
                //阻塞，等待該客戶端的輸出流
                String line = reader.readLine();

                JSONObject json = new JSONObject(line);

                //若客戶端退出，則退出連線。
                if (line == null){
                    flag = false;
                    continue;
                }

                switch (json.getInt("action")) {
                    case _SEND_MSG: {
                        String getMsg = json.getString("msg");
                        String sendMsg = "[" + socketName + "] " + userName + " : " + getMsg;
                        System.out.println(sendMsg);
                        //向線上客戶端輸出資訊
                        print(sendMsg);
                        break;
                    }
                    case _SET_NAME:{
                        String name = json.getString("name");
                        setUserName(name);
                        String sendMsg = "[" + socketName + "] " + userName + " 名稱已更改為 " + name;
                        System.out.println(sendMsg);
                        //向線上客戶端輸出資訊
                        print(sendMsg);
                        break;
                    }
                }
            }
            closeConnect();
        } catch (IOException e) {
            try {
                closeConnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 向所有線上客戶端socket轉發訊息
     * @param msg
     * @throws IOException
     */
    private void print(String msg) throws IOException {
        PrintWriter out = null;

        synchronized (sockets){
            for (Socket sc : sockets){
                out = new PrintWriter(sc.getOutputStream());
                out.println(msg);
                out.flush();
            }
        }
    }

    /**
     * 關閉該socket的連線
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        System.out.println("[" + socketName + "] " + userName + "已退出聊天");
        print("[" + socketName + "] " + userName + "已退出聊天");
        //移除沒連線上的客戶端
        synchronized (sockets){
            sockets.remove(socket);
        }
        socket.close();
    }

    /**
     * 關閉該socket的連線
     * @throws IOException
     */
    public void setUserName(String userName){
        this.userName = userName;
    }
}