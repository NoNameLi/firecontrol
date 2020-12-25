package cn.turing.firecontrol.datahandler.biz;

import cn.turing.firecontrol.common.msg.TableResultResponse;
import cn.turing.firecontrol.datahandler.entity.ElasticSearchEntity;
import cn.turing.firecontrol.datahandler.entity.SensorAbnormal;
import cn.turing.firecontrol.datahandler.util.ESTransportUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created on 2019/03/11 10:49
 *
 * @Description TODO
 * @Version V1.0
 */
@Service
@Slf4j
public class SensorAbnormalBiz {

    @Autowired
    private ESTransportUtil esTransportUtil;
    //    @Autowired
//    private TransportClient transportClient;
    @Value("${tmc.config.elasicSearch.abnormal.index}")
    private String ABNORMAL_INDEX;
    @Value("${tmc.config.elasicSearch.abnormal.type.firedoor}")
    private String ABNORMAL_TYPE_FIREDOOR;

    /**
     * 查询租户的所有报警类型
     *
     * @param tenantId
     * @return
     */
    public List<String> queryAlarmTypes(String tenantId) {
        AggregationBuilder agg = AggregationBuilders.terms("alarmType").field("alarmType.keyword").size(Integer.MAX_VALUE);
        QueryBuilder matchQuery = QueryBuilders.matchQuery("tenantId", tenantId);
        SearchResponse response = esTransportUtil.queryAllInType(ABNORMAL_INDEX, ABNORMAL_TYPE_FIREDOOR, matchQuery, agg);
        List<String> types = Lists.newArrayList();
        Terms aggregations = response.getAggregations().get("alarmType");
        for (Terms.Bucket b : aggregations.getBuckets()) {
            types.add(b.getKeyAsString());
        }
        return types;
    }


