package cn.turing.common.entity.sanjiangsea;


import lombok.Data;

/**
 * 泛海三江-消防主机
 */
@Data
public class SanJiangSeaDevice {
    /**
     * 端口
     */
    private String port;
    /**
     * ip
     */
    private String ip;
    /**
     * 回路
     */
    private String loopNo;
    /**
     * 地址
     */
    private String addressNo;
    /**
     * 特征字符
     */
    private String data_type;
    /**
     * 告警类型：20：火警或反馈 ，40故障，24模块的启动
     *
     */
    private String alarm_type;
    /**
     * 主类型
     */
    private String main_type;
    /**
     * 从类型
     */
    private String from_type;
    /**
     * 区号
     */
    private String areaNo;
    /**
     * 栋号
     */
    private String buildingNo;
    /**
     * 层号
     */
    private String layerNo;
    /**
     * 房号
     */
    private String roomNo;

    public SanJiangSeaDevice(String data){
        data_type=data.substring(8,10);
        port=data.substring(10,12);
        alarm_type=data.substring(18,20);
        main_type=data.substring(20,22);
        from_type=data.substring(22,24);
        String j= String.valueOf(((Integer.parseInt(data.substring(26,28),16)/2)+1));
        String k= String.valueOf(((Integer.parseInt(data.substring(26,28),16)%2)+1));
        addressNo=String.valueOf(Integer.parseInt(data.substring(30,32)+data.substring(28,30),16));
        areaNo=String.valueOf(Integer.parseInt(data.substring(36,38),16)+1);
        buildingNo=String.valueOf(Integer.parseInt(data.substring(38,40),16)+1);
        layerNo=String.valueOf(Integer.parseInt(data.substring(40,42),16)+1);
        roomNo=String.valueOf(Integer.parseInt(data.substring(42,44),16)+1);
        loopNo=j+k+layerNo;
    }
}
