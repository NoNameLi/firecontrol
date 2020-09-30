package cn.turing.firecontrol.device.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2019/02/20 9:23
 *
 * @Description 萤石开放平台接口调用工具类
 * @Version V1.0
 */
@Data
@Slf4j
public class YingShiUtil {

    private static final String HOST = "https://open.ys7.com";
    private String appKey;
    private String appSecret;
    private JSONObject alarmTypes;
    @Autowired
    private ResourceLoader resourceLoader;

    public YingShiUtil(String appKey,String appSecret){
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    @PostConstruct
    public void loadAlarmTypes() throws IOException {
        log.info("加载萤石报警类型代号");
        ClassPathResource resource = new ClassPathResource("yingShiAlarmType.json");
        if(!resource.exists()){
            throw new RuntimeException("class:yingShiAlarmType.json文件不存在");
        }
        String json = IOUtils.toString(resource.getInputStream(),"UTF-8");
        alarmTypes = JSONObject.parseObject(json);
        log.info("加载萤石报警类型代号成功");
    }

    //根据appKey和secret获取accessToken
    //返回数据：
    //"accessToken": "at.7jrcjmna8qnqg8d3dgnzs87m4v2dme3l-32enpqgusd-1jvdfe4-uxo15ik0s"
    //"expireTime": 1470810222045
    private JSONObject getaccessToken(){
        String url = HOST + "/api/lapp/token/get";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> querys = new HashMap<>();
        querys.put("appKey",appKey);
        querys.put("appSecret",appSecret);
        String res = HttpUtils.doPost(url,headers,querys);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if(!"200".equals(jsonObject.getString("code"))){
            log.error("获取AccessToken失败:{}",res);
            throw new RuntimeException("获取AccessToken失败:" + jsonObject.getString("msg"));
        }
        log.info("获取AccessToken成功:{}",res);
        return jsonObject.getJSONObject("data");
    }

    //根据appKey和secret获取accessToken
    public String getaccessToken(RedisTemplate<String,String> redisTemplate){
        String accessToken = redisTemplate.opsForValue().get(Constants.DeviceRedisKey.YING_SHI_ACESS_TOKEN.getKey());
        if(accessToken != null){
            return accessToken;
        }
        JSONObject jsonObject = getaccessToken();
        accessToken = jsonObject.getString("accessToken");
        Long expireTime = jsonObject.getLong("expireTime") - System.currentTimeMillis() - 60*60*1000;
        redisTemplate.opsForValue().set(Constants.DeviceRedisKey.YING_SHI_ACESS_TOKEN.getKey(),accessToken,expireTime,TimeUnit.MILLISECONDS);
        return accessToken;
    }

    //添加设备到账号下
    public void addDevice(String accessToken, String deviceSerial, String validateCode){
        String url = HOST + "/api/lapp/device/add";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> querys = new HashMap<>();
        querys.put("accessToken",accessToken);
        querys.put("deviceSerial",deviceSerial);
        querys.put("validateCode",validateCode);
        String res = HttpUtils.doPost(url,headers,querys);
        JSONObject jsonObject = JSONObject.parseObject(res);
        List<String> successCodes = Arrays.asList(new String[]{"200","20017"});
        if(!successCodes.contains(jsonObject.getString("code"))){
            log.error("添加设备失败:{}",res);
            throw new RuntimeException("添加设备失败:" + jsonObject.getString("msg"));
        }
        log.info("添加设备成功:{}",res);
    }

    /**
     * 批量开通直播功能
     * @param accessToken
     * @param deviceSerialAndChannelNos 数据样例："deviceSerial:chanelNo"
     */
    public void openLive(String accessToken, List<String> deviceSerialAndChannelNos){
        String url = HOST + "/api/lapp/live/video/open";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> querys = new HashMap<>();
        querys.put("accessToken",accessToken);
        StringBuilder source = new StringBuilder();
        for(String s : deviceSerialAndChannelNos){
            source.append(s).append(",");
        }
        if (source.length() > 0){
            source.deleteCharAt(source.lastIndexOf(","));
        }
        querys.put("source",source.toString());
        String res = HttpUtils.doPost(url,headers,querys);
        JSONObject jsonObject = JSONObject.parseObject(res);
        List<String> successCodes = Arrays.asList(new String[]{"200","20017"});
        if(!successCodes.contains(jsonObject.getString("code"))){
            log.error("开通直播功能失败:{}",res);
            throw new RuntimeException("开通直播功能失败:" + jsonObject.getString("msg"));
        }
        log.info("开通直播功能成功:{}",res);
    }


    //关闭设备视频加密
    public void offEncrypt(String accessToken, String deviceSerial, String validateCode){
        String url = HOST + "/api/lapp/device/encrypt/off";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> querys = new HashMap<>();
        querys.put("accessToken",accessToken);
        querys.put("deviceSerial",deviceSerial);
        querys.put("validateCode",validateCode);
        String res = HttpUtils.doPost(url,headers,querys);
        JSONObject jsonObject = JSONObject.parseObject(res);
        List<String> successCodes = Arrays.asList(new String[]{"200","60016"});
        if(!successCodes.contains(jsonObject.getString("code"))){
            log.error("关闭设备视频加密失败:{}",res);
            throw new RuntimeException("关闭设备视频加密失败:" + jsonObject.getString("msg"));
        }
        log.info("关闭设备视频加密成功:{}",res);
    }

    //删除账号下设备
    public void deleteDevice(String accessToken, String deviceSerial){
        String url = HOST + "/api/lapp/device/delete";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> querys = new HashMap<>();
        querys.put("accessToken",accessToken);
        querys.put("deviceSerial",deviceSerial);
        String res = HttpUtils.doPost(url,headers,querys);
        JSONObject jsonObject = JSONObject.parseObject(res);
        List<String> successCodes = Arrays.asList(new String[]{"200","20018"});
        if(!successCodes.contains(jsonObject.getString("code"))){
            log.error("删除设备失败:{}",res);
            throw new RuntimeException("删除设备失败:" + jsonObject.getString("msg"));
        }
        log.info("删除设备成功:{}",res);
    }

    //查询用户下指定设备的基本信息
    public JSONObject getDevice(String accessToken, String deviceSerial){
        String url = HOST + "/api/lapp/device/info";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> querys = new HashMap<>();
        querys.put("accessToken",accessToken);
        querys.put("deviceSerial",deviceSerial);
        String res = HttpUtils.doPost(url,headers,querys);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if(!"200".equals(jsonObject.getString("code"))){
            log.error("查询设备的基本信息失败:{}",res);
            throw new RuntimeException("查询设备的基本信息失败:" + jsonObject.getString("msg"));
        }
        log.info("查询设备的基本信息成功:{}",res);
        return jsonObject.getJSONObject("data");
    }


    /**
     *获取监控点列表
     * @param accessToken 授权过程获取的access_token
     * @return
     */
    public List<JSONObject>  getCameraList(String accessToken){
        List<JSONObject> list = Lists.newArrayList();
        Page<JSONObject> page = null;
        int pageStart = 0, pageSize = 50;
        do{
            page = getCameraList(accessToken,pageStart++,pageSize);
            list.addAll(page.getList());
        }while (page.hasNext());
        return list;
    }

    /**
     *获取监控点列表
     * @param accessToken 授权过程获取的access_token
     * @param pageStart 分页起始页，从0开始
     * @param pageSize 分页大小，默认为10，最大为50
     * @return
     */
    private Page<JSONObject>  getCameraList(String accessToken, int pageStart, int pageSize){
        String url = HOST + "/api/lapp/camera/list";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> querys = new HashMap<>();
        querys.put("accessToken",accessToken);
        querys.put("pageStart",Integer.toString(pageStart));
        querys.put("pageSize",Integer.toString(pageSize));
        String res = HttpUtils.doPost(url,headers,querys);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if(!"200".equals(jsonObject.getString("code"))){
            log.error("查询设备基本信息列表失败:{}",res);
            throw new RuntimeException("查询设备基本信息列表失败:" + jsonObject.getString("msg"));
        }
        log.info("查询设备基本信息列表成功:{}",res);
        int total = jsonObject.getJSONObject("page").getInteger("total");
        List<JSONObject> data = jsonObject.getJSONArray("data").toJavaList(JSONObject.class);
        Page<JSONObject> page = new Page<>(pageStart,pageSize,total,data);
        return page;
    }

    //抓拍设备当前画面
    public String getImageUrl(String accessToken, String deviceSerial, Integer channelNo){
        String url = HOST + "/api/lapp/device/capture";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> querys = new HashMap<>();
        querys.put("accessToken",accessToken);
        querys.put("deviceSerial",deviceSerial);
        querys.put("channelNo",channelNo.toString());
        String res = HttpUtils.doPost(url,headers,querys);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if(!"200".equals(jsonObject.getString("code"))){
            log.error("抓拍设备当前画面失败:{}",res);
            throw new RuntimeException("抓拍设备当前画面失败:" + jsonObject.getString("msg"));
        }
        log.info("抓拍设备当前画面成功:{}",res);
        return jsonObject.getJSONObject("data").getString("picUrl");
    }


    //数据示例： [
    //    {
    //      "deviceSerial": "202480707",
    //      "channelNo": 1,
    //      "deviceName": "C4S(202480707)",
    //      "hls": "http://hls.open.ys7.com/openlive/abbb687faaae47118501e10ebf6374bb.m3u8",
    //      "hlsHd": "http://hls.open.ys7.com/openlive/abbb687faaae47118501e10ebf6374bb.hd.m3u8",
    //      "rtmp": "rtmp://rtmp.open.ys7.com/openlive/abbb687faaae47118501e10ebf6374bb",
    //      "rtmpHd": "rtmp://rtmp.open.ys7.com/openlive/abbb687faaae47118501e10ebf6374bb.hd",
    //      "flvAddress": "https://flvopen.ys7.com:9188/openlive/abbb687faaae47118501e10ebf6374bb.flv",
    //      "hdFlvAddress": "https://flvopen.ys7.com:9188/openlive/abbb687faaae47118501e10ebf6374bb.hd.flv",
    //      "status": 1,
    //      "exception": 0,
    //      "ret": "200",
    //      "desc": "获取成功!"
    //    }
    //  ]
    //获取直播地址

    /**
     *获取直播地址
     * @param accessToken
     * @param deviceSerialAndChannelNos 数据样例："deviceSerial:chanelNo"
     * @return
     */
    public Map<String,String> getLiveAddress(String accessToken, List<String> deviceSerialAndChannelNos){
        String url = HOST + "/api/lapp/live/address/get";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> querys = new HashMap<>();
        querys.put("accessToken",accessToken);
        StringBuilder source = new StringBuilder();
        for(String s : deviceSerialAndChannelNos){
            source.append(s).append(",");
        }
        if (source.length() > 0){
            source.deleteCharAt(source.lastIndexOf(","));
        }
        querys.put("source",source.toString());
        String res = HttpUtils.doPost(url,headers,querys);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if(!"200".equals(jsonObject.getString("code"))){
            log.error("获取直播地址失败:{}",res);
            throw new RuntimeException("获取直播地址失败:" + jsonObject.getString("msg"));
        }
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        JSONObject temp = null;
        String deviceSerial = null;
        String channelNo = null;
        Map<String,String> map = Maps.newHashMap();
        for(Object obj : jsonArray){
            temp = (JSONObject)obj;
            String ret = temp.getString("ret");
            deviceSerial = temp.getString("deviceSerial");
            channelNo = temp.getString("channelNo");
            if(!"200".equals(ret)){
                log.error("获取{}的直播地址失败：{}",deviceSerial,temp.getString("desc"));
                continue;
            }
            temp.remove("deviceSerial");
            temp.remove("channelNo");
            temp.remove("deviceName");
            temp.remove("status");
            temp.remove("exception");
            temp.remove("ret");
            temp.remove("desc");
            map.put(deviceSerial + ":" + channelNo,temp.toJSONString());
        }
        log.info("获取直播地址成功:{}",res);
        return map;
    }




    /**
     * 获取所有告警信息列表
     * @param accessToken 	String	授权过程获取的access_token	Y
     * @param startTime long	告警查询开始时间，时间格式为1457420564508，精确到毫秒，默认为今日0点，最多查询当前时间7天前以内的数据	N
     * @param endTime long	告警查询结束时间，时间格式为1457420771029，精确到毫秒，默认为当前时间	N
     * @param alarmType int	告警类型，默认为-1（全部）	N
     * @param status int	告警消息状态：2-所有，1-已读，0-未读，默认为0（未读状态）	N
     */
    public List<JSONObject> getAllAlarmList(String accessToken, Date startTime, Date endTime, Integer alarmType, Integer status){
        List<JSONObject> list = Lists.newArrayList();
        Integer pageStart = 0,pageSize = 50;
        Page page = null;
        do{
            page = getAllAlarmListByPage(accessToken,startTime,endTime,alarmType,status,pageStart,pageSize);
            list.addAll(page.getList());
        }while (page.hasNext());
        return list;
    }

    /**
     * 获取所有告警信息列表(分页)
     * @param accessToken 	String	授权过程获取的access_token	Y
     * @param startTime long	告警查询开始时间，时间格式为1457420564508，精确到毫秒，默认为今日0点，最多查询当前时间7天前以内的数据	N
     * @param endTime long	告警查询结束时间，时间格式为1457420771029，精确到毫秒，默认为当前时间	N
     * @param alarmType int	告警类型，默认为-1（全部）	N
     * @param status int	告警消息状态：2-所有，1-已读，0-未读，默认为0（未读状态）	N
     * @param pageStart int	分页起始页，从0开始，默认为0	N
     * @param pageSize int	分页大小，默认为10，最大为50
     */
    public Page<JSONObject> getAllAlarmListByPage(String accessToken, Date startTime, Date endTime, Integer alarmType, Integer status, Integer pageStart, Integer pageSize){
        String url = HOST + "/api/lapp/alarm/list";
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        Map<String,String> querys = new HashMap<>();
        querys.put("accessToken",accessToken);
        if(startTime != null){
            querys.put("startTime",Long.toString(startTime.getTime()));
        }
        if(endTime != null){
            querys.put("endTime",Long.toString(endTime.getTime()));
        }
        if(alarmType != null){
            querys.put("alarmType",alarmType.toString());
        }
        if(status != null){
            querys.put("status",status.toString());
        }
        if(pageStart != null){
            querys.put("pageStart",pageStart.toString());
        }
        if(pageSize != null){
            querys.put("pageSize",pageSize.toString());
        }
        String res = HttpUtils.doPost(url,headers,querys);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if(!"200".equals(jsonObject.getString("code"))){
            log.error("获取所有告警信息列表失败:{}",res);
            throw new RuntimeException("获取所有告警信息列表失败:" + jsonObject.getString("msg"));
        }
        log.info("获取所有告警信息列表成功:{}",res);
        List<JSONObject> list = jsonObject.getJSONArray("data").toJavaList(JSONObject.class);
        JSONObject jo = null;
        for(int i=0; i<list.size() ; i++){
            jo = list.get(i);
            jo.put("alarmType",alarmTypes.getString(jo.getInteger("alarmType").toString()));
        }
        int total = jsonObject.getJSONObject("page").getInteger("total");
        return new Page<>(pageStart,pageSize,total,list);
    }

    //接口调用分页信息
    @Data
    @AllArgsConstructor
    class Page <E> {
        Integer pageNo;
        Integer pageSize;
        Integer total;
        List<E> list;

        Boolean hasNext(){
            return total > (pageNo + 1) * pageSize;
        }

    }




    public static void main(String[] args) {
        String accessToken = "at.9v767jps3nw0amgq2g0jdf5yajaud3pr-4n5fvyxtti-194c0cu-7pjg27mxr";
        String deviceSerial = "202480707";
        String validateCode = "HHLMSF";
        YingShiUtil util = new YingShiUtil("304b6624f5cf42e09f67f26552c9049f","174d8787093a43108a3579e00947bcf6");
//        util.getaccessToken();
//        util.deleteDevice(accessToken,deviceSerial);
//        util.addDevice(accessToken,deviceSerial,validateCode);
//        util.getDevice(accessToken,deviceSerial);
//        util.getImageUrl(accessToken,deviceSerial);
        List<String> deviceSerials = new ArrayList<>();
        deviceSerials.add(deviceSerial);
        Map<String,String> map = util.getLiveAddress(accessToken,deviceSerials);
        log.info("地址:{}",map.get(deviceSerial));
    }





}
