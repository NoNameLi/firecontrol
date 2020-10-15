package cn.turing.common.entity.hikvision;

import cn.turing.common.entity.hikvision.UplinkMessage.InformationSoftwareVersion;
import cn.turing.common.entity.hikvision.UplinkMessage.StatusOfBuildingFireServiceSystem;
import cn.turing.common.entity.hikvision.UplinkMessage.UploadUserInformationTransmission;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * NP-FCT100型用户信息传输装置
 */
@Data
public class NPFCT100DeviceInfo {
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private String serialNumber;//业务流水号
    private String version;//协议版本号
    private Date upload_time; //设备信息数据的时间
    private Date recieve_time;//我方程序接受时间
    private String sourceAddress;//源地址
    private String destinationAddress;//目的地址
    private String length;//应用数据单元长度
    private Integer cmd;//命令字节 0预留、1控制命令、2发送数据、3确认、4请求、5应答、6否认
    private Integer typeLogo;//类型标志
    private Integer num;//信息数目
    private InformationSoftwareVersion softwareVersion;
    private UploadUserInformationTransmission uploads;
    public NPFCT100DeviceInfo(String s) {
      serialNumber=s.substring(4,8);
      version=s.substring(8,12);
//      String date=String.valueOf(Integer.parseInt(s.substring(22,24)))
//              +String.valueOf(Integer.parseInt(s.substring(20,22)))
//              +String.valueOf(Integer.parseInt(s.substring(18,20)))
//              +String.valueOf(Integer.parseInt(s.substring(16,18)))
//              +String.valueOf(Integer.parseInt(s.substring(14,16)))
//              +String.valueOf(Integer.parseInt(s.substring(12,14)));
//        try {
           // upload_time=simpleDateFormat.parse(date);
            upload_time=new Date();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        recieve_time=new Date();
        sourceAddress="192.168.0.254";//s.substring(24,36);
        destinationAddress="44444";//s.substring(36,48).substring(0,5);
        length=s.substring(48,52);
        cmd=Integer.parseInt(s.substring(52,54),16);
        typeLogo=Integer.parseInt(s.substring(54,56),16);
        num=Integer.parseInt(s.substring(56,58),16);
        if (typeLogo!=3 ||typeLogo!=6){
            String applicationData=s.substring(58,s.length()-4);
            if (typeLogo==1){
                StatusOfBuildingFireServiceSystem serviceSystem=new StatusOfBuildingFireServiceSystem(applicationData);
            }else if (typeLogo==2){
                softwareVersion=new InformationSoftwareVersion(applicationData);
            }else if (typeLogo==21){
            }else if (typeLogo==24){
                 uploads=new UploadUserInformationTransmission(applicationData);
            }else if (typeLogo==25){

            }

        }

    }
}
