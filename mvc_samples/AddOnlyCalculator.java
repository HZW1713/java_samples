import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// 電卓の計算処理を行うクラス
class CalculatorModel {
    private int result = 0;             // 計算結果
    private int num = 0;                // 入力された数
    private int display = 0;            // ラベルに表示する数
    private char lastKey = '=';         // 最後の入力
    private char lastOperator = '=';    // 1つ前の演算子

    // インスタンス変数をリセットする
    private void clear() {
        result = 0;
        num = 0;
        display = 0;
        lastKey = '=';
        lastOperator = '=';
    }

    // 入力に対する処理
    public void inputKey(String key) throws RuntimeException {
        char c = key.charAt(0);
        switch (c) {
            case '+':
                // 加算は2数のみに限定. 2数以上を加算する場合は "lastOperator != c" を削除
                if (Character.isDigit(lastKey) && lastOperator != c) {
                    result += num;
                    num = 0;
                    lastOperator = c;
                }
                else {
                    clear();
                    throw new RuntimeException("invalid operation : "+c);
                }
                break;
            
            case '=':
                if (Character.isDigit(lastKey) && lastOperator != c) {
                    if (lastOperator == '+') {
                        result += num;
                    }
                    display = result;
                    result = 0;
                    num = 0;
                    lastOperator = c;
                }
                else {
                    clear();
                    throw new RuntimeException("invalid operation : "+c);
                }
                break;
        
            default:
                num = num*10 + (c-'0');
                display = num;
                break;
        }
        lastKey = c;
    }

    // 表示する数を提供
    public int getNumber() {
        return display;
    }
}

// 電卓の処理と表示の管理を行うクラス
class CalculatorController {
    private CalculatorModel calcModel;
    private CalculatorView calcView;

    public void setCalculatorModel(CalculatorModel cm) {
        calcModel = cm;
    }

    public void setCalculatorView(CalculatorView cv) {
        calcView = cv;
    }

    // CalculatorModelクラスに入力を渡して返ってくる結果をCalculatorViewクラスに表示させる
    public void inputKey(String key) {
        try {
            calcModel.inputKey(key);
            String display = Integer.toString(calcModel.getNumber());
            calcView.updateLabel(display);
        } catch (RuntimeException re) {
            calcView.updateLabel(re.getMessage());
        }
    }
}

// 電卓のGUIを提供するクラス
class CalculatorView extends JFrame implements ActionListener {
    private CalculatorController calcCtrl;   // GUIを管理するクラス
    private JLabel label;

    // GUIを作成する
    public CalculatorView(CalculatorController cc) {
        super("AddOnlyCalculator");
        setLayout(new BorderLayout());

        calcCtrl = cc;
        label = new JLabel("Calculator");
        label.setHorizontalAlignment(JLabel.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 3));

        String[] name = {"0", "1", "2", "3", "4", "5",
                         "6", "7", "8", "9", "+", "="};
        for (int i = 0; i < name.length; i++) {
            JButton button = new JButton(name[i]);
            button.addActionListener(this);
            panel.add(button);
        }

        add(label, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    // ラベルを更新する
    public void updateLabel(String str) {
        label.setText(str);
    }

    // ボタンクリックに対する処理
    @Override
    public void actionPerformed(ActionEvent ae) {
        calcCtrl.inputKey(ae.getActionCommand());
    }
}

// MVCモデルの電卓を作成
public class AddOnlyCalculator {
    private CalculatorModel calcModel;
    private CalculatorController calcCtrl;
    private CalculatorView calcView;

    public AddOnlyCalculator() {
        calcModel = new CalculatorModel();
        calcCtrl = new CalculatorController();
        calcView = new CalculatorView(calcCtrl);

        calcCtrl.setCalculatorModel(calcModel);
        calcCtrl.setCalculatorView(calcView);
    }

    public static void main(String[] args) {
        new AddOnlyCalculator();
    }
}