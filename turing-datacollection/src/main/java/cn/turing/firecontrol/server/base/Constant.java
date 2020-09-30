package cn.turing.firecontrol.server.base;

import java.util.Date;
import java.util.HashMap;

public class Constant {

    //
    public static int ST_WARN=0;
    public static int ST_ALARM=1;
    public static int ST_NORM=2;
    public static int ST_OFFLINE = 4;
    //温度
    public static String TEMP1 = "TEMP1";//温度值, 报警，故障 A相温度
    public static String TEMP2 = "TEMP2";//温度值   B相温度
    public static String TEMP3 = "TEMP3";//温度值   C相温度

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


    public static String YG="YG";//烟感
    public static String YL="YL";//压力

    //消防栓
    public  static String XELE="XELE";//电量
    public  static String XWLS="XWLS";//水位开关
    public  static String XWPS="XWPS";//水压开关
    public  static String XFC="XFC";//防拆
    public  static String XZJ="XZJ";//撞击
    public  static String XQX="XQX";//倾斜
    public  static String XTEMP="XTEMP";//温度
    public  static double  ElectricBoundary=10;//电量分界线 以下报警

    //消防主机（黄陵公积金）北大青鸟 BD
 //   public static  String BDYG="BDYG";//烟感
    public static String HANDN="HANDN";//手报Hand newspaper
    public static String ACOP="ACOP";//声光Acousto-optic
// 广州躬远科技产
    public static String  INFR ="INFR";// 红外 Infrared
    public static String  WF ="WF";// 水浸  writer Flooding
    public static String  DM ="DM"; //门磁  Door magnetic
    public static String  DL ="DL" ; //电量//无单位
    //防拆
    public static String  AJZT ="AJZT";   //按键状态
    public static String  HUM="HUM";  //湿度 单位 %
    public static String  VOL="VOL"; //电压 （电池）单位 v
    public static String  FFH="FFH"; //Fire-fighting helmet 按钮sos
    public static String  AIRP="AIRP"; //Air pressure  气压
    public static String  SOS="SOS";  //SOS
    public static String WC="WC";  //井盖
    public static String POWER="POWER";  //功率 Power
    public static String POWYZ="POWYZ";  //g功率因子
    public static String  VOLF="VOLF";  //电压频率 Voltage frequency
    public static String  POWDN="POWDN";//有功电能
    public static String YW="YW"; //液位
    public static String ECE ="ECE";//电流 单位 A
    public static String EAST="EAST"; //  东经
    public static String WEST="WEST"; //西经
    public static String SOUTH="SOUTH"; //南纬
    public static String NORTH="NORTH";// 北纬
    public static String WATT="WATT";// 电表度数。千瓦时kW·h
    public static String WBTT="WBTT";//水表吨数. 立方米(m³)

    //赛特威尔
    public static String COMG="COMG"; //可燃气体 Combustible gas 单位：%
//    public static String TEMP="℃";
//    public static String ELECTRICITY="mA";
//    public static String VOLTAGE="V";
//    public static String EQUIPMENT="equipment";
//    public static String ALARMDATE="alarmdate";
//    public static String TEMPERATURE="temperature";
//    public static String HUMIDITY="humidity";

    //中科turing 空气检测
    public static String PM1_0="PM1_0";//PM1.0
    public static String PM2_5="PM2_5";//PM2.5
    public static String PM10="PM10";//PM10
    public static String VOC="VOC";//异味
    //ES常量类配置
    public static class ESConstant{
        //接受到的数据，es索引，均是1条
        public static String ES_INDEX="primary_data";
        public static String ES_STRING="string";
        //解析后的数据，es索引，均是1条
        public static String ES_INDEX_SENSOR="sensor_data";
        public static String ES_SOURCE_TYPE="source_type";
    }

    //redis常量类配置
    public static class RedisConstant{
        public static String CHANNEL="__keyevent@*__:expired";//订阅频道
        //public static String MESSAGE="hello";//消息内容
        public static Integer TIMEOUT=120;
    }

    //中储粮智慧消防
    public static String PublicKey="";//获取的公钥
    public static String COMPANYID="";//单位id
    public static String TOKEN="";//获取到的token

    //测点
    public static String FPOOL="FPOOL";
    public static String FROOM="FROOM";
    public static String TEMP4="TEMP4";//配电箱0回路8号点位
    public static String TEMP5="TEMP5";//配电箱零线温度
    public static String TEMP6="TEMP6";//N线温度
    public static String TEMP7="TEMP7";//环境温度
    public static String RCT="RCT";//剩余电流

    public  static HashMap<String, Date> hashMap=new HashMap<>();


    //安瑞科
    public static String TEMPA1="TEMPA1";
    public static String TEMPA2="TEMPA2";
    public static String TEMPA3="TEMPA3";
    public static String TEMPA4="TEMPA4";
    public static String HZ="HZ"; //频率
    public static String VOLI="VOLI";//电压不平衡度
    public static String VOLM="VOLM";//相电压平均值
    public static String VOLZ="VOLZ";//零序电压
    public static String VOLAB="VOLAB";//AB 相线电压
    public static String VOLAC="VOLAC";//AB 相线电压
    public static String VOLBC="VOLBC";//AB 相线电压
    public static String VOLX="VOLX";//线电压平均值
    public static String DI1="DI1";//开关量输入状态表示 DI1~DI2 状态；
    public static String DI2="DI2";//开关量输入状态表示 DI1~DI2 状态；
    public static String DO1="DO1";//开关量输出状态表示 DO1~DO2 状态；
    public static String DO2="DO2";//开关量输出状态表示 DO1~DO2 状态；
    public static String ECCI="ECCI";//电流不平衡度
    public static String ECCZ="ECCZ";//零序电流
    public static String ECCM="ECCM";//相电压平均值
    public static String PFA="PFA";//A 相功率因数
    public static String PFB="PFB";
    public static String PFC="PFC";
    public static String TODAY="TODAY";

    //智慧工厂
    public static String SIGN="SIGN";//信号强度
}
