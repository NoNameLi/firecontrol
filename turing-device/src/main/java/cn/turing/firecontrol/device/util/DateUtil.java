package cn.turing.firecontrol.device.util;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.*;


public class DateUtil {

    /**
     * 获取两个日期之间的所有日期（yyyy-MM-dd）
     * @Description TODO
     * @param begin
     * @param end
     * @return
     */
    public static List<Date> getBetweenDates(Date begin, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(begin);
        while(begin.getTime()<=end.getTime()){
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
            begin = tempStart.getTime();
        }
        return result;
    }

    /**
     *  获取当天凌晨0点0分0秒Date
     * @return
     */
    public static Date getStartTime() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        Date beginOfDate = calendar1.getTime();
        return beginOfDate;
    }

    /**
     * 获取当天凌晨23点59分59秒Date
     * @return
     */
    public static Date getEndTime() {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        Date endOfDate = calendar2.getTime();
        return endOfDate;
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

    /**
     * 根据提供的年月获取该月份的最后一天
     * @return
     */
    public static Date getEndDayofMonth(Date date) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(date);
        startDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        startDate.set(Calendar.HOUR_OF_DAY, 23);
        startDate.set(Calendar.MINUTE, 59);
        startDate.set(Calendar.SECOND, 59);
        startDate.set(Calendar.MILLISECOND, 999);
        Date endDate = startDate.getTime();
        return endDate;
    }

    /**
     * 获取本周的开始时间
     * @return
     */
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return cal.getTime();
    }

    /**
     * 获取本周的结束时间
     * @return
     */
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return weekEndSta;
    }

    /**
     * 当前年的开始时间
     * @return
     */
    public static Date getCurrentYearStartTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.DATE, 1);
            now =c.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前年的结束时间
     * @return
     */
    public static Date getCurrentYearEndTime() {
        Calendar c = Calendar.getInstance();
        Date now = null;
        try {
            c.set(Calendar.MONTH, 11);
            c.set(Calendar.DATE, 31);
            now = c.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 获取今年是哪一年
     * @return
     */
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    /**
     * 获取哪一月
     * @return
     */
    public static Integer getMonth() {
        Calendar cal = Calendar.getInstance();
        //int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH )+1;
        return month;
    }

    /**
     * 获取当前季度 起始时间
     * @return
     */
    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 6);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间
     * @return
     */
    public static  Date getCurrentQuarterEndTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                c.set(Calendar.MONTH, 2);
                c.set(Calendar.DATE, 31);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                c.set(Calendar.MONTH, 8);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
    /**
     * 获得昨天开始时间
     * @return
     */
    public static  String getYesterdayStartDay() {
        Calendar   cal   =   Calendar.getInstance();
        cal.add(Calendar.DATE,   -1);
        String date = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
        return date+" 00:00:00";
    }

    /**
     * 获得昨天结束时间
     * @return
     */
    public static  String getYesterdayEedDay() {
        Calendar   cal   =   Calendar.getInstance();
        cal.add(Calendar.DATE,   -1);
        String date = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
        return date+" 23:59:59";
    }

    /**
     * 获得最近7天开始时间
     * @return
     */
    public static  String getRecentlySevenStartDay() {
        Calendar   cal   =   Calendar.getInstance();
        cal.add(Calendar.DATE,   -6);
        String date = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
        return date+" 00:00:00";
    }

    /**
     * 获取近几天的时间
     * @param amount
     * @return
     */
    public static  String getRecentlySevenStartDay(int amount) {
        Calendar   cal   =   Calendar.getInstance();
        cal.add(Calendar.DATE,   amount);
        String date = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
        return date;
    }

    /**
     * 获得最近30天开始时间
     * @return
     */
    public static  String getRecentlyStartDay() {
        Calendar   cal   =   Calendar.getInstance();
        cal.add(Calendar.DATE,   -29);
        String date = new SimpleDateFormat( "yyyy-MM-dd ").format(cal.getTime());
        return date+" 00:00:00";
    }

    private static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }



    public static String getFriendlytime(Date d){
        Date date = new Date();
        long delta = (date.getTime()-d.getTime())/1000;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        if(delta<=0)return d.toLocaleString();
//        if(delta/(60*60*24*365) > 0) return delta/(60*60*24*365) +"年前";
//        if(delta/(60*60*24*30) > 0) return delta/(60*60*24*30) +"个月前";
//        if(delta/(60*60*24*7) > 0)return delta/(60*60*24*7) +"周前";
//        if(delta/(60*60*24) > 0) return delta/(60*60*24) +"天前";
//        if(delta/(60*60) > 0)return delta/(60*60) +"小时前";
        if(delta/(60*60*24) > 0 ||  DateUtil.isSameDate(date,d)==false) return sdf.format(d);
        if(delta/(60*60) > 0 && DateUtil.isSameDate(date,d)==true)return simpleDateFormat.format(d).substring(10,16);
        if(delta/(60) > 0)return delta/(60) +"分钟前";
        return "刚刚";
    }

    public static String getHandletime(Date d1,Date d2){
        long diff = d1.getTime()-d2.getTime();
        long day=diff/(24*60*60*1000);
        long hour=(diff/(60*60*1000)-day*24);
        long min=((diff/(60*1000))-day*24*60-hour*60);
        long s=(diff/1000-day*24*60*60-hour*60*60-min*60);
        //System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
        if(day==0 && hour !=0){
            return hour+"小时"+min+"分"+s+"秒";
        }
        if(day==0 && hour==0 && min!=0){
            return min+"分"+s+"秒";
        }
        if(day==0 && hour==0 && min==0){
            return s+"秒";
        }
        return ""+day+"天"+hour+"小时"+min+"分"+s+"秒";
    }

    //获取某一天的开始时间
    public static Date getStartTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
        return calendar.getTime();
    }

    //获取某一天的结束时间
    public static Date getEndTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),23,59,59);
        return calendar.getTime();
    }

    /**
     * 获取两个时间的时间差：X天X时X分X秒
     * @return
     */
    public static String getTimeDuration(Date startTime, Date endTime){
        long time = endTime.getTime()  - startTime.getTime();
        long secondMil = 1000L, minuteMil = 60 * secondMil, hourMil = 60 * minuteMil, dayMil = 24*hourMil;
        long[] times = {0,0,0,0},timeMils = {dayMil,hourMil,minuteMil,secondMil};
        String[] units = {"天","小时","分钟","秒"};
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < timeMils.length; i++){
            times[i] = time / timeMils[i];
            time -= times[i] * timeMils[i];
            if(times[i] > 0){
                sb.append(times[i]).append(units[i]);
            }
        }
        return sb.toString();
    }


    //测试
    public static void main(String[] args) throws  Exception{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = dateFormat.parse("2019-04-17 00:00:00");
        Date end = dateFormat.parse("2019-04-17 18:14:01");
        String s = getTimeDuration(start,end);
        System.out.println(s);
    }
}
