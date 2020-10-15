package cn.turing.common.base;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 设备过期时间
 */
public class SensorExpirationTime {
    //保留两位小数
    public static DecimalFormat df = new DecimalFormat("#.00");

    //鑫豪斯电气火灾过期时间66分钟
    public static int Xsh_DQHZ=3960;

    public static Double decimaFormat(Double f){
		BigDecimal bg = new BigDecimal(f);
		double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}
}
