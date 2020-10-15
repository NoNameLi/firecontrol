package cn.turing.common.base;

public enum DeviceStatusEnum {
    ALL("00","设备整体状态"),
    RES_CURRENT1("00000000","正常"),
    RES_CURRENT2("00000001","剩余电流过载（报警）"),
    RES_CURRENT3("00000002","剩余电流端口开路（故障）"),
    RES_CURRENT4("00000003","剩余电流端口短路（故障）"),
    TEMP1("00000004","过温（报警）"),
    TEMP2("00000005","温度端口开路（故障）"),
    TEMP3("00000006","温度端口短路（故障）"),
    TEMP4("00000007","缺相（报警）"),
    VOLTAGE_A("00000008","电压过压（报警）"),
    VOLTAGE_B("00000009","电压欠压（报警）"),
    VOLTAGE_C("0000000a","电流过载（报警）"),
    PA("0000000b","电流端口开路（故障）"),
    PB("0000000c","电压欠压（报警）"),
    PC("0000000d","电流端口短路（故障）");
    private String type;
    private String message;

    DeviceStatusEnum(String type, String message) {
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
        for(DeviceStatusEnum en : values()){
            if (en.getType().equals(type)){
                return en.getMessage();
            }
        }
        return null;
    }
}
