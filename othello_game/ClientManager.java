import java.io.*;
import java.net.*;

public class ClientManager implements Runnable {
    private Socket socket;          // クライアントのソケット
    private BufferedReader br;      // 入力
    private PrintStream ps;         // 出力
    private Server server;          // サーバ
    private int clientNumber;       // クライアント番号
    public boolean playing;         // 対局中であることを示すフラグ

    // コンストラクタ
    public ClientManager(Socket socket, Server server, int clientNumber) {
        this.socket = socket;
        this.server = server;
        this.clientNumber = clientNumber;
        playing = false;

        // 入出力の設定
        try {
            InputStream input = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            ps = new PrintStream(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // クライアントの接続を知らせる
        System.out.println("Client:No." + clientNumber + ":connected:" + socket);
    }

    @Override
    public void run() {
        String input;             // クライアントからの入力
        String[] command;         // 入力を解析するための配列
        String name;              // ユーザ名
        String pass;              // パスワード
        boolean success;          // 新規登録，ログインの成功
        int opponent;             // 相手プレイヤ
        
        try {
            while (true) {
                input = br.readLine();         // クライアントからの入力
                command = input.split(" ");    // 入力を区切り文字で分割
                // 先頭にある命令で分岐
                switch (command[0]) {
                    // 新規登録
                    case "signup":
                        name = command[1];
                        pass = command[2];
                        success = server.signup(name, pass, clientNumber);
                        if (success) {
                            sendCommand(command[0] + " success");
                        } else {
                            sendCommand(command[0] + " fail");
                        }
                        server.sendRoom();
                        break;

                    // ログイン
                    case "login":
                        name = command[1];
                        pass = command[2];
                        success = server.login(name, pass, clientNumber);
                        if (success) {
                            sendCommand(command[0] + " success");
                        } else {
                            sendCommand(command[0] + " fail");
                        }
                        server.sendRoom();
                        break;

                    // ログアウト
                    case "logout":
                        server.logout(clientNumber);
                        br.close();
                        ps.close();
                        socket.close();
                        server.quitClientManager(clientNumber);
                        break;

                    // マッチング
                    case "match":
                        opponent = Integer.parseInt(command[1]);
                        server.send(opponent, command[0] + " " + command[2]);
                        break;

                    // 対局を受理
                    case "accept":
                        opponent = Integer.parseInt(command[1]);
                        server.send(opponent, "black");
                        sendCommand("white");
                        playing = true;
                        server.setPlayingState(opponent, true);
                        server.sendRoom();
                        System.out.println("Client:No."+opponent+" and Client:No."+clientNumber+" started game");
                        break;

                    // 対局を拒否
                    case "reject":
                        opponent = Integer.parseInt(command[1]);
                        server.send(opponent, command[0]);
                        break;

                    // 相手に制限時間を送信
                    case "time":
                        opponent = Integer.parseInt(command[1]);
                        String time = command[2];
                        server.send(opponent, command[0] + " " + time);
                        break;

                    // 相手に制限時間を確認したことを通知
                    case "timeOk":
                        opponent = Integer.parseInt(command[1]);
                        server.send(opponent, command[0]);
                        break;

                    // 対局終了時の処理
                    case "finish":
                        playing  = false;
                        server.sendRoom();
                        System.out.println("Client:No." + clientNumber + " finished game");
                        break;
                
                    // 置いた石の位置を相手に送信
                    default:
                        opponent = Integer.parseInt(command[0]);
                        server.send(opponent, command[1] + " " + command[2] + " " + command[3]);
                        break;
                }
            }
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    // 対応するクライアントに送信
    public synchronized void sendCommand(String command) {
        ps.println(command);
        ps.flush();
    }
}
