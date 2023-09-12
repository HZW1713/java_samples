import java.util.*;

public class PlayerModel {
	private String playerName;                       // ユーザ名
    private HashMap<String, String> opponents;       // 対戦相手の名前とクライアント番号を格納する表

    public PlayerModel() {
        opponents = new HashMap<String, String>();
    }

    public void setName(String playerName) {
    	this.playerName = playerName;
    }

    public String getPlayerName() {
    	return playerName;
    }

    // ハッシュマップを更新
    public void setOpponents(String[] room) {
        opponents.clear();
        for (int i = 0; i < room.length; i+=2) {
            opponents.put(room[i], room[i+1]);
        }
    }

    // 相手のクライアント番号を取得
    public String getOpposClientNum(String opponent) {
        return opponents.get(opponent);
    }
}
