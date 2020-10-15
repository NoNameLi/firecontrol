package cn.turing.common.entity.huiding.v1;


import cn.turing.common.entity.DeviceInfo;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class XBoosDeviceInfo extends DeviceInfo {
    private Integer signalStrength;//信号强度
    private String factoryId;//工厂id
    private Integer cycle;//周期
    private Double volX;//电池电量
    private BChannelSensor bChannelSensor;
    private CChannelSensor cChannelSensor;
    private DChannelSensor dChannelSensor;
    private EChannelSensor eChannelSensor;
    public XBoosDeviceInfo(String s) throws Exception{
        device_id=s.substring(12,20);
        signalStrength=Integer.parseInt(s.substring(20,22),16);
        factoryId=s.substring(22,30);
        cycle=Integer.parseInt(s.substring(30,38),16);
        volX=Integer.parseInt(s.substring(38,42),16)*0.01;//Float.intBitsToFloat(Integer.parseInt(s.substring(38,42)));
        BigDecimal b = new BigDecimal(volX);
        volX = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        //B通道
        String data=s.substring(42,s.length()-4);
        String channelType=data.substring(6,8);
        Integer lengthb=Integer.parseInt(data.substring(4,6),16);
        if (channelType.equals("02")){//02表示通道b
            overOpen02(data,lengthb,channelType);
        }else if (channelType.equals("03")){//02表示通道C
            overOpen03(data,lengthb,channelType);
        }else if (channelType.equals("04")){//04表示通道D
            overOpen04(data,lengthb);
        }else if (channelType.equals("05")){//05表示通道E
            eChannelSensor=new EChannelSensor(data);
        }else{
        }

    }
    public void overOpen02(String data, Integer lengthb,String channelType)throws  Exception{
        String bdata=data.substring(0,lengthb*2+4);
        bChannelSensor=new BChannelSensor(bdata);
        //加入B通道处理完
        try {
            data = data.substring(lengthb*2+4);
        }catch (StringIndexOutOfBoundsException e){
            return;
        }
        if (data.isEmpty()){
        }else{
            Integer lengthc=Integer.parseInt(data.substring(4,6),16);
            channelType=data.substring(6,8);
            if (channelType.equals("03")){
                overOpen03(data,lengthc,channelType);

            }else if (channelType.equals("04")){//04表示通道D
                overOpen04(data,lengthc);

            }else if (channelType.equals("05")){//05表示通道E
                String edata=data.substring(0,lengthb*2+4);
                eChannelSensor=new EChannelSensor(edata);
            }
        }

    }


    public void overOpen03(String data ,Integer lengthc,String channelType){
        String cdata=data.substring(0,lengthc*2+4);
        cChannelSensor=new CChannelSensor(cdata);
        try {
            data = data.substring(lengthc*2+4);
        }catch (StringIndexOutOfBoundsException e){
            return;
        }
        if (data.isEmpty()){
        }else{
            Integer lengthd=Integer.parseInt(data.substring(4,6),16);
            channelType=data.substring(6,8);
            if (channelType.equals("04")){
                overOpen04(data,lengthd);
            }else if (channelType.equals("05")){//05表示通道E
                String edata=data.substring(0,lengthd*2+4);
                eChannelSensor=new EChannelSensor(edata);
            }
        }

    }

    public void overOpen04(String data,Integer lengthd ){
        String ddata=data.substring(0,lengthd*2+4);
        dChannelSensor=new DChannelSensor(ddata);
        try {
            data = data.substring(lengthd*2+4);
        }catch (StringIndexOutOfBoundsException e){
            return;
        }
        if (data.isEmpty()){

        }else{
            eChannelSensor=new EChannelSensor(data);
        }

    }

    @Override
    public JSONObject toDeviceJSON() {
        return null;
    }

    public JSONObject toDeviceMessage() {
        return null;
    }
}
