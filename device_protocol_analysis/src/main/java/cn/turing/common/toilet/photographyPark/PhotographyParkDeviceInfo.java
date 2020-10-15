package cn.turing.common.toilet.photographyPark;

import lombok.Data;

/**
 * 摄影公园设备解析
 */
@Data
public class PhotographyParkDeviceInfo {
    private String QN;//请求编码
    private String ST;//系统编码
    private String CN;//命令编码
    private String PW;//访问密码
    private String MN;//设备唯一标识
    private String Flag;//标志
    private String DataTime;//数据采集时间
    private String a34002_Rtd;//可吸入颗粒物PM10  μg/m3
    private String a34004_Rtd;//可吸入颗粒物PM2.5  μg/m3
    private String a01001_Rtd;//温度 ◦C
    private String a01002_Rtd;//湿度 %
    private String a01008_Rtd;//风向  【角】度
    private String a01007_Rtd;//风速 m/s
    private String a01006_Rtd;//气压  kPa

    public PhotographyParkDeviceInfo(String data){

        int startData=data.indexOf("QN");
        int endData=data.lastIndexOf("&&");
        //截取有效数据
        String repData=data.substring(startData,endData);

        String [] dataList=repData.split(";");
        for (String parameters :dataList){
            //获取等号前的参数
            String parameter=parameters.substring(0,parameters.indexOf("="));
            String value= parameters.substring(parameters.indexOf("="));
            parameter(parameter,value);
        }
    }


    public void parameter(String parameter,String value){

        if (parameter.equals("QN")){
             QN=value.substring(1);
             return;
        }
        if (parameter.equals("ST")){
             ST=value.substring(1);
            return;
        }
        if (parameter.equals("CN")){
             CN=value.substring(1);
            return;
        }
        if (parameter.equals("PW")){
             PW=value.substring(1);
            return;
        }
        if (parameter.equals("MN")){
              MN=value.substring(1);
            return;
        }
        if (parameter.equals("Flag")){
              Flag=value.substring(1);
            return;
        }
        if (parameter.equals("CP")){
            String v=value.substring(value.lastIndexOf("=")+1);
            DataTime=v;
            return;
        }
        if (parameter.equals("a34002-Rtd")){
            a34002_Rtd=value.substring(1,value.indexOf(","));
            return;
        }
        if (parameter.equals("a34004-Rtd")){
             a34004_Rtd=value.substring(1,value.indexOf(","));
            return;
        }
        if (parameter.equals("a01001-Rtd")){
             a01001_Rtd=value.substring(1,value.indexOf(","));
            return;
        }
        if (parameter.equals("a01002-Rtd")){
             a01002_Rtd=value.substring(1,value.indexOf(","));
            return;
        }
        if (parameter.equals("a01008-Rtd")){
              a01008_Rtd=value.substring(1,value.indexOf(","));
            return;
        }
        if (parameter.equals("a01007-Rtd")){
              a01007_Rtd=value.substring(1,value.indexOf(","));
            return;
        }
        if (parameter.equals("a01006-Rtd")){
              a01006_Rtd=value.substring(1,value.indexOf(","));
            return;
        }
    }
}
