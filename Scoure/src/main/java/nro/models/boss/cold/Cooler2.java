package nro.models.boss.cold;

import nro.consts.ConstItem;
import nro.models.boss.*;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.TaskService;
import nro.utils.Util;

/**
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 */
public class Cooler2 extends FutureBoss {

    public Cooler2() {
        super(BossFactory.COOLER2, BossData.COOLER2);
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
        if (Util.isTrue(70, 100)) {
            int[] set1 = {562, 564, 566, 561};
            itemMap = new ItemMap(this.zone, set1[Util.nextInt(0, set1.length - 1)], 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
        } else if (Util.isTrue(10, 20)) {
            int[] set2 = {555, 556, 563, 557, 558, 565, 559, 567, 560};
            itemMap = new ItemMap(this.zone, set2[Util.nextInt(0, set2.length - 1)], 1, x, y, pl.id);
            RewardService.gI().initBaseOptionClothes(itemMap.itemTemplate.id, itemMap.itemTemplate.type, itemMap.options);
        } else if (Util.isTrue(1, 5)) {
            itemMap = new ItemMap(this.zone, 15, 1, x, y, pl.id);
        } else if (Util.isTrue(1, 2)) {
            itemMap = new ItemMap(this.zone, 16, 1, x, y, pl.id);
        
        } else if (Util.isTrue(2, 2)) {
            itemMap = new ItemMap(this.zone, 2082, 2, x, y, pl.id);
        
        
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
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {

        textTalkAfter = new String[]{"Ta đã giấu hết ngọc rồng rồi, các ngươi tìm vô ích hahaha"};
    }

    @Override
    public void leaveMap() {
        BossManager.gI().getBossById(BossFactory.COOLER).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}
