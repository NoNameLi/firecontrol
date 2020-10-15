package cn.turing.common.base;

public enum DeviceChannelEnum {
    ALL("00","设备整体状态"),
    RES_CURRENT1("01","第1路剩余电流报警状态"),
    RES_CURRENT2("02","第2路剩余电流报警状态"),
    RES_CURRENT3("03","第3路剩余电流报警状态"),
    RES_CURRENT4("04","第4路剩余电流报警状态"),
    TEMP1("05","第1路温度报警状态"),
    TEMP2("06","第2路温度报警状态"),
    TEMP3("07","第3路温度报警状态"),
    TEMP4("08","第4路温度报警状态"),
    VOLTAGE_A("09","电压 Ua 报警状态"),
    VOLTAGE_B("0A","电压 Ub 报警状态"),
    VOLTAGE_C("0B","电压 Uc 报警状态"),
    ELE_CURRENT_A("0C","电流 Ia 报警状态"),
    ELE_CURRENT_B("0D","电流 Ib 报警状态"),
    ELE_CURRENT_C("0E","电流 Ic 报警状态"),
    RES_CURRENT1_VALUE("0F","第1路剩余电流"),
    RES_CURRENT2_VALUE("10","第2路剩余电流"),
    RES_CURRENT3_VALUE("11","第3路剩余电流"),
    RES_CURRENT4_VALUE("12","第4路剩余电流"),
    TEMP1_VALUE("13","第1路温度"),
    TEMP2_VALUE("14","第2路温度"),
    TEMP3_VALUE("15","第3路温度"),
    TEMP4_VALUE("16","第4路温度"),
    VOLTAGE_A_VALUE("17","电压 Ua "),
    VOLTAGE_B_VALUE("18","电压 Ub "),
    VOLTAGE_C_VALUE("19","电压 Uc "),
    ELE_CURRENT_A_VALUE("1A","电流 Ia "),
    ELE_CURRENT_B_VALUE("1B","电流 Ib "),
    ELE_CURRENT_C_VALUE("1C","电流 Ic "),

    PA("1D","有功功率 Pa(KW)"),
    PB("1E","有功功率 Pb(KW)"),
    PC("1F","有功功率 Pc(KW)"),
    COSQA("20","功率因数 cosqa "),
    COSQB("21","功率因数 cosqb "),
    COSQC("22","功率因数 cosqc"),
    HZ_A("23","A相频率 "),
    HZ_B("24","B相频率 "),
    HZ_C("25","C相频率 "),
    EPA("26","电量 Epa(KWH) "),
    EPB("27","电量 Epb(KWH)"),
    EPC("28","电量 Epc(KWH)"),
    RESET_FINSH("40","设备复位完成状态"),
    RESET("80","服务器主动复位设备"),
    HEART("FE","心跳"),
    LOGIN("FF","登录");
    private String type;
    private String message;

    DeviceChannelEnum(String type, String message) {
        this.type=type;
        this.message=message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessageByType(String type){
        for(DeviceChannelEnum en : values()){
            if (en.getType().equals(type)){
                return en.getMessage();
            }
        }
        return null;
    }
}
