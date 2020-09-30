package cn.turing.firecontrol.device.util;

import java.util.Random;
import java.util.UUID;
/**
 * 
 * java通过UUID生成16位唯一号
 * 
 * 
 * */
public class UUIDUtils {

     public static String getNumberByUUId() {
            int first = new Random(10).nextInt(8) + 1;
            //System.out.println(first);
            int hashCodeV = UUID.randomUUID().toString().hashCode();
            if (hashCodeV < 0) {//有可能是负数
                hashCodeV = -hashCodeV;
            }
            // 0 代表前面补充0
            // 4 代表长度为4
            // d 代表参数为正数型
            return first + String.format("%015d", hashCodeV);
        }

     public static void main(String[] args) {
        String orderingID= getNumberByUUId();
        System.out.println(orderingID);
    }
}