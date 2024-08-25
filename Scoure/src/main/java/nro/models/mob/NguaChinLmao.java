package nro.models.mob;

import java.util.ArrayList;
import java.util.List;
import nro.models.mob.Mob;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.Service;
import nro.utils.Util;

public class NguaChinLmao extends BigBoss {

    public NguaChinLmao(Mob mob) {
        super(mob);
    }

    @Override
    public synchronized void injured(Player plAtt, int damage, boolean dieWhenHpFull) {
        damage /= 2;
        int max = this.point.hp / 20;
        if (max <= 0) {
            max = 1;
        }
        if (damage > max) {
            damage = max;
        }
        super.injured(plAtt, damage, dieWhenHpFull);
    }

    @Override
    public void attack(Player playerrrrrr) {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && Util.canDoWithTime(lastBigBossAttackTime, 3000)) {
            // 10 : di chuyển, 11 - 20 : tấn công, 21 : bay, 22 : ..., 23 : die

            if (this.zone.getNotBosses().isEmpty()) {
                return;
            }
            List<Player> players = new ArrayList<>();
            action = Util.nextInt(11, 12);

            switch (action) {
                case 11 -> {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 50) {
                            players.add(pl);
                            break;
                        }
                    }
                }
                case 12 -> {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 100) {
                            players.add(pl);
                            break;
                        }
                    }
                }
            }

            if (players.isEmpty()) {
                int index = Util.nextInt(0, this.zone.getNotBosses().size() - 1);
                players.add(this.zone.getNotBosses().get(index));
                action = 10;
//                return;
            }

            Message msg = null;
            try {
                msg = new Message(102);
                msg.writer().writeByte(action);
                msg.writer().writeByte(this.id);
                switch (action) {
                    case 10, 21 -> {
                        for (Player player : players) {
                            this.location.x = player.location.x + Util.nextInt(-10, 10);
                            this.location.y = player.location.y;
                        }
                        msg.writer().writeShort(this.location.x);
                        msg.writer().writeShort(this.location.y);
                    }
                    case 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 -> {
                        msg.writer().writeByte(players.size()); // sl player;
                        int dir = 0;
                        for (Player pl : players) {
                            int dame = pl.injured(null, this.point.getDameAttack(), false, true);
                            msg.writer().writeInt((int) pl.id); // id player
                            msg.writer().writeInt(dame); // dame
                            dir = pl.location.x < this.location.x ? -1 : 1;
                        }
                        msg.writer().writeByte(dir); // dir
                    }
                    case 22 -> {
                    }
                    case 23 -> {
                    }
                    default -> {
                    }
                }
                Service.gI().sendMessAllPlayerInMap(this.zone, msg);
                lastBigBossAttackTime = System.currentTimeMillis();
            } catch (Exception e) {
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

}
