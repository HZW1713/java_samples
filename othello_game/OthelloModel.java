public class OthelloModel {
    public boolean turn;                        // ゲームのターン管理用フラグ
    public boolean myturn;                      // 自分の手番
    public int[][] board;                       // 局面
    public int time;                            // 制限時間

    final int EMPTY = 0;
    final int WHITE = 1;
    final int BLACK = 2;

    final int FIRST = 1;                        // 先行
    final int SECOND = 2;                       // 後攻
    public int mycolor;                         // 現在のターンのコマの色 
    public int yourcolor;                       // ターンでないコマの色

    public int white;                            // 白の数
    public int black;                            // 黒の数
    public int empty;                            // 空きの数

    //コンストラクタ
    OthelloModel() {
        board = new int[8][8];
        //初期配置の設定
        initBoard();
    }

    public void initBoard() {
        turn = true;
        mycolor = BLACK;
        yourcolor = WHITE;

        white = 2;
        black = 2;
        empty = 60;
        
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                if (i==3 && j==3) {
                    board[i][j] = WHITE;
                }
                else if (i==3 && j==4) {
                    board[i][j] = BLACK;
                }
                else if (i==4 && j==3) {
                    board[i][j] = BLACK;
                }
                else if (i==4 && j==4) {
                    board[i][j] = WHITE;
                }
                else {
                    board[i][j] = EMPTY;
                }
            }
        }
    }

    //コマの色を設定
    public void setColor() {
            if(turn) {
                mycolor = BLACK;
                yourcolor = WHITE;
            }
            else {
                mycolor = WHITE;
                yourcolor = BLACK;
            }
    }

    //コマを置き、盤面を更新する
    public void put(int color, int x, int y){
        board[y][x] = color;
        turnLeftUp(x, y);
        turnUp(x, y);
        turnRightUp(x, y);
        turnLeft(x, y);
        turnRight(x, y);
        turnLeftDown(x, y);
        turnDown(x, y);
        turnRightDown(x, y);
        // コマを数える
        countPiece();
    }

    //盤面取得現在の盤面状況を返す
    public int[][] getBoard(){
        return board;
    }

    // 制限時間を設定
    public void setTime(int time) {
        this.time = time;
    }

    // 制限時間を取得
    public int getTime() {
        return time;
    }

    //手番の交代
    public void changeTurn() {
        //true:先攻、false:後攻
        myturn = !myturn;
        turn = !turn;
        setColor();
    }

    //board[y][x]にコマを置けるか判定
    public boolean canPut(int x, int y) {
        if (board[y][x] == EMPTY) {
            if (checkLeft(x, y) == true) {
                return true;
            }
            else if (checkLeftUp(x, y) == true) {
                return true;
            }
            else if (checkUp(x, y) == true) {
                return true;
            }
            else if (checkRightUp(x, y) == true) {
                return true;
            }
            else if (checkRight(x, y) == true){
                return true;
            }
            else if (checkRightDown(x, y) == true) {
                return true;
            }
            else if (checkDown(x, y) == true) {
                return true;
            }
            else if (checkLeftDown(x, y) == true) {
                return true;
            }
        }

        return false;
    }

    //置ける場所があるかを確認
    public boolean putExist() {
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                if (canPut(i,j)==true) {
                    return true;
                }
            }
        }
        return false;
    }

    // 盤面のコマの数をカウント
    private void countPiece() {
        int n_white = 0;
        int n_black = 0;
        int n_empty = 0;

        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                if (board[i][j] == WHITE) {
                    n_white++;
                }
                else if (board[i][j] == BLACK) {
                    n_black++;
                }
                else {
                    n_empty++;
                }
            }
        }
        this.white = n_white;
        this.black = n_black;
        this.empty = n_empty;
    }

    // 左のラインにあるコマを裏返す処理
    private void turnLeft(int x, int y) {
        int i=1;
        while (x-i>=0 && board[y][x-i]!=EMPTY) {
            if (board[y][x-i] == mycolor) {
                for (int j=1; j<i; j++) {
                    board[y][x-j] = mycolor;
                }
                break;
            }
            i++;
        }
    }

    // 左上のラインにあるコマを裏返す処理
    private void turnLeftUp(int x, int y) {
        int i = 1;
        while (x-i>=0 && y-i>=0 && board[y-i][x-i]!=EMPTY) {
            // 同じ色のコマがあったらその間のコマを裏返す
            if (board[y-i][x-i] == mycolor) {
                for (int j=1; j<i; j++) {
                    board[y-j][x-j] = mycolor;
                }
                break;
            }
            i++;
        }
    }

    // 上のラインにあるコマを裏返す処理
    private void turnUp(int x, int y) {
        int i = 1;
        while (y-i>=0 && board[y-i][x]!=EMPTY) {
            if (board[y-i][x] == mycolor) {
                for (int j=1; j<i; j++) {
                    board[y-j][x] = mycolor;
                }
                break;
            }
            i++;
        }
    }

    // 右上のラインにあるコマを裏返す処理
    private void turnRightUp(int x, int y) {
        int i = 1;
        while (x+i<=7 && y-i>=0 && board[y-i][x+i]!=EMPTY) {
            if (board[y-i][x+i] == mycolor) {
                for (int j=1; j<i; j++) {
                    board[y-j][x+j] = mycolor;
                }
                break;
            }
            i++;
        }
    }

    // 右のラインにあるコマを裏返す処理
    private void turnRight(int x, int y) {
        int i=1;
        while (x+i<=7 && board[y][x+i]!=EMPTY) {
            if (board[y][x+i] == mycolor) {
                for (int j=1; j<i; j++) {
                    board[y][x+j] = mycolor;
                }
                break;
            }
            i++;
        }
    }

    // 下のラインにあるコマを裏返す処理
    private void turnDown(int x, int y) {
        int i=1;
        while (y+i<=7 && board[y+i][x]!=EMPTY) {
            if (board[y+i][x] == mycolor) {
                for (int j=1; j<i; j++) {
                    board[y+j][x] = mycolor;
                }
                break;
            }
            i++;
        }
    }

    // 右下のラインにあるコマを裏返す処理
    private void turnRightDown(int x, int y) {
        int i=1;
        while (x+i<=7 && y+i<=7 && board[y+i][x+i]!=EMPTY) {
            if (board[y+i][x+i] == mycolor) {
                for (int j=1; j<i; j++) {
                    board[y+j][x+j] = mycolor;
                }
                break;
            }
            i++;
        }
    }

    // 左下のラインにあるコマを裏返す処理
    private void turnLeftDown(int x, int y) {
        int i=1;
        while (x-i>=0 && y+i<=7 && board[y+i][x-i]!=EMPTY) {
            if (board[y+i][x-i] == mycolor) {
                for (int j=1; j<i; j++) {
                    board[y+j][x-j] = mycolor;
                }
                break;
            }
            i++;
        }
    }

    // 左のラインの2つ以上先に同じ色のコマがあるか確認する処理
    private boolean checkLeft(int x, int y) {
        if (x>0 && board[y][x-1]==yourcolor) {
            int i = 2;
            while (x-i>=0 && board[y][x-i]!=EMPTY) {
                // 同じ色のコマがあった場合
                if (board[y][x-i] == mycolor) {
                    return true;
                }
                i++;
            }
        }

        return false;
    }

    // 左上のラインの2つ以上先に同じ色のコマがあるか確認する処理
    private boolean checkLeftUp(int x, int y) {
        if (x>0 && y>0 && board[y-1][x-1]==yourcolor) {
            int i = 2;
            while (x-i>=0 && y-i>=0 && board[y-i][x-i]!=EMPTY) {
                if (board[y-i][x-i] == mycolor) {
                    return true;
                }
                i++;
            }
        }

        return false;
    }

    // 上のラインの2つ以上先に同じ色のコマがあるか確認する処理
    private boolean checkUp(int x, int y) {
        if (y>0 && board[y-1][x]==yourcolor) {
            int i = 2;
            while (y-i>=0 && board[y-i][x]!=EMPTY) {
                if (board[y-i][x] == mycolor) {
                    return true;
                }
                i++;
            }
        }

        return false;
    }

    // 右上のラインの2つ以上先に同じ色のコマがあるか確認する処理
    private boolean checkRightUp(int x, int y) {
        if (x<7 && y>0 && board[y-1][x+1]==yourcolor) {
            int i = 2;
            while (x+i<=7 && y-i>=0 && board[y-i][x+i]!=EMPTY) {
                if (board[y-i][x+i] == mycolor) {
                    return true;
                }
                i++;
            }
        }

        return false;
    }

    // 右のラインの2つ以上先に同じ色のコマがあるか確認する処理
    private boolean checkRight(int x, int y) {
        if (x<7 && board[y][x+1]==yourcolor) {
            int i = 2;
            while (x+i<=7 && board[y][x+i]!=EMPTY) {
                if (board[y][x+i] == mycolor) {
                    return true;
                }
                i++;
            }
        }

        return false;
    }

    // 右下のラインの2つ以上先に同じ色のコマがあるか確認する処理
    private boolean checkRightDown(int x, int y) {
        if (x<7 && y<7 && board[y+1][x+1]==yourcolor) {
            int i = 2;
            while (x+i<=7 && y+i<=7 && board[y+i][x+i]!=EMPTY) {
                if (board[y+i][x+i] == mycolor) {
                    return true;
                }
                i++;
            }
        }

        return false;
    }

    // 下のラインの2つ以上先に同じ色のコマがあるか確認する処理
    private boolean checkDown(int x, int y) {
        if (y<7 && board[y+1][x]==yourcolor) {
            int i = 2;
            while (y+i<=7 && board[y+i][x]!=EMPTY) {
                if (board[y+i][x] == mycolor) {
                    return true;
                }
                i++;
            }
        }

        return false;
    }

    // 左下のラインの2つ以上先に同じ色のコマがあるか確認する処理
    private boolean checkLeftDown(int x, int y) {
        if (x>0 && y<7 && board[y+1][x-1]==yourcolor) {
            int i = 2;
            while (x-i>=0 && y+i<=7 && board[y+i][x-i]!=EMPTY) {
                if (board[y+i][x-i] == mycolor) {
                    return true;
                }
                i++;
            }
        }

        return false;
    }
}
