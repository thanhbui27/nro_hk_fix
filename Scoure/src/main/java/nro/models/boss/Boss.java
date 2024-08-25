package nro.models.boss;

import nro.consts.ConstItem;
import nro.consts.ConstMap;
import nro.consts.ConstPlayer;
import nro.consts.ConstRatio;
import nro.event.Event;
import nro.lib.RandomCollection;
import nro.models.boss.cdrd.CBoss;
import nro.models.boss.iboss.BossInterface;
import nro.models.boss.mabu_war.BossMabuWar;
import nro.models.boss.nappa.Kuku;
import nro.models.boss.nappa.MapDauDinh;
import nro.models.boss.nappa.Rambo;
import nro.models.item.Item;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.server.ServerNotify;
import nro.services.*;
import nro.services.*;
import nro.services.func.ChangeMapService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nro.models.boss.mapoffline.Boss_ThanMeo;
import nro.models.boss.mapoffline.Boss_Yanjiro;
import nro.models.mob.Mob;
import nro.models.npc.Npc;
import nro.server.io.Message;

/**
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 */
public abstract class Boss extends Player implements BossInterface {

    //type dame
    public static final byte DAME_NORMAL = 0;
    public static final byte DAME_PERCENT_HP_HUND = 1;
    public static final byte DAME_PERCENT_MP_HUND = 2;
    public static final byte DAME_PERCENT_HP_THOU = 3;
    public static final byte DAME_PERCENT_MP_THOU = 4;
    public static final byte DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN = 5;

    //type hp
    public static final byte HP_NORMAL = 0;
    public static final byte HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN = 1;

    protected static final byte DO_NOTHING = 71;
    protected static final byte RESPAWN = 77;
    protected static final byte JUST_RESPAWN = 75; // khởi tạo lại, rồi chuyển sang nghỉ
    protected static final byte REST = 0; //boss chưa xuất hiện
    protected static final byte JUST_JOIN_MAP = 1; // vào map rồi chuyển sang nói chuyện lúc đầu
    protected static final byte TALK_BEFORE = 2; //chào hỏi chuyển sang trạng thái khác
    protected static final byte ATTACK = 3;
    protected static final byte IDLE = 4;
    protected static final byte DIE = 5;
    protected static final byte TALK_AFTER = 6;
    protected static final byte LEAVE_MAP = 7;

    //--------------------------------------------------------------------------
    protected BossData data;
    @Setter
    protected byte status;
    protected short[] outfit;
    protected byte typeDame;
    protected byte typeHp;
    protected long percentDame;
    protected short[] mapJoin;
    protected Player playerAtt;

    protected byte indexTalkBefore;
    protected String[] textTalkBefore;
    protected byte indexTalkAfter;
    protected String[] textTalkAfter;
    protected String[] textTalkMidle;
    
    public int zoneHold;
    public boolean isMabuBoss;
    public long timeStartDie;
    public boolean startDie = true;
    public boolean isUseSpeacialSkill;
    public long lastTimeUseSpeacialSkill;

    protected long lastTimeTalk;
    protected int timeTalk;
    protected byte indexTalk;
    protected boolean doneTalkBefore;
    protected boolean doneTalkAffter;

    public long lastTimeRest;
    //thời gian nghỉ chuẩn bị đợt xuất hiện sau
    protected int secondTimeRestToNextTimeAppear = 1800;

    protected int maxIdle;
    protected int countIdle;

    private final List<Skill> skillsAttack;
    private final List<Skill> skillsSpecial;

    protected Player plAttack;
    protected int targetCountChangePlayerAttack;
    protected int countChangePlayerAttack;

    public long lastTimeStartLeaveMap;

    public int timeDelayLeaveMap = 2000;

    protected boolean joinMapIdle;

    public int timeAppear = 0;
    public long lastTimeUpdate;
    public int TIME_RESEND_LOCATION = 15;

    public void changeStatus(byte status) {
        this.status = status;
    }

    public Boss(int id, BossData data) {
        super();
        this.id = id;
        this.skillsAttack = new ArrayList<>();
        this.skillsSpecial = new ArrayList<>();
        this.data = data;
        this.isBoss = true;
        this.initTalk();
        this.respawn();
        setJustRest();
        if (!(this instanceof CBoss)) {
            BossManager.gI().addBoss(this);
        }
    }

