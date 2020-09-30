package cn.turing.firecontrol.server.constant;


import cn.turing.common.base.Constant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 设备类型
 */
public class LoraDeviceTypeConst {


    /**
     * LoRa组合式电器火灾
     * 监测三相电电压、电流、温度、漏电；2.默认上报周期10min；
     */
    public final static  String LORA_CEF="TR-18EF3133L";
    /**
     * 1.上报周期6h；2.支持按键自检并上传数据；3.支持烟雾探测与温度检测；
     * 4.支持远程消音；5.支持远程修改探测浓度；6.双光源探测降低误报率；7.电量监测。
     */
    public final static String LORA_SMOKE="TR-19SD01L";
    /**
     * 鑫豪斯NB烟感
     */
    public final static String NB_SMOKE="JTY-GF-XF9132";
    /**
     * 1.测量三线温度；2.默认上报周期10min；3.支持远程修改温度报警门限值与上报周期；
     */
    public final static String LORA_CEF_TEMP="TR-19EF01L";
    /**
     * 1.上报周期6h；2.防拆报警；3.碰撞报警；4.倾斜报警；5.水压欠压报警；6.温度检测；7.电量监测；8.水压监测；9.所有报警解除后上报数据。
     */
    public final static String LORA_XHS="TR-18FC01L";
    /**
     * 电表
     */
    public final static String LORA_ELE="TR-19";//未确定
    /**
     * 水表
     */
    public final static String LORA_WATER="TR-19";//未确定
    /**
     *1.监测PM1.0,PM2.5,PM10，温度，湿度，异味；2.默认上报周期10min；3.参数突变立即上报数据
     */
    public final static String LORA_AIR="TR-19AD01L";
    /**
     * 1.上报周期4h；2.投入式安装，默认量程5米；3.采样周期2min；4.电量监测。
     */
    public final static String LORA_Liquid_Level="TR-19WD01L";
    /**
     * 1.上报周期4h；2.被动式红外探测；3.支持按键自检且上报数据。
     */
    public final static String LORA_INFRARED="TR-19IBD01L";
    /**
     * 1.默认上报周期4h，低压时上报周期1h；2.投入式安装，默认量程2MPa；3.采样周期2min；4.电量监测。
     */
    public final static String LORA_SPRAY="TR-19SP01L";
    /**
     * 1.上报周期2h；2.用于检测可燃气体；3.浓度超过门限值报警；4.浓度恢复到正常值后上传数据。
     */
    public final static String LORA_RANQI="TR-18GD01L";
    /**
     * 1.上报周8h；2.防拆报警；3.开合均发数据；4.电量监测
     */
    public final static String LORA_MENCI="TR-18DM01L";
    //燃气和门磁集合
    public final static List<String> LIST= Arrays.asList(LORA_RANQI,LORA_MENCI);
    //惠联无限的烟感，门磁燃气
    public final static List<String> POINTS=Arrays.asList(Constant.YG,Constant.DM,Constant.COMG);
    /**
     * 1.检测漏水溢水；2.上报周期6h；水浸
     */
    public final static String LORA_WATER_IMMERESION="TR-19WL01L";
    /**
     * 1.上报周期4h；2.井盖翻转，位移等监测；3.电量监测
     */
    public final static String LORA_MANHOLE_COVER="TR-18MC01L";
    /**
     * 1.上报周期4h；2.一键报警
     */
    public final static String LORA_SOS_BUTTON="TR-19EB01L";
    /**
     * NB可燃气体
     */
    public final static String NB_GAS="TR-20GD01N";

    public final static String LORA_GAS="TR-18GD01L";
    /**
     * nb空气
     */
    public final static String NB_AIR="TR-20AD01N";
    /**
     * NBsos 一键报警
     */
    public final static String NB_SOS="";
}
