package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.auth.client.annotation.CheckClientToken;
import cn.turing.firecontrol.auth.client.annotation.CheckUserToken;
import cn.turing.firecontrol.common.exception.base.ParamErrorException;
import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.common.util.UUIDUtils;
import cn.turing.firecontrol.core.context.BaseContextHandler;
import cn.turing.firecontrol.device.entity.*;
import cn.turing.firecontrol.device.util.Constants;
import cn.turing.firecontrol.device.util.DateUtil;
import cn.turing.firecontrol.device.util.ESTransportUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2019/02/28 11:03
 *
 * @Description
 * @Version V1.0
 */
@Service
@Slf4j
public class DeviceVideoAbnormalDataBiz {

    @Autowired
    private TransportClient transportClient;
    @Autowired
    private DeviceVideoGroupBiz deviceVideoGroupBiz;
    @Autowired
    private ESTransportUtil esTransportUtil;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private DeviceSensorBiz deviceSensorBiz;
    @Autowired
    private DeviceVideoExtBiz deviceVideoExtBiz;
    @Value("${tmc.config.rabbitmq.abnormal.msg.exchange}")
    private String EXCHANGE_NAME;
    @Value("${tmc.config.rabbitmq.abnormal.msg.routingKey}")
    private String ROUTING_KEY;
    /*@Value("${tmc.config.rabbitmq.abnormal.msg.queue}")
    private String QUEUE_NAME;*/
    public final String REDIS_ABNORMAL_HASH = "device:abnormals";


    @PostConstruct
    public void init(){
        redisTemplate.opsForHash().put(REDIS_ABNORMAL_HASH,"init",Long.toString(System.currentTimeMillis()));
        redisTemplate.persist(REDIS_ABNORMAL_HASH);
    }

    /**
     * 查询租户的所有报警类型
     * @param tenantId
     * @return
     */
    public List<String> queryAlarmTypes(String tenantId){
        AggregationBuilder agg = AggregationBuilders.terms("alarmType").field("alarmType").size(Integer.MAX_VALUE);
        QueryBuilder matchQuery = QueryBuilders.matchQuery("tenantId",tenantId);
        SearchResponse response = transportClient.prepareSearch(Constants.AnalysisSolution.FIRE.getAbnormalEsIndex()).setTypes(Constants.AnalysisSolution.FIRE.getCode()).setQuery(matchQuery).addAggregation(agg).execute().actionGet();
        List<String> types = Lists.newArrayList();
        Terms aggregations = response.getAggregations().get("alarmType");
        for(Terms.Bucket b : aggregations.getBuckets()){
            types.add(b.getKeyAsString());
        }
        return types;
    }


    /**
     * 查询租户的所有设备系列
     * @param tenantId
     * @return
     */
    public List<String> queryDeviceSerials(String tenantId){
        AggregationBuilder agg = AggregationBuilders.terms("deviceSerial").field("deviceSerial").size(Integer.MAX_VALUE);
        QueryBuilder matchQuery = QueryBuilders.matchQuery("tenantId",tenantId);
        SearchResponse response = transportClient.prepareSearch(Constants.AnalysisSolution.FIRE.getAbnormalEsIndex()).setTypes(Constants.AnalysisSolution.FIRE.getCode()).setQuery(matchQuery).addAggregation(agg).execute().actionGet();
        List<String> types = Lists.newArrayList();
        Terms aggregations = response.getAggregations().get("deviceSerial");
        for(Terms.Bucket b : aggregations.getBuckets()){
            types.add(b.getKeyAsString());
        }
        return types;
    }

