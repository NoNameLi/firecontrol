package cn.turing.common.entity.zhgd;

import lombok.Data;

/**
 * 状态位
 */
@Data
public class BinaryStatusInfo {

    //预跳闸报警状态
    private String  yTrippingAlarm;
    //继电器命令状态
    private String relayAlarm;
    //当前运行时区
    private String nowUTC;
    //继电器状态
    private String relayStatus;
    //红外认证、编程允许状态
    private String redStatus;
    //供电方式
    private String powerSupply;
    //当前运行时段
    private String relayTime;
    //bit4和bit6为0通，bit4和bit6为1断
    private String eleStatus;

    public BinaryStatusInfo(String data){
        yTrippingAlarm=data.substring(0,1);
        relayAlarm=data.substring(1,2);
        nowUTC=data.substring(2,3);
        relayStatus=data.substring(3,4);
        redStatus=data.substring(4,5);
        powerSupply=data.substring(5,7);
        relayTime=data.substring(7);

        if (redStatus.equals("1") && relayAlarm.equals("1")){
            eleStatus="1";
        }

        if (redStatus.equals("0") | relayAlarm.equals("0")){
            eleStatus="1";
        }
    }

}
