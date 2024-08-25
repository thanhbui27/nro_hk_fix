package nro.consts;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class ConstPlayer {

    public static final int[] HEADMONKEY = {192, 195, 196, 199, 197, 200, 198};

    // AURA BIẾN HÌNH Ở ĐÂY
    public static final byte[][] AURABIENHINH = {
        {13, 13, 15, 14, 26}, // TD
        {28, 28, 28, 28, 28}, // NM
        {13, 13, 15, 14, 25} // XD
    };
    // SỬA NGOẠI HÌNH TỪ LV 1-5 Ở ĐÂY
    public static final short[][] HEADBIENHINH = {
        {1428, 1425, 1419, 1416, 1422}, // 5 head TD 
        {1431, 1434, 1435, 1436, 1437},// 5 haed NM
         {1442, 1445, 1446, 1447, 1448}, // 5 head XD
    };
    // THÂN NGOẠI HÌNH LV 1-5
    public static final short[] BODYBIENHINH = {1429, 1432, 1443}; // TD /NM/ XD
    public static final short[] LEGBIENHINH = {1430, 1433, 1444}; // TD /NM/ XD

    public static final byte TRAI_DAT = 0;
    public static final byte NAMEC = 1;
    public static final byte XAYDA = 2;

    //type pk
    public static final byte NON_PK = 0;
    public static final byte PK_PVP = 3;
    public static final byte PK_ALL = 5;

    //type fushion
    public static final byte NON_FUSION = 0;
    public static final byte LUONG_LONG_NHAT_THE = 4;
    public static final byte HOP_THE_PORATA = 6;
    public static final byte HOP_THE_PORATA2 = 8;
}
