package cn.turing.firecontrol.device.business;


import cn.turing.firecontrol.common.validator.ValidatorUtils;
import cn.turing.firecontrol.device.biz.DeviceBuildingBiz;
import cn.turing.firecontrol.device.biz.DeviceSensorBiz;
import cn.turing.firecontrol.device.entity.DeviceBuilding;
import cn.turing.firecontrol.device.entity.DeviceSensor;
import cn.turing.firecontrol.device.util.Constant;
import cn.turing.firecontrol.device.util.ESTransportUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.AggregatorFactories;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BusinessImpl implements BusinessI {

    @Autowired
    private ESTransportUtil esTransportUtil;

    @Autowired
    private DeviceBuildingBiz deviceBuildingBiz;
    @Autowired
    private DeviceSensorBiz deviceSensorBiz;
    private static final Logger log = LoggerFactory.getLogger(BusinessI.class);

    /**
     * 返回一天的平均值
     *
     * @param json
     * @return
     * @throws ParseException
     */
    @Override
    public List<Map<String, Object>> querySensorData(JSONObject json) throws ParseException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (ValidatorUtils.hasAnyBlank(json.get("mac"), json.get("sensorType"), json.get("startTime"), json.get("endTime"))) {
            throw new RuntimeException("deviceid or codeName or startTime or end Time is error! ");
        }
        String deviceid = json.get("mac").toString().trim();
        String codeName = json.get("sensorType").toString().trim();
        String startTime = parseStart(json.get("startTime").toString().trim());
        String endTime = parseEnd(json.get("endTime").toString().trim());
        log.info("deviceid---->" + deviceid);
        log.info("codeName---->" + codeName);
        log.info("startTime--->" + startTime);
        log.info("endTime--->" + endTime);
        SearchResponse searchResponse = null;
        try {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            //筛选编号
            queryBuilder.must(QueryBuilders.matchQuery("deviceid", deviceid));
            //筛选时间
            RangeQueryBuilder rangequerybuilder = QueryBuilders
                    .rangeQuery("uploadtime").from(startTime).to(endTime);//.format("yyyy-MMdd HH:ss:mm")
            queryBuilder.must(rangequerybuilder);
            //根据测点的代号查询相应的字段
            AvgAggregationBuilder teamAgg1 = AggregationBuilders.avg("TEM_AS").field(codeName + ".alarmValue");
            DateHistogramAggregationBuilder teamAgg2 = AggregationBuilders.dateHistogram("lastupdatetime_Agg").field("uploadtime").dateHistogramInterval(DateHistogramInterval.DAY).subAggregation(teamAgg1).format("yyyy-MM-dd").offset("-8h");
            FilterAggregationBuilder teamAgg4 = AggregationBuilders.filter("range", queryBuilder);
            teamAgg4.subAggregation(teamAgg2);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(queryBuilder).size(10000).aggregation(teamAgg4);
            searchResponse = esTransportUtil.query(Constant.ESConstant.ES_INDEX_SENSOR, null, searchSourceBuilder);
            Map aggMap = searchResponse.getAggregations().asMap();
            InternalFilter teamAgg = (InternalFilter) aggMap.get("range");
            JSONObject jsonObject = JSONObject.parseObject(teamAgg.toString());
            JSONObject json2 = (JSONObject) jsonObject.get("range");
            JSONObject json3 = (JSONObject) json2.get("lastupdatetime_Agg");
            JSONArray array = JSONArray.parseArray(json3.get("buckets").toString());
            log.info("array size--->" + array.size());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DecimalFormat d = new DecimalFormat("#.00");
            //保留两位小数后四舍五入
            for (int i = 0; i < array.size(); i++) {
                JSONObject jo = array.getJSONObject(i);
                if (Integer.parseInt(jo.get("doc_count").toString()) != 0) {
                    JSONObject jsonData = (JSONObject) jo.get("TEM_AS");
                    Map fix = new HashMap();
                    //分组减8小时排序后，需要增加一天的显示
                    Date date = new Date(simpleDateFormat.parse(jo.get("key_as_string").toString()).getTime() + 1000 * 60 * 60 * 24);
                    if (jsonData.get("value") != null) {
//                        fix.put("temperature",new BigDecimal(jsonData.get("value").toString()).floatValue());
                        double num = Double.parseDouble(d.format(new BigDecimal(jsonData.get("value").toString()).floatValue()));
                        fix.put("temperature", (double) Math.round(num * 10) / 10);
                        fix.put("inputdate", simpleDateFormat.format(date));//时间
                        list.add(fix);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 返回一天的所有值
     *
     * @param json
     * @return
     * @throws ParseException
     */
    public JSONArray querySensorDataTest(JSONObject json) throws ParseException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (ValidatorUtils.hasAnyBlank(json.get("mac"), json.get("sensorType"), json.get("startTime"), json.get("endTime"))) {
            throw new RuntimeException("deviceid or codeName or startTime or end Time is error! ");
        }
        String deviceid = json.get("mac").toString().trim();
        String codeName = json.get("sensorType").toString().trim();
        String startTime = parseStart(json.get("startTime").toString().trim());
        String endTime = parseEnd(json.get("endTime").toString().trim());
        log.info("deviceid---->" + deviceid);
        log.info("codeName---->" + codeName);
        log.info("startTime--->" + startTime);
        log.info("endTime--->" + endTime);
        SearchResponse searchResponse = null;
        try {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            //筛选编号
            queryBuilder.must(QueryBuilders.matchQuery("deviceid", deviceid));
//            queryBuilder.must(QueryBuilders.termQuery("deviceid", deviceid));
            //筛选时间
            RangeQueryBuilder rangequerybuilder = QueryBuilders
                    .rangeQuery("uploadtime").from(startTime).to(endTime);//.format("yyyy-MMdd HH:ss:mm")
            queryBuilder.must(rangequerybuilder);
            //根据测点的代号查询相应的字段
            SortBuilder sortBuilder = SortBuilders.fieldSort("uploadtime").unmappedType("date").order(SortOrder.ASC);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(queryBuilder).sort(sortBuilder);
            searchSourceBuilder.from(0).size(Integer.MAX_VALUE);
            searchResponse = esTransportUtil.query(Constant.ESConstant.ES_INDEX_SENSOR, null, searchSourceBuilder);
            SearchHits searchHits = searchResponse.getHits();
            JSONArray jsonArray = new JSONArray();
            DecimalFormat d = new DecimalFormat("#.00");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
            for (SearchHit hit : searchHits) {
                String source = hit.getSourceAsString();
                JSONObject jasonObject = JSONObject.parseObject(source);
                JSONObject temp = new JSONObject();
                if (jasonObject.get(codeName) != null && jasonObject.get("uploadtime") != null && ((JSONObject) jasonObject.get(codeName)).get("alarmValue") != null && ((JSONObject) jasonObject.get(codeName)).get("alarmValue").toString() != null) {
                    String time = jasonObject.get("uploadtime").toString();
                    double num = Double.parseDouble(d.format(new BigDecimal(((JSONObject) jasonObject.get(codeName)).get("alarmValue").toString()).floatValue()));
                    temp.put("temperature", (double) Math.round(num * 10) / 10);
                    temp.put("inputdate", simpleDateFormat2.format(simpleDateFormat1.parse(time)));
                    jsonArray.add(temp);
                }
            }
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }

    /**
     * 室外传感器返回一个星期每天的数据
     *
     * @param json
     * @return
     * @throws ParseException
     */
    public JSONArray querySensorDataDay(JSONObject json) throws ParseException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (ValidatorUtils.hasAnyBlank(json.get("mac"), json.get("sensorType"), json.get("startTime"), json.get("endTime"))) {
            throw new RuntimeException("deviceid or codeName or startTime or end Time is error! ");
        }
        String deviceid = json.get("mac").toString().trim();
        String codeName = json.get("sensorType").toString().trim();
        String startTime = parseStart(json.get("startTime").toString().trim());
        String endTime = parseEnd(json.get("endTime").toString().trim());
        log.info("deviceid---->" + deviceid);
        log.info("codeName---->" + codeName);
        log.info("startTime--->" + startTime);
        log.info("endTime--->" + endTime);
        SearchResponse searchResponse = null;
        try {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            //筛选编号
            queryBuilder.must(QueryBuilders.matchQuery("deviceid", deviceid));
//            queryBuilder.must(QueryBuilders.termQuery("deviceid", deviceid));
            //筛选时间
            RangeQueryBuilder rangequerybuilder = QueryBuilders
                    .rangeQuery("uploadtime").from(startTime).to(endTime);//.format("yyyy-MMdd HH:ss:mm")
            queryBuilder.must(rangequerybuilder);
            //根据测点的代号查询相应的字段
            SortBuilder sortBuilder = SortBuilders.fieldSort("uploadtime").unmappedType("date").order(SortOrder.ASC);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(queryBuilder).size(10000).sort(sortBuilder);
            searchResponse = esTransportUtil.query(Constant.ESConstant.ES_INDEX_SENSOR, null, searchSourceBuilder);
            SearchHits searchHits = searchResponse.getHits();
            JSONArray jsonArray = new JSONArray();
            DecimalFormat d = new DecimalFormat("#.00");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
            for (SearchHit hit : searchHits) {
                JSONObject jasonObject = JSONObject.parseObject(hit.getSourceAsString());
                JSONObject temp = new JSONObject();
                if (jasonObject.get(codeName) != null && jasonObject.get("uploadtime") != null && ((JSONObject) jasonObject.get(codeName)).get("alarmValue") != null && ((JSONObject) jasonObject.get(codeName)).get("alarmValue").toString() != null) {
                    String time = jasonObject.get("uploadtime").toString();
                    double num = Double.parseDouble(d.format(new BigDecimal(((JSONObject) jasonObject.get(codeName)).get("alarmValue").toString()).floatValue()));
                    temp.put("temperature", (double) Math.round(num * 10) / 10);
                    temp.put("inputdate", simpleDateFormat2.format(simpleDateFormat1.parse(time)));
                    jsonArray.add(temp);
                }
            }
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }


    /**
     * 消防主机传感器返回一天所有的异常数据
     *
     * @param json
     * @return
     * @throws ParseException
     */
    public JSONArray queryMianSensorData(JSONObject json) throws ParseException {
        String serverIp = json.get("serverIp").toString().trim();
        String port = json.get("port").toString().trim();
        String loopNo = json.get("loopNo").toString().trim();
        String address = json.get("address").toString().trim();
        String codeName = "alarm";
        String startTime = parseStart(json.get("startTime").toString().trim());
        String endTime = parseEnd(json.get("endTime").toString().trim());
        log.info("serverIp---->" + serverIp);
        log.info("port---->" + port);
        log.info("loopNo---->" + loopNo);
        log.info("address---->" + address);
        log.info("startTime--->" + startTime);
        log.info("endTime--->" + endTime);
        SearchResponse searchResponse = null;
        JSONArray jsonArray = new JSONArray();
        try {
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            //服务器ip
            queryBuilder.must(QueryBuilders.matchPhraseQuery("ip", serverIp));
            //端口
            queryBuilder.must(QueryBuilders.matchPhraseQuery("port", port));
            //回路
            queryBuilder.must(QueryBuilders.matchPhraseQuery("loopNo", loopNo));
            //地址
            queryBuilder.must(QueryBuilders.matchPhraseQuery("localtionNo", address));
            //筛选时间
            RangeQueryBuilder rangequerybuilder = QueryBuilders
                    .rangeQuery("uploadtime").from(startTime).to(endTime);//.format("yyyy-MMdd HH:ss:mm")
            queryBuilder.must(rangequerybuilder);
            //根据测点的代号查询相应的字段
            SortBuilder sortBuilder = SortBuilders.fieldSort("uploadtime").unmappedType("date").order(SortOrder.ASC);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(queryBuilder).size(10000).sort(sortBuilder);
            searchResponse = esTransportUtil.query(Constant.ESConstant.ES_INDEX_SENSOR, null, searchSourceBuilder);
            SearchHits searchHits = searchResponse.getHits();
            DecimalFormat d = new DecimalFormat("#.00");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
            for (SearchHit hit : searchHits) {
                JSONObject jasonObject = JSONObject.parseObject(hit.getSourceAsString());
                JSONObject temp = new JSONObject();
                try {
                    if (jasonObject.get(codeName) != null && jasonObject.get("uploadtime") != null && ((JSONObject) jasonObject.get(codeName)).get("alarmValue") != null && ((JSONObject) jasonObject.get(codeName)).get("alarmValue").toString() != null) {
                        //判断状态是否为正常
                        String status = ((JSONObject) jasonObject.get(codeName)).get("alarmStatus").toString();
                        if (!"2".equals(status)) {
                            String time = jasonObject.get("uploadtime").toString();
                            double num = Double.parseDouble(d.format(new BigDecimal(((JSONObject) jasonObject.get(codeName)).get("alarmValue").toString()).floatValue()));
                            temp.put("temperature", (double) Math.round(num * 10) / 10);
                            temp.put("inputdate", simpleDateFormat2.format(simpleDateFormat1.parse(time)));
                            jsonArray.add(temp);
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            return jsonArray;
        } catch (Exception e) {
            return jsonArray;
        }

    }

    //查询各建筑物电量
    @Override
    public List<Map<String, Object>> queryElectricityByBuilding(Date startTime, Date endTime, String tenantId) {
        //获取建筑物
        List<DeviceBuilding> buildings = deviceBuildingBiz.getAll(null);
        List<Integer> buildingIds = new ArrayList<>();
        Map<Integer, String> buildingNames = new HashMap<>();
        for (DeviceBuilding db : buildings) {
            buildingIds.add(db.getId());
            buildingNames.put(db.getId(), db.getBName());
        }
        List<DeviceSensor> sensors = deviceSensorBiz.queryByBuildings(buildingIds);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> data = null;
        String DEVICE_SERIES = "电气火灾探测器";
        for (DeviceSensor sensor : sensors) {
            //只处理电气火灾探测器
            if (!DEVICE_SERIES.equals(sensor.getEquipmentType())) {
                continue;
            }
            data = querySensorData(startTime, endTime, sensor.getSensorNo());
            data.put("sensorNo", sensor.getSensorNo());
            data.put("buildingName", buildingNames.get(sensor.getBuildingId()));
            list.add(data);
        }
        return list;
    }

    /**
     * 获取设备的最后一条实时数据
     *
     * @param sensorNo
     * @return
     */
    @Override
    public JSONObject queryLastData(String sensorNo) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.matchQuery("deviceid", sensorNo));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(boolQuery).size(1).sort("uploadtime", SortOrder.ASC);
        SearchResponse searchResponse = esTransportUtil.query(Constant.ESConstant.ES_INDEX_SENSOR, null, searchSourceBuilder);
        Iterator<SearchHit> iterator = searchResponse.getHits().iterator();
        if (!iterator.hasNext()) {
            return null;
        } else {
            return JSONObject.parseObject(iterator.next().getSourceAsString());
        }
    }

    private Map<String, Object> querySensorData(Date startTime, Date endTime, String sensorNo) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        boolQuery.filter(QueryBuilders.rangeQuery("uploadtime").format(pattern).
                gte(dateFormat.format(startTime)).lte(dateFormat.format(endTime)));
        boolQuery.must(QueryBuilders.matchQuery("deviceid", sensorNo));
        AggregationBuilder dateAgg = AggregationBuilders.dateHistogram("temp_per_hour")
                .dateHistogramInterval(DateHistogramInterval.HOUR).field("uploadtime");
        //查询温度1
        AggregatorFactories.Builder aggBuilder = new AggregatorFactories.Builder();
        aggBuilder.addAggregator(AggregationBuilders.max("max_temp1").field("TEMP1.alarmValue"));
        aggBuilder.addAggregator(AggregationBuilders.max("max_temp2").field("TEMP2.alarmValue"));
        aggBuilder.addAggregator(AggregationBuilders.max("max_temp3").field("TEMP3.alarmValue"));
        dateAgg.subAggregations(aggBuilder);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(boolQuery).size(10000)
                .sort("uploadtime", SortOrder.ASC).aggregation(dateAgg);
        SearchResponse searchResponse1 = esTransportUtil.query(Constant.ESConstant.ES_INDEX_SENSOR, null, searchSourceBuilder);

        InternalDateHistogram dateHistogram = searchResponse1.getAggregations().get("temp_per_hour");
        Date time = null;
        double temp = 0.0;
        List<Date> dates = new ArrayList<>();
        Map<Date, Double> map = new HashMap<>();
        for (InternalDateHistogram.Bucket b : dateHistogram.getBuckets()) {
            time = ((DateTime) b.getKey()).toDate();
            Max max1 = b.getAggregations().get("max_temp1");
            Max max2 = b.getAggregations().get("max_temp2");
            Max max3 = b.getAggregations().get("max_temp3");
            temp = Double.max(Double.max(max1.getValue(), max2.getValue()), max3.getValue());
            map.put(time, Double.isInfinite(temp) ? 0.0 : (double) Math.round(temp * 10) / 10);
            dates.add(time);
        }
        List<Double> temps = new ArrayList<>();
        for (Date d : dates) {
            temps.add(map.get(d));
        }
        Map<String, Object> res = new HashMap<>();
        res.put("date", dates);
        res.put("temp", temps);
        return res;
    }

    public String parseStart(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long startLong = simpleDateFormat.parse((str + " 00:00:00")).getTime() - 1000 * 60 * 60 * 8;
        String startTime = simpleDateFormat.format(new Date(startLong));
        startTime = startTime.replace(" ", "T") + ".000Z";
        return startTime;
    }

    public String parseEnd(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long startLong = simpleDateFormat.parse((str + " 23:59:59")).getTime() - 1000 * 60 * 60 * 8;
        String startTime = simpleDateFormat.format(new Date(startLong));
        startTime = startTime.replace(" ", "T") + ".000Z";
        return startTime;
    }


}



