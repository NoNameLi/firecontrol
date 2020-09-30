package cn.turing.firecontrol.device.util;

import cn.turing.firecontrol.common.msg.ObjectRestResponse;
import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.device.entity.DeviceHardwareFacilities;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[]args) throws Exception {
        String time = "2018-09-30T09:37:08.255Z";
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("GMT"));
        simpleDateFormat1.parse(time);
        System.out.println(simpleDateFormat1.parse(time));

    }
    /**
     * 根据提供的年月日获取该月份的第一天
     * @return
     */
    public static Date getBeginDayofMonth(Date date) {
        date.getTime();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);
        startDate.set(Calendar.DAY_OF_MONTH, 1);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        Date firstDate = startDate.getTime();
        return firstDate;
    }

    public static boolean validatorOutlet(String outlet, DeviceHardwareFacilities entity, ObjectRestResponse responseResult){
        boolean flag = true;
        if("0".equals(outlet)){
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeOne(),entity.getOutletValueOne())){
                responseResult.setStatus(500);
                responseResult.setMessage("第一出水口缺少参数");
                flag = false;
                return flag;
            }
        }else if("1".equals(outlet)){
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeOne(),entity.getOutletValueOne())){
                responseResult.setStatus(500);
                responseResult.setMessage("第一出水口缺少参数");
                flag = false;
                return flag;
            }
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeTwo(),entity.getOutletValueTwo())){
                responseResult.setStatus(500);
                responseResult.setMessage("第二出水口缺少参数");
                flag = false;
                return flag;
            }
        }else if("2".equals(outlet)){
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeOne(),entity.getOutletValueOne())){
                responseResult.setStatus(500);
                responseResult.setMessage("第一出水口缺少参数");
                flag = false;
                return flag;
            }
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeTwo(),entity.getOutletValueTwo())){
                responseResult.setStatus(500);
                responseResult.setMessage("第二出水口缺少参数");
                flag = false;
                return flag;
            }
            if(ValidatorUtils.hasAnyBlank(entity.getOutletTypeThree(),entity.getOutletValueThree())){
                responseResult.setStatus(500);
                responseResult.setMessage("第三出水口缺少参数");
                flag = false;
                return flag;
            }
        }
        return flag;
    }
}
