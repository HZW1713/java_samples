import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

// ファイルの読み込みと保存を行うクラス
class EditorModel {
    // 指定された名前でテキストファイルを保存
    public void saveText(String fileName, String text) {
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(text);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 指定されたファイルからテキストを取得
    public String openText(String fileName) {
        String text = null;
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            String line;
            StringBuffer strBuffer = new StringBuffer();
            while ((line = br.readLine()) != null) {
                strBuffer.append(line+'\n');
            }
            text = strBuffer.toString();

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }
}

// エディタの管理をするクラス
class EditorController {
    private EditorModel edtrModel;
    private EditorView edtrView;

    // モデルのクラスを設定
    public void setEditorModel(EditorModel em) {
        edtrModel = em;
    }

    // ビューのクラスを設定
    public void setEditorView(EditorView ev) {
        edtrView = ev;
    }

    // 受け取ったコマンドに対する処理
    public void inputCommand(String command) {   
        if (command.equals("開く")) {
            String fileName = edtrView.getFileName(command, FileDialog.LOAD);
            String text = edtrModel.openText(fileName);
            edtrView.setText(text);
        }
        else if (command.equals("保存する")) {
            String fileName = edtrView.getFileName(command, FileDialog.SAVE);
            String text = edtrView.getText();
            edtrModel.saveText(fileName, text);
        }
        else if (command.equals("終了")) {
            System.exit(0);
        }
    }
}

// GUIを提供するクラス
class EditorView extends JFrame implements ActionListener {
    private EditorController edtrCtrl;
    private TextArea textArea;
    private FileDialog fileDialog;

    // GUIを作成する
    public EditorView(String title, EditorController ec) {
        fileDialog = new FileDialog(this);
        edtrCtrl = ec;

        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createMenu();

        textArea = new TextArea();
        textArea.setPreferredSize(new Dimension(600, 400));
        JPanel panel = (JPanel) getContentPane();
        panel.setLayout(new FlowLayout());
        panel.add(textArea);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // メニューバーを作成する
    private void createMenu() {
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);
        JMenu fileMenu = new JMenu("ファイル");
        JMenuItem open = new JMenuItem("開く");
        JMenuItem close = new JMenuItem("保存する");
        JMenuItem end = new JMenuItem("終了");
        menubar.add(fileMenu);
        fileMenu.add(open);
        fileMenu.add(close);
        fileMenu.add(end);
        open.addActionListener(this);
        close.addActionListener(this);
        end.addActionListener(this);
    }

    // テキストエリアに文字列を設定する
    public void setText(String text) {
        textArea.setText(text);
    }

    // テキストエリアから文字列を取得
    public String getText() {
        String text = textArea.getText();
        return text;
    }

    // ファイルダイアログを開いてファイル名を取得
    public String getFileName(String title, int mode) {
        String file;

        fileDialog.setTitle(title); 
        fileDialog.setMode(mode);
        fileDialog.setVisible(true);
        file = fileDialog.getDirectory()+File.separatorChar+fileDialog.getFile();

        return file;
    }

    // 発生したイベントをEditorControllerクラスに知らせる
    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        edtrCtrl.inputCommand(command);
    }
}

public class SimpleEditor {
    EditorModel edtrModel;
    EditorController edtrCtrl;
    EditorView edtrView;

    // MVCモデルの簡易エディタを作成
    public SimpleEditor(String name) {
        edtrModel = new EditorModel();
        edtrCtrl = new EditorController();
        edtrView = new EditorView(name, edtrCtrl);

        edtrCtrl.setEditorModel(edtrModel);
        edtrCtrl.setEditorView(edtrView);
    }

    public static void main(String[] args) {
        new SimpleEditor("Simple Editor");
    }
}