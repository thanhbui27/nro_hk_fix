package nro.models.player;

import java.sql.Timestamp;
import java.time.LocalTime;
import nro.card.Card;
import nro.card.CollectionBook;
import nro.consts.ConstAchive;
import nro.consts.ConstPlayer;
import nro.consts.ConstTask;
import nro.data.DataGame;
import nro.dialog.ConfirmDialog;
import nro.models.clan.Buff;
import nro.models.item.CaiTrang;
import nro.models.item.FlagBag;
import nro.models.boss.event.EscortedBoss;
import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import nro.models.intrinsic.IntrinsicPlayer;
import nro.models.item.Item;
import nro.models.item.ItemTime;
import nro.models.map.ItemMap;
import nro.models.map.TrapMap;
import nro.models.map.Zone;
import nro.models.map.war.BlackBallWar;
import nro.models.map.mabu.MabuWar;
import nro.models.map.war.NamekBallWar;
import nro.models.mob.MobMe;
import nro.models.npc.specialnpc.MabuEgg;
import nro.models.npc.specialnpc.MagicTree;
import nro.models.pvp.PVP;
import nro.models.skill.PlayerSkill;
import nro.models.task.TaskPlayer;
import nro.server.Client;
import nro.server.Manager;
import nro.server.io.Message;
import nro.server.io.Session;
import nro.services.*;
import nro.services.func.ChangeMapService;
import nro.services.func.CombineNew;
import nro.services.func.PVPServcice;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossManager;
import nro.models.boss.cdrd.Saibamen;
import nro.models.boss.mapoffline.Boss_Tau77;
import nro.models.boss.mapoffline.Boss_Yanjiro;
import nro.models.item.ItemOption;
import static nro.models.item.ItemTime.TEXT_NHAN_BUA_MIEN_PHI;
import static nro.models.item.ItemTime.TEXT_NHIEM_VU_HANG_NGAY;
import nro.models.map.DaiHoiVoThuat.DaiHoiVoThuatService;
import nro.models.mob.Mob;
import nro.models.npc.NpcFactory;
import nro.models.skill.Skill;
import nro.models.skill.SkillSpecial;
import nro.sendEff.SendEffect;
import nro.services.func.minigame.ChonAiDay_Gem;
import nro.services.func.minigame.ChonAiDay_Gold;
import nro.services.func.minigame.ChonAiDay_Ruby;
import nro.services.func.EffectMapService;
import nro.services.func.MiniGame;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class Player {

    public int server;
    public byte[] buyLimit;

    public PlayerEvent event;
    public List<String> textRuongGo = new ArrayList<>();
    public boolean receivedWoodChest;
    public int goldChallenge;
    public int gemChallenge;
    public int levelWoodChest;
    public boolean isInvisible;
    public boolean sendMenuGotoNextFloorMabuWar;
    public long lastTimeBabiday;
    public long lastTimeChangeZone;
    public long lastTimeChatGlobal;
    public long lastTimeChatPrivate;
    public long lastTimeChangeMap;
    public Date firstTimeLogin;
    private Session session;
    public byte countSaveFail;
    public boolean beforeDispose;

    public long timeFixInventory;
    public boolean isPet;
    public boolean isBoss;
    public boolean isMiniPet;

    public boolean isChangeMap = false;

    public String nameClan;
    public int levelBDKBDone;
    public long timeBDKBDone;

    public Taixiu taixiu;
    public boolean isShopKiGuiSuKien = false;

    public boolean isHaveYajiro = false;

    public long lastTimeUpdateTopBDKB;

    // DÀNH CHO ÐAI HOI VO THUAT THUONG //
    public boolean lockPK;
    public Timer timerDHVT;
    public Player _friendGiaoDich;

    // END DHVT THUONG //
    public int playerTradeId = -1;
    public Player playerTrade;

    public boolean isBatTu = false;
    
    //clone player
    public PlayerClone clone;
    public boolean isClone;
    
    //DANH CHO THACH DAU NPC//
    public int thachDauNPC;

    public int doneThachDauThanMeo; // Thần mèo
    public int doneThachDauYanjiro; // Yanjiro
    public int doneThachDauPoPo; // Popo
    public int doneThachDauThuongDe; // Thượng đế
    public int doneThachDauBubbles; // Bubbbles
    public int doneThachDauThanVuTru; // Thần vũ trụ
    //END DANH CHO THACH DAU NPC//

    public int DoneVoDaiBaHatMit = 0;

    public int dameMaFuBa = 0;

    public int DanhQuaiNhanNgoc = 0;

    public long idPlayerForNPC;

    public boolean haveTau77;

    public boolean hide_Yanjiro = false;

    public int MaBaoVe_TamThoi = 0;

    public boolean haveMaPhongBa = false;

    public int activeYajiro = 0;

    public int mapIdBeforeLogout;
    public List<Zone> mapBlackBall;
    public Zone zone;
    public Zone mapBeforeCapsule;
    public List<Zone> mapCapsule;
    public Pet pet;
    public Boss boss;
    public MiniPet minipet;

    public int goldNormar;
    public int goldVIP;
    public int id_CSMM_Gold;

    public int soDuVND;

    public int soThoiVang;

    public boolean thanhVien;

    public int rubyNormar;
    public int rubyVIP;
    public int id_CSMM_Ruby;

    public int gemNormar;
    public int gemVIP;
    public int id_CSMM_Gem;

    public MobMe mobMe;
    public Location location;
    public SetClothes setClothes;
    public EffectSkill effectSkill;
    public MabuEgg mabuEgg;
    public TaskPlayer playerTask;
    public ItemTime itemTime;
    public Fusion fusion;
    public MagicTree magicTree;
    public IntrinsicPlayer playerIntrinsic;
    public Inventory inventory;
    public PlayerSkill playerSkill;
    public CombineNew combineNew;
    public IDMark iDMark;
    public Charms charms;
    public EffectSkin effectSkin;
    public Gift gift;
    public NPoint nPoint;
    public RewardBlackBall rewardBlackBall;
    public EffectFlagBag effectFlagBag;

    public int rankSieuHang;

    public Clan clan;
    public ClanMember clanMember;

    public ListFriendEnemy<Friend> friends;
    public ListFriendEnemy<Enemy> enemies;

    protected boolean actived = false;
    public boolean loaded;

    public long id;
    public String name;
    public byte gender;
    public boolean isNewMember = true;
    public short head;

    public Timestamp lastimelogin;

    public int tongnap;

    public long lastimelogin2;

    public byte typePk;

    public boolean isUseMaBaoVe;

    public int MaBaoVe;

    public long lastTimeNotifyTimeHoldBlackBall;
    public long lastTimeHoldBlackBall;
    public int tempIdBlackBallHold = -1;
    public int tempIdNamecBallHold = -1;
    public boolean isHoldBlackBall;
    public boolean isHoldNamecBall;

    public byte cFlag;
    public long lastTimeChangeFlag;
    public long lastTimeTrade;

    public boolean haveTennisSpaceShip;
    private byte useSpaceShip;

    public boolean isGoHome;

    public boolean justRevived;
    public long lastTimeRevived;
    public boolean immortal;

    public long lastTimeBan;
    public long lastTimeUpdate;
    public boolean isBan;

    public boolean isGotoFuture;
    public long lastTimeGoToFuture;
    public boolean isgotoPrimaryForest;
    public long lastTimePrimaryForest;

    public int maxTime;
    public byte type;

    public boolean isGoToBDKB;
    public long lastTimeGoToBDKB;
    public long lastTimeAnXienTrapBDKB;
    private short powerPoint;
    private short percentPowerPont;

    public SkillSpecial skillSpecial;

    // DANH HIỆU BY LOUIS GOKU
    public boolean DH1 = false;

    public boolean isTitleUse;
    public long lastTimeTitle1;
    public int IdDanhHieu_1;
    public int ChiSoHP_1;
    public int ChiSoKI_1;
    public int ChiSoSD_1;

    public boolean DH2 = false;
    public boolean isTitleUse2;
    public long lastTimeTitle2;
    public int IdDanhHieu_2;
    public int ChiSoHP_2;
    public int ChiSoKI_2;
    public int ChiSoSD_2;

    public boolean DH3 = false;
    public boolean isTitleUse3;
    public long lastTimeTitle3;
    public int IdDanhHieu_3;
    public int ChiSoHP_3;
    public int ChiSoKI_3;
    public int ChiSoSD_3;

    public boolean DH4 = false;
    public boolean isTitleUse4;
    public long lastTimeTitle4;
    public int IdDanhHieu_4;
    public int ChiSoHP_4;
    public int ChiSoKI_4;
    public int ChiSoSD_4;

    public boolean DH5 = false;
    public boolean isTitleUse5;
    public long lastTimeTitle5;
    public int IdDanhHieu_5;
    public int ChiSoHP_5;
    public int ChiSoKI_5;
    public int ChiSoSD_5;

    public long lastTimeSwapWhis;
    public long lastTimeKillWhis;
    public int levelKillWhis;

    public int levelKillWhisDone;
    public long timeKillWhis;

    public int levelWhis;

    public long lastTimePickItem;
    @Setter
    @Getter
    private CollectionBook collectionBook;
    @Getter
    @Setter
    private boolean isSaving, isDisposed;
    @Getter
    @Setter
    private boolean interactWithKarin;
    @Getter
    @Setter
    private EscortedBoss escortedBoss;
    @Setter
    @Getter
    private ConfirmDialog confirmDialog;
    @Getter
    @Setter
    public byte[] rewardLimit;
    @Setter
    @Getter
    private PetFollow petFollow;
    @Setter
    @Getter
    private Buff buff;
    public int goldXiu;
    public int goldTai;
    public boolean strRequire;

    public int getLevelKillWhis() {
        return levelKillWhis;
    }

    public void setLevelKillWhis(int levelKillWhis) {
        this.levelKillWhis = levelKillWhis;
    }

    // Các phương thức getter và setter cho timeKillWhis
    public long getTimeKillWhis() {
        return timeKillWhis;
    }

    public void setTimeKillWhis(long timeKillWhis) {
        this.timeKillWhis = timeKillWhis;
    }

    public Player() {
        location = new Location();
        nPoint = new NPoint(this);
        inventory = new Inventory(this);
        playerSkill = new PlayerSkill(this);
        setClothes = new SetClothes(this);
        effectSkill = new EffectSkill(this);
        fusion = new Fusion(this);
        playerIntrinsic = new IntrinsicPlayer(this);
        rewardBlackBall = new RewardBlackBall(this);
        effectFlagBag = new EffectFlagBag(this);
        //----------------------------------------------------------------------
        taixiu = new Taixiu();
        iDMark = new IDMark();
        combineNew = new CombineNew();
        playerTask = new TaskPlayer(this);
        friends = new ListFriendEnemy<>(this);
        skillSpecial = new SkillSpecial(this);
        enemies = new ListFriendEnemy<>(this);
        itemTime = new ItemTime(this);
        charms = new Charms(this);
        gift = new Gift(this);
        effectSkin = new EffectSkin(this);
        event = new PlayerEvent(this);
        buyLimit = new byte[13];
        buff = Buff.NONE;
    }

    //--------------------------------------------------------------------------
    public short getPowerPoint() {
        return powerPoint;
    }

    public void addPowerPoint(int value) {
        powerPoint += value;
    }

    public short getPercentPowerPont() {
        return percentPowerPont;
    }

    public void addPercentPowerPoint(int value) {
        percentPowerPont += value;
    }

    public void resetPowerPoint() {
        percentPowerPont = 0;
        powerPoint = 0;
    }

    public void setUseSpaceShip(byte useSpaceShip) {
        // 0 - không dùng
        // 1 - tàu vũ trụ theo hành tinh
        // 2 - dịch chuyển tức thời
        // 3 - tàu tenis
        this.useSpaceShip = useSpaceShip;
    }

    public byte getUseSpaceShip() {
        return this.useSpaceShip;
    }

    public boolean isDie() {
        if (this.nPoint != null) {
            return this.nPoint.hp <= 0;
        }
        return true;
    }

    //--------------------------------------------------------------------------
    public void setSession(Session session) {
        this.session = session;
    }

    public void sendMessage(Message msg) {
        if (this.session != null) {
            session.sendMessage(msg);
        }
    }

    public Session getSession() {
        return this.session;
    }

    public int version() {
        return session == null ? 231 : session.version;
    }

    public boolean isVersionAbove(int version) {
        return version() >= version;
    }

    public void update() {
        if (!this.beforeDispose) {
            try {
                if (!isBan) {
                    if (!this.isBoss) {
                        this.idPlayerForNPC = this.id;
                    }
                    if (this.lastTimeTitle1 > 0) {
                        DH1 = true;
                    }
                    if (this.lastTimeTitle2 > 0) {
                        DH2 = true;
                    }
                    if (this.lastTimeTitle3 > 0) {
                        DH3 = true;
                    }
                    if (this.lastTimeTitle4 > 0) {
                        DH4 = true;
                    }
                    if (this.lastTimeTitle5 > 0) {
                        DH5 = true;
                    }
                    if (nPoint != null) {
                        nPoint.update();
                    }
                    if (fusion != null) {
                        fusion.update();
                    }
                    if (effectSkin != null) {
                        effectSkill.update();
                    }
                    if (mobMe != null) {
                        mobMe.update();
                    }
                    if (effectSkin != null) {
                        effectSkin.update();
                    }
                    if (pet != null) {
                        pet.update();
                    }
                    if (minipet != null) {
                        minipet.update();
                    }
                    if (clone != null) {
                        clone.update();
                    }
                    if (magicTree != null) {
                        magicTree.update();
                    }
                    if (itemTime != null) {
                        itemTime.update();
                    }
                    if (event != null) {
                        event.update();
                    }
                    BlackBallWar.gI().update(this);
                    if (!this.isBoss && !this.isPet && !this.isMiniPet && !this.isClone) {
                        MabuWar.gI().update(this);
                        if (this.server != Manager.SERVER) {
                            PlayerService.gI().banPlayer(this);
                        }
                        if (this.isDie()) {
                            EffectSkillService.gI().removeMaPhongBa(this);
                        }
                        checkLocation();
                        if (lastimelogin2 == 0) {
                            lastimelogin2 = System.currentTimeMillis();
                        }
                        doneTask_HoatDong_ChamChi();
                        send_text_time_nhiem_vu();
                        send_text_time_nhan_bua_mien_phi();
                        checkDoneBDKBSom();
                        checkDoneKhiGasSom();
                    }
                    if (!this.isBoss && !this.isPet && !this.isMiniPet && !this.isClone) {
                        boolean doneSendNotify = false;
                        if (this.zone.map.mapId == 57) {
                            if (this.zone.isCheckKilledAll(57)) {
                                if (doneSendNotify = false) {
                                    Service.getInstance().sendThongBao(this, "Mau đi tìm độc nhãn");
                                    doneSendNotify = true;
                                }
                            }
                        }
                    }
                    if (this.lastTimeTitle1 != 0 && Util.canDoWithTime(this.lastTimeTitle1, 6000)) {
                        lastTimeTitle1 = 0;
                        isTitleUse = false;
                        IdDanhHieu_1 = -1;
                        ChiSoHP_1 = 0;
                        ChiSoKI_1 = 0;
                        ChiSoSD_1 = 0;
                        SendEffect.getInstance().removeTitle(this);
                    }
                    if (this.lastTimeTitle2 != 0 && Util.canDoWithTime(this.lastTimeTitle2, 6000)) {
                        lastTimeTitle2 = -1;
                        isTitleUse2 = false;
                        IdDanhHieu_2 = 0;
                        ChiSoHP_2 = 0;
                        ChiSoKI_2 = 0;
                        ChiSoSD_2 = 0;
                        SendEffect.getInstance().removeTitle(this);
                    }

                    if (this.lastTimeTitle3 != 0 && Util.canDoWithTime(this.lastTimeTitle3, 6000)) {
                        lastTimeTitle3 = -1;
                        isTitleUse3 = false;
                        IdDanhHieu_3 = 0;
                        ChiSoHP_3 = 0;
                        ChiSoKI_3 = 0;
                        ChiSoSD_3 = 0;
                        SendEffect.getInstance().removeTitle(this);
                    }

                    if (this.lastTimeTitle4 != 0 && Util.canDoWithTime(this.lastTimeTitle4, 6000)) {
                        lastTimeTitle4 = -1;
                        isTitleUse4 = false;
                        IdDanhHieu_4 = 0;
                        ChiSoHP_4 = 0;
                        ChiSoKI_4 = 0;
                        ChiSoSD_4 = 0;
                        SendEffect.getInstance().removeTitle(this);
                    }

                    if (this.lastTimeTitle5 != 0 && Util.canDoWithTime(this.lastTimeTitle5, 6000)) {
                        lastTimeTitle5 = -1;
                        isTitleUse5 = false;
                        IdDanhHieu_5 = 0;
                        ChiSoHP_5 = 0;
                        ChiSoKI_5 = 0;
                        ChiSoSD_5 = 0;
                        SendEffect.getInstance().removeTitle(this);
                    }
                    if (this.lastTimeTitle1 == 0 && this.lastTimeTitle2 > 0) {
                        lastTimeTitle1 = lastTimeTitle2;
                        isTitleUse = true;
                        IdDanhHieu_1 = IdDanhHieu_2;
                        ChiSoHP_1 = ChiSoHP_2;
                        ChiSoKI_1 = ChiSoKI_2;
                        ChiSoSD_1 = ChiSoSD_2;

                        lastTimeTitle2 = 0;
                        isTitleUse2 = false;
                        IdDanhHieu_2 = 0;
                        ChiSoHP_2 = 0;
                        ChiSoKI_2 = 0;
                        ChiSoSD_2 = 0;
                        SendEffect.getInstance().removeTitle(this);
                    }
                    if (this.lastTimeTitle2 == 0 && this.lastTimeTitle3 > 0) {
                        lastTimeTitle2 = lastTimeTitle3;
                        isTitleUse2 = true;
                        IdDanhHieu_2 = IdDanhHieu_3;
                        ChiSoHP_2 = ChiSoHP_3;
                        ChiSoKI_2 = ChiSoKI_3;
                        ChiSoSD_2 = ChiSoSD_3;

                        lastTimeTitle3 = 0;
                        isTitleUse3 = false;
                        IdDanhHieu_3 = 0;
                        ChiSoHP_3 = 0;
                        ChiSoKI_3 = 0;
                        ChiSoSD_3 = 0;
                        SendEffect.getInstance().removeTitle(this);
                    }
                    if (this.lastTimeTitle3 == 0 && this.lastTimeTitle4 > 0) {
                        lastTimeTitle3 = lastTimeTitle4;
                        isTitleUse3 = true;
                        IdDanhHieu_3 = IdDanhHieu_4;
                        ChiSoHP_3 = ChiSoHP_4;
                        ChiSoKI_3 = ChiSoKI_4;
                        ChiSoSD_3 = ChiSoSD_4;

                        lastTimeTitle4 = 0;
                        isTitleUse4 = false;
                        IdDanhHieu_4 = 0;
                        ChiSoHP_4 = 0;
                        ChiSoKI_4 = 0;
                        ChiSoSD_4 = 0;
                        SendEffect.getInstance().removeTitle(this);
                    }
                    if (this.lastTimeTitle4 == 0 && this.lastTimeTitle5 > 0) {
                        lastTimeTitle4 = lastTimeTitle5;
                        isTitleUse4 = true;
                        IdDanhHieu_4 = IdDanhHieu_5;
                        ChiSoHP_4 = ChiSoHP_5;
                        ChiSoKI_4 = ChiSoKI_5;
                        ChiSoSD_4 = ChiSoSD_5;

                        lastTimeTitle5 = 0;
                        isTitleUse5 = false;
                        IdDanhHieu_5 = 0;
                        ChiSoHP_5 = 0;
                        ChiSoKI_5 = 0;
                        ChiSoSD_5 = 0;
                        SendEffect.getInstance().removeTitle(this);
                    }
                    if (isgotoPrimaryForest && Util.canDoWithTime(lastTimePrimaryForest, 6000)) {
                        ChangeMapService.gI().changeMap(this, 161, -1, 169, 312);
                        this.isgotoPrimaryForest = false;
                    }
                    if (this.zone != null) {
                        TrapMap trap = this.zone.isInTrap(this);
                        if (trap != null) {
                            trap.doPlayer(this);
                        }
                    }
                } else {
                    if (Util.canDoWithTime(lastTimeBan, 5000)) {
                        Client.gI().kickSession(session);
                    }
                }
            } catch (Exception e) {
                Log.error(Player.class, e, "Lỗi tại player: " + this.name);
            }
        }
    }

    public void checkDoneBDKBSom() {
        if (this.zone.map.mapId == 137) {
            if (this.zone.isCheckKilledAll(137)) {
                if (this.clan != null) {
                    if (this.clan.banDoKhoBau != null) {
                        if (this.clan.banDoKhoBau.trungUyIsDie) {
                            if (this.clan.banDoKhoBau.doneBDKBSom == false) {
                                this.clan.banDoKhoBau.doneBDKBSom = true;
                                this.clan.banDoKhoBau.lasTimeDoneDoanhTraiSom = System.currentTimeMillis();
                            }
                        }
                    }
                }
            }
        }
    }

    public void checkDoneKhiGasSom() {
        if (this.zone.map.mapId == 148) {
            if (this.zone.isCheckKilledAll(148)) {
                if (this.clan != null) {
                    if (this.clan.khiGas != null) {
                        this.clan.khiGas.isSpawnDrLychee = true;
                    }
                }
            }
        }
    }

    public void doneTask_HoatDong_ChamChi() {
        if (Util.canDoWithTime(lastimelogin2, 3600000)) {
            this.playerTask.achivements.get(ConstAchive.HOAT_DONG_CHAM_CHI).count++;
            lastimelogin2 = System.currentTimeMillis();
        }
    }

    public long lastimelogin3;

    public long lastTimeSendTextTime;

    public void send_text_time_nhiem_vu() {
        if (this.playerTask.sideTask.template != null) {
            if (Util.canDoWithTime(lastimelogin3, 60000)) {
                ItemTimeService.gI().sendTextTime(this, TEXT_NHIEM_VU_HANG_NGAY, "Nhiệm vụ hằng ngày: " + this.playerTask.sideTask.getName() + " (" + this.playerTask.sideTask.getPercentProcess() + "%)", 20);
                lastimelogin3 = System.currentTimeMillis();
            }
        }
    }

    public void send_text_time_nhan_bua_mien_phi() {
        if (Util.canDoWithTime(lastTimeSendTextTime, 60000)) {
            if (this.event.luotNhanBuaMienPhi == 1) {
                ItemTimeService.gI().sendTextTime(this, TEXT_NHAN_BUA_MIEN_PHI, "Nhận ngẫu nhiên bùa 1h mỗi ngày tại Bà Hạt Mít ở vách núi", 30);
            }
            lastTimeSendTextTime = System.currentTimeMillis();
        }
    }

    private void checkLocation() {
        if (this.zone.map.mapId != 140) {
            if (this.itemTime.isDanhNhanBan && !this.itemTime.doneDanhNhanBan) {
                this.itemTime.doneDanhNhanBan = true;
                ItemTimeService.gI().sendItemTime(this, 2295, 1);
                PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
                Service.getInstance().sendThongBao(this, "Bạn đã thất bại, hãy thử sức lại vào ngày mai");
            }
            return;
        }
        if (this.zone.map.mapId == 47) {
            Service.getInstance().callTau77(this);
        } else {
            this.haveTau77 = false;
        }
    }

    //--------------------------------------------------------------------------
//    /*
//     * {380, 381, 382}: ht lưỡng long nhất thể xayda trái đất
//     * {383, 384, 385}: ht porata xayda trái đất
//     * {391, 392, 393}: ht namếc
//     * {870, 871, 872}: ht c2 trái đất
//     * {873, 874, 875}: ht c2 namếc
//     * {867, 878, 869}: ht c2 xayda
//     */
    private static final short[][] idOutfitFusion = {
        {380, 381, 382}, //luong long
        {383, 384, 385},// porata 
        {391, 392, 393}, //hop the chung namec

        {870, 871, 872},//trai dat c2
        {873, 874, 875}, //namec c2
        {867, 868, 869}, //xayda c2
    };

    public byte getAura() {
        
        if (this.isPl() && this.effectSkill != null && this.effectSkill.isBienHinh) {
            return ConstPlayer.AURABIENHINH[this.gender][this.effectSkill.levelBienHinh - 1];
        }
        
        CollectionBook book = getCollectionBook();
        if (book != null) {
            Card card = book.getCards().stream()
                    .filter(t -> t.isUse() && t.getCardTemplate().getAura() != -1)
                    .findAny()
                    .orElse(null);
            if (card != null) {
                return (byte) card.getCardTemplate().getAura();
            }
        }
        return -1;
    }

    public byte getEffFront() {
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 10) {
            return -1;
        }
        int[] levels = new int[5];
        ItemOption[] options = new ItemOption[5];
        Item[] items = new Item[]{
            this.inventory.itemsBody.get(0),
            this.inventory.itemsBody.get(1),
            this.inventory.itemsBody.get(2),
            this.inventory.itemsBody.get(3),
            this.inventory.itemsBody.get(4)
        };
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            for (ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 72) {
                    levels[i] = io.param;
                    options[i] = io;
                    break;
                }
            }
        }
        int minLevel = Integer.MAX_VALUE;
        int count = 0;
        for (int level : levels) {
            if (level >= 4 && level <= 8) {
                minLevel = Math.min(minLevel, level);
                count++;
            }
        }
        if (count == 5) {
            return (byte) minLevel;
        } else {
            return -1;
        }
    }

    public boolean checkSkinFusion() {
        if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            Short idct = inventory.itemsBody.get(5).template.id;
            if (idct >= 601 && idct <= 603 || idct >= 639 && idct <= 641) {
                return true;
            }
        }
        return false;
    }

   public boolean checkSkinFusionNew() {
        if (inventory != null && inventory.itemsBody.get(5).isNotNullItem() && pet != null && pet.inventory != null && pet.inventory.itemsBody.get(5).isNotNullItem()) {
            Short idCaiTrangSP = inventory.itemsBody.get(5).template.id;
            Short idCaitrangDT = pet.inventory.itemsBody.get(5).template.id;
            if (idCaiTrangSP == 1340 && idCaitrangDT == 1327 || idCaiTrangSP == 1340 && idCaitrangDT == 1327) {
                return true;
            }
        }
        return false;
    }

    public short getHead() {
        if (this.id == 1000000) {
            return 412;
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        }
        if (effectSkill != null && effectSkill.isBienHinh) {
            return (short) ConstPlayer.HEADBIENHINH[this.gender][effectSkill.levelBienHinh - 1];
        
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 412;
        }
        if (effectSkill != null && effectSkill.isMaPhongBa) {
            return 1376;
        } else if (effectSkin != null && effectSkin.isHoaDa) {
            return 454;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (checkSkinFusionNew()) {
                return 1447;
            } else if (checkSkinFusion()) {
                CaiTrang ct = Manager.gI().getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
                return (short) (ct.getID()[0] != -1 ? ct.getID()[0] : inventory.itemsBody.get(5).template.part);
            } else if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][0];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            CaiTrang ct = Manager.gI().getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
            if (checkSkinFusion()) {
                return this.head;
            }
            if (ct != null) {
                return (short) (ct.getID()[0] != -1 ? ct.getID()[0] : inventory.itemsBody.get(5).template.part);
            }
        }
        return this.head;
    }

    public short getBody() {
        if (this.id == 1000000) {
            return 413;
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 193;
        }
        if (effectSkill != null && effectSkill.isBienHinh) {
            return (short) ConstPlayer.BODYBIENHINH[this.gender];
        
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 413;
        }
        if (effectSkill != null && effectSkill.isMaPhongBa) {
            return 1377;
        } else if (effectSkin != null && effectSkin.isHoaDa) {
            return 455;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (checkSkinFusionNew()) {
                return 1443;
            } else if (checkSkinFusion()) {
                CaiTrang ct = Manager.gI().getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
                return (short) ct.getID()[1];
            }
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//                if (this.pet.isMabu) {
//                    return idOutfitFusion[3 + this.gender][1];
//                }
//                if (this.pet.isGokuSSJ4) {
//                    if (this.inventory.itemsBody.get(5).isNotNullItem()) {
//                        if (this.inventory.itemsBody.get(5).template.id == 1305) {
//                            return 1389;
//                        }
//                    }
//                }
//                if (this.pet.isVegetaSSJ4) {
//                    if (this.inventory.itemsBody.get(5).isNotNullItem()) {
//                        if (this.inventory.itemsBody.get(5).template.id == 1304) {
//                            return 1389;
//                        }
//                    }
//                }
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][1];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            CaiTrang ct = Manager.gI().getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
            if (checkSkinFusion()) {
                if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
                    if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
                        return inventory.itemsBody.get(0).template.part;
                    }
                }
            }
            if (ct != null && ct.getID()[1] != -1) {
                return (short) ct.getID()[1];
            }
        }
        if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
            return inventory.itemsBody.get(0).template.part;
        }
        return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
    }

    public short getLeg() {
        if (this.id == 1000000) {
            return 414;
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 194;
        }
        if (effectSkill != null && effectSkill.isBienHinh) {
            return (short) ConstPlayer.LEGBIENHINH[this.gender];
        
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 414;
        }
        if (effectSkill != null && effectSkill.isMaPhongBa) {
            return 1378;
        } else if (effectSkin != null && effectSkin.isHoaDa) {
            return 456;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (checkSkinFusionNew()) {
                return 1444;
            } else if (checkSkinFusion()) {
                CaiTrang ct = Manager.gI().getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
                return (short) ct.getID()[2];
            }
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//                if (this.pet.isMabu) {
//                    return idOutfitFusion[3 + this.gender][2];
//                }
//                if (this.pet.isGokuSSJ4) {
//                    if (this.inventory.itemsBody.get(5).isNotNullItem()) {
//                        if (this.inventory.itemsBody.get(5).template.id == 1305) {
//                            return 1390;
//                        }
//                    }
//                }
//                if (this.pet.isVegetaSSJ4) {
//                    if (this.inventory.itemsBody.get(5).isNotNullItem()) {
//                        if (this.inventory.itemsBody.get(5).template.id == 1304) {
//                            return 1390;
//                        }
//                    }
//                }
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][2];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            if (checkSkinFusion()) {
                if (inventory != null && inventory.itemsBody.get(1).isNotNullItem()) {
                    return inventory.itemsBody.get(1).template.part;
                }
            }
            CaiTrang ct = Manager.gI().getCaiTrangByItemId(inventory.itemsBody.get(5).template.id);
            if (ct != null && ct.getID()[2] != -1) {
                return (short) ct.getID()[2];
            }
        }
        if (inventory != null && inventory.itemsBody.get(1).isNotNullItem()) {
            return inventory.itemsBody.get(1).template.part;
        }
        return (short) (gender == 1 ? 60 : 58);
    }

    public short getFlagBag() {
        if (this.isHoldBlackBall) {
            return 31;
        } else if (this.isHoldNamecBall) {
            return 30;
        } else if (this.inventory.itemsBody.size() >= 9 && this.inventory.itemsBody.get(8).isNotNullItem()) {
            FlagBag f = FlagBagService.gI().getFlagBagByName(this.inventory.itemsBody.get(8).template.name);
            if (f != null) {
                return (short) f.id;
            }
        } else if (TaskService.gI().getIdTask(this) == ConstTask.TASK_3_2) {
            return 28;
        } else if (this.clan != null) {
            return (short) this.clan.imgId;
        }
        return -1;
    }

    public short getMount() {
        if (this.isVersionAbove(220)) {
            for (Item item : inventory.itemsBody) {
                if (item.isNotNullItem()) {
                    if (item.template.type == 24) {
                        if (item.template.gender == 3 || item.template.gender == this.gender) {
                            return item.template.id;
                        } else {
                            return -1;
                        }
                    }
                    if (item.template.type == 23) {
                        if (item.template.id < 500) {
                            return item.template.id;
                        } else {
                            Object mount = DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
                            if (mount == null) {
                                return -1;
                            }
                            return (short) mount;
                        }
                    }
                }
            }
        } else {
            for (Item item : inventory.itemsBag) {
                if (item.isNotNullItem()) {
                    if (item.template.type == 24) {
                        if (item.template.gender == 3 || item.template.gender == this.gender) {
                            return item.template.id;
                        } else {
                            return -1;
                        }
                    }
                    if (item.template.type == 23) {
                        if (item.template.id < 500) {
                            return item.template.id;
                        } else {
                            Object mount = DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
                            if (mount == null) {
                                return -1;
                            }
                            return (short) mount;
                        }
                    }
                }
            }
        }
        return -1;
    }

    //--------------------------------------------------------------------------
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        int mstChuong = this.nPoint.mstChuong;
        int giamst = this.nPoint.tlGiamst;
        if (!this.isDie()) {
            if (this.isBatTu) {
                return 0;
            }
            if (this.isMiniPet) {
                return 0;
            }
            if (plAtt != null) {
                if (this.pet != null && this.pet.status < 3) {
                    this.pet.angry(plAtt);
                }
                if (!this.isBoss && plAtt.nPoint.xDameChuong && SkillUtil.isUseSkillChuong(plAtt)) {
                    damage = plAtt.nPoint.tlDameChuong * damage;
                    plAtt.nPoint.xDameChuong = false;
                }
                if (mstChuong > 0 && SkillUtil.isUseSkillChuong(plAtt)) {
                    PlayerService.gI().hoiPhuc(this, 0, damage * mstChuong / 100);
                    damage = 0;
                }
            }
            if (!SkillUtil.isUseSkillBoom(plAtt)) {
                if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 100)) {
                    return 0;
                }
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (isMobAttack && this.charms.tdBatTu > System.currentTimeMillis() && damage >= this.nPoint.hp) {
                damage = this.nPoint.hp - 1;
            }
            if (giamst > 0) {
                damage -= nPoint.calPercent(damage, giamst);
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                if (plAtt != null) {
                    if (MapService.gI().isMapMabuWar(plAtt.zone.map.mapId)) {
                        plAtt.addPowerPoint(5);
                        Service.getInstance().sendPowerInfo(plAtt, "TL", plAtt.getPowerPoint());
                    }
                }
                setDie(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }

    public void setDie(Player plAtt) {
        //xóa phù
        if (this.effectSkin.xHPKI > 1) {
            this.effectSkin.xHPKI = 1;
            Service.getInstance().point(this);
        }
        //xóa tụ skill đặc biệt
        this.playerSkill.prepareQCKK = false;
        this.playerSkill.prepareLaze = false;
        this.playerSkill.prepareTuSat = false;
        //xóa hiệu ứng skill
        this.effectSkill.removeSkillEffectWhenDie();

//        EffectSkillService.gI().removeMaPhongBa(plAtt);
        //
        nPoint.setHp(0);
        nPoint.setMp(0);
        //xóa trứng
        if (this.mobMe != null) {
            this.mobMe.mobMeDie();
        }
        Service.getInstance().charDie(this);
        //add kẻ thù
        if (!this.isPet && !this.isBoss && plAtt != null && !plAtt.isPet && !plAtt.isBoss) {
            if (!plAtt.itemTime.isUseAnDanh) {
                FriendAndEnemyService.gI().addEnemy(this, plAtt);
                // doan nay` de code nhiem vu thach dau 
                if (TaskService.gI().getIdTask(plAtt) == ConstTask.TASK_16_0) {
                    TaskService.gI().checkDoneTaskKillPlayer(plAtt);
                }
                plAtt.playerTask.achivements.get(ConstAchive.TRAM_TRAN_TRAM_THANG).count++;
            }
        }
        if (this.effectSkin.isSocola) {
            reward(plAtt);
        }
        if (MapService.gI().isMapMabuWar(this.zone.map.mapId)) {
            if (this.powerPoint < 20) {
                this.powerPoint = 0;
            }
            if (this.percentPowerPont < 100) {
                this.percentPowerPont = 0;
            }
        }
        //kết thúc pk
        PVPServcice.gI().finishPVP(this, PVP.TYPE_DIE);
        BlackBallWar.gI().dropBlackBall(this);
        if (isHoldNamecBall) {
            NamekBallWar.gI().dropBall(this);
        }
        if (this.clone != null) {
            this.clone.setDie(plAtt);
        }
      //  EffectSkillService.gI().removeMaPhongBa(plAtt);
    }

    public void reward(Player pl) {
        if (pl != null) {
            int x = this.location.x;
            int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
            ItemMap itemMap = new ItemMap(this.zone, 516, 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
            if (itemMap != null) {
                Service.getInstance().dropItemMap(zone, itemMap);
            }
        }
    }

    //--------------------------------------------------------------------------
    public void setClanMember() {
        if (this.clanMember != null) {
            this.clanMember.powerPoint = this.nPoint.power;
            this.clanMember.head = this.getHead();
            this.clanMember.body = this.getBody();
            this.clanMember.leg = this.getLeg();
        }
    }

    public boolean isAdmin() {
        return this.session.isAdmin;
    }

    public void setJustRevivaled() {
        this.justRevived = true;
        this.lastTimeRevived = System.currentTimeMillis();
        this.immortal = true;
    }

    public void dispose() {
        if (escortedBoss != null) {
            escortedBoss.stopEscorting();
        }
        if (skillSpecial != null) {
            skillSpecial.dispose();
            skillSpecial = null;
        }
        isDisposed = true;
        if (pet != null) {
            pet.dispose();
            pet = null;
        }
        playerTrade = null;
        if (mapBlackBall != null) {
            mapBlackBall.clear();
            mapBlackBall = null;
        }
        zone = null;
        mapBeforeCapsule = null;
        if (mapCapsule != null) {
            mapCapsule.clear();
            mapCapsule = null;
        }
        if (mobMe != null) {
            mobMe.dispose();
            mobMe = null;
        }
        location = null;
        if (setClothes != null) {
            setClothes.dispose();
            setClothes = null;
        }
        if (effectSkill != null) {
            effectSkill.dispose();
            effectSkill = null;
        }
        if (mabuEgg != null) {
            mabuEgg.dispose();
            mabuEgg = null;
        }
        if (playerTask != null) {
            playerTask.dispose();
            playerTask = null;
        }
        if (itemTime != null) {
            itemTime.dispose();
            itemTime = null;
        }
        if (fusion != null) {
            fusion.dispose();
            fusion = null;
        }
        if (magicTree != null) {
            magicTree.dispose();
            magicTree = null;
        }
        if (clone != null) {
            clone.dispose();
            clone = null;
        }
        if (playerIntrinsic != null) {
              playerIntrinsic.dispose();
            playerIntrinsic = null;
        }
        if (inventory != null) {
            inventory.dispose();
            inventory = null;
        }
        if (playerSkill != null) {
            playerSkill.dispose();
            playerSkill = null;
        }
        if (combineNew != null) {
            combineNew.dispose();
            combineNew = null;
        }
        iDMark = null;
        if (charms != null) {
            charms.dispose();
            charms = null;
        }
        if (effectSkin != null) {
            effectSkin.dispose();
            effectSkin = null;
        }
        if (gift != null) {
            gift.dispose();
            gift = null;
        }
        if (nPoint != null) {
            nPoint.dispose();
            nPoint = null;
        }
        if (rewardBlackBall != null) {
            rewardBlackBall.dispose();

            rewardBlackBall = null;
        }
        if (effectFlagBag != null) {
            effectFlagBag.dispose();
            effectFlagBag = null;
        }
        effectFlagBag = null;
        clan = null;
        clanMember = null;
        friends = null;
        enemies = null;
        session = null;
        name = null;
    }

    public String percentGold(int type) {
        try {
            if (type == 0) {
                double denominator = ChonAiDay_Gold.gI().goldNormar;
                if (denominator != 0) {
                    double percent = ((double) this.goldNormar / denominator) * 100;
                    return String.valueOf(Math.ceil(percent));
                } else {
                    return "0";
                }
            } else if (type == 1) {
                double denominator = ChonAiDay_Gold.gI().goldVip;
                if (denominator != 0) {
                    double percent = ((double) this.goldVIP / denominator) * 100;
                    return String.valueOf(Math.ceil(percent));
                } else {
                    return "0";
                }
            }
        } catch (ArithmeticException e) {
            return "0";
        }
        return "0";
    }

    public String percentRuby(int type) {
        try {
            if (type == 0) {
                double denominator2 = ChonAiDay_Ruby.gI().rubyNormar;
                if (denominator2 != 0) {
                    double percent = ((double) this.rubyNormar / denominator2) * 100;
                    return String.valueOf(Math.ceil(percent));
                } else {
                    return "0";
                }
            } else if (type == 1) {
                double denominator2 = ChonAiDay_Ruby.gI().rubyVip;
                if (denominator2 != 0) {
                    double percent = ((double) this.rubyVIP / denominator2) * 100;
                    return String.valueOf(Math.ceil(percent));
                } else {
                    return "0";
                }
            }
        } catch (ArithmeticException e) {
            return "0";
        }
        return "0";
    }

    public String percentGem(int type) {
        try {
            if (type == 0) {
                double denominator3 = ChonAiDay_Gem.gI().gemNormar;
                if (denominator3 != 0) {
                    double percent = ((double) this.gemNormar / denominator3) * 100;
                    return String.valueOf(Math.ceil(percent));
                } else {
                    return "0";
                }
            } else if (type == 1) {
                double denominator3 = ChonAiDay_Gem.gI().gemVip;
                if (denominator3 != 0) {
                    double percent = ((double) this.gemVIP / denominator3) * 100;
                    return String.valueOf(Math.ceil(percent));
                } else {
                    return "0";
                }
            } else {
                return "0";
            }
        } catch (ArithmeticException | NullPointerException e) {
            // Xử lý nếu có lỗi
            return "0";
        }
    }

    public boolean isPl() {
        return !isPet && !isBoss && !isMiniPet && !isClone;
    }

    @Override
    public String toString() {
        return "Player{" + "server=" + server + ", buyLimit=" + buyLimit + ", event=" + event + ", textRuongGo=" + textRuongGo + ", receivedWoodChest=" + receivedWoodChest + ", goldChallenge=" + goldChallenge + ", gemChallenge=" + gemChallenge + ", levelWoodChest=" + levelWoodChest + ", isInvisible=" + isInvisible + ", sendMenuGotoNextFloorMabuWar=" + sendMenuGotoNextFloorMabuWar + ", lastTimeBabiday=" + lastTimeBabiday + ", lastTimeChangeZone=" + lastTimeChangeZone + ", lastTimeChatGlobal=" + lastTimeChatGlobal + ", lastTimeChatPrivate=" + lastTimeChatPrivate + ", lastTimeChangeMap=" + lastTimeChangeMap + ", firstTimeLogin=" + firstTimeLogin + ", session=" + session + ", countSaveFail=" + countSaveFail + ", beforeDispose=" + beforeDispose + ", timeFixInventory=" + timeFixInventory + ", isPet=" + isPet + ", isBoss=" + isBoss + ", isMiniPet=" + isMiniPet + ", isChangeMap=" + isChangeMap + ", nameClan=" + nameClan + ", levelBDKBDone=" + levelBDKBDone + ", timeBDKBDone=" + timeBDKBDone + ", taixiu=" + taixiu + ", isShopKiGuiSuKien=" + isShopKiGuiSuKien + ", isHaveYajiro=" + isHaveYajiro + ", lastTimeUpdateTopBDKB=" + lastTimeUpdateTopBDKB + ", lockPK=" + lockPK + ", timerDHVT=" + timerDHVT + ", _friendGiaoDich=" + _friendGiaoDich + ", playerTradeId=" + playerTradeId + ", playerTrade=" + playerTrade + ", isBatTu=" + isBatTu + ", thachDauNPC=" + thachDauNPC + ", doneThachDauThanMeo=" + doneThachDauThanMeo + ", doneThachDauYanjiro=" + doneThachDauYanjiro + ", doneThachDauPoPo=" + doneThachDauPoPo + ", doneThachDauThuongDe=" + doneThachDauThuongDe + ", doneThachDauBubbles=" + doneThachDauBubbles + ", doneThachDauThanVuTru=" + doneThachDauThanVuTru + ", DoneVoDaiBaHatMit=" + DoneVoDaiBaHatMit + ", dameMaFuBa=" + dameMaFuBa + ", DanhQuaiNhanNgoc=" + DanhQuaiNhanNgoc + ", idPlayerForNPC=" + idPlayerForNPC + ", haveTau77=" + haveTau77 + ", hide_Yanjiro=" + hide_Yanjiro + ", MaBaoVe_TamThoi=" + MaBaoVe_TamThoi + ", haveMaPhongBa=" + haveMaPhongBa + ", activeYajiro=" + activeYajiro + ", mapIdBeforeLogout=" + mapIdBeforeLogout + ", mapBlackBall=" + mapBlackBall + ", zone=" + zone + ", mapBeforeCapsule=" + mapBeforeCapsule + ", mapCapsule=" + mapCapsule + ", pet=" + pet + ", boss=" + boss + ", minipet=" + minipet + ", goldNormar=" + goldNormar + ", goldVIP=" + goldVIP + ", id_CSMM_Gold=" + id_CSMM_Gold + ", soDuVND=" + soDuVND + ", soThoiVang=" + soThoiVang + ", thanhVien=" + thanhVien + ", rubyNormar=" + rubyNormar + ", rubyVIP=" + rubyVIP + ", id_CSMM_Ruby=" + id_CSMM_Ruby + ", gemNormar=" + gemNormar + ", gemVIP=" + gemVIP + ", id_CSMM_Gem=" + id_CSMM_Gem + ", mobMe=" + mobMe + ", location=" + location + ", setClothes=" + setClothes + ", effectSkill=" + effectSkill + ", mabuEgg=" + mabuEgg + ", playerTask=" + playerTask + ", itemTime=" + itemTime + ", fusion=" + fusion + ", magicTree=" + magicTree + ", playerIntrinsic=" + playerIntrinsic + ", inventory=" + inventory + ", playerSkill=" + playerSkill + ", combineNew=" + combineNew + ", iDMark=" + iDMark + ", charms=" + charms + ", effectSkin=" + effectSkin + ", gift=" + gift + ", nPoint=" + nPoint + ", rewardBlackBall=" + rewardBlackBall + ", effectFlagBag=" + effectFlagBag + ", rankSieuHang=" + rankSieuHang + ", clan=" + clan + ", clanMember=" + clanMember + ", friends=" + friends + ", enemies=" + enemies + ", actived=" + actived + ", loaded=" + loaded + ", id=" + id + ", name=" + name + ", gender=" + gender + ", isNewMember=" + isNewMember + ", head=" + head + ", lastimelogin=" + lastimelogin + ", tongnap=" + tongnap + ", lastimelogin2=" + lastimelogin2 + ", typePk=" + typePk + ", isUseMaBaoVe=" + isUseMaBaoVe + ", MaBaoVe=" + MaBaoVe + ", lastTimeNotifyTimeHoldBlackBall=" + lastTimeNotifyTimeHoldBlackBall + ", lastTimeHoldBlackBall=" + lastTimeHoldBlackBall + ", tempIdBlackBallHold=" + tempIdBlackBallHold + ", tempIdNamecBallHold=" + tempIdNamecBallHold + ", isHoldBlackBall=" + isHoldBlackBall + ", isHoldNamecBall=" + isHoldNamecBall + ", cFlag=" + cFlag + ", lastTimeChangeFlag=" + lastTimeChangeFlag + ", lastTimeTrade=" + lastTimeTrade + ", haveTennisSpaceShip=" + haveTennisSpaceShip + ", useSpaceShip=" + useSpaceShip + ", isGoHome=" + isGoHome + ", justRevived=" + justRevived + ", lastTimeRevived=" + lastTimeRevived + ", immortal=" + immortal + ", lastTimeBan=" + lastTimeBan + ", lastTimeUpdate=" + lastTimeUpdate + ", isBan=" + isBan + ", isGotoFuture=" + isGotoFuture + ", lastTimeGoToFuture=" + lastTimeGoToFuture + ", isgotoPrimaryForest=" + isgotoPrimaryForest + ", lastTimePrimaryForest=" + lastTimePrimaryForest + ", maxTime=" + maxTime + ", type=" + type + ", isGoToBDKB=" + isGoToBDKB + ", lastTimeGoToBDKB=" + lastTimeGoToBDKB + ", lastTimeAnXienTrapBDKB=" + lastTimeAnXienTrapBDKB + ", powerPoint=" + powerPoint + ", percentPowerPont=" + percentPowerPont + ", skillSpecial=" + skillSpecial + ", DH1=" + DH1 + ", isTitleUse=" + isTitleUse + ", lastTimeTitle1=" + lastTimeTitle1 + ", IdDanhHieu_1=" + IdDanhHieu_1 + ", ChiSoHP_1=" + ChiSoHP_1 + ", ChiSoKI_1=" + ChiSoKI_1 + ", ChiSoSD_1=" + ChiSoSD_1 + ", DH2=" + DH2 + ", isTitleUse2=" + isTitleUse2 + ", lastTimeTitle2=" + lastTimeTitle2 + ", IdDanhHieu_2=" + IdDanhHieu_2 + ", ChiSoHP_2=" + ChiSoHP_2 + ", ChiSoKI_2=" + ChiSoKI_2 + ", ChiSoSD_2=" + ChiSoSD_2 + ", DH3=" + DH3 + ", isTitleUse3=" + isTitleUse3 + ", lastTimeTitle3=" + lastTimeTitle3 + ", IdDanhHieu_3=" + IdDanhHieu_3 + ", ChiSoHP_3=" + ChiSoHP_3 + ", ChiSoKI_3=" + ChiSoKI_3 + ", ChiSoSD_3=" + ChiSoSD_3 + ", DH4=" + DH4 + ", isTitleUse4=" + isTitleUse4 + ", lastTimeTitle4=" + lastTimeTitle4 + ", IdDanhHieu_4=" + IdDanhHieu_4 + ", ChiSoHP_4=" + ChiSoHP_4 + ", ChiSoKI_4=" + ChiSoKI_4 + ", ChiSoSD_4=" + ChiSoSD_4 + ", DH5=" + DH5 + ", isTitleUse5=" + isTitleUse5 + ", lastTimeTitle5=" + lastTimeTitle5 + ", IdDanhHieu_5=" + IdDanhHieu_5 + ", ChiSoHP_5=" + ChiSoHP_5 + ", ChiSoKI_5=" + ChiSoKI_5 + ", ChiSoSD_5=" + ChiSoSD_5 + ", lastTimeSwapWhis=" + lastTimeSwapWhis + ", lastTimeKillWhis=" + lastTimeKillWhis + ", levelKillWhis=" + levelKillWhis + ", levelKillWhisDone=" + levelKillWhisDone + ", timeKillWhis=" + timeKillWhis + ", levelWhis=" + levelWhis + ", lastTimePickItem=" + lastTimePickItem + ", collectionBook=" + collectionBook + ", isSaving=" + isSaving + ", isDisposed=" + isDisposed + ", interactWithKarin=" + interactWithKarin + ", escortedBoss=" + escortedBoss + ", confirmDialog=" + confirmDialog + ", rewardLimit=" + rewardLimit + ", petFollow=" + petFollow + ", buff=" + buff + ", goldXiu=" + goldXiu + ", goldTai=" + goldTai + ", strRequire=" + strRequire + ", lastimelogin3=" + lastimelogin3 + ", lastTimeSendTextTime=" + lastTimeSendTextTime + '}';
    }
    
    
}