    /**
     * 分页按条件查询异常
     *
     * @param page
     * @param limit
     * @param abnormalType
     * @param isHandle
     * @param alarmType
     * @param startAlarmTime
     * @param endAlarmTime
     * @param buildingName
     * @param sensorNo
     * @param floor
     * @param manufacturer
     * @param deviceSeries
     * @param model
     * @param handlePersonName
     * @param startRestoreTime
     * @param endRestoreTime
     * @return
     */
    public TableResultResponse queryPage(Integer page, Integer limit, Integer abnormalType, Boolean isHandle,
                                         String alarmType, Date startAlarmTime, Date endAlarmTime, String buildingName, String sensorNo,
                                         Integer floor, String manufacturer, String deviceSeries, String model,
                                         String handlePersonName, Date startRestoreTime, Date endRestoreTime) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (abnormalType != null) {
            boolQuery.filter(QueryBuilders.matchQuery("abnormalType", abnormalType));
        }
        if (isHandle != null) {
            boolQuery.filter(QueryBuilders.matchQuery("isHandle", isHandle));
        }
        if (alarmType != null) {
            boolQuery.filter(QueryBuilders.matchQuery("alarmType", alarmType));
        }
        if (startAlarmTime != null && endAlarmTime != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("alarmTime").gte(ElasticSearchEntity.dateFormat.format(startAlarmTime)).
                    lte(ElasticSearchEntity.dateFormat.format(startAlarmTime)));
        }
        if (buildingName != null) {
            boolQuery.filter(QueryBuilders.matchQuery("buildingName", buildingName));
        }
        if (sensorNo != null) {
            boolQuery.filter(QueryBuilders.wildcardQuery("sensorNo", "*" + sensorNo + "*"));
        }
        if (floor != null) {
            boolQuery.filter(QueryBuilders.matchQuery("floor", floor));
        }
        if (manufacturer != null) {
            boolQuery.filter(QueryBuilders.matchQuery("manufacturer", manufacturer));
        }
        if (deviceSeries != null) {
            boolQuery.filter(QueryBuilders.matchQuery("deviceSeries", deviceSeries));
        }
        if (model != null) {
            boolQuery.filter(QueryBuilders.matchQuery("model", model));
        }
        if (handlePersonName != null) {
            boolQuery.filter(QueryBuilders.wildcardQuery("handlePersonName", "*" + handlePersonName + "*"));
        }
        if (startRestoreTime != null && endRestoreTime != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("restoreTime").gte(ElasticSearchEntity.dateFormat.format(startRestoreTime))
                    .lte(ElasticSearchEntity.dateFormat.format(endRestoreTime)));
        }
        int from = (page - 1) * limit;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(boolQuery).from(from).size(limit)
                .sort("alarmTime.keyword", SortOrder.DESC);
        SearchResponse response = esTransportUtil.query(ABNORMAL_INDEX, ABNORMAL_TYPE_FIREDOOR, searchSourceBuilder);
        List<SensorAbnormal> abnormals = Lists.newArrayList();
        String json = null;
        SensorAbnormal sensorAbnormal = null;
        SearchHits hits = response.getHits();
        for (SearchHit h : hits) {
            json = h.getSourceAsString();
            sensorAbnormal = JSONObject.parseObject(json, SensorAbnormal.class);
            sensorAbnormal.setId(h.getId());
            abnormals.add(sensorAbnormal);
        }
        return new TableResultResponse(hits.getTotalHits().value, abnormals);
    }

    /**
     * 获取设备尚未恢复的异常
     */
    public List<SensorAbnormal> getNotRestores(String sensorNo) {
        return getNotRestores(sensorNo, null);
    }

    /**
     * 获取设备尚未恢复的指定异常
     */
    public List<SensorAbnormal> getNotRestores(String sensorNo, String measuringPoint) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.matchQuery("sensorNo", sensorNo));
        boolQuery.mustNot(QueryBuilders.existsQuery("restoreTime"));
        if (measuringPoint != null) {
            boolQuery.filter(QueryBuilders.matchQuery("measuringPoint", measuringPoint));
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(boolQuery).from(0).size(10000);
        SearchResponse response = esTransportUtil.query(ABNORMAL_INDEX, ABNORMAL_TYPE_FIREDOOR, searchSourceBuilder);
        List<SensorAbnormal> abnormals = Lists.newArrayList();
        String json = null;
        SensorAbnormal sensorAbnormal = null;
        SearchHits hits = response.getHits();
        for (SearchHit h : hits) {
            sensorAbnormal = JSONObject.parseObject(h.getSourceAsString(), SensorAbnormal.class);
            sensorAbnormal.setId(h.getId());
            abnormals.add(sensorAbnormal);
        }
        return abnormals;
    }


    /**
     * 处理异常
     *
     * @param id
     * @param handleResult
     */
    public void handleAbnormal(String id, Integer handleResult, String name) {
        String json = esTransportUtil.searchById(ABNORMAL_INDEX, ABNORMAL_TYPE_FIREDOOR, id);
        if (json == null) {
            throw new RuntimeException("ID不存在");
        }
        SensorAbnormal sensorAbnormal = JSONObject.parseObject(json, SensorAbnormal.class);
        sensorAbnormal.setIsHandle(true);
        sensorAbnormal.setHandlePersonName(name);
        sensorAbnormal.setHandleTime(new Date());
        sensorAbnormal.setHandleResult(handleResult);
        esTransportUtil.updateDocument(ABNORMAL_INDEX, ABNORMAL_TYPE_FIREDOOR, id, sensorAbnormal);
    }


    /**
     * 查询一段时间内的某一字段的分组统计情况
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private Map<String, Object> getCountByfield(String tenantId, String filedName, Date startTime, Date endTime) {
        AggregationBuilder agg = AggregationBuilders.terms(filedName).field(filedName).size(Integer.MAX_VALUE);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.matchQuery("tenantId", tenantId));
        boolQuery.filter(QueryBuilders.rangeQuery("alarmTime.keyword").gte(ElasticSearchEntity.dateFormat.format(startTime)).
                lte(ElasticSearchEntity.dateFormat.format(endTime)));
        SearchResponse response = esTransportUtil.queryAllInType(ABNORMAL_INDEX, ABNORMAL_TYPE_FIREDOOR, boolQuery, agg);
        Terms aggregations = response.getAggregations().get(filedName);
        Map<String, Object> res = Maps.newHashMap();
        for (Terms.Bucket b : aggregations.getBuckets()) {
            res.put(b.getKeyAsString(), b.getDocCount());
        }
        return res;
    }


    /**
     * 查询一段时间内的处理情况
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public Map<String, Object> getHandleStatus(String tenantId, Date startTime, Date endTime) {
        String fieldName = "isHandle";
        return getCountByfield(tenantId, fieldName, startTime, endTime);
    }


    /**
     * 查询一段时间内的各报警类型的分布情况
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public Map<String, Object> getAlarmTypeStatus(String tenantId, Date startTime, Date endTime) {
        String fieldName = "alarmType.keyword";
        return getCountByfield(tenantId, fieldName, startTime, endTime);
    }


}