    @Override
    public void init() {
        this.name = data.name.replaceAll("%1", String.valueOf(Util.nextInt(0, 100)));
        this.gender = data.gender;
        this.typeDame = data.typeDame;
        this.typeHp = data.typeHp;
        this.nPoint.power = 1;
        this.nPoint.mpg = 752002;
        int dame = data.dame;
        int hp = 1;
        if (data.secondsRest != -1) {
            this.secondTimeRestToNextTimeAppear = data.secondsRest;
        }

        int[] arrHp = data.hp[Util.nextInt(0, data.hp.length - 1)];
        if (arrHp.length == 1) {
            hp = arrHp[0];
        } else {
            hp = Util.nextInt(arrHp[0], arrHp[1]);
        }
        switch (this.typeHp) {
            case HP_NORMAL:
                this.nPoint.hpg = (int) hp;
                break;
            case HP_TIME_PLAYER_WITH_HIGHEST_DAME_IN_CLAN:

                break;
        }
        switch (this.typeDame) {
            case DAME_NORMAL:
                this.nPoint.dameg = (int) dame;
                break;
            case DAME_PERCENT_HP_HUND:
                this.percentDame = dame;
                this.nPoint.dameg = (int) (this.nPoint.hpg * dame / 100);
                break;
            case DAME_PERCENT_MP_HUND:
                this.percentDame = dame;
                this.nPoint.dameg = (int) (this.nPoint.mpg * dame / 100);
                break;
            case DAME_PERCENT_HP_THOU:
                this.percentDame = dame;
                this.nPoint.dameg = (int) (this.nPoint.hp * dame / 1000);
                break;
            case DAME_PERCENT_MP_THOU:
                this.percentDame = dame;
                this.nPoint.dameg = (int) (this.nPoint.mpg * dame / 1000);
                break;
            case DAME_TIME_PLAYER_WITH_HIGHEST_HP_IN_CLAN:

                break;
        }
        this.nPoint.calPoint();
        this.outfit = data.outfit;
        this.mapJoin = data.mapJoin;
        if (data.timeDelayLeaveMap != -1) {
            this.timeDelayLeaveMap = data.timeDelayLeaveMap;
        }
        this.joinMapIdle = data.joinMapIdle;
        initSkill();
    }

    @Override
    public int version() {
        return 214;
    }

    protected void initSkill() {
        this.playerSkill.skills.clear();
        this.skillsAttack.clear();
        this.skillsSpecial.clear();
        int[][] skillTemp = data.skillTemp;
        for (int i = 0; i < skillTemp.length; i++) {
            Skill skill = SkillUtil.createSkill(skillTemp[i][0], skillTemp[i][1]);
            skill.coolDown = skillTemp[i][2];
            this.playerSkill.skills.add(skill);
            switch (skillTemp[i][0]) {
                case Skill.DRAGON:
                case Skill.DEMON:
                case Skill.GALICK:
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC:
                    this.skillsAttack.add(skill);
                    break;
                case Skill.TAI_TAO_NANG_LUONG:
                case Skill.THAI_DUONG_HA_SAN:
                case Skill.BIEN_KHI:
                case Skill.THOI_MIEN:
                case Skill.TROI:
                case Skill.KHIEN_NANG_LUONG:
                case Skill.SOCOLA:
                case Skill.DE_TRUNG:
                case Skill.BIEN_HINH:
                    this.skillsSpecial.add(skill);
                    break;
            }
        }
    }

