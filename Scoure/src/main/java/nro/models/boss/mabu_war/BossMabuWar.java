package nro.models.boss.mabu_war;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.player.Player;
import nro.services.SkillService;

/**
 * @author outcast c-cute hột me 😳
 */
public class BossMabuWar  extends Boss {

    protected int mapID;

    protected int zoneId;

    public BossMabuWar(int id, BossData data) {
        super(id, data);
    }

    @Override
    protected boolean useSpecialSkill() {
        this.playerSkill.skillSelect = this.getSkillSpecial();
        if (SkillService.gI().canUseSkillWithCooldown(this)) {
            SkillService.gI().useSkill(this, null, null, null);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void rewards(Player pl) {

    }

    @Override
    public void initTalk() {

    }

    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }
}
