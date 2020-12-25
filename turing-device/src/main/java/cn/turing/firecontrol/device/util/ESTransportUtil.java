package cn.turing.firecontrol.device.util;

import cn.turing.firecontrol.device.entity.ElasticSearchEntity;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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

    private RestHighLevelClient client;

    private String alias = "";
    private Integer shards = 1;
    private Integer replicas = 1;

    public ESTransportUtil(RestHighLevelClient client, String alias, Integer shards, Integer replicas) {
        this.client = client;
        this.alias = alias;
        this.shards = shards;
        this.replicas = replicas;
    }

    //判断索引是否存在
    public boolean isIndexExist(String index) {
        GetRequest getRequest = new GetRequest(index);
        try {
            return client.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return false;
        }
    }

    //删除索引
    public boolean deleteIndex(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            log.error("删除错误，", e);
            return false;
        }
    }

    //新增索引
    public boolean addIndex(String index) {
        //索引名称
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(Settings.builder().put("index.number_of_shards", shards)
                .put("index.number_of_replicas", replicas));
        request.alias(new Alias(alias));
        try {
            client.indices().create(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            log.error("创建索引失败，", e);
            return false;
        }
    }

    //判断inde下指定type是否存在
    public boolean isTypeExist(String index, String type) {
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        try {
            return client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return false;
        }

    }

    /**
     * 新增类型
     *
     * @param index
     * @param type
     * @param fields key为字段名，Value为字段类型
     * @return
     * @throws IOException
     */
    public boolean addIndexAndType(String index, String type, Map<String, String> fields) {
        // 创建索引映射,相当于创建数据库中的表操作
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            XContentBuilder mapping = builder.startObject().startObject("properties");// 设置自定义字段
            Set<Map.Entry<String, String>> entries = fields.entrySet();
            for (Map.Entry<String, String> e : entries) {
                mapping.startObject(e.getKey()).field("type", e.getValue());
                if ("date".equals(e.getValue())) {
                    mapping.field("format", "yyyy-MM-dd HH:mm:ss");
                }
                mapping.endObject();
            }
            mapping.endObject().endObject();
            createIndex(index, builder);
            return true;
        } catch (IOException e) {
            log.error("创建索引失败", e);
            throw new RuntimeException("创建索引失败", e);
        }
    }

    public boolean addIndexAndType(String index, String type, Class clazz) {
        // 创建索引映射,相当于创建数据库中的表操作
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            XContentBuilder mapping = builder.startObject().startObject("properties");// 设置自定义字段
            Field[] fields = clazz.getDeclaredFields();
            String fieldType = null;
            for (Field f : fields) {
                fieldType = toEsType(f.getType());
                if (fieldType == null) {
                    continue;
                }
                mapping.startObject(f.getName()).field("type", fieldType);
                if ("date".equals(fieldType)) {
                    mapping.field("format", "yyyy-MM-dd HH:mm:ss");
                }
                mapping.endObject();
            }
            mapping.endObject().endObject();
            createIndex(index, builder);
            return true;
        } catch (IOException e) {
            log.error("创建索引失败", e);
            throw new RuntimeException("创建索引失败", e);
        }
    }

    private CreateIndexResponse createIndex(String index, XContentBuilder builder) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(Settings.builder().put("index.number_of_shards", shards)
                .put("index.number_of_replicas", replicas));
        request.alias(new Alias(alias));
        request.mapping(builder);
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    private String toEsType(Class clazz) {
        String className = clazz.getSimpleName();
        if ("String".equals(className)) {
            return "keyword";
        }
        if ("List".equals(className)) {
            return "text";
        }
        return className.toLowerCase();
    }

    //新增文档
    public long addDocument(String index, String type, String id, ElasticSearchEntity entity) {
        try {
            Map<String, Object> map = entity.toMap();
            IndexRequest indexRequest = new IndexRequest(index, type, id);
            indexRequest.source(map);
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            return response.getVersion();
        } catch (Exception e) {
            log.error("插入文档失败", e);
            throw new RuntimeException("插入文档失败", e);
        }
    }

    public long addDocument(String index, String type, String id, Map<String, Object> map) {
        try {
            IndexRequest indexRequest = new IndexRequest(index, type, id);
            indexRequest.source(map);
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            return response.getVersion();
        } catch (Exception e) {
            log.error("插入文档失败", e);
            throw new RuntimeException("插入文档失败", e);
        }
    }

    //删除文档
    public String deleteDocument(String index, String type, String id) {
        DeleteRequest request = new DeleteRequest(index, type, id);
        try {
            DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
            if (deleteResponse.getShardInfo().getFailed() <= 0) {
                return deleteResponse.getId();
            } else {
                throw new RuntimeException(deleteResponse.getShardInfo().getFailures()[0].reason());
            }
        } catch (IOException e) {
            throw new RuntimeException("删除文档失败", e);
        }
    }


    //更新文档
    public String updateDocument(String index, String type, String id, ElasticSearchEntity entity) {
        try {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(index);
            updateRequest.type(type);
            updateRequest.id(id);
            updateRequest.doc(entity.toMap());
            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
            return updateResponse.getId();
        } catch (Exception e) {
            log.error("更新文档失败", e);
            throw new RuntimeException("更新文档失败", e);
        }
    }

    //依据id查询
    public String searchById(String index, String type, String id) {
        GetRequest getRequest = new GetRequest(index, type, id);
        try {
            GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);
            return documentFields.getSourceAsString();
        } catch (IOException e) {
            throw new RuntimeException("根据文档的id查询索引文档失败", e);
        }
    }

    //查询索引下所有数据
    public List<String> queryAll(String index) {
        SearchResponse response = queryAllInType(index, null, QueryBuilders.matchAllQuery(), null);
        List<String> list = new ArrayList<>();
        for (SearchHit searchHit : response.getHits()) {
            list.add(searchHit.getSourceAsString());
        }
        return list;
    }

    //查询类型下所有数据
    public SearchResponse queryAllInType(String index, String type, QueryBuilder queryBuilder, AggregationBuilder agg) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(agg);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(searchSourceBuilder);
        try {
            return client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("根据索引查询文档失败", e);
        }
    }

    public SearchResponse query(String index, String type, SearchSourceBuilder searchSourceBuilder) {
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);
        try {
            return client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("根据索引查询文档失败", e);
        }
    }

    //查询索引下所有数据
    public List<String> queryMatch(String index, String type, Map<String, Object> matchs, String orderBy, Boolean isAsc, int pageNum, int pageSize) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder query = null;
        if (matchs != null) {
            for (Map.Entry<String, Object> e : matchs.entrySet()) {
                query = QueryBuilders.matchQuery(e.getKey(), e.getValue());
                searchSourceBuilder.query(query);
            }
        }
        int from = pageSize * (pageNum - 1);
        searchSourceBuilder.from(from).size(pageSize);
        if (orderBy != null) {
            if (isAsc) {
                searchSourceBuilder.sort(orderBy, SortOrder.ASC);
            } else {
                searchSourceBuilder.sort(orderBy, SortOrder.DESC);
            }
        }
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            List<String> list = new ArrayList<>();
            for (SearchHit searchHit : response.getHits()) {
                String hit = searchHit.getSourceAsString();
                JSONObject jsonObject = JSONObject.parseObject(hit);
                jsonObject.put("id", searchHit.getId());
                list.add(jsonObject.toJSONString());
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException("查询错误", e);
        }
    }


    public static void main(String[] args) throws IOException {

    }
}
