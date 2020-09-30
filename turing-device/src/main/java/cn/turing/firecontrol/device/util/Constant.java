package cn.turing.firecontrol.device.util;

public class Constant {
    public static String TEMP="℃";
    public static String ELECTRICITY="A";
    public static String VOLTAGE="V";
    public static String EQUIPMENT="equipment";
    public static String ALARMDATE="alarmdate";
    public static String TEMPERATURE="temperature";
    public static String HUMIDITY="humidity";




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

    public static class compiler
    {
        public static final String str1= "0x 02";
        public static final String str2= "0x 03";
        public static final String str3= "0x 04";
        public static final String str4= "0x 05";
        public static final String str5="0x 06";
        public static final String str6="0x 07";
        public static final String str7="0x 08";
        public static final String str8="0x 09";
        public static final String str9="0x 0A";
        public static final String str10="0x 0B";
        public static final String str11="0x 0C";
        public static final String str12="0x 0D";
        public static final String str13="0x 0E";
        public static final String str14="0x 0F";
        public static final String str15="0x 10";
        public static final String str16="0x 11";
        public static final String str17="0x 12";
        public static final String str18="0x 13";
        public static final String str19="0x 14";
        public static final String str20="0x 15";
        public static final String str21="0x 16";
        public static final String str22="0x 17";






        public static final String getName(String lCode)
        {
            String strReturn = "";
            if(lCode.equals(str1)){
                strReturn = "1-8路漏电报警状态值";
            }else if(lCode.equals(str2)){
                strReturn = "";
            }else if(lCode.equals(str3)){
                strReturn = "1路漏电数据";
            }else if(lCode.equals(str4)){
              //  0x 05
                strReturn = "A相电压数据";
            }else if(lCode.equals(str5)){

               // 0x 06
                strReturn = "B相电压数据";
            }else if(lCode.equals(str6)){

               // 0x 07
                strReturn = "C相电压数据";
            }else if(lCode.equals(str7)){

               // 0x 08
                strReturn = "A相电流数据";
            }else if(lCode.equals(str8)){
                strReturn ="B相电流数据";
               // 0x 09
            }else if(lCode.equals(str9)){
                strReturn ="C相电流数据";
               // 0x 0A
            }else if(lCode.equals(str10)){
                strReturn ="1路温度数据";
               // 0x 0B
            }else if(lCode.equals(str11)){
                strReturn ="2路温度数据";
              //  0x 0C
            }else if(lCode.equals(str12)){
                strReturn ="3路温度数据";
              //  0x 0D
            }else if(lCode.equals(str13)){
                strReturn ="4路温度数据";
              //  0x 0E
            }else if(lCode.equals(str14)){
                strReturn ="1路漏电动作值";
              //  0x 0F
            }else if(lCode.equals(str15)){
                strReturn ="过压阈值";
              //  0x 10
            }else if(lCode.equals(str16)){
                strReturn ="欠压阈值";
               // 0x 11
            }else if(lCode.equals(str17)){
                strReturn ="电流阈值";
               // 0x 12
            }else if(lCode.equals(str18)){
                strReturn ="欠流阈值";
               // 0x 13
            }else if(lCode.equals(str19)){
                strReturn ="1路温度动作值";
               // 0x 14
            }else if(lCode.equals(str20)){
                strReturn ="2路温度动作值";
               // 0x 15
            }else if(lCode.equals(str21)){
                strReturn ="3路温度动作值";
              //  0x 16
            }else if(lCode.equals(str22)){
                strReturn ="4路温度动作值";
               // 0x 17
            }

            return strReturn;
        }


    }




    public static void main(String[] str){

        String text="02021a00002a0000000000000000000000000000011d000000000000";
       // System.out.println(text.substring(, ));

        //System.out.println(DraftBillStatus.getName("str22"));

    }

}
