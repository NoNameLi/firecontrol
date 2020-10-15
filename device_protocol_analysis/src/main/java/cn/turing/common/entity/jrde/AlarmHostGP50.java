package cn.turing.common.entity.jrde;

import cn.turing.common.util.ByteUtil;
import lombok.Data;

@Data
public abstract class AlarmHostGP50 {
    /**
     * 开始标识
     */
    private String start;
    /**
     * 序号
     */
    private int count;
    /**
     * 长度
     */
    private int length;
    /**
     * 校验
     */
    private String crc16;
    /**
     * 选项字
     */
    private String ack;
    /**
     * 命令
     */
    private String cmd;
    /**
     * 结束标识
     */
    private String end;

    public AlarmHostGP50(String data){
        start=data.substring(0,2);
        count=Integer.parseInt(data.substring(2,4),16);
        length=Integer.parseInt(data.substring(4,8),16);
        crc16=data.substring(8,12);
        ack= ByteUtil.buqi2(Integer.toBinaryString(Integer.parseInt(data.substring(12,14),16)));
        cmd=data.substring(14,18);
        end=data.substring(data.length()-4);
    }
}
