class OthelloController {
    private Client client;                       // クライアントのクラス
    private OthelloModel othelloModel;           // オセロ対局の処理をするクラス
    private OthelloView othelloView;             // 対局時のGUIを提供するクラス

    public OthelloController(Client client) {
        this.client = client;
    }

    public void setModel(OthelloModel om) {
        othelloModel = om;
    }

    public void setView(OthelloView ov) {
        othelloView = ov;
    }

    // Clientから受け取った情報に対する処理
    public void processInput(String input) {
        String[] command = input.split(" ");

        switch (command[0]) {
            // 対局を開始
            case "start":
                othelloModel.initBoard();
                othelloView.createOthelloDisplay();

                int minute = othelloModel.getTime() / 60;
                int second = othelloModel.getTime() % 60;
                if (second < 10) {
                    othelloView.setTime(minute + ":0" + second);
                } else {
                    othelloView.setTime(minute + ":" + second);
                }

                if (othelloModel.myturn) {
                    othelloView.setPlayerInfo(client.getPlayerName(), "黒");
                } else {
                    othelloView.setPlayerInfo(client.getPlayerName(), "白");
                }

                othelloView.paintTurn(othelloModel.myturn);
                othelloView.startTime(othelloModel.getTime());
                break;

            // 制限時間をセット
            case "time":
                int time = Integer.parseInt(command[1]);
                othelloModel.setTime(time);
                break;

            // 自分が先手（黒）
            case "black":
                othelloModel.myturn = true;
                break;

            // 自分が後手（白）
            case "white":
                othelloModel.myturn = false;
                break;
        
            // 相手の手を盤面に反映
            default:
                othelloView.stopTime();
                int color = Integer.parseInt(command[0]);
                int x = Integer.parseInt(command[1]);
                int y = Integer.parseInt(command[2]);
                othelloModel.put(color, x, y);
                othelloView.updateBoard(othelloModel.getBoard());
                othelloView.paintCount(othelloModel.black, othelloModel.white);
                othelloModel.changeTurn();
                othelloView.paintTurn(othelloModel.myturn);
                othelloView.startTime(othelloModel.getTime());
                // 盤面が埋まった場合
                if (othelloModel.empty == 0) {
                    // 終了
                    othelloView.stopTime();
                    othelloView.paintFinishDisplay(othelloModel.black, othelloModel.white);
                    othelloModel.myturn = false;
                }
                // 次の手番で自分のコマの置き場所がなかった場合
                else if (!othelloModel.putExist()) {
                    // パス
                    othelloView.stopTime();
                    othelloView.paintPassDisplay();
                    othelloModel.changeTurn();
                    othelloView.paintTurn(othelloModel.myturn);
                    othelloView.startTime(othelloModel.getTime());
                    // その次の相手の手番でコマが置けない
                    if (!othelloModel.putExist()) {
                        // 終了
                        othelloView.stopTime();
                        othelloView.paintFinishDisplay(othelloModel.black, othelloModel.white);
                        othelloModel.myturn = false;
                    }
                }
        }
    }

    // プレイヤの操作に対する処理
    public void processEvent(String event) {
        String[] command = event.split(" ");

        // 時間切れ
        if (event.equals("timeover")) {
            othelloView.paintFinishDisplay(othelloModel.black, othelloModel.white);
            if (othelloModel.myturn) {
                othelloView.setMessage("あなたの負けです");
            }
            else {
                othelloView.setMessage("あなたの勝ちです");
            }
        }

        // 対局終了を確認
        else if (event.equals("end")) {
            othelloView.closeDialog();
            // client.clearDisplay();
            othelloView.closeOthelloDisplay();
            client.send("finish");
            client.notifyPlayerCtrl("end");
        }

        // コマを置く処理
        else {
            int color = othelloModel.mycolor;
            int x = Integer.parseInt(command[0]);
            int y = Integer.parseInt(command[1]);

            // 自分手番かつ指定した場所における
            if (othelloModel.myturn && othelloModel.canPut(x, y)) {
                othelloView.stopTime();
                othelloModel.put(color, x, y);
                othelloView.updateBoard(othelloModel.getBoard());
                othelloView.paintCount(othelloModel.black, othelloModel.white);
                client.send(client.getAddress() + " " + color + " " + event);
                othelloModel.changeTurn();
                othelloView.paintTurn(othelloModel.myturn);
                othelloView.startTime(othelloModel.getTime());
                // 盤面が埋まった場合
                if (othelloModel.empty == 0) {
                    // 終了
                    othelloView.stopTime();
                    othelloView.paintFinishDisplay(othelloModel.black, othelloModel.white);
                    othelloModel.myturn = false;
                }
                // 次の手番で相手のコマの置き場所がなかった場合
                else if (!othelloModel.putExist()) {
                    // パス
                    othelloView.stopTime();
                    othelloView.paintPassDisplay();
                    othelloModel.changeTurn();
                    othelloView.paintTurn(othelloModel.myturn);
                    othelloView.startTime(othelloModel.getTime());
                    // その次の自分の手番でコマが置けない
                    if (!othelloModel.putExist()) {
                        // 終了
                        othelloView.stopTime();
                        othelloView.paintFinishDisplay(othelloModel.black, othelloModel.white);
                        othelloModel.myturn = false;
                    }
                }
            }
        }
    }
}