package cn.turing.firecontrol.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Long getMisToEndOfDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        return calendar.getTimeInMillis() - date.getTime();
    }

    public static void main(String[] args) throws ParseException {
        System.out.print(getMisToEndOfDay(new Date()));
    }



}
