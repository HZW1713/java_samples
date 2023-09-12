public class PlayerController {
    private Client client;                                         // クライアントのクラス
    private PlayerModel playerModel;                               // プレイヤ情報を保管するクラス
    private PlayerView playerView;                                 // 対局画面以外のGUIを備えるクラス

    private boolean connected = false;                             // サーバに接続したことを示すフラグ
    private String matching;                                       // 対局する相手プレイヤ名を一時的に保管する変数
    private boolean matchFlag = true;                              // マッチング可能なことを示すフラグ
    private boolean sentTimeFlag = false;                          // 制限時間を送ったことを示すフラグ
    private boolean recievedTimeFlag = false;                      // 相手が選択した制限時間を受け取ったことを示すフラグ
    private int myTime;                                            // 自分が選択した制限時間を一時的に保管する変数
    private int opposTime;                                         // 相手が選択した制限時間を一時的に保管する変数
    private boolean[] startFlag = new boolean[] {false, false};    // 対局開始を同期するためのフラグ

    PlayerController(Client client) {
        this.client = client;
    }

    public void setModel(PlayerModel pm) {
        playerModel = pm;
    }

    public void setView(PlayerView pv) {
        playerView = pv;
    }

    // サーバから受け取った情報に対する処理
    public void processInput(String input) {
        String[] command = input.split(" ");

        switch (command[0]) {
            // 新規登録の結果
            case "signup":
                if (command[1].equals("success")) {
                    playerView.closeLoginDisplay();
                    playerView.createMatchingDisplay();
                }
                else {
                    // ユーザ名：半角英数字20以下，パスワード：半角数字4文字以上
                    playerView.setMessage("ユーザ名またはパスワードが正しくありません");
                    playerView.enableOKButton();
                }
                break;

            // ログインの結果
            case "login":
                if (command[1].equals("success")) {
                    playerView.closeLoginDisplay();
                    playerView.createMatchingDisplay();
                }
                else {
                   playerView.setMessage("ユーザ名またはパスワードが正しくありません");
                   playerView.enableOKButton();
                }
                break;

            // ルーム情報
            case "room":
                String[] room = new String[(command.length-1)/2];
                String[] opposInfo = new String[command.length-1];
                int j = 0;
                for (int i = 1; i < command.length; i++) {
                    if (!command[i].equals(playerModel.getPlayerName())) {
                        if (i % 2 == 1) {
                            room[j++] = command[i];
                            opposInfo[i-1] = command[i];
                        } else {
                            opposInfo[i-1] = command[i];
                        }
                    }
                }
                playerView.updateRoom(room);
                playerModel.setOpponents(opposInfo);
                break;

            // 対局が成立
            case "black":
                playerView.successMatchingDisplay();
                playerView.setTurn("あなた：先手（黒）", "相手プレイヤ：後手（白）");
                break;
            
            // 対局が成立
            case "white":
                playerView.closeDialog();
                playerView.successMatchingDisplay();
                playerView.setTurn("あなた：後手（白）", "相手プレイヤ：先手（黒）");
                break;

            // 対局要求が拒否された
            case "reject":
                playerView.failMatchingDisplay();
                matchFlag = true;
                break;

            // 相手の対局要求
            case "match":
                if (matchFlag) {
                    matchFlag = false;
                    playerView.requestMatchingDisplay();
                    playerView.setMessage(command[1]+"から対局の要求が来ています");
                    matching = command[1];
                    client.setAddress(playerModel.getOpposClientNum(matching));
                    playerView.setMatchButtonState(false);
                    playerView.setLogoutButtonState(false);
                }
                else {
                    client.send("reject " + playerModel.getOpposClientNum(command[1]));
                }
                break;

            // 相手が選択した制限時間
            case "time":
                recievedTimeFlag = true;
                opposTime = Integer.parseInt(command[1]);
                if (sentTimeFlag) {
                    opposTime = Integer.parseInt(command[1]);
                    int t = (myTime + opposTime) / 2;
                    String minute = Integer.toString(t / 60);
                    String second = Integer.toString(t % 60);
                    if (t%60 < 10) {
                        second = "0"+ second;
                    }
                    playerView.closeDialog();
                    playerView.createVerifyTimeDisplay();
                    playerView.setMessage("制限時間は " + minute + ":" + second + " に決定されました");
                    client.notifyOthelloCtrl(command[0] + " " + t);
                    sentTimeFlag = false;
                    recievedTimeFlag = false;
                }
                break;

            // 相手が制限時間を確認した
            case "timeOk":
                startFlag[1] = true;
                if (startFlag[0] && startFlag[1]) {
                    playerView.closeDialog();
                    playerView.closeMatchingDisplay();
                    client.notifyOthelloCtrl("start");
                    startFlag[0] = false;
                    startFlag[1] = false;
                }
                break;

            // 対局が終了した
            case "end":
                playerView.createMatchingDisplay();
                matchFlag = true;
                break;

            default:
                break;
        }
    }

    // プレイヤの操作に対する処理
    public void processEvent(String event) {
        String output = null;      // サーバへ送信する情報
        String address;            // 宛先

        switch (event) {
            // ログイン画面でOKが押された
            case "loginOk":
                boolean flag = true;
                output = playerView.getLoginOpSelection();

                String user = playerView.getUserName();
                // ユーザ名の文字数が正しく，かつ区切り文字が使用されていないことをチェック
                if (user.length() == 0 || user.length() > 20) {
                    flag = false;
                }
                else {
                    for (int i = 0; i < user.length(); i++) {
                        if (user.charAt(i) == ' ') {
                            flag = false;
                            break;
                        }
                    }
                }

                String pswd = playerView.getPassword();
                // パスワードが4文字以上20文字以下の数字かチェック
                if (pswd.length() < 4 || pswd.length() > 20) {
                    flag = false;
                }
                else {
                    for (int i = 0; i < pswd.length(); i++) {
                        if (pswd.charAt(i) < '0' || pswd.charAt(i) > '9') {
                            flag = false;
                            break;
                        }
                    }
                }

                if (flag) {
                    playerModel.setName(user);
                    output += " " + user + " " + pswd;
                    // サーバに接続済み
                    if (connected) {
                        client.send(output);
                    }
                    // サーバに未接続
                    else if (client.makeSocket()) {
                        connected = true;
                        client.send(output);
                    }
                    // 接続失敗
                    else {
                        playerView.setMessage("サーバに接続できませんでした");
                        playerView.enableOKButton();
                    }
                }
                else {
                    playerView.setMessage("ユーザ名またはパスワードが正しくありません");
                    playerView.enableOKButton();
                }
                break;

            // ログアウトボタンが押された
            case "logout":
                connected = false;
                client.send(event);
                client.closeSocket();
                playerView.closeMatchingDisplay();
                playerView.createLoginDisplay();
                playerView.enableOKButton();
                break;

            // マッチングボタンが押された
            case "match":
                String opponent = playerView.getOpponent();
                address = playerModel.getOpposClientNum(opponent);
                client.setAddress(address);
                output = event + " " + address + " " + playerModel.getPlayerName();
                client.send(output);
                matchFlag = false;
                break;

            // 相手の対局要求を許可
            case "accept":
                output = event + " " + client.getAddress();
                client.send(output);
                playerView.closeDialog();
                break;

            // 相手の対局要求を拒否
            case "reject":
                output = event + " " + client.getAddress();
                client.send(output);
                playerView.closeDialog();
                playerView.failMatchingDisplay();
                matchFlag = true;
                break;

            // 対局の成立を確認
            case "matchOK":
                // 制限時間の設定画面を描画
                playerView.closeDialog();
                playerView.createSetTimeDisplay();
                break;

            // 制限時間が選択された
            case "time":
                sentTimeFlag = true;
                switch (playerView.getTime()) {
                    case "0:30":
                        myTime = 30;
                        break;

                    case "1:00":
                        myTime = 60;
                        break;

                    case "1:30":
                        myTime = 90;
                        break;

                    case "2:00":
                        myTime = 120;
                        break;

                    case "2:30":
                        myTime = 150;
                        break;

                    case "3:00":
                        myTime = 180;
                        break;
                
                    default:
                        break;
                }
                output = event + " " + client.getAddress() + " " + myTime;
                client.send(output);
                
                if (recievedTimeFlag) {
                    int t = (myTime + opposTime) / 2;
                    String minute = Integer.toString(t / 60);
                    String second = Integer.toString(t % 60);
                    if (t%60 < 10) {
                        second = "0"+ second;
                    }
                    playerView.closeDialog();
                    playerView.createVerifyTimeDisplay();
                    playerView.setMessage("制限時間は " + minute + ":" + second + " に決定されました");
                    client.notifyOthelloCtrl(event + " " + t);
                    recievedTimeFlag = false;
                    sentTimeFlag = false;
                }
                break;

            // 制限時間が確認された
            case "timeOk":
                output = event + " " + client.getAddress();
                startFlag[0] = true;
                client.send(output);
                if (startFlag[0] && startFlag[1]) {
                    playerView.closeDialog();
                    playerView.closeMatchingDisplay();
                    client.notifyOthelloCtrl("start");
                    startFlag[0] = false;
                    startFlag[1] = false;
                }
                break;
            
            // 退出処理(exit)
            default:
                if (connected) {
                    client.send("logout");
                }
                break;
        }
    }
}