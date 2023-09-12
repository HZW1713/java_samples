import java.net.*;
import java.util.*;

public class Server {
    private final String password = "123456";                                   // 管理者パスワード
    private final int MAX = 10;                                                 // 最大接続数
    private final int portNumber = 8004;                                        // ポート番号（仮）
    private String[] room = new String[MAX];                                    // ルーム
    private HashMap<String, String> users = new HashMap<String, String>();      // ユーザ登録，ログイン用のハッシュマップ
    private Thread[] clientThread = new Thread[MAX];                            // クライアントを管理するスレッド
    private ClientManager[] clientManager = new ClientManager[MAX];             // クライアントを管理するクラス

    public static void main(String[] args) {
        Server server = new Server();
        server.addminLogin();
        server.serverProcess();
    }

    // 管理者認証
    public void addminLogin() {
        Scanner stdin = new Scanner(System.in);
        String input;

        // 管理者ログイン処理
        boolean flag = true;
        System.out.println("パスワードを入力してください");
        while(flag) {
            System.out.printf("password: ");
            input = stdin.nextLine();
            if (input.equals(password)) {
                flag = false;
            }
        }
    }

    // クライアントの接続
    public void serverProcess() {
        try(ServerSocket serverSocket = new ServerSocket(portNumber);) {
            System.out.println("ServerSocket="+serverSocket); 
            while (true) {
                int p;
                for (p = 0; p < MAX; p++){
                    if (clientManager[p] == null)
                        break;
                }
                if (p == MAX)
                    continue;
        
                Socket socket = serverSocket.accept( );
                
                //クライアントに応対するスレッドを作成し、処理を任せる
                clientManager[p] = new ClientManager(socket, this, p); 
                clientThread[p] = new Thread(clientManager[p]);      
                clientThread[p].start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // アカウントの新規作成
    public boolean signup(String name, String pass, int clientNumber) {
        if (users.containsKey(name)) {
            return false;
        } else {
            users.put(name, pass);
            room[clientNumber] = name;
            return true;
        }
    }

    // ログイン処理
    public boolean login(String name, String pass, int clientNumber) {
        if (users.containsKey(name) && users.get(name).equals(pass)) {
            room[clientNumber] = name;
            return true;
        } else {
            return false;
        }
    }

    // ログアウト処理
    public void logout(int clientNumber) {
        room[clientNumber] = null;
        sendRoom();
    }

    // クライアントを管理するスレッドを終了
    public void quitClientManager(int clientNumber) {
        clientManager[clientNumber] = null;
        clientThread[clientNumber] = null;
        System.out.println("Client:No."+clientNumber+":quit");
    }

    // クライアントにルームの情報（ログイン中のユーザ名とクライアント番号）を送信
    // 先頭に送る情報，区切り文字は半角スペース(" ")
    public synchronized void sendRoom() {
        String command = "room";
        for (int i = 0; i < MAX; i++) {
            if (room[i] != null && !clientManager[i].playing) {
                command = command + " " + room[i] + " " + Integer.toString(i);
            }
        }

        for (int i = 0; i < MAX; i++) {
            if (clientManager[i] != null && room[i] != null) {
                clientManager[i].sendCommand(command);
            }
        }
    }

    // 指定したクライアントに情報を送信
    public void send(int clientNumber, String command) {
        clientManager[clientNumber].sendCommand(command);
    }

    // 対局の状況を設定する
    public void setPlayingState(int clientNumber, boolean state) {
        clientManager[clientNumber].playing = state;
    }
}