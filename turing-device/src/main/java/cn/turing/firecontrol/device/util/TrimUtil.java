package cn.turing.firecontrol.device.util;



import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class TrimUtil {

    //将实体类的String字段trim
    public static void trimObject(Object obj) {
        if (obj == null) return;
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int j = 0; j < fields.length; j++) {
            try {
                fields[j].setAccessible(true);
                // 字段值
                if (fields[j].getType().getName().equals(
                        java.lang.String.class.getName())) {
                    fields[j].set(obj, fields[j].get(obj).toString().trim());
                }
            }catch (Exception e){
                continue;
            }
        }
    }


    //将实体类的String字段trim并转印特殊字符
    public static void trimObjectEscape(Object obj) {
        if (obj == null) return;
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int j = 0; j < fields.length; j++) {
            try {
                fields[j].setAccessible(true);
                // 字段值
                if (fields[j].getType().getName().equals(
                        java.lang.String.class.getName())) {
                    fields[j].set(obj, StringEscapeUtils.unescapeHtml4( fields[j].get(obj).toString().trim()));
                }
            }catch (Exception e){
                continue;
            }
        }
    }
    //当有一个必填字段未空时
    //将实体类的String字段trim
    public static void trimNull(String... strs) {
        for(String str:strs){
            if(StringUtils.isBlank(str)){
                throw new RuntimeException(Constants.SAVE_ERROR);
            }
        }

    }

    public static void main(String []args){
        TrimUtil.trimNull();
    }

}
