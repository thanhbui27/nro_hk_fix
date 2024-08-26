package nro.models.skill;

import nro.consts.Cmd;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 */
public class PlayerSkill {

    public Timer timer;
    private Player player;
    public List<Skill> skills;
    public Skill skillSelect;
    public long lastTimePrepareTuSat;

    public PlayerSkill(Player player) {
        this.player = player;
        skills = new ArrayList<>();
        timer = new Timer();
    }

    public Skill getSkillbyId(int id) {
        for (Skill skill : skills) {
            if (skill.template.id == id) {
                return skill;
            }
        }
        return null;
    }

    public byte[] skillShortCut = new byte[11];

    public void sendSkillShortCut() {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 61);
            msg.writer().writeUTF("KSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
            msg.cleanup();
            msg = Service.getInstance().messageSubCommand((byte) 61);
            msg.writer().writeUTF("OSkill");
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendSkillShortCutNew() {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand(Cmd.CHANGE_ONSKILL);
            msg.writer().writeInt(skillShortCut.length);
            msg.writer().write(skillShortCut);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public boolean prepareQCKK;
    public boolean prepareTuSat;
    public boolean prepareLaze;

    public byte getIndexSkillSelect() {
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
            case Skill.DEMON:
            case Skill.GALICK:
            case Skill.KAIOKEN:
            case Skill.LIEN_HOAN:
                return 1;
            case Skill.KAMEJOKO:
            case Skill.ANTOMIC:
            case Skill.MASENKO:
                return 2;
            case Skill.THAI_DUONG_HA_SAN:
            case Skill.TAI_TAO_NANG_LUONG:
            case Skill.TRI_THUONG:
                return 3;
            case Skill.DE_TRUNG:
            case Skill.BIEN_KHI:
                return 4;
            case Skill.TU_SAT:
            case Skill.QUA_CAU_KENH_KHI:
            case Skill.MAKANKOSAPPO:
                return 5;
            case Skill.KHIEN_NANG_LUONG:
                return 6;
            case Skill.THOI_MIEN:
            case Skill.TROI:
            case Skill.SOCOLA:
                return 7;
            case Skill.HUYT_SAO:
            case Skill.DICH_CHUYEN_TUC_THOI:
                return 8;
            case Skill.MAFUBA:
            case Skill.SUPER_KAME:
            case Skill.SUPER_ANTOMIC:
                return 9;
            case Skill.BIEN_HINH:
                return 10;  
            case Skill.PHAN_THAN:
                return 11;      
            default:
                return 11;
        }
    }

    public byte getSizeSkill() {
        byte size = 0;
        for (Skill skill : skills) {
            if (skill.skillId != -1) {
                size++;
            }
        }
        return size;
    }

    public void dispose() {
        this.player = null;
        this.skillSelect = null;
        this.skills = null;
    }
}
