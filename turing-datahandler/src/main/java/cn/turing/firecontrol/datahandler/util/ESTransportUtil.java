package cn.turing.firecontrol.datahandler.util;

import cn.turing.firecontrol.datahandler.entity.ElasticSearchEntity;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2019/02/15 15:47
 *
 * @Description TODO
 * @Version V1.0
 */
@Slf4j
public class ESTransportUtil {

    private TransportClient transportClient;

    public ESTransportUtil(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    //判断索引是否存在
    public boolean isIndexExist(String index) {
        return transportClient.admin().indices().prepareExists(index).execute().actionGet().isExists();
    }

    //删除索引
    public boolean deleteIndex(String index) {
        return isIndexExist(index) && transportClient.admin().indices().prepareDelete(index).execute().actionGet().isAcknowledged();
    }

    //新增索引
    public boolean addIndex(String index) {
        return !isIndexExist(index) && transportClient.admin().indices().prepareCreate(index).execute().actionGet().isAcknowledged();
    }

    //判断inde下指定type是否存在
    public boolean isTypeExist(String index, String type) {
        return isIndexExist(index) && transportClient.admin().indices().prepareTypesExists(index).setTypes(type).execute().actionGet().isExists();
    }

    /**
     * 新增类型
     * @param index
     * @param type
     * @param fields key为字段名，Value为字段类型
     * @return
     * @throws IOException
     */
    public boolean addIndexAndType(String index,String type, Map<String,String> fields){
        // 创建索引映射,相当于创建数据库中的表操作
        CreateIndexRequestBuilder cib = transportClient.admin().indices().prepareCreate(index);
        XContentBuilder mapping = null;
        try {
            mapping = XContentFactory.jsonBuilder().startObject().startObject("properties");// 设置自定义字段
            Set<Map.Entry<String, String>> entries = fields.entrySet();
            for (Map.Entry<String, String> e : entries) {
                mapping.startObject(e.getKey()).field("type", e.getValue());
                if ("date".equals(e.getValue())) {
                    mapping.field("format", "yyyy-MM-dd HH:mm:ss");
                }
                mapping.endObject();
            }
            mapping.endObject().endObject();
        }catch (IOException e){
            log.error("创建索引失败",e);
            throw new RuntimeException("创建索引失败",e);
        }
        cib.addMapping(type, mapping);
        return cib.execute().actionGet().isAcknowledged();
    }

    public boolean addIndexAndType(String index,String type, Class clazz){
        // 创建索引映射,相当于创建数据库中的表操作
        CreateIndexRequestBuilder cib = transportClient.admin().indices().prepareCreate(index);
        XContentBuilder mapping = null;
        try {
            mapping = XContentFactory.jsonBuilder().startObject().startObject("properties");// 设置自定义字段
            Field[] fields = clazz.getDeclaredFields();
            String fieldType = null;
            for(Field f : fields) {
                fieldType = toEsType(f.getType());
                if(fieldType == null){
                    continue;
                }
                mapping.startObject(f.getName()).field("type", fieldType);
                if ("date".equals(fieldType)) {
                    mapping.field("format", "yyyy-MM-dd HH:mm:ss");
                }
                mapping.endObject();
            }
            mapping.endObject().endObject();
        }catch (IOException e){
            log.error("创建索引失败",e);
            throw new RuntimeException("创建索引失败",e);
        }
        cib.addMapping(type, mapping);
        return cib.execute().actionGet().isAcknowledged();
    }


    private String toEsType(Class clazz){
        String className = clazz.getSimpleName();
        if("String".equals(className)){
            return "keyword";
        }
        if("List".equals(className)){
            return "text";
        }
        return className.toLowerCase();
    }


    //新增文档
    public long addDocument(String index, String type, String id,  ElasticSearchEntity entity){
        try {
            Map<String,Object> map = entity.toMap();
            IndexResponse response = transportClient.prepareIndex(index,type,id).setSource(map).get();
            return response.getVersion();
        } catch (Exception e) {
            log.error("插入文档失败",e);
            throw new RuntimeException("插入文档失败",e);
        }
    }

    //删除文档
    public String deleteDocument(String index,String type,String id) {
        return transportClient.prepareDelete(index, type, id).get().getId();
    }


    //更新文档
    public String updateDocument(String index, String type, String id, ElasticSearchEntity entity){
        try {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(index);
            updateRequest.type(type);
            updateRequest.id(id);
            updateRequest.doc(entity.toMap());
            UpdateResponse response = transportClient.update(updateRequest).get();
            return response.getId();
        } catch (Exception e) {
            log.error("更新文档失败",e);
            throw new RuntimeException("更新文档失败",e);
        }
    }

    //依据id查询
    public String searchById(String index,String type,String id) {
        GetResponse response = transportClient.prepareGet(index, type, id).execute().actionGet();
        String jsonStr = response.getSourceAsString();
        return jsonStr;
    }

    //查询索引下所有数据
    public List<String> queryAll(String index) {
        QueryBuilder query = QueryBuilders.matchAllQuery();
        SearchResponse response = transportClient.prepareSearch(index).setQuery(query).execute().actionGet();
        List<String> list = new ArrayList<>();
        for (SearchHit searchHit : response.getHits()) {
            list.add(searchHit.getSourceAsString());
        }
        return list;
    }

    //查询类型下所有数据
    public List<String> queryAllInType(String index, String type) {
        SearchResponse response = transportClient.prepareSearch(index).setTypes(type).execute().actionGet();
        List<String> list = new ArrayList<>();
        for (SearchHit searchHit : response.getHits()) {
            list.add(searchHit.getSourceAsString());
        }
        return list;
    }

    //查询索引下所有数据
    public List<String> queryMatch(String index,String type, Map<String, Object> matchs,String orderBy, Boolean isAsc, int pageNum, int pageSize) {
        SearchRequestBuilder request = transportClient.prepareSearch(index).setTypes(type);
        QueryBuilder query = null;
        if(matchs != null){
            for(Map.Entry<String,Object> e : matchs.entrySet()){
                query = QueryBuilders.matchQuery(e.getKey(),e.getValue());
                request.setQuery(query);
            }
        }
        int from = pageSize * ( pageNum - 1);
        request.setFrom(from).setSize(pageSize);
        if(orderBy != null){
            if(isAsc){
                request.addSort(orderBy,SortOrder.ASC);
            }else {
                request.addSort(orderBy,SortOrder.DESC);
            }
        }
        SearchResponse response = request.execute().actionGet();
        List<String> list = new ArrayList<>();
        for (SearchHit searchHit : response.getHits()) {
            String hit = searchHit.getSourceAsString();
            JSONObject jsonObject = JSONObject.parseObject(hit);
            jsonObject.put("id",searchHit.getId());
            list.add(jsonObject.toJSONString());
        }
        return list;
    }




    public static void main(String[] args) throws IOException {

    }
}
