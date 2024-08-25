package nro.models.boss.mabu_war;

import java.util.logging.Level;
import java.util.logging.Logger;
import nro.consts.ConstRatio;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.services.MapService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.TaskService;
import nro.services.func.ChangeMapService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Mr Blue
 */
public class Mabu_14H extends BossMabuWar {

    public Mabu_14H(int mapID, int zoneId) {
        super(BossFactory.MABU_MAP, BossData.MABU_MAP2);
        this.mapID = mapID;
        this.zoneId = zoneId;
        this.zoneHold = zoneId;
        this.isMabuBoss = true;
    }

    @Override
    public void attack() {
        if (this.isDie()) {
            die();
            return;
        }
        try {
            if (Util.isTrue(50, 100)) {
                this.talk();
            }
            Player pl = getPlayerAttack();
            if (pl != null) {
                if (!useSpecialSkill()) {
                    if (this.id != BossFactory.MABU_MAP) {
                        if (this.id == BossFactory.BU_HAN && this.nPoint.hp <= 200000) {
                            isUseSpeacialSkill = true;
                            this.playerSkill.skillSelect = this.playerSkill.skills.get(8);
                           // SkillService.gI().useSkill(this, pl, null);
                            return;
                        }
                        byte idSkill = (byte) Util.nextInt(0, 2);
                        List<Player> list = getListPlayerAttack(70);
                        switch (idSkill) {
                            case 0:
                                if (pl.isPl() && MapService.gI().getZoneJoinByMapIdAndZoneId(this, 128, zoneHold).getNumOfPlayers() < 4 && Util.canDoWithTime(pl.effectSkill.lastTimeHoldMabu, 9000) && pl.zone.map.mapId != 128 && pl.effectSkill.isTaskHoldMabu <= 0 && Util.canDoWithTime(this.lastTimeUseSpeacialSkill, 12000)) {
                                    Service.getInstance().eatPlayer(this, pl);
                                    if (Util.isTrue(20, 100)) {
                                        this.nextMabu(false);
                                    }
                                }
                                break;
                            case 1:
                                if (Util.canDoWithTime(lastTimeUseSpeacialSkill, 10000) && Util.isTrue(30, 100)) {
                                    Service.getInstance().Mabu14hAttack(this, pl, pl.location.x, pl.location.y, (byte) 1);
                                }
                                break;
                            default:
                                if (Util.canDoWithTime(lastTimeUseSpeacialSkill, 7000) && !list.isEmpty() && Util.isTrue(40, 100)) {
                                    Service.getInstance().Mabu14hAttack(this, pl, this.location.x + ((pl.location.x <= this.location.x) ? -30 : 30), this.location.y, (byte) 0);
                                }
                                break;
                        }
                        list.clear();
                    }
                    if (!isUseSpeacialSkill && pl.effectSkill.isTaskHoldMabu <= 0) {
                        this.playerSkill.skillSelect = this.getSkillAttack();
                        if (this.id == BossFactory.BU_HAN) {
                            while (this.playerSkill.skillSelect.template.id == Skill.QUA_CAU_KENH_KHI) {
                                this.playerSkill.skillSelect = this.getSkillAttack();
                            }
                        }
                        if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                            if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                                goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                        Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                            }
//                            SkillService.gI().useSkill(this, pl, null);
                            checkPlayerDie(pl);
                        } else {
                            goToPlayer(pl, false);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.error(Mabu_14H.class, ex);
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie()) {
            return 0;
        } else {
            int dame = super.injuredNotCheckDie(plAtt, damage, piercing);
            if (this.isDie()) {
                rewards(plAtt);
            }
            return dame;
        }
    }

    @Override
    public void joinMap() {
        this.zone = getMapCanJoin(mapID);
        int x = Util.nextInt(50, this.zone.map.mapWidth - 50);
        ChangeMapService.gI().changeMap(this, this.zone, x, this.zone.map.yPhysicInTop(x, 0));
    }

    @Override
    public Zone getMapCanJoin(int mapId) {
        Zone map = MapService.gI().getZoneJoinByMapIdAndZoneId(this, mapId, zoneId);
        if (map.isBossCanJoin(this)) {
            return map;
        } else {
            return getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
        }
    }

    @Override
    public void idle() {
    }

    @Override
    public void rewards(Player pl) {
        for (int i = 0; i < zone.getPlayers().size(); i++) {
            Player plAll = zone.getPlayers().get(i);
            if (plAll != null) {
                if (plAll.effectSkill.isHoldMabu) {
                    Service.getInstance().removeMabuEat(plAll);
                }
                plAll.effectSkill.lastTimeHoldMabu = System.currentTimeMillis();
                ChangeMapService.gI().changeMap(plAll, 114, this.zoneHold, (short) -1, (short) 5);
            }
        }
        try {
            int[] itemDos = new int[]{556, 558, 560};
            int randomDo = new Random().nextInt(itemDos.length);
            if (Util.isTrue(50, 100)) {
                if (Util.isTrue(1, 5)) {
                    Service.getInstance().dropItemMap(this.zone, Util.ratiItem(zone, itemDos[randomDo], 1, this.location.x, this.location.y, pl.id));
                }
            }
            int[] listitem = {Util.nextInt(18, 19), 861};
            ItemMap itemMap = new ItemMap(this.zone, listitem[Util.nextInt(0, listitem.length - 1)], 1, pl.location.x, this.zone.map.yPhysicInTop(pl.location.x, pl.location.y - 24), pl.id);
            Service.getInstance().dropItemMap(this.zone, itemMap);
        } catch (Exception ex) {
            Logger.getLogger(Mabu_14H.class.getName()).log(Level.SEVERE, null, ex);
        }
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
        generalRewards(pl);
    }

    @Override
    public void checkPlayerDie(Player pl) {
    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{"Bư! Bư! Bư!", "Bư! Bư! Bư!"};
        this.textTalkMidle = new String[]{"Oe Oe Oe"};
        this.textTalkAfter = new String[]{"Huhu"};
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        this.changeToIdle();
    }
}
