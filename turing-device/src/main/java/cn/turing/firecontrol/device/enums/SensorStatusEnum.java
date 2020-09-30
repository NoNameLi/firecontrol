package cn.turing.firecontrol.device.enums;

public enum SensorStatusEnum {

    //[0=故障/1=报警/2=正常/3=未启用/4=离线]
    FAULT(0, "故障"),
    WARNING(1, "报警"),
    NORMAL(2, "正常"),
    NOT_USE(3, "未启用"),
    OFF_LINE(4, "离线");


    private int type;
    private String message;


    private SensorStatusEnum(int type, String message){
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static String getMessageByType(int type){
        for(SensorStatusEnum en : values()){
            if (en.getType() == type){
                return en.getMessage();
            }
        }
        return null;
    }
}
