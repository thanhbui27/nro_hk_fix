package nro.models.boss.bosstuonglai;

import nro.models.boss.NguHanhSon.*;
import nro.consts.ConstItem;
import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.PetService;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.SkillService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 * @author outcast c-cute hột me 😳
 */
public class Frieren extends Boss {

    public Frieren() {
        super(BossFactory.Frieren, BossData.Frieren);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }


    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        damage = 200000;
        return super.injured(plAtt, damage, piercing, isMobAttack);
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
            Log.error(NgoKhong.class, ex);
        }
    }

    @Override
    public void idle() {
    }

    @Override
    public void rewards(Player pl) {
        for (int i = 0; i < 1; i++) {
            this.dropItemReward(2080, (int) pl.id);
        }
        if (pl.pet == null) {
            PetService.gI().createNormalPet(pl);
        }
        generalRewards(pl);
    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{};
        this.textTalkMidle = new String[]{"Nực quá hahaha!!", "Mi khá đấy nhưng so với ta chỉ là hạng tôm tép",
                "Tất cả nhào vô hết đi", "Cứ chưởng tiếp đi. haha", "Các ngươi yếu thế này sao hạ được ta đây. haha",
                " Nóng Quá aaaaaaaaaaa!!", "Ta sẽ đốt cháy các ngươi"};
        this.textTalkAfter = new String[]{"Các ngươi được lắm", "Hãy đợi đấy thời gian tới ta sẽ quay lại.."};
    }
}
