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

class Server
{
    protected static Vector<Socket> sockets = new Vector<>();
    protected static Vector<String> usernames = new Vector<>();

    Server(){
    }

    void startServer() throws IOException {
        //建立服務端
        ServerSocket server = new ServerSocket(6600);
        boolean flag = true;
        int nowID = 0;

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
                nowID++;
                //多個伺服器執行緒進行對客戶端的響應
                Thread thread = new Thread(new ServerThread(accept,nowID));
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
    int userID;

    final int _SEND_MSG = 0;
    final int _GET_MSG = 1;
    final int _SET_NAME = 2;
    final int _SEND_GAMEMAP = 3;
    final int _GET_GAMEMAP = 4;
    final int _GET_COMPETITOR = 5;
    final int _SEND_COMPETITOR = 6;
    final int _SET_ID = 7;
    final int _SET_ONLINEMEMBER = 8;

    ServerThread(Socket socket, int userID){
        this.socket = socket;
        this.userID = userID;
    }

    @Override
    public void run()
    {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //設定該客戶端的端點地址
            socketName = socket.getRemoteSocketAddress().toString();

            //客戶端設定用戶名稱
            userName = new JSONObject(reader.readLine()).getString("name");
            synchronized (usernames){
                usernames.add(new String(userName));
            }

            //寄送在線用戶給每個用戶更新資訊
            sendMember();

            //寄送ID給用戶
            sendID();

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

                /**
                 * 解析Json
                 * 透過action了解用戶動作在做行動
                 */
                switch (json.getInt("action")) {
                    case _SEND_MSG: //向聊天室發送訊息
                        {
                        String getMsg = json.getString("msg");
                        String sendMsg = "[" + socketName + "] " + userName + " : " + getMsg;
                        System.out.println(sendMsg);
                        //向線上客戶端輸出資訊
                        print(sendMsg);
                        break;
                    }
                    case _SET_NAME: //客戶端設定名稱
                        {
                        String name = json.getString("name");
                        setUserName(name);
                        String sendMsg = "[" + socketName + "] " + userName + " 名稱已更改為 " + name;
                        System.out.println(sendMsg);
                        //向線上客戶端輸出資訊
                        print(sendMsg);
                        break;
                    }
                    case _SEND_COMPETITOR: //寄送對戰邀請給對手
                        {
                        String toUserAddress = json.getString("touseraddress"); //欲尋找的對手位址
                        String UserAddress = json.getString("useraddress");     //發來的Client端位置
                        String UserName = json.getString("username");           //發來的Client端名稱

                        //透過Adress尋找對手
                        for(Socket sc : sockets){
                            if(sc.getRemoteSocketAddress().toString().equals(toUserAddress))
                            {
                                //找到了
                                System.out.println("開始寄送邀請");

                                //寄送邀請
                                JSONObject Json = new JSONObject();
                                Json.put("action",_GET_COMPETITOR);
                                Json.put("useraddress",UserAddress);
                                Json.put("username",UserName);

                                synchronized (sc){
                                    PrintWriter out = new PrintWriter(sc.getOutputStream());
                                    out.println(Json.toString());
                                    out.flush();
                                }
                            }
                        }
                        break;
                    }
                    case _SEND_GAMEMAP: //寄送當前地圖給對手
                        {
                        String toUserAddress = json.getString("touseraddress");

                        //透過Adress尋找對手
                        for(Socket sc : sockets){
                            if(sc.getRemoteSocketAddress().toString().equals(toUserAddress))
                            {
                                //找到了開始寄送地圖
                                JSONObject Json = new JSONObject();
                                Json.put("action",_GET_GAMEMAP);
                                Json.put("map",json.get("map"));
                                Json.put("score",json.getInt("score"));
                                synchronized (sc){
                                    PrintWriter out = new PrintWriter(sc.getOutputStream());
                                    out.println(Json.toString());
                                    out.flush();
                                }
                            }
                        }
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
    private void print(String msg) throws IOException
    {
        PrintWriter out = null;

        JSONObject Jmsg = new JSONObject();
        Jmsg.put("action",_GET_MSG);
        Jmsg.put("msg",msg);

        synchronized (sockets){
            for (Socket sc : sockets){
                out = new PrintWriter(sc.getOutputStream());
                out.println(Jmsg.toString());
                out.flush();
            }
        }
    }

    /**
     * 寄送在線用戶資訊
     * @throws IOException
     */
    private void sendMember() throws IOException
    {
        JSONObject json = new JSONObject();
        String [] member = new String[usernames.size()];
        String [] address = new String[sockets.size()];
        synchronized (sockets){
            int i = 0;
            for (Socket sc : sockets){
                address[i] = sc.getRemoteSocketAddress().toString();
                i++;
            }
        }
        synchronized (usernames){
            int i = 0;
            for (String name : usernames){
                member[i] = name;
                i++;
            }
        }
        json.put("action",_SET_ONLINEMEMBER);
        json.put("member",member);
        json.put("address",address);

        PrintWriter out = null;
        synchronized (sockets){
            for (Socket sc : sockets){
                out = new PrintWriter(sc.getOutputStream());
                out.println(json.toString());
                out.flush();
            }
        }
    }

    /**
     * 設定用戶ID
     * @throws IOException
     */
    private void sendID() throws IOException
    {
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        JSONObject Jmsg = new JSONObject();
        Jmsg.put("action",_SET_ID);
        Jmsg.put("id", userID);
        out.println(Jmsg.toString());
        out.flush();
    }

    /**
     * 關閉該socket的連線
     * @throws IOException
     */
    public void closeConnect() throws IOException
    {
        System.out.println("[" + socketName + "] " + userName + "已退出聊天");
        print("[" + socketName + "] " + userName + "已退出聊天");
        //移除沒連線上的客戶端
        synchronized (sockets){
            sockets.remove(socket);
        }
        usernames.remove(userName);
        //退出時更新客戶端Member
        sendMember();
        socket.close();
    }

    /**
     * 設定用戶名稱
     * @param userName
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}