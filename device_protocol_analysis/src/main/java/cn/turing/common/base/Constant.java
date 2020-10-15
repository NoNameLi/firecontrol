package cn.turing.common.base;

import java.util.Date;
import java.util.HashMap;

/**
 * 测点信息
 */
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
    public static String PF="PF";//总相功率因数
    public static String PFA="PFA";//A 相功率因数
    public static String PFB="PFB";//b相功率因数
    public static String PFC="PFC";//c 相功率因数
    public static String TODAY="TODAY";

    //智慧工厂
    public static String SIGN="SIGN";//信号强度

    public static String SPR="SPR";//喷淋 ，单位兆帕（Mpa）

    public static String PM="PM";//电源检测（有电，无电）

    public static String SWITCH="SWITCH";//电源监测开关

    //特斯联-电气火灾
    public static String RES1="RES1";//一路剩余电流
    public static String RES2="RES2";//2路剩余电流
    public static String RES3="RES3";//3路剩余电流
    public static String RES4="RES4";//4路剩余电流
    public static String TEMPT1="TEMPT1";//1 路温度
    public static String TEMPT2="TEMPT2";//2 路温度
    public static String TEMPT3="TEMPT3";//3 路温度
    public static String TEMPT4="TEMPT4";//4 路温度
    public static String PA="PA";//有功功率A
    public static String PB="PB";//有功功率A
    public static String PC="PC";//有功功率A
    public static String HZA="HZA";//A相频率
    public static String HZB="HZB";//A相频率
    public static String HZC="HZC";//A相频率
    public static String EPA="EPA";//电量epa
    public static String EPB="EPB";//电量epb
    public static String EPC="EPC";//电量epc

    public static String DHS="DHS";//电池，或市电供电

    public static String YDC="YDC";//惠联无限。烟感门磁、可燃气体

    public static String PW="PW";//瞬时有功功率
    public static String PWA="PWA";//瞬时有功功率A
    public static String PWB="PWB";//瞬时有功功率B
    public static String PWC="PWC";//瞬时有功功率C

    public static String WPW="WPW";//瞬时无功功率
    public static String WPWA="WPWA";//瞬时无功功率A
    public static String WPWB="WPWB";//瞬时无功功率B
    public static String WPWC="WPWC";//瞬时无功功率C


    public static String EEG="EEG";//组合有功总电能
    public static String EEGJ="EEGJ";//组合有功总电能尖
    public static String EEGF="EEGF";//组合有功总电能峰
    public static String EEGP="EEGP";//组合有功总电能平
    public static String EEGG="EEGG";//组合有功总电能谷

    public static String ZPW="ZPW";//正向有功总电能
    public static String ZPWJ="ZPWJ";//正向有功总电能尖
    public static String ZPWF="ZPWF";//正向有功总电能峰
    public static String ZPWP="ZPWP";//正向有功总电能平
    public static String ZPWG="ZPWG";//正向有功总电能谷

    public static String FPW="FPW";//反向有功总电能
    public static String FPWJ="FPWJ";//反向有功总电能尖
    public static String FPWF="FPWF";//反向有功总电能峰
    public static String FPWP="FPWP";//反向有功总电能平
    public static String FPWG="FPWG";//反向有功总电能谷

    public static String CADY="CADY";//当前有功需量

    public static String CADW="CADW";//当前无功需量

	public static String ANGLE="ANGLE";//角度 比如90°

	public static String WCF="WCF";//水表累计流量 m3

	public static String WW="WW";//水表状态

	public static String QC="QC";//强磁
	//消防主机
	public static String FIREMAIN="FIREMAIN";
	//红外电表
	public static String REDMETER="REDMETER";
	//红外计数
	public static String REDSUM="REDSUM";

}
