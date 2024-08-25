package nro.models.boss.broly;

import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.server.ServerNotify;
import nro.services.EffectSkillService;
import nro.services.MapService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.func.ChangeMapService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static nro.models.boss.Boss.DAME_PERCENT_HP_HUND;
import static nro.models.boss.Boss.DAME_PERCENT_HP_THOU;
import static nro.models.boss.Boss.DAME_PERCENT_MP_HUND;
import static nro.models.boss.Boss.DAME_PERCENT_MP_THOU;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class Broly extends Boss {

    static final int MAX_HP = 16777080;
    private static final int DIS_ANGRY = 100;

    private static final int HP_CREATE_SUPER_1 = 1000000;
    private static final int HP_CREATE_SUPER_2 = 2000000;
    private static final int HP_CREATE_SUPER_3 = 4000000;
    private static final int HP_CREATE_SUPER_4 = 6000000;
    private static final int HP_CREATE_SUPER_5 = 7000000;
    private static final int HP_CREATE_SUPER_6 = 10000000;
    private static final int HP_CREATE_SUPER_7 = 13000000;
    private static final int HP_CREATE_SUPER_8 = 14000000;
    private static final int HP_CREATE_SUPER_9 = 15000000;
    private static final int HP_CREATE_SUPER_10 = 16000000;

    private static final byte RATIO_CREATE_SUPER_10 = 10;
    private static final byte RATIO_CREATE_SUPER_20 = 20;
    private static final byte RATIO_CREATE_SUPER_30 = 30;
    private static final byte RATIO_CREATE_SUPER_40 = 40;
    private static final byte RATIO_CREATE_SUPER_50 = 50;
    private static final byte RATIO_CREATE_SUPER_60 = 60;
    private static final byte RATIO_CREATE_SUPER_70 = 70;
    private static final byte RATIO_CREATE_SUPER_80 = 80;
    private static final byte RATIO_CREATE_SUPER_90 = 90;
    private static final byte RATIO_CREATE_SUPER_100 = 100;

    private final Map angryPlayers;
    private final List<Player> playersAttack;

    public Broly() {
        super(BossFactory.BROLY, BossData.BROLY);
        this.angryPlayers = new HashMap();
        this.playersAttack = new LinkedList<>();
    }

    protected Broly(int id, BossData bossData) {
        super(id, bossData);
        this.angryPlayers = new HashMap();
        this.playersAttack = new LinkedList<>();
    }

    @Override
    public void initTalk() {
        this.textTalkAfter = new String[]{"Các ngươi chờ đấy, ta sẽ quay lại sau"};
    }

    @Override
    public void attack() {
        try {
            if (!charge()) {
                angry();
                Player pl = getPlayerAttack();
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                    }
                    this.effectCharger();
                    try {
                        SkillService.gI().useSkill(this, pl, null, null);
                    } catch (Exception e) {
                        Log.error(Broly.class, e);
                    }
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
                if (Util.isTrue(5, ConstRatio.PER100)) {
                    this.changeIdle();
                }
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void idle() {
        if (this.countIdle >= this.maxIdle) {
            this.maxIdle = Util.nextInt(0, 3);
            this.countIdle = 0;
            this.changeAttack();
        } else {
            this.countIdle++;
        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (plAtt != null) {
                int skill = plAtt.playerSkill.skillSelect.template.id;
                if (skill != Skill.TU_SAT || skill != Skill.MAKANKOSAPPO || skill != Skill.QUA_CAU_KENH_KHI) {
                    if (damage > this.nPoint.hpg / 100) {
                        damage = this.nPoint.hpg / 100;
                    }
                }
                resetPoint(damage);
                addPlayerAttack(plAtt);
            }
            return super.injured(plAtt, damage, piercing, isMobAttack);
        } else {
            return 0;
        }
    }

    private int maxCountResetPoint;
    private int countResetPoint;

    private void resetPoint(int damageInjured) {
        if (this.nPoint.hpg < MAX_HP && this.countResetPoint++ >= maxCountResetPoint) {
            this.nPoint.hpg += damageInjured;
            if (this.nPoint.hpg > MAX_HP) {
                this.nPoint.hpg = MAX_HP;
            }
            switch (this.typeDame) {
                case DAME_PERCENT_HP_HUND:
                    this.nPoint.dameg =(int) ((int) this.nPoint.hpg * this.percentDame / 100);
                    break;
                case DAME_PERCENT_MP_HUND:
                    this.nPoint.dameg = (int) (this.nPoint.hpg * this.percentDame / 100);
                    break;
                case DAME_PERCENT_HP_THOU:
                    this.nPoint.dameg = (int) (this.nPoint.hpg * this.percentDame / 1000);
                    break;
                case DAME_PERCENT_MP_THOU:
                    this.nPoint.dameg = (int) (this.nPoint.mpg * this.percentDame / 1000);
                    break;
            }
            maxCountResetPoint = Util.nextInt(3, 7);
            countResetPoint = 0;
        }
    }

    @Override
    public Player getPlayerAttack() throws Exception {
        try {
            if (countChangePlayerAttack < targetCountChangePlayerAttack && plAttack != null && plAttack.zone.equals(this.zone) && !plAttack.effectSkin.isVoHinh) {
                if (!plAttack.isDie()) {
                    this.countChangePlayerAttack++;
                    return plAttack;
                }
            }
        } catch (Exception e) {
            this.playersAttack.remove(plAttack);
        }
        if (!playersAttack.isEmpty()) {
            this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
            this.countChangePlayerAttack = 0;
            Player plAtt = playersAttack.get(Util.nextInt(0, playersAttack.size() - 1));
            return (this.plAttack = plAtt);
        } else {
            throw new Exception();
        }
    }

    private void addPlayerAttack(Player plAtt) {
        boolean haveInList = false;
        for (Player pl : playersAttack) {
            if (pl.equals(plAtt)) {
                haveInList = true;
                break;
            }
        }
        if (!haveInList) {
            playersAttack.add(plAtt);
            Service.getInstance().chat(this, "Mi làm ta nổi giận rồi "
                    + plAtt.name.replaceAll("$", "").replaceAll("#", ""));
        }
    }

    protected boolean charge() {
        if (this.effectSkill.isCharging && Util.isTrue(15, 100)) {
            this.effectSkill.isCharging = false;
            return false;
        }
        if (Util.isTrue(1, 20)) {
            for (Skill skill : this.playerSkill.skills) {
                if (skill.template.id == Skill.TAI_TAO_NANG_LUONG) {
                    this.playerSkill.skillSelect = skill;
                    if (this.nPoint.getCurrPercentHP() < Util.nextInt(0, 100) && SkillService.gI().canUseSkillWithCooldown(this)
                            && SkillService.gI().useSkill(this, null, null, null)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void goToXY(int x, int y, boolean isTeleport) {
        EffectSkillService.gI().stopCharge(this);
        super.goToXY(x, y, isTeleport);
    }

    protected void effectCharger() {
        if (Util.isTrue(15, ConstRatio.PER100)) {
            EffectSkillService.gI().sendEffectCharge(this);
        }
    }

    private void angry() {
        if (this.playersAttack.size() < 5 && Util.isTrue(7, ConstRatio.PER100)) {

            Iterator i = (Iterator) this.zone.getPlayers();
            while (i.hasNext()) {
                Player pl = (Player) i.next();

                if (pl == null) {
                    continue;
                }
                if (pl != null && !pl.equals(this) && Util.getDistance(this, pl) <= DIS_ANGRY
                        && !pl.isBoss && !pl.isDie() && !isInListPlayersAttack(pl)) {
                    try {
                        int count = (int) angryPlayers.get(pl);
                        if (++count > 2) {
                            addPlayerAttack(pl);
                        } else {
                            Service.getInstance().chat(this, "Tránh xa ta ra, đừng để ta nổi giận");
                            effectCharger();

                        }
                        angryPlayers.put(pl, count);
                        break;
                    } catch (Exception e) {
                        Service.getInstance().chat(this, "Tránh xa ta ra, đừng để ta nổi giận");
                        effectCharger();

                        angryPlayers.put(pl, 1);
                        break;
                    }
                }
            }
        }
    }

    private boolean isInListPlayersAttack(Player player) {
        for (Player pl : playersAttack) {
            if (player.equals(pl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkPlayerDie(Player pl) {
        if (pl.isDie()) {
            Service.getInstance().chat(this, "Chừa nha " + plAttack.name + " động vào ta chỉ có chết.");
            this.angryPlayers.put(pl, 0);
            this.playersAttack.remove(pl);
            this.plAttack = null;
        }
    }

    @Override
    public void joinMap() {
        this.zone = getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
        int x = Util.nextInt(50, this.zone.map.mapWidth - 50);
        ChangeMapService.gI().changeMap(this, this.zone, x, this.zone.map.yPhysicInTop(x, 0));
        ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName + "");
    }

    @Override
    public void respawn() {
        super.respawn();
        this.plAttack = null;
        if (this.playersAttack != null) {
            this.playersAttack.clear();
        }
        if (this.angryPlayers != null) {
            this.angryPlayers.clear();
        }
    }

    @Override
    public Zone getMapCanJoin(int mapId) {
        return super.getMapCanJoin(mapId);
    }

    @Override
    public void leaveMap() {
        MapService.gI().exitMap(this);
    }

    @Override
    public void die() {
        this.secondTimeRestToNextTimeAppear = Util.nextInt(20, 30);
        super.die();
    }

    @Override
    public void rewards(Player pl) {

        long hpGoc = this.nPoint.hpg;

        if (hpGoc >= HP_CREATE_SUPER_10) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_9) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_8) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_7) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_6) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_5) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_4) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_3) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_2) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        } else if (hpGoc >= HP_CREATE_SUPER_1) {
            if (Util.isTrue(RATIO_CREATE_SUPER_30, ConstRatio.PER100)) {
                BossFactory.createBoss(BossFactory.SUPER_BROLY);
            }
        }
        generalRewards(pl);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

}
