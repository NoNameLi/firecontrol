package cn.turing.firecontrol.datahandler.util;

import java.util.*;

/**
 * @author: zhangpeng
 * @email: 723308025@qq.com
 * @create: 2018-11-08 09:48
 **/
public class SortUtil {

    public static List sort(List<Map<String,Object>> list) {
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return (Integer)o2.get("count")-((Integer)o1.get("count"));
            }
        });
        return new ArrayList(list);
    }
}
