package cn.turing.firecontrol.server.util;

import java.math.BigDecimal;

public class NumberUtil {
    public static double div(int d1,int d2,int scale){
        //  当然在此之前，你要判断分母是否为0，
        //  为0你可以根据实际需求做相应的处理

        BigDecimal bd1 = new BigDecimal(Integer.toString(d1));
        BigDecimal bd2 = new BigDecimal(Integer.toString(d2));
        return bd1.divide
                (bd2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static void main(String[] args) {
        System.out.println(NumberUtil.div(230,10,1));
    }
}
