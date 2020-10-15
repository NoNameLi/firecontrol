package cn.turing.common.entity.xinhaosi;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.EasyLinkInDeviceInfo;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;


@Data
public class XiaoFangShuanDeviceInfoV1 extends EasyLinkInDeviceInfo {
    //以下为设备相关信息
    private String data;
    private String head;
    private String length;
    private String cmd;
//    private String address;
    private String crc;
    private String partType;
    private String PartTypeStatus;//上传的类型

    public XiaoFangShuanDeviceInfoV1(JSONObject jsonObject) throws Exception{
        super(jsonObject);
        if(ByteUtil.checkXHSDeviceCRC2(this.getData())){
            this.sensorDetails= Lists.newArrayList();
            try{
                String header=data.substring(0,4);//帧头
                String length=""+Integer.parseInt(data.substring(4,6),16); //数据长度
                String cmd=data.substring(6,8);//命令
                String partType=data.substring(8,10);//部件类型
                String  temp=data.substring(10,24);//数据体
                System.out.println("temp :"+temp);
                String crc =data.substring(24); //校验
                String regTemp=   ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(temp.substring(0,2), 16)));//开关值
                    System.out.println("regTemp:"+regTemp);
                    sensorDetails.add(parseDianLiang(temp.substring(2,6)));//电量值
                    sensorDetails.add(parseShuiWei(regTemp));//水位开关
                    sensorDetails.add(parseShuiYa(regTemp,temp.substring(6,10),temp.substring(2,6)));//水压开关+水压值
                    sensorDetails.add(parseFangChai(regTemp));//防拆
                    sensorDetails.add(parseZhuangJi(regTemp));//撞击
                    sensorDetails.add(parseQingXie(regTemp));//倾斜
                    sensorDetails.add(parseWuDu(temp.substring(10),temp.substring(2,6)));//温度

                this.setHead(header);
                this.setLength(length);
                this.setCmd(cmd);
                this.setPartType(partType);
                this.setCrc(crc);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
        }

    }

    public SensorDetail parseDianLiang(String input){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XELE);//电量
        int num1=Integer.parseInt(input,16);
        System.out.println("电量:"+num1);
       int num= ByteUtil.dianLiangYunSuan(num1);
        sensorDetail.setAlarmValue(num);
        if (num<Constant.ElectricBoundary){//需要重新定义
            sensorDetail.setAlarmType("电量低");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            has_guzhang =true;
        }else {
            sensorDetail.setAlarmType("电量正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }
        return sensorDetail;
    }

    public SensorDetail parseShuiWei(String input){
            SensorDetail sensorDetail=new SensorDetail();
            sensorDetail.setAlarmCode(Constant.XWLS);//水位开关
         if (input.substring(1,2).equals("1")){//说明:未装漏水传感器的设备此位一直是1
             sensorDetail.setAlarmType("正常");
             sensorDetail.setAlarmStatus(Constant.ST_NORM);
             sensorDetail.setAlarmValue(Constant.ST_NORM);
         }else{
            sensorDetail.setAlarmType("漏水");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
             sensorDetail.setAlarmValue(Constant.ST_WARN);
             has_guzhang =true;
         }

        return sensorDetail;
    }

    public SensorDetail parseShuiYa(String input,String shuiya,String dianya){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XWPS);
        if (input.substring(2,3).equals("0")){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
        }else{
            sensorDetail.setAlarmType("欠压");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            has_guzhang =true;
        }
        int num=Integer.parseInt(shuiya,16);
        int num1=Integer.parseInt(dianya,16);
        System.out.println("水压："+num);
        System.out.println("电压："+num1);
        sensorDetail.setAlarmValue(ByteUtil.shuiYaYunSuan(num,num1));
        return  sensorDetail;
    }

    public SensorDetail parseFangChai(String input){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XFC);
        if (input.substring(3,4).equals("0")){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else{
            sensorDetail.setAlarmType("被拆");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang =true;
        }
        return sensorDetail;

    }
    public SensorDetail parseZhuangJi(String input){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XZJ);
        if (input.substring(4,5).equals("0")){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else{
            sensorDetail.setAlarmType("被撞");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang =true;
        }

        return sensorDetail;
    }
    public SensorDetail parseQingXie(String input){
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XQX);
        if (input.substring(5,6).equals("0")){
            sensorDetail.setAlarmType("正常");
            sensorDetail.setAlarmStatus(Constant.ST_NORM);
            sensorDetail.setAlarmValue(Constant.ST_NORM);
        }else{
            sensorDetail.setAlarmType("倾斜");
            sensorDetail.setAlarmStatus(Constant.ST_WARN);
            sensorDetail.setAlarmValue(Constant.ST_WARN);
            has_guzhang =true;
        }
        return sensorDetail;
    }
    public SensorDetail parseWuDu(String input,String input1){ //再定，温度有没有报警
        SensorDetail sensorDetail=new SensorDetail();
        sensorDetail.setAlarmCode(Constant.XTEMP);
        sensorDetail.setAlarmType("温度");
        sensorDetail.setAlarmStatus(Constant.ST_NORM);
        int num2=Integer.parseInt(input,16);
       System.out.println("温度:"+num2);
       int num3=Integer.parseInt(input1,16);
        System.out.println("电压:"+num3);
        sensorDetail.setAlarmValue(ByteUtil.wenduYunSuan(num2,num3));
        return  sensorDetail;
    }

    public JSONObject toDeviceMessage() {
        return null;
    }
}
