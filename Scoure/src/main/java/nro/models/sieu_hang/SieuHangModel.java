package nro.models.sieu_hang;

import nro.models.player.Player;

public class SieuHangModel {

    public long player_id;
    public int dame;
    public int defend;
    public int rank;
    public String message;
    public Player player = new Player();

    @Override
    public String toString() {
        return "SieuHangModel{" + "player_id=" + player_id + ", dame=" + dame + ", defend=" + defend + ", rank=" + rank + ", message=" + message + ", player=" + player.toString() + '}';
    }
    
    
}
