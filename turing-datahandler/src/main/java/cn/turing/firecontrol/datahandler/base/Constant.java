package cn.turing.firecontrol.datahandler.base;

public class Constant {//测点值 温度报警
    //报警的状态
    public static int CT_WARN = 0;
    public static int CT_ALARM = 1;
    public static int CT_NORM =2;
    //设备的故障
    public static int ST_WARN=0;
    public static int ST_ALARM=1;
    public static int ST_NORM=2;
    public static int ST_OFFLINE = 4;
    //温度
    public static String TEMP1 = "TEMP1";//温度值, 报警，故障
    public static String TEMP2 = "TEMP2";//温度值
    public static String TEMP3 = "TEMP3";//温度值

    //漏电
    public static String LK = "LK";//漏电值  报警，故障

    //电压
    public static String VLA = "VLA";//缺相 0正常，1异常 -- 通过三根项算出来的
    public static String VLB = "VLB";//缺相 0正常，1异常
    public static String VLC = "VLC";//缺相 0正常，1异常
    //电流
    public static String ECA ="ECA";//电流值 过流
    public static String ECB ="ECB";//过流 电流值
    public static String ECC ="ECC";//过流 电流值

    //缺省保留字，OFFLINE 设备下线
    public static String OFFLINE = "OFFLINE";




    //ES常量类配置
    public static class ESConstant{
        public static String ES_INDEX="primary_data";             //原始数据
        public static String ES_INDEX_SENSOR="sensor_data";       //传感器信息
        public static String ES_STRING="string";                  //数据类型
        public static String ES_INDEX_WARNING="warning_data";   //报警数据
        public static String ES_INDEX_MESSAGE="message_data";  //未推送的消息
        public static String ES_SOURCE_TYPE="source_type";


    }



    //redis常量类配置
    public static class RedisConstant{
        public static String CHANNEL="__keyevent@*__:expired";//订阅频道
        public static String MESSAGE="hello";//消息内容
        public static Integer TIMEOUT=120;
    }



    public static void main(String[] str){

        String text="02021a00002a0000000000000000000000000000011d000000000000";
       // System.out.println(text.substring(, ));

        //System.out.println(DraftBillStatus.getName("str22"));

    }

}
