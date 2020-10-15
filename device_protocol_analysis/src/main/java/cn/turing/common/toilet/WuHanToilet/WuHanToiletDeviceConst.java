package cn.turing.common.toilet.WuHanToilet;

import cn.hutool.http.HttpUtil;
import cn.turing.common.entity.DeviceInfo;
import cn.turing.common.entity.turing.AirMonitoringDeviceInfoV2;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WuHanToiletDeviceConst {

    public static String username="tuling";
    public static String password="123456";

    public static String TOKEN="ec0513074da84ee388a821f4ff361c67";

    public static String BASE_URL="http://58.49.165.13:86/";
    //获取token

    public static String GET_TOKEN=BASE_URL+"api/Auth/GetToken";
    //（4）环境实时监测数据接口

    public static String POST_AIR=BASE_URL+"api/Collect/UploadSensorData";
    //（5）人流量数据接口 ：

    public static String POST_PERSON=BASE_URL+"api/Traffic/UploadTrafficData";

    //厕位余量数据接口

    public static String POST_YU_PSERSON=BASE_URL+"api/ToiletRemain/UploadRemainData";

    //清洁时间数据接口

    public static String POST_CLEAR=BASE_URL+"api/CleanTime/UploadCleanData";

    public static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 获取token
     * @return
     */
    public static JSONObject getToken(){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("username",username);
        map.put("password",password);
        map.put("no",(int)((Math.random()*9+1)*10000000));
        String json= HttpUtil.get(GET_TOKEN,map);
        JSONObject jsonObject=JSONObject.parseObject(json);
        return jsonObject;
    }

    /**
     *
     * @param token
     * @param stationId
     * @param deviceInfo
     * @return
     */
    public static String uploadSensorData(String token, Integer stationId, DeviceInfo deviceInfo){

        AirMonitoringDeviceInfoV2 deviceInfoV2= (AirMonitoringDeviceInfoV2) deviceInfo;
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("stationId",stationId);
        jsonObject.put("dataTime",sdf.format(new Date()));

        JSONObject airJson=new JSONObject();
        airJson.put("mTemperature",deviceInfoV2.getTemp());
        airJson.put("mHumidity",deviceInfoV2.getHum());
        airJson.put("wTemperature",deviceInfoV2.getTemp());
        airJson.put("wHumidity",deviceInfoV2.getHum());
        if (1-deviceInfoV2.getVoc()<=0.05){
            airJson.put("mAmmonia",0.13);
            airJson.put("mHydrogenSulfide",0.05);
            airJson.put("wAmmonia",0.14);
            airJson.put("wHydrogenSulfide",0.06);
        }else if (1-deviceInfoV2.getVoc()>=1){
            airJson.put("mAmmonia",10);
            airJson.put("mHydrogenSulfide",5);
            airJson.put("wAmmonia",10.2);
            airJson.put("wHydrogenSulfide",4.9);
        }else{
            airJson.put("mAmmonia",10 - (10 / 0.95)*(1-deviceInfoV2.getVoc()));
            airJson.put("mHydrogenSulfide", 5 - (5 / 0.95) *(1-deviceInfoV2.getVoc()));
            airJson.put("wAmmonia",10 - (10 / 0.95)*(1-deviceInfoV2.getVoc()));
            airJson.put("wHydrogenSulfide", 5 - (5 / 0.95) *(1-deviceInfoV2.getVoc()));
        }
        JSONArray jsonArray=new JSONArray();
        jsonArray.add(airJson);
        jsonObject.put("data",jsonArray);
        String result=HttpUtil.post(POST_AIR+"/?token="+token+"&no="+(int)((Math.random()*9+1)*10000000),jsonObject.toJSONString());
        return result;
    }

    /**
     * 厕所余量
     * @param token
     * @param stationId
     * @param deviceCode
     * @return
     */
    public  static String UploadRemainData(String token, Integer stationId, String deviceCode){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("stationId",stationId);
        jsonObject.put("dataTime",sdf.format(new Date()));

        JSONArray jsonArray=new JSONArray();
        JSONObject object=new JSONObject();
        object.put("manLeft",2);
        object.put("woManLeft",2);

        JSONArray arraywMam=new JSONArray();
        JSONObject objectS=new JSONObject();
        objectS.put("wCsnName",deviceCode);
        objectS.put("status",0);
        arraywMam.add(objectS);

        object.put("wManStatus",arraywMam);

        JSONArray arraywWom=new JSONArray();
        JSONObject objectWW=new JSONObject();
        objectWW.put("wCsnName",deviceCode);
        objectWW.put("status",0);
        arraywWom.add(objectWW);

        object.put("wWomanStatus",arraywWom);

        jsonArray.add(object);

        jsonObject.put("data",jsonArray);
        System.out.println(jsonObject.toJSONString());
        String result=HttpUtil.post(POST_YU_PSERSON+"/?token="+token+"&no="+(int)((Math.random()*9+1)*10000000),jsonObject.toJSONString());
        return result;
    }

    public static void main(String[] args) {
        UploadRemainData("ec0513074da84ee388a821f4ff361c67",901,"866971038866930");
    }
}
