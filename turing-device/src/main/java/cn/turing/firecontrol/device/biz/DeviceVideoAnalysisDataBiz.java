package cn.turing.firecontrol.device.biz;

import cn.turing.firecontrol.device.util.ESTransportUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * Created on 2019/03/01 11:26
 *
 * @Description
 * @Version V1.0
 */
@Service
public class DeviceVideoAnalysisDataBiz {

    @Autowired
    private ESTransportUtil esTransportUtil;
    @Autowired
    private TransportClient transportClient;

    /**
     * 查询指定索引和类型的分站点数据量,最后一条的时间
     * @param index
     * @param type
     * @return
     */
    public Map<String,Object> countData(String index, String type, String tenantId){
        SearchRequestBuilder request = transportClient.prepareSearch(index).setTypes(type);
        QueryBuilder query = QueryBuilders.matchQuery("tenantId",tenantId);
        SearchResponse response = request.addSort("analysisTime",SortOrder.DESC).setFrom(0).setSize(1).setQuery(query).execute().actionGet();
        List<JSONObject> list = new ArrayList<>();
        Map<String,Object> result = Maps.newHashMap();
        SearchHits hits = response.getHits();
        for (SearchHit searchHit : hits) {
            String hit = searchHit.getSourceAsString();
            JSONObject jsonObject = JSONObject.parseObject(hit);
            list.add(jsonObject);
        }
        if(list.isEmpty()){
            result.put("count",0);
            result.put("time",new Date());
        }else {
            result.put("count",hits.getTotalHits());
            result.put("time",list.get(0).getDate("analysisTime"));
        }
        return result;
    }






}
