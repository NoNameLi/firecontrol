package cn.turing.firecontrol.device.util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SplitUtil {


    public static Integer[] splitInt(String str){
        Integer [] ins = new Integer[0];
        if(StringUtils.isNotBlank(str)){
            String []temp = str.split(",");
            ins = new Integer[temp.length];
            for(int i=0;i<ins.length;i++){
                try{
                    ins[i] = Integer.parseInt(temp[i]);
                }catch (Exception e){
                    return null;
                }
            }
        }
        return ins;
    }

    public static Long[] splitLong(String str){
        Long [] ins = new Long[0];
        if(StringUtils.isNotBlank(str)){
            String []temp = str.split(",");
            ins = new Long[temp.length];
            for(int i=0;i<ins.length;i++){
                try{
                    ins[i] = Long.parseLong(temp[i]);
                }catch (Exception e){
                    return null;
                }
            }
        }
        return ins;
    }

    public static String merge(List<Integer> lists){
        StringBuffer stringBuffer=new StringBuffer("");
        if(lists!=null&&lists.size()>0){
            for (int i=0;i<lists.size();i++){
                if(i==lists.size()-1){
                    stringBuffer.append(lists.get(i));
                }else{
                    stringBuffer.append(lists.get(i)+",");
                }
            }
        }
        return stringBuffer.toString();
    }

    public static String mergeString(List<String> lists){
        StringBuffer stringBuffer=new StringBuffer("");
        if(lists!=null&&lists.size()>0){
            for (int i=0;i<lists.size();i++){
                if(i==lists.size()-1){
                    stringBuffer.append(lists.get(i));
                }else{
                    stringBuffer.append(lists.get(i)+",");
                }
            }
        }
        return stringBuffer.toString();
    }
}
