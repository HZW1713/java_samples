import java.net.*;
import java.io.*;

public class Client implements Runnable {
    private String hostName;                     // IPアドレス(ホスト名)
    private int portNumber;                      // ポート番号
    private Socket socket;                       // ソケット
    private BufferedReader br;                   // 入力ストリーム
    private PrintStream ps;                      // 出力ストリーム
    private Thread thread;                       // サーバから情報を受信するスレッド
    private String address;                       // 送信先（対戦相手のクライアント番号）

    private PlayerModel playerModel;             // プレイヤ情報を保管するクラス
    private PlayerView playerView;               // 対局画面以外のGUIを備えるクラス
    private PlayerController playerCtrl;         // PlayerModelとPlayerViewを制御するクラス

    private OthelloModel othelloModel;           // オセロ対局の処理をするクラス
    private OthelloView othelloView;             // 対局時のGUIを提供するクラス
    private OthelloController othelloCtrl;       // OthelloModelとOthelloViewを制御するクラス

    // main関数
    public static void main(String[] args) {
        // コマンドライン引数でIPアドレスとポート番号を指定
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        // クライアントを起動
        new Client(hostName, portNumber);
    }

    // コンストラクタ
    Client(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;

        playerModel = new PlayerModel();
        playerCtrl = new PlayerController(this);
        playerView = new PlayerView(playerCtrl);
        playerCtrl.setModel(playerModel);
        playerCtrl.setView(playerView);

        othelloModel = new OthelloModel();
        othelloCtrl = new OthelloController(this);
        othelloView = new OthelloView(othelloCtrl);
        othelloCtrl.setModel(othelloModel);
        othelloCtrl.setView(othelloView);
    }

    // サーバから情報を受け取るスレッド
    @Override
    public void run() {
        String input;             // サーバからの入力
        String[] command;         // 入力を解析するための配列

        try {
            while ((input = br.readLine()) != null) {
                command = input.split(" ");              // 入力を区切り文字で分割
                switch (command[0]) {
                    case "signup":                       // 新規登録の結果
                    case "login":                        // ログインの結果
                    case "room":                         // ルーム情報
                    case "accept":                       // 対局要求が受理された
                    case "reject":                       // 対局要求が拒否された
                    case "match":                        // 相手の対局要求
                    case "time":                         // 制限時間を受け取った
                    case "timeOk":                       // 制限時間が確認された
                        notifyPlayerCtrl(input);
                        break;

                    case "black":                        // 対局成立と手番の通知
                    case "white":
                        notifyPlayerCtrl(input);
                        notifyOthelloCtrl(input);
                        break;
                    
                    default:                             // 先手後手，相手が石を置いた位置
                        notifyOthelloCtrl(input);
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // サーバに接続
    public boolean makeSocket() {
        try {
            // ソケット作成
            socket = new Socket(hostName, portNumber);
            // 入出力ストリームを設定
            InputStream is = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            OutputStream os = socket.getOutputStream();
            ps = new PrintStream(os);
            // サーバから情報を受信する
            thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            // PlayerControllerに接続失敗を伝える
            return false;
        }
        return true;
    }

    // サーバと接続を切る
    public void closeSocket() {
        try {
            br.close();
            ps.close();
            socket.close();
            address = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        thread = null;
    }

    // サーバに送信
    public void send(String command) {
        ps.println(command);
        ps.flush();
    }

    // OthelloControllerに通知
    public void notifyOthelloCtrl(String n) {
        othelloCtrl.processInput(n);
    }

    // PlayerControllerに通知
    public void notifyPlayerCtrl(String n) {
        playerCtrl.processInput(n);
    }

    // 送信先を設定
    public void setAddress(String address) {
        this.address = address;
    }

    // 送信先を取得
    public String getAddress() {
        return address;
    }

    public String getPlayerName() {
        return playerModel.getPlayerName();
    }
} // Clientクラス