    @Override
    public void update() {
        super.update();
        try {
            if (!this.effectSkill.isHaveEffectSkill()
                    && !this.effectSkill.isCharging) {
                this.immortalMp();
                switch (this.status) {
                    case RESPAWN:
                        respawn();
                        break;
                    case JUST_RESPAWN:
                        this.changeStatus(REST);
                        break;
                    case REST:
                        if (Util.canDoWithTime(lastTimeRest, secondTimeRestToNextTimeAppear * 1000)) {
                            this.changeStatus(JUST_JOIN_MAP);
                        }
                        break;
                    case JUST_JOIN_MAP:
                        joinMap();
                        if (this.zone != null) {
                            changeStatus(TALK_BEFORE);
                        }
                        break;
                    case TALK_BEFORE:
                        if (talk()) {
                            if (!this.joinMapIdle) {
                                changeToAttack();
                            } else {
                                this.changeStatus(IDLE);
                            }
                        }
                        break;
                    case ATTACK:
                        this.talk();
                        if (this.playerSkill.prepareTuSat || this.playerSkill.prepareLaze
                                || this.playerSkill.prepareQCKK) {
                            break;
                        } else {
                            this.attack();
                        }
                        break;
                    case IDLE:
                        this.idle();
                        break;
                    case DIE:
                        if (this.joinMapIdle) {
                            this.changeToIdle();
                            timeStartDie = System.currentTimeMillis();
                        }
                        changeStatus(TALK_AFTER);
                        break;
                    case TALK_AFTER:
                        if (talk()) {
                            changeStatus(LEAVE_MAP);
                            this.lastTimeStartLeaveMap = System.currentTimeMillis();
                        }
                        break;
                    case LEAVE_MAP:
                        if ("Yanjirô".equals(this.name)) {
                            if (Util.canDoWithTime(lastTimeStartLeaveMap, timeDelayLeaveMap)) {
                                if (this.zone != null) {
                                    Player pl = super.zone.findPlayerByID(this.idPlayerForNPC);
                                    if (pl != null) {
                                        this.leaveMap();
                                        this.changeStatus(RESPAWN);
                                        pl.zone.loadAnotherToMe(pl);
                                        pl.zone.load_Me_To_Another(pl);
                                    }
                                    
                               
                                }
                            }
                        } else if ("Mr.PôPô".equals(this.name)) {
                            if (Util.canDoWithTime(lastTimeStartLeaveMap, timeDelayLeaveMap)) {
                                if (this.zone != null) {
                                    Player pl = super.zone.findPlayerByID(this.idPlayerForNPC);
                                    if (pl != null) {
                                        this.leaveMap();
                                        this.changeStatus(RESPAWN);
                                        pl.zone.loadAnotherToMe(pl);
                                        pl.zone.load_Me_To_Another(pl);
                                    }
                                }
                            }
                        } else if ("Karin".equals(this.name)) {
                            if (Util.canDoWithTime(lastTimeStartLeaveMap, timeDelayLeaveMap)) {
                                if (this.zone != null) {
                                    Player pl = super.zone.findPlayerByID(this.idPlayerForNPC);
                                    if (pl != null) {
                                        this.leaveMap();
                                        this.changeStatus(RESPAWN);
                                        hide_npc(pl, 18, 1);
                                    }
                                }
                            }
                        } else if ("Tàu Pảy Pảy".equals(this.name)) {
                            Player pl = super.zone.findPlayerByID(this.idPlayerForNPC);
                            PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
                            PlayerService.gI().changeAndSendTypePK(pl, ConstPlayer.NON_PK);
                            if (Util.canDoWithTime(lastTimeStartLeaveMap, timeDelayLeaveMap)) {
                                if (this.zone != null) {
                                    if (pl != null) {
                                        ChangeMapService.gI().spaceShipArrive(this, (byte) 2, ChangeMapService.DEFAULT_SPACE_SHIP);
                                        this.leaveMap();
                                    }
                                }
                            }
                        } else if ("Thượng đế".equals(this.name)) {
                            if (Util.canDoWithTime(lastTimeStartLeaveMap, timeDelayLeaveMap)) {
                                if (this.zone != null) {
                                    Player pl = super.zone.findPlayerByID(this.idPlayerForNPC);
                                    if (pl != null) {
                                        this.leaveMap();
                                        PlayerService.gI().changeAndSendTypePK(pl, ConstPlayer.NON_PK);
                                        ChangeMapService.gI().changeMap(pl, 45, 0, 396, 408);
                                    }
                                }
                            }
                        } else if (this.zone.map.mapId == 154) {
                            if (Util.canDoWithTime(lastTimeStartLeaveMap, timeDelayLeaveMap)) {
                                Player pl = super.zone.findPlayerByID(this.idPlayerForNPC);
                                if (pl != null) {
                                    this.leaveMap();
                                    this.changeStatus(RESPAWN);
                                    hide_npc(pl, 56, 1);
                                }
                            }
                        } else {
                            if (Util.canDoWithTime(lastTimeStartLeaveMap, timeDelayLeaveMap)) {
                                this.leaveMap();
                                this.changeStatus(RESPAWN);
                            }
                        }
                        break;
                    case DO_NOTHING:
                        break;
                }
            }
            if (Util.canDoWithTime(lastTimeUpdate, 60000)) {
                if (timeAppear >= TIME_RESEND_LOCATION) {
                    if (this.zone != null && !MapService.gI().isMapBanDoKhoBau(this.zone.map.mapId) && !MapService.gI().isMapDoanhTrai(this.zone.map.mapId) && !(this instanceof BossMabuWar) && !(this instanceof Boss_ThanMeo) && !(this instanceof Boss_Yanjiro)) {
                        ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
                        timeAppear = 0;
                    }
                } else {
                    timeAppear++;
                }
                lastTimeUpdate = System.currentTimeMillis();
            }
        } catch (Exception e) {
            Log.error(Boss.class, e);
        }
    }

