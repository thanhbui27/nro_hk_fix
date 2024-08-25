package nro.models.boss.cell;

import nro.consts.ConstItem;
import nro.consts.ConstRatio;
import nro.models.boss.*;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.TaskService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class XenBoHungHoanThien extends FutureBoss {

    public XenBoHungHoanThien() {
        super(BossFactory.XEN_BO_HUNG_HOAN_THIEN, BossData.XEN_BO_HUNG_HOAN_THIEN);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }


    @Override
    public void rewards(Player pl) {
        ItemMap itemMap = null;
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);
        if (Util.isTrue(50, 100)) {
            int[] set1 = {555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567};
            itemMap = new ItemMap(this.zone, set1[Util.nextInt(0, set1.length - 1)], 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
        } else if (Util.isTrue(50, 100)) {
            int[] set2 = {555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567};
            itemMap = new ItemMap(this.zone, set2[Util.nextInt(0, set2.length - 1)], 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
        } else if (Util.isTrue(10, 10)) {
            itemMap = new ItemMap(this.zone, 15, 1, x, y, pl.id);
        } else if (Util.isTrue(11, 11)) {
            itemMap = new ItemMap(this.zone, 16, 1, x, y, pl.id);
        }
        if (Manager.EVENT_SEVER == 4 && itemMap == null) {
            itemMap = new ItemMap(this.zone, ConstItem.LIST_ITEM_NLSK_TET_2023[Util.nextInt(0, ConstItem.LIST_ITEM_NLSK_TET_2023.length - 1)], 1, x, y, pl.id);
            itemMap.options.add(new ItemOption(74, 0));
        }
        if (itemMap != null) {
            Service.getInstance().dropItemMap(zone, itemMap);
        }
        TaskService.gI().checkDoneTaskKillBoss(pl, this);
        generalRewards(pl);
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl != null) {
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                    }
                    SkillService.gI().useSkill(this, pl, null, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
            Log.error(XenBoHungHoanThien.class, ex);
        }
    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{};
        this.textTalkMidle = new String[]{"Tất cả nhào vô", "Mình ta cũng đủ để hủy diệt các ngươi"};
        this.textTalkAfter = new String[]{};
    }

    @Override
    public void joinMap() {
        super.joinMap();
    }

    @Override
    public void leaveMap() {
        BossManager.gI().getBossById(BossFactory.XEN_BO_HUNG_1).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