    public Long getAlarmCount(String tenantId,String sensorNo){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery("tenantId", tenantId));
        boolQueryBuilder.filter(QueryBuilders.termQuery("sensorNo", sensorNo));
        SearchResponse response = transportClient.prepareSearch(Constants.AnalysisSolution.FIRE.getAbnormalEsIndex())
                .setTypes(Constants.AnalysisSolution.FIRE.getCode()).setQuery(boolQueryBuilder).setSize(0).execute().actionGet();
        return response.getHits().getTotalHits();
    }

    /**
     * 分页查询视频设备异常数据
     * @return
     */
    public TableResultResponse queryByPage(String tenantId,Integer page, Integer limit, Boolean hasRestore, String alarmType, Date startTime, Date endTime, String deviceName, String sensorNo, String deviceSerial){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();//组合查询
        if(StringUtils.isNotBlank(tenantId)){
            boolQueryBuilder.filter(QueryBuilders.matchQuery("tenantId", tenantId));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(StringUtils.isNotBlank(alarmType)){
            boolQueryBuilder.filter(QueryBuilders.matchQuery("alarmType",alarmType));
        }
        if(startTime != null && endTime != null){
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("alarmTime").gte(dateFormat.format(startTime)).lte(dateFormat.format(endTime)));
        }
        if(StringUtils.isNotBlank(deviceName)){
            boolQueryBuilder.filter(QueryBuilders.wildcardQuery("deviceName","*" + deviceName +"*"));
        }
        if(StringUtils.isNotBlank(sensorNo)){
            boolQueryBuilder.filter(QueryBuilders.wildcardQuery("sensorNo","*" + sensorNo +"*"));
        }
        if(StringUtils.isNotBlank(deviceSerial)){
            boolQueryBuilder.filter(QueryBuilders.matchQuery("deviceSerial",deviceSerial));
        }
        QueryBuilder isRestoreQuery = QueryBuilders.existsQuery("restoreTime");
        if(hasRestore){
            boolQueryBuilder.must(isRestoreQuery);
        }else {
            boolQueryBuilder.mustNot(isRestoreQuery);
        }
        int from = limit * ( page - 1);
        SearchResponse response = transportClient.prepareSearch(Constants.AnalysisSolution.FIRE.getAbnormalEsIndex())
                .setQuery(boolQueryBuilder).setFrom(from).setSize(limit).addSort("alarmTime",SortOrder.DESC)
                .execute().actionGet();
        List<JSONObject> list = new ArrayList<>();
        SearchHits searchHits =  response.getHits();
        List<String> sensorNos = Lists.newArrayList();
        for (SearchHit searchHit : searchHits) {
            String hit = searchHit.getSourceAsString();
            JSONObject jsonObject = JSONObject.parseObject(hit);
            jsonObject.put("id",searchHit.getId());
            sensorNos.add(jsonObject.getString("sensorNo"));
            list.add(jsonObject);
        }
        if(!list.isEmpty()){
            Map<String,Map<String,Object>> sensors = deviceVideoGroupBiz.queryBySensorNos(sensorNos);
            for(JSONObject jo : list){
                String sn = jo.getString("sensorNo");
                Map<String,Object> data = sensors.get(sn);
                if(data != null){
                    jo.put("image",data.get("image"));
                    jo.put("positionSign",data.get("positionSign"));
                }
                //将设备序列号分解为系列号和通道号
                String[] sensorNoAndChannelNo = sn.split(":");
                String channelNo = sensorNoAndChannelNo.length >=2 ? sensorNoAndChannelNo[1] : "1";
                jo.put("sensorNo",sensorNoAndChannelNo[0]);
                jo.put("channelNo",channelNo);
                String alarmDuration = "";
                Date alarmTime = jo.getDate("alarmTime");
                Date restoreTime = jo.getDate("restoreTime");
                if(restoreTime != null){
                    alarmDuration = DateUtil.getTimeDuration(alarmTime,restoreTime);
                }
                jo.put("alarmDuration",alarmDuration);
            }
        }
        return new TableResultResponse(searchHits.getTotalHits(),list);
    }

    public TableResultResponse listHistoryEvent(String tenantId,Integer page,Integer limit,String sensorNo){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();//组合查询
        boolQueryBuilder.filter(QueryBuilders.matchQuery("tenantId", tenantId));
        boolQueryBuilder.filter(QueryBuilders.matchQuery("sensorNo",sensorNo));
        QueryBuilder isRestoreQuery = QueryBuilders.existsQuery("restoreTime");
        boolQueryBuilder.must(isRestoreQuery);

        int from = limit * ( page - 1);
        SearchResponse response = transportClient.prepareSearch(Constants.AnalysisSolution.FIRE.getAbnormalEsIndex())
                .setQuery(boolQueryBuilder).setFrom(from).setSize(limit).addSort("alarmTime",SortOrder.DESC)
                .execute().actionGet();
        List<JSONObject> list = new ArrayList<>();
        SearchHits searchHits =  response.getHits();
        List<String> sensorNos = Lists.newArrayList();
        for (SearchHit searchHit : searchHits) {
            String hit = searchHit.getSourceAsString();
            JSONObject jsonObject = JSONObject.parseObject(hit);
            jsonObject.put("id",searchHit.getId());
            sensorNos.add(jsonObject.getString("sensorNo"));
            list.add(jsonObject);
        }
        if(!list.isEmpty()){
            Map<String,Map<String,Object>> sensors = deviceVideoGroupBiz.queryBySensorNos(sensorNos);
            for(JSONObject jo : list){
                String sn = jo.getString("sensorNo");
                Map<String,Object> data = sensors.get(sn);
                if(data != null){
                    jo.put("image",data.get("image"));
                    jo.put("positionSign",data.get("positionSign"));
                }
                //将设备序列号分解为系列号和通道号
                String[] sensorNoAndChannelNo = sn.split(":");
                String channelNo = sensorNoAndChannelNo.length >=2 ? sensorNoAndChannelNo[1] : "1";
                jo.put("sensorNo",sensorNoAndChannelNo[0]);
                jo.put("channelNo",channelNo);
            }
        }
        return new TableResultResponse(searchHits.getTotalHits(),list);
    }


    /**
     * 保存异常信息
     * @param solution
     * @param abnormalData
     */
    public void saveAbnormal(Constants.AnalysisSolution solution,DeviceVideoAbnormalData abnormalData){
        String lastAlarmTypes = (String)redisTemplate.opsForHash().get(REDIS_ABNORMAL_HASH,abnormalData.getSensorNo());
        //需重新定义火警的key,根据key设置过期时间

        //redisTemplate.opsForHash().put("device:abnormals:fire-202480707:1","a-001",JSONArray.toJSONString("火警"));
        //redisTemplate.expire("device:abnormals:fire-202480707:1", 10, TimeUnit.SECONDS);
        String key="device:abnormal:fire_"+abnormalData.getSensorNo();
        redisTemplate.opsForHash().put(key,abnormalData.getSensorNo(),JSONArray.toJSONString("火警"));
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        List<String> alarmTypes = new ArrayList<>();
        if(StringUtils.isNotBlank(lastAlarmTypes)){
            alarmTypes = JSONArray.parseArray(lastAlarmTypes).toJavaList(String.class);
        }
        //上次是异常数据且与本次异常相同
        if(alarmTypes.contains(abnormalData.getAlarmType())){
            //获取当前设备的最后一次异常记录
            Map<String,Object> matches = Maps.newHashMap();
            matches.put("sensorNo",abnormalData.getSensorNo());
            matches.put("alarmType",abnormalData.getAlarmType());
            List<String> abnormalDataList = esTransportUtil.queryMatch(solution.getAbnormalEsIndex(),solution.getCode(),matches,"alarmTime",false,1,1);
           log.info("从es获取数据：{}",JSONObject.toJSON(abnormalDataList));
            DeviceVideoAbnormalData lastAbnormalData = null;
            if(abnormalDataList != null && !abnormalDataList.isEmpty()){
                lastAbnormalData = JSONObject.parseObject(abnormalDataList.get(0),DeviceVideoAbnormalData.class);
            }
            //增加该设备最后一条异常记录的图片,并修改异常数据记录
            if(lastAbnormalData.getPictures() != null && abnormalData.getPictures() != null){
                lastAbnormalData.getPictures().addAll(abnormalData.getPictures());
            }
            if(lastAbnormalData.getAnalysisDataIds() != null && abnormalData.getAnalysisDataIds() != null){
                lastAbnormalData.getAnalysisDataIds().addAll(abnormalData.getAnalysisDataIds());
            }
            lastAbnormalData.setUpdateTime(abnormalData.getAlarmTime());
            log.info("更新异常数据：{}，参数：{}----{}---{}", JSONObject.toJSONString(lastAbnormalData),solution.getAbnormalEsIndex(),solution.getCode(),lastAbnormalData.getId());
            esTransportUtil.updateDocument(solution.getAbnormalEsIndex(),solution.getCode(),lastAbnormalData.getId(),lastAbnormalData);
            log.info("更新完成");
        }else{
            //将异常信息保存到Redis用于异常折叠
            alarmTypes.add(abnormalData.getAlarmType());
            redisTemplate.opsForHash().put(REDIS_ABNORMAL_HASH,abnormalData.getSensorNo(),JSONArray.toJSONString(alarmTypes));
            //增加异常记录
            esTransportUtil.addDocument(solution.getAbnormalEsIndex(),solution.getCode(),UUIDUtils.generateUuid(),abnormalData);
        }

        //发送异常MQ通知
        Map<String,Object> mqMsg = Maps.newHashMap();
        mqMsg.put("type","video");
        mqMsg.put("data",abnormalData.toMap());
        log.info("发送异常MQ消息:{}",mqMsg);
        amqpTemplate.convertAndSend(EXCHANGE_NAME,ROUTING_KEY,JSONObject.toJSONString(mqMsg));
    }


    /**
     * 如果alarmType为空，则恢复所有异常，否则恢复指定异常
     * @param sensorNo
     * @param solution
     * @param alarmType
     */
    public void restoreAbnormal(String sensorNo,Constants.AnalysisSolution solution,String alarmType){
        //将历史报警信息恢复正常
        Date time = new Date();
        DeviceVideoAbnormalData abnormalData = null;
        List<JSONObject> abnormalDataList =  queryByPage(null,1,1000, false,
                alarmType,null,null,null,sensorNo,null)
                .getData().getRows();
        for(JSONObject abs : abnormalDataList){
            abs.put("sensorNo",abs.getString("sensorNo") + ":" + abs.getString("channelNo"));
            abnormalData = abs.toJavaObject(DeviceVideoAbnormalData.class);
            abnormalData.setRestoreTime(time);
            abnormalData.setUpdateTime(time);
            esTransportUtil.updateDocument(solution.getAbnormalEsIndex(),solution.getCode(),abnormalData.getId(),abnormalData);
        }
        if(alarmType == null){
            redisTemplate.opsForHash().delete(REDIS_ABNORMAL_HASH,sensorNo);
        }else {
            String lastAlarmTypes = (String)redisTemplate.opsForHash().get(REDIS_ABNORMAL_HASH,sensorNo);
            List<String> lastAlarmTypeList = new ArrayList<>();
            if(StringUtils.isNotBlank(lastAlarmTypes)){
                lastAlarmTypeList = JSONArray.parseArray(lastAlarmTypes).toJavaList(String.class);
            }
            lastAlarmTypeList.remove(alarmType);
            redisTemplate.opsForHash().put(REDIS_ABNORMAL_HASH,sensorNo,JSONObject.toJSONString(lastAlarmTypeList));
        }
    }



}