    public void hide_npc(Player player, int idnpc, int action) {
        Message msg;
        try {
            msg = new Message(-73);
            msg.writer().writeByte(idnpc);
            msg.writer().writeByte(action);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception ex) {

        }
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        int dame = 0;
        if (this.isDie()) {
            return dame;
        } else {
            if (Util.isTrue(1, 5) && plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.TU_SAT:
                    case Skill.QUA_CAU_KENH_KHI:
                    case Skill.MAKANKOSAPPO:
                        break;
                    default:
                        return 0;
                }
            }
            dame = super.injured(plAtt, damage, piercing, isMobAttack);
            if (this.isDie()) {
                rewards(plAtt);
                notifyPlayeKill(plAtt);
                die();
                EffectSkillService.gI().removeMaPhongBa(this);
            }
            return dame;
        }
    }
    public void nextMabu(boolean isDie) {
        if ((isDie ? this.isDie() : true) && this.head != 427 && !Util.canDoWithTime(this.timeStartDie, 3200)) {
            if (this.startDie) {
                this.startDie = false;
                Service.getInstance().hsChar(this, -1, -1);
                EffectSkillService.gI().startCharge(this);
            }
            return;
        }
        this.startDie = false;
        EffectSkillService.gI().stopCharge(this);
        int id = (int) this.id;
        switch (id) {
            case BossFactory.MABU_MAP:// boss die là bư mập => Summon Super Bư
                this.leaveMap();
                this.id = BossFactory.SUPER_BU;
                this.data = BossData.SUPER_BU;
                this.changeStatus(RESPAWN);
                break;
            case BossFactory.SUPER_BU:// boss die là Super Bư => Summon Kid Bư
                this.leaveMap();
                this.id = BossFactory.KID_BU;
                this.data = BossData.KID_BU;
                this.changeStatus(RESPAWN);
                break;
            case BossFactory.KID_BU:// boss die là Kid Bư => Summon Bư Tenk nếu nuốt được người trong dạng kid bư
                this.leaveMap();
                this.id = BossFactory.BU_TENK;
                this.data = BossData.BU_TENK;
                this.changeStatus(RESPAWN);
                break;
            case BossFactory.BU_TENK:// boss die là Bư Tenk => Summon bư Han
                this.leaveMap();
                this.id = BossFactory.BU_HAN;
                this.data = BossData.BU_HAN;
                this.changeStatus(RESPAWN);
                break;
            default:
                if (isDie) {
                    this.leaveMap();
                }
                break;
        }
    }

    protected void notifyPlayeKill(Player player) {
        if (player != null) {
            ServerNotify.gI().notify(player.name + " vừa tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ");
        }
    }

    public int injuredNotCheckDie(Player plAtt, int damage, boolean piercing) {
        if (this.isDie()) {
            return 0;
        } else {
            int dame = super.injured(plAtt, damage, piercing, false);
            return dame;
        }
    }

    protected Skill getSkillAttack() {
        return skillsAttack.get(Util.nextInt(0, skillsAttack.size() - 1));
    }

    protected Skill getSkillSpecial() {
        return skillsSpecial.get(Util.nextInt(0, skillsSpecial.size() - 1));
    }

    protected Skill getSkillById(int skillId) {
        return SkillUtil.getSkillbyId(this, skillId);
    }

    @Override
    public void die() {
        setJustRest();
        changeStatus(DIE);
    }

    @Override
    public void joinMap() {
        if (this.zone == null) {
            if (this.name == "Karin") {
            } else {
                this.zone = getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            }
        }
        if (this.zone != null) {
            if (this.name == "Karin") {
                ChangeMapService.gI().changeMap(this, 46, this.zone.zoneId, 420, 408);
                System.out.println("BOSS " + this.name + " (" + this.id + ")" + ": " + this.zone.map.mapName + " khu vực " + this.zone.zoneId + "(" + this.zone.map.mapId + ")");
            } else {
                if (!MapService.gI().isMapDoanhTrai(this.zone.map.mapId) && !MapService.gI().isMapBanDoKhoBau(this.zone.map.mapId)) {
                    ChangeMapService.gI().changeMapBySpaceShip(this, this.zone, ChangeMapService.TENNIS_SPACE_SHIP);
                    ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
                    System.out.println("BOSS " + this.name + " (" + this.id + ")" + ": " + this.zone.map.mapName + " khu vực " + this.zone.zoneId + "(" + this.zone.map.mapId + ")");
                }
            }
        }
    }

    public Zone getMapCanJoin(int mapId) {
        Zone map = MapService.gI().getMapWithRandZone(mapId);
        if (map.isBossCanJoin(this)) {
            return map;
        } else {
            return getMapCanJoin(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
        }
    }

    @Override
    public void leaveMap() {
        MapService.gI().exitMap(this);
    }

    @Override
    public boolean talk() {
        switch (status) {
            case TALK_BEFORE:
                if (this.textTalkBefore == null || this.textTalkBefore.length == 0) {
                    return true;
                }
                if (Util.canDoWithTime(lastTimeTalk, 5000)) {
                    if (indexTalkBefore < textTalkBefore.length) {
                        this.chat(textTalkBefore[indexTalkBefore++]);
                        if (indexTalkBefore >= textTalkBefore.length) {
                            return true;
                        }
                        lastTimeTalk = System.currentTimeMillis();
                    } else {
                        return true;
                    }
                }
                break;
            case IDLE:
            case ATTACK:
                if (this.textTalkMidle == null || this.textTalkMidle.length == 0 || !Util.isTrue(1, 30)) {
                    return true;
                }
                if (Util.canDoWithTime(lastTimeTalk, Util.nextInt(15000, 20000))) {
                    this.chat(textTalkMidle[Util.nextInt(0, textTalkMidle.length - 1)]);
                    lastTimeTalk = System.currentTimeMillis();
                }
                break;
            case TALK_AFTER:
                if (this.textTalkAfter == null || this.textTalkAfter.length == 0) {
                    return true;
                }

                this.chat(textTalkAfter[indexTalkAfter++]);

                if (indexTalkAfter >= textTalkAfter.length) {
                    return true;
                }
                if (indexTalkAfter > textTalkAfter.length - 1) {
                    indexTalkAfter = 0;
                }
                break;
        }
        return false;
    }

    @Override
    public void respawn() {
        this.init();
        this.indexTalkBefore = 0;
        this.indexTalkAfter = 0;
        this.nPoint.setFullHpMp();
        this.changeStatus(JUST_RESPAWN);
    }

    protected void goToPlayer(Player pl, boolean isTeleport) {
        goToXY(pl.location.x, pl.location.y, isTeleport);
    }

    protected void goToXY(int x, int y, boolean isTeleport) {
        if (!isTeleport) {
            byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
            byte move = (byte) Util.nextInt(50, 100);
            PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
        } else {
            ChangeMapService.gI().changeMapYardrat(this, this.zone, x, y);
        }
    }

    protected int getRangeCanAttackWithSkillSelect() {
        int skillId = this.playerSkill.skillSelect.template.id;
        if (skillId == Skill.KAMEJOKO || skillId == Skill.MASENKO || skillId == Skill.ANTOMIC) {
            return Skill.RANGE_ATTACK_CHIEU_CHUONG;
        } else {
            return Skill.RANGE_ATTACK_CHIEU_DAM;
        }
    }

    @Override
    public Player getPlayerAttack() throws Exception {
        if (countChangePlayerAttack < targetCountChangePlayerAttack && plAttack != null && plAttack.zone != null && plAttack.zone.equals(this.zone)) {
            if (!plAttack.isDie() && !plAttack.effectSkin.isVoHinh && !plAttack.isMiniPet) {
                this.countChangePlayerAttack++;
                return plAttack;
            } else {
                plAttack = null;
            }
        } else {
            this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
            this.countChangePlayerAttack = 0;

            // Đảm bảo rằng this.zone không null trước khi thực hiện getRandomPlayerInMap()
            if (this.zone != null) {
                plAttack = this.zone.getRandomPlayerInMap();

                // Kiểm tra plAttack có null hay không và có đang ở trong trạng thái VoHinh không
                if (plAttack != null && plAttack.effectSkin.isVoHinh) {
                    plAttack = null;
                }
            } else {
                // Xử lý khi this.zone là null
                plAttack = null;
            }
        }
        return plAttack;
    }
    public List<Player> getListPlayerAttack(int dis) {
        List<Player> Players = new ArrayList<>();
        for (int i = 0; i < this.zone.getHumanoids().size(); i++) {
            Player pl = this.zone.getHumanoids().get(i);
            if (pl != null && !pl.isDie() && !pl.effectSkill.isHoldMabu && Util.getDistance(this, pl) <= dis) {
                Players.add(pl);
            }
        }
        return Players;
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl != null && !pl.isDie() && !pl.isMiniPet) {
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        } else {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
            Log.error(Boss.class, ex);
        }
    }

    private void immortalMp() {
        this.nPoint.mp = this.nPoint.mpg;
    }

    protected abstract boolean useSpecialSkill();

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public short getHead() {
        if (effectSkill.isSocola || effectSkin.isSocola) {
            return 412;
        }
        if (effectSkill.isMaPhongBa || effectSkin.isMaPhongBa) {
            return 1376;
        }
        return this.outfit[0];
    }

    @Override
    public short getBody() {
        if (effectSkill.isSocola || effectSkin.isSocola) {
            return 413;
        }
        if (effectSkill.isMaPhongBa || effectSkin.isMaPhongBa) {
            return 1377;
        }
        return this.outfit[1];
    }

    @Override
    public short getLeg() {
        if (effectSkill.isSocola || effectSkin.isSocola) {
            return 414;
        }
        if (effectSkill.isMaPhongBa || effectSkin.isMaPhongBa) {
            return 1378;
        }
        return this.outfit[2];
    }

    @Override
    public short getFlagBag() {
        if (this.outfit.length < 4) {
            return -1;
        }
        return this.outfit[3];
    }

    @Override
    public byte getAura() {
        if (this.outfit.length < 5) {
            return -1;
        }
        return (byte) this.outfit[4];
    }

    @Override
    public byte getEffFront() {
        if (this.outfit.length < 6) {
            return -1;
        }
        return (byte) this.outfit[5];
    }

    //status
    protected void changeIdle() {
        this.changeStatus(IDLE);
    }

    /**
     * Đổi sang trạng thái tấn công
     */
    protected void changeAttack() {
        this.changeStatus(ATTACK);
    }

    public void setJustRest() {
        this.lastTimeRest = System.currentTimeMillis();
    }

    public void setJustRestToFuture() {
        this.lastTimeRest = System.currentTimeMillis() + 8640000000L;
    }

    @Override
    public void dropItemReward(int tempId, int playerId, int... quantity) {
        if (!this.zone.map.isMapOffline && this.zone.map.type == ConstMap.MAP_NORMAL) {
            int x = this.location.x + Util.nextInt(-30, 30);
            if (x < 30) {
                x = 30;
            } else if (x > zone.map.mapWidth - 30) {
                x = zone.map.mapWidth - 30;
            }
            int y = this.location.y;
            if (y > 24) {
                y = this.zone.map.yPhysicInTop(x, y - 24);
            }
            ItemMap itemMap = new ItemMap(this.zone, tempId, (quantity != null && quantity.length == 1) ? quantity[0] : 1, x, y, playerId);
            Service.getInstance().dropItemMap(itemMap.zone, itemMap);
        }
    }

    @Override
    public void generalRewards(Player player) {//Hmmmmm....phẩn thưởng chung (boss nào cũng rớt - boss phó bản)
        if (player != null) {
            ItemMap itemMap = null;
            int x = this.location.x;
            int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);

            if (!(this instanceof Kuku) && !(this instanceof Rambo) && !(this instanceof MapDauDinh)) {
                if (Util.isTrue(20, 100)) {
                    RandomCollection<Integer> rd = new RandomCollection<>();
                    rd.add(1, ConstItem.MANH_AO);
                    rd.add(1, ConstItem.MANH_QUAN);
                    rd.add(1, ConstItem.MANH_GANG_TAY);
                    if (Event.isEvent()) {
                        rd.add(1, ConstItem.QUE_DOT);
                    }
                    int rwID = rd.next();
                    itemMap = new ItemMap(this.zone, rwID, 1, x, y, player.id);

                    RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
                }
            }
            if (itemMap != null) {
                Service.getInstance().dropItemMap(zone, itemMap);
            }
        }
    }

    /**
     * Đổi trạng thái máu trắng -> đỏ, chuyển trạng thái tấn công
     */
    public void changeToAttack() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.PK_ALL);
        changeStatus(ATTACK);
    }

    /**
     * Đổi trạng thái máu đỏ -> trắng, chuyển trạng thái đứng
     */
    public void changeToIdle() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
        changeStatus(IDLE);
    }

    protected void chat(String text) {
        Service.getInstance().chat(this, text);
    }
}
