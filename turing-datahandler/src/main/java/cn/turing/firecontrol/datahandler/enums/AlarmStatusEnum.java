package cn.turing.firecontrol.datahandler.enums;

public enum AlarmStatusEnum {

    //是否真实火警[2=火警测试/1=是/0=否]'
    NOT_USE(0, "误报"),
    FIRE_WARNING(1, "火警"),
    TEST(2, "测试");



    private int type;
    private String message;


    private AlarmStatusEnum(int type, String message){
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
        for(AlarmStatusEnum en : values()){
            if (en.getType() == type){
                return en.getMessage();
            }
        }
        return null;
    }
}
