package cn.turing.common.entity.jrde;


import cn.turing.common.util.ByteUtil;
import lombok.Data;

@Data
public class HeartAlarmHostGP50 extends AlarmHostGP50 {
    /**
     * csq值
     */
    private Integer csq;
    /**
     * 0x01 表示外出布防,0x02 表示留守布防,0x03 表示撤防
     * 布撤防
     */
    private String clothRemoval;
    /**
     * 故障状态
     */
    private String failureState;
    /**
     * 报警状态
     */
    private String alarmState;
    /**
     * 心跳周期
     */
    private Integer heartCycle;
    public HeartAlarmHostGP50(String data) {
        super(data);
        csq=Integer.parseInt(data.substring(18,20),16);
        clothRemoval=data.substring(20,22);
        failureState= ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(data.substring(22,24),16)))+
                ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(data.substring(24,26),16)));
        alarmState=data.substring(26,28);
        heartCycle=Integer.parseInt(data.substring(28,32),16);
    }
}
