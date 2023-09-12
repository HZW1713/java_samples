import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class OthelloView implements MouseListener {
    private OthelloController othelloCtrl;

    private JFrame othello;                   // 対局画面
    private ImageIcon whiteIcon;              // 白
    private ImageIcon blackIcon;              // 黒
    private ImageIcon boardIcon;              // 空

	private JButton[][] buttonArray;          // 盤面
    private JTextField timeField;             // 制限時間表示
    private JTextArea numberArea;             // 個数表示
    private JTextField playerInfoField;       // プレイヤ情報
    private JTextField turnField;             // 手番表示

    private Thread timerThread;               // 制限時間用のスレッド
    private Clock timer;                      // 制限時間を計算するクラス

    private JFrame dialog;                    // ダイアログ
    private JLabel message;                   // メッセージ

	private JButton okButton;                 // 終了確認ボタン

    // コンストラクタ
	public OthelloView(OthelloController oc){
		othelloCtrl = oc;

        whiteIcon = new ImageIcon("White.jpg");
        blackIcon = new ImageIcon("Black.jpg");
        boardIcon = new ImageIcon("GreenFrame.jpg");
	}

    // 対局画面を描画
    public void createOthelloDisplay() {
        othello = new JFrame();
        othello.setSize(480, 640);
		othello.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        othello.setTitle("ネットワーク対戦型オセロゲーム");
        othello.setResizable(false);

        // 盤面を構成
        JPanel buttonPanel = new JPanel(new GridLayout(8, 8));
        buttonPanel.setPreferredSize(new Dimension(400, 400));

        buttonArray = new JButton[8][8];
        for (int i = 0; i < buttonArray[0].length; i++) {
            for (int j = 0; j < buttonArray[0].length; j++) {
                buttonArray[j][i] = new JButton();
                buttonArray[j][i].addMouseListener(this);
                buttonPanel.add(buttonArray[j][i]);
            }
        }

        updateBoard(
            new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 2, 0, 0, 0},
                {0, 0, 0, 2, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0}
            }
        );

        // 制限時間，個数表示を構成
        JPanel northPanel = new JPanel();
        northPanel.setPreferredSize(new Dimension(400, 50));
        northPanel.setLayout(null);
        // 制限時間表示
        timeField = new JTextField();
        timeField.setEditable(false);
        timeField.setHorizontalAlignment(JTextField.CENTER);
        timeField.setBorder(new LineBorder(Color.BLACK));
        timeField.setFont(new Font(null, Font.PLAIN, 20));
        timeField.setBounds(0, 5, 100, 40);
        northPanel.add(timeField);
        // 個数表示
        numberArea = new JTextArea(" 黒：2\n 白：2");
        numberArea.setEditable(false);
        numberArea.setBorder(new LineBorder(Color.BLACK));
        numberArea.setFont(new Font(null, Font.PLAIN, 15));
        numberArea.setBounds(340, 5, 60, 40);
        northPanel.add(numberArea);

        // 手番表示を構成
        JPanel southPanel = new JPanel();
        southPanel.setPreferredSize(new Dimension(400, 100));
        // プレイヤの情報
        playerInfoField = new JTextField();
        playerInfoField.setPreferredSize(new Dimension(400, 40));
        playerInfoField.setHorizontalAlignment(JTextField.CENTER);
        playerInfoField.setEditable(false);
        southPanel.add(playerInfoField);
        // 手番の表示
        turnField = new JTextField();
        turnField.setPreferredSize(new Dimension(400, 40));
        turnField.setHorizontalAlignment(JTextField.CENTER);
        turnField.setEditable(false);
        southPanel.add(turnField);

        // コンポーネントを配置
        othello.setLayout(new FlowLayout());
        othello.add(northPanel);
        othello.add(buttonPanel);
        othello.add(southPanel);

        // ウィンドウを表示
        othello.setLocationRelativeTo(null);
        othello.setVisible(true);
    }

    // 対局画面を閉じる
    public void closeOthelloDisplay() {
        othello.dispose();
    }

    // 盤面を更新
	public void updateBoard(int grids[][]) {
		for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids.length; j++) {
                // 空
                if (grids[i][j] == 0) {
                    buttonArray[j][i].setIcon(boardIcon);
                }
                // 白
                else if (grids[i][j] == 1) {
                    buttonArray[j][i].setIcon(whiteIcon);
                }
                // 黒
                else if (grids[i][j] == 2) {
                    buttonArray[j][i].setIcon(blackIcon);
                }
                buttonArray[j][i].setActionCommand(j + " " + i);
            }
        }
	}

    // 指定された制限時間を動かす
    public void startTime(int time) {
        timer = new Clock(this, time);
        timerThread = new Thread(timer);
        timerThread.start();
    }

    // 制限時間を更新
	public void setTime(String t) {
        timeField.setText(t);
	}

    // 時間を停止
    public void stopTime() {
        timer.stopTime();
    }

    // 時間切れを通知
    public void notifyTimeOver() {
        othelloCtrl.processEvent("timeover");
        stopTime();
    }

    // コマの個数を更新
	public void paintCount(int c1,int c2) {
		numberArea.setText(" 黒："+c1+"\n 白："+c2);
	}

    // プレイヤ情報を設定
	public void setPlayerInfo(String n, String c) {
		playerInfoField.setText("プレイヤ名："+n+"    あなたは"+c+"です");
	}

    // 手番を更新
	public void paintTurn(boolean t) {
		if(t==true) {
			turnField.setText("あなたの番です");
		}else {
			turnField.setText("相手の番です");
		}
	}

    // 対局終了画面を描画
	public void paintFinishDisplay(int c1,int c2) {
		dialog = new JFrame();
		dialog.setSize(320,120);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        message = new JLabel();
        message.setHorizontalAlignment(JLabel.CENTER);
        message.setPreferredSize(new Dimension(320, 20));

        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(320, 20));

		if(c1 > c2) {
			message.setText("黒の勝ちです");
		}
        else if (c1 < c2) {
			message.setText("白の勝ちです");
		}
        else {
            message.setText("引き分けです");
        }
        label.setText("黒："+c1+"個    白："+c2+"個");

        okButton = new JButton("OK");
        okButton.setActionCommand("end");
        okButton.addMouseListener(this);

        dialog.setLayout(new FlowLayout());
		dialog.add(message);
        dialog.add(label);
		dialog.add(okButton);

        dialog.setLocationRelativeTo(othello);
		dialog.setVisible(true);
	}

    // パス画面を描画
	public void paintPassDisplay() {
		JFrame passDialog= new JFrame();
		passDialog.setSize(320,120);
        passDialog.setResizable(false);
        passDialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JLabel passmessage = new JLabel("置き場所がありません");
        passmessage.setHorizontalAlignment(JLabel.CENTER);
        passmessage.setPreferredSize(new Dimension(300, 20));

        JLabel label = new JLabel("パスします");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(300, 20));

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(320, 120));
        panel.add(passmessage);
        panel.add(label);

		passDialog.add(panel);

        passDialog.setLocationRelativeTo(othello);
		passDialog.setVisible(true);
        java.util.Timer t = new java.util.Timer();
        t.schedule(
            new TimerTask(){
                @Override
                public void run() {
                    passDialog.dispose();
                }
            }, 
            5000
        );
	}

    public void setMessage(String str) {
        message.setText(str);
    }

    public void closeDialog() {
        dialog.dispose();
    }

    // マウスクリックイベント
    @Override
    public void mouseClicked(MouseEvent me) {
        JButton b = (JButton) me.getComponent();
        othelloCtrl.processEvent(b.getActionCommand());
    }

    // 以下のメソッドは使用しない
    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}