import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

class PlayerView implements ActionListener {
    private PlayerController playerCtrl;        // PlayerModelとPlayerViewを制御するクラス

    private JFrame login;
    private ButtonGroup buttons;                // 新規登録，ログイン選択ボタン
    private JLabel message;                     // メッセージの表示
    private JTextField nameField;               // ユーザ名入力フィールド
    private JPasswordField pswdField;           // パスワード入力フィールド
    private JButton okButton;                   // OKボタン

    private JFrame matching;
    private JPanel matchingPanel;               // マッチング画面
    private JList<String> room;                 // ルーム
    private JTextField selectField;             // 選択した対戦相手を表示するフィールド
    private JButton matchButton;                // 対局ボタン
    private JButton logoutButton;               // ログアウトボタン

    private JFrame dialogFrame;                 // ダイアログ表示用のウィンドウ
    private JLabel playerTurnLabel;             // プレイヤの手番を通知するラベル
    private JLabel oppoTurnLabel;               // 相手プレイヤの手番を通知するラベル

    private JComboBox<String> times;            // 制限時間選択メニュー

    // コンストラクタ
    public PlayerView(PlayerController pc) {
        playerCtrl = pc;
        createLoginDisplay();

        // ルームを作成
        room = new JList<String>();
        room.setFont(new Font(null, Font.PLAIN, 25));
        room.setLayoutOrientation(JList.VERTICAL);
        room.setVisibleRowCount(10);
        room.setPreferredSize(new Dimension(400, 400));
        room.addListSelectionListener(
            new ListSelectionListener(){
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    selectField.setText(room.getSelectedValue());
                }
            }
        );
    }

    // ログイン画面の描画
    public void createLoginDisplay() {
        // ウィンドウの設定
        login = new JFrame("ネットワーク対戦型オセロゲーム");
        login.setSize(320, 240);
        login.setResizable(false);
        login.setLocationRelativeTo(null);
        login.addWindowListener(
            new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) { }

                @Override
                public void windowClosing(WindowEvent e) {
                    playerCtrl.processEvent("exit");
                    System.exit(0);
                }

                @Override
                public void windowClosed(WindowEvent e) { }

                @Override
                public void windowIconified(WindowEvent e) { }

                @Override
                public void windowDeiconified(WindowEvent e) { }

                @Override
                public void windowActivated(WindowEvent e) { }

                @Override
                public void windowDeactivated(WindowEvent e) { }
            }
        );

        // ボタンを作成
        JRadioButton[] rbutton = new JRadioButton[2];
        rbutton[0] = new JRadioButton("新規登録");
        rbutton[0].setActionCommand("signup");
        rbutton[1] = new JRadioButton("ログイン", true);
        rbutton[1].setActionCommand("login");
        
        buttons = new ButtonGroup();
        buttons.add(rbutton[0]);
        buttons.add(rbutton[1]);

        okButton = new JButton("OK");
        okButton.setActionCommand("loginOk");
        okButton.addActionListener(this);

        message = new JLabel("　　　　　　　　　　　　　　　");

        // ユーザ名，パスワード入力フィールドを作成
        nameField = new JTextField(16);
        pswdField = new JPasswordField(16);

        // コンポーネントを配置
        JPanel bPanel = new JPanel();
        bPanel.add(rbutton[0]);
        bPanel.add(rbutton[1]);

        JPanel name = new JPanel(new FlowLayout());
        name.add(new JLabel("ユーザ名　"));
        name.add(nameField);

        JPanel pswd = new JPanel(new FlowLayout());
        pswd.add(new JLabel("パスワード"));
        pswd.add(pswdField);

        login.setLayout(new FlowLayout());
        login.add(bPanel);
        login.add(message);
        login.add(name);
        login.add(pswd);
        login.add(okButton);

        // ウィンドウを表示
        login.setVisible(true);
    }

    // ログイン画面を閉じる
    public void closeLoginDisplay() {
        login.dispose();
    }

    // マッチング画面の描画
    public void createMatchingDisplay() {
        matching = new JFrame();
        matching.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        matching.setResizable(false);
        matching.setSize(480, 640);
        matching.setLocationRelativeTo(null);
        matching.setTitle("ネットワーク対戦型オセロゲーム");

        JLabel label =  new JLabel("対戦相手を選択してください");
        label.setPreferredSize(new Dimension(400, 40));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(new Font(null, Font.PLAIN, 15));

        // 選択した対戦相手を表示するフィールド
        selectField = new JTextField();
        selectField.setHorizontalAlignment(JTextField.CENTER);
        selectField.setPreferredSize(new Dimension(400, 40));
        selectField.setFont(new Font(null, Font.PLAIN, 25));
        selectField.setBorder(new LineBorder(Color.BLACK));
        selectField.setEditable(false);

        // 対局ボタン
        matchButton = new JButton("対局");
        matchButton.setPreferredSize(new Dimension(400, 40));
        matchButton.setActionCommand("match");
        matchButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (selectField.getText().length() != 0) {
                        matchButton.setEnabled(false);
                        logoutButton.setEnabled(false);
                        playerCtrl.processEvent(ae.getActionCommand());
                    }
                }
            }
        );

        // ログアウトボタン
        logoutButton = new JButton("ログアウト");
        logoutButton.setPreferredSize(new Dimension(400, 40));
        logoutButton.setActionCommand("logout");
        logoutButton.addActionListener(this);

        // コンポーネントを配置
        matchingPanel = new JPanel();
        matchingPanel.add(label);
        matchingPanel.add(room);
        matchingPanel.add(selectField);
        matchingPanel.add(matchButton);
        matchingPanel.add(logoutButton);
        matching.add(matchingPanel);

        matching.setVisible(true);
    }

    // マッチング画面を閉じる
    public void closeMatchingDisplay() {
        matching.dispose();
    }

    // 対局成立を通知する画面を描画
    public void successMatchingDisplay() {
		dialogFrame = new JFrame();
		dialogFrame.setSize(320,160);
        dialogFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialogFrame.setResizable(false);
        // 対局成立メッセージ
        JLabel successMessage = new JLabel("対局が成立しました");
        successMessage.setHorizontalAlignment(JLabel.CENTER);
        successMessage.setPreferredSize(new Dimension(320, 20));
        // 自分の手番
        playerTurnLabel = new JLabel();
        playerTurnLabel.setHorizontalAlignment(JLabel.CENTER);
        playerTurnLabel.setPreferredSize(new Dimension(320, 20));
        // 相手の手番
        oppoTurnLabel = new JLabel();
        oppoTurnLabel.setHorizontalAlignment(JLabel.CENTER);
        oppoTurnLabel.setPreferredSize(new Dimension(320, 20));
        // OKボタン
        JButton matchOk = new JButton("OK");
        matchOk.setActionCommand("matchOK");
        matchOk.addActionListener(this);

        dialogFrame.setLayout(new FlowLayout());
        dialogFrame.add(successMessage);
        dialogFrame.add(playerTurnLabel);
        dialogFrame.add(oppoTurnLabel);
        dialogFrame.add(matchOk);

        dialogFrame.setLocationRelativeTo(matching);
        dialogFrame.setVisible(true);
	}

    // 手番を表示
    public void setTurn(String player, String opponent) {
        playerTurnLabel.setText(player);
        oppoTurnLabel.setText(opponent);
    }

    // 対局不成立を通知する画面を描画
    public void failMatchingDisplay() {
		JFrame failDialog = new JFrame();
		failDialog.setSize(320,120);
		failDialog.setResizable(false);
        failDialog.addWindowListener(
            new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) { }

                @Override
                public void windowClosing(WindowEvent e) {
                    setMatchButtonState(true);
                    setLogoutButtonState(true);
                }

                @Override
                public void windowClosed(WindowEvent e) { }

                @Override
                public void windowIconified(WindowEvent e) { }

                @Override
                public void windowDeiconified(WindowEvent e) { }

                @Override
                public void windowActivated(WindowEvent e) { }

                @Override
                public void windowDeactivated(WindowEvent e) { }
            }
        );

        JLabel failmessage = new JLabel("対局が成立しませんでした");
        failmessage.setHorizontalAlignment(JLabel.CENTER);
        failmessage.setPreferredSize(new Dimension(320, 20));

        JButton okButton = new JButton("OK");
        okButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    failDialog.dispose();
                    setMatchButtonState(true);
                    setLogoutButtonState(true);
                }
            }
        );

        failDialog.setLayout(new FlowLayout());
        failDialog.add(failmessage);
        failDialog.add(okButton);

        failDialog.setLocationRelativeTo(matching);
        failDialog.setVisible(true);
	}

    // 相手からの対局要求を通知する画面を描画
    public void requestMatchingDisplay() {
		dialogFrame = new JFrame();
		dialogFrame.setSize(320,120);
		dialogFrame.setResizable(false);
		dialogFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        message = new JLabel();
        message.setHorizontalAlignment(JLabel.CENTER);
        message.setPreferredSize(new Dimension(320, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(320, 40));

        JButton acceptButton = new JButton("Accept");
        acceptButton.setActionCommand("accept");
        acceptButton.addActionListener(this);
        buttonPanel.add(acceptButton);

        JButton rejectButton = new JButton("Reject");
        rejectButton.setActionCommand("reject");
        rejectButton.addActionListener(this);
        buttonPanel.add(rejectButton);

        dialogFrame.setLayout(new FlowLayout());
        dialogFrame.add(message);
        dialogFrame.add(buttonPanel);

        dialogFrame.setLocationRelativeTo(matching);
        dialogFrame.setVisible(true);
	}

    // 制限時間を選択する画面を描画
    public void createSetTimeDisplay() {
        // ウィンドウの設定
        dialogFrame = new JFrame("Set Time");
        dialogFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialogFrame.setSize(320, 140);
        dialogFrame.setResizable(false);
        
        // メッセージ
    	message = new JLabel("制限時間を選択してください");
        message.setPreferredSize(new Dimension(320, 20));
        message.setHorizontalAlignment(JLabel.CENTER);
        // 制限時間選択メニュー
    	String[] item = new String[] {"0:30", "1:00", "1:30", "2:00", "2:30", "3:00"};
    	times = new JComboBox<String>(item);
        times.setPreferredSize(new Dimension(100, 20));
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(320, 40));
        panel.add(times);
        // OKボタン
    	JButton okButton = new JButton("OK");
    	okButton.setActionCommand("time");
    	okButton.addActionListener(this);
        //コンポーネントを配置
        dialogFrame.setLayout(new FlowLayout());
        dialogFrame.add(message);
    	dialogFrame.add(panel);
    	dialogFrame.add(okButton);

        dialogFrame.setLocationRelativeTo(matching);
        dialogFrame.setVisible(true);
    }

    // 制限時間を確認する画面を描画
    public void createVerifyTimeDisplay() {
        dialogFrame = new JFrame();
		dialogFrame.setSize(320,120);
        dialogFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialogFrame.setResizable(false);
        // 制限時間を通知するラベル
        message = new JLabel();
        message.setHorizontalAlignment(JLabel.CENTER);
        message.setPreferredSize(new Dimension(320, 20));
        // OKボタン
        JButton matchOk = new JButton("OK");
        matchOk.setActionCommand("timeOk");
        matchOk.addActionListener(this);

        dialogFrame.setLayout(new FlowLayout());
        dialogFrame.add(message);
        dialogFrame.add(matchOk);

        dialogFrame.setLocationRelativeTo(matching);
        dialogFrame.setVisible(true);
    }

    // ダイアログを閉じる
    public void closeDialog() {
        dialogFrame.setVisible(false);
    }

    // 新規登録かログインか取得
    public String getLoginOpSelection() {
        return buttons.getSelection().getActionCommand();
    }

    // ユーザ名を取得
    public String getUserName() {
        return nameField.getText();
    }

    // パスワードを取得
    public String getPassword() {
        return String.valueOf(pswdField.getPassword());
    }

    // OKボタンを有効にする
    public void enableOKButton() {
        okButton.setEnabled(true);
    }

    // メッセージを表示
    public void setMessage(String str) {
        message.setText(str);
    }

    // 対局ボタンを有効，無効にする
    public void setMatchButtonState(boolean enable) {
        matchButton.setEnabled(enable);
    }

    // ログアウトボタンを有効，無効にする
    public void setLogoutButtonState(boolean enable) {
        logoutButton.setEnabled(enable);
    }

    // ルームの情報を更新
    public void updateRoom(String[] users) {
        room.setListData(users);
    }

    // 選択された対戦相手を取得
    public String getOpponent() {
        return selectField.getText();
    }

    // 選択された制限時間を取得
    public String getTime() {
        return (String) times.getSelectedItem();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JButton button = (JButton) ae.getSource();
        button.setEnabled(false);
        String command = button.getActionCommand();
        playerCtrl.processEvent(command);

    }
}