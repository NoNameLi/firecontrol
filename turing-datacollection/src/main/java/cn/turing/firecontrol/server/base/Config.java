package cn.turing.firecontrol.server.base;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * 如果布置tzj 这个类需要毙掉，
 */
@Configuration
public class Config {
    private  static final Logger log = LoggerFactory.getLogger(Config.class);
//ES配置
    @Value("${spring.elasticsearch.cluster-nodes}")
    private  String clusterNodes;
    @Value("${spring.elasticsearch.cluster-name}")
    private String clusterName;

    @Bean
    public TransportClient getClient() {
        log.info("获得ESClient开始");
        TransportClient client = null;
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", clusterName)//设置集群名称
                    .put("client.transport.sniff", true).build();//自动嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中
            // .put("thread_pool.search.size", Integer.parseInt(5))//增加线程池个数，暂时设为5
            client=new PreBuiltTransportClient(settings);
            String[] nodes = clusterNodes.split(",");
            for (String node : nodes) {
                if (node.length() > 0) {//跳过为空的node（当开头、结尾有逗号或多个连续逗号时会出现空node）
                    String[] hostPort = node.split(":");
                    client.addTransportAddress(new TransportAddress(InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1])));
                }
            }
            //检查是否存在 两个基本的索引，如果没有就创建
            IndicesExistsRequest request = new IndicesExistsRequest(Constant.ESConstant.ES_INDEX);
            IndicesExistsResponse response = client.admin().indices().exists(request).actionGet();
            if (response.isExists() == false) {
                log.info("不存在 "+Constant.ESConstant.ES_INDEX+" 开始创建");
                Config.defineIndexTypeMapping(client);
            }
            //
            request = new IndicesExistsRequest(Constant.ESConstant.ES_INDEX_SENSOR);
            response = client.admin().indices().exists(request).actionGet();
            if (response.isExists() == false) {
                log.info("不存在 "+Constant.ESConstant.ES_INDEX_SENSOR+" 开始创建");
                Config.defineSensorIndexTypeMapping(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
        }
        log.info("获得ESClient完成");
        return client;
    }
//
//    @Bean
//    public StringRedisTemplate redisTemplate(RedisConnectionFactory factory) {
//
//        StringRedisTemplate template = new StringRedisTemplate(factory);
//
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//
//        ObjectMapper om = new ObjectMapper();
//
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//
//        template.afterPropertiesSet();
//
//        return template;
//
//    }
    // 定义索引的映射类型
    public static void defineIndexTypeMapping(TransportClient client) {
        try {
            String TypeName = Constant.ESConstant.ES_STRING;
            String IndexName = Constant.ESConstant.ES_INDEX;
            XContentBuilder mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject()
                    .startObject(TypeName)
                    .startObject("_all").field("enabled", false).endObject()
                    .startObject("properties")
                    .startObject("channel").field("type", "keyword").field("index", true).endObject()
                    .startObject("deviceid").field("type", "keyword").field("index", true).endObject()
                    .startObject("uploadtime").field("type", "date").field("format", "strict_date_optional_time||epoch_millis").endObject()
                    .startObject("recievetime").field("type", "date").field("format", "strict_date_optional_time||epoch_millis").endObject()
                    .startObject("rawdata").field("type", "text").field("index", false).endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            client.admin().indices().prepareCreate(IndexName)
                    //.setSettings(Settings.builder().put("index.number_of_shards", index.getNumber_of_shards()).put("index.number_of_replicas", index.getNumber_of_replicas()))
                    .addMapping(TypeName, mapBuilder)
                    .get();
//            PutMappingRequest putMappingRequest = Requests
//                    .putMappingRequest(IndexName).type(TypeName)
//                    .source(mapBuilder);
//            client.admin().indices().putMapping(putMappingRequest).actionGet();
        } catch (IOException e) {
            log.error(e.toString());
        }
    }
    //
    public static void defineSensorIndexTypeMapping(TransportClient client) {
        try {
            String TypeName = Constant.ESConstant.ES_SOURCE_TYPE;
            String IndexName = Constant.ESConstant.ES_INDEX_SENSOR;
            XContentBuilder mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject()
                    .startObject(TypeName)
                    .startObject("_all").field("enabled", false).endObject()
                    .startObject("properties")
                    .startObject("deviceid").field("type", "keyword").field("index", true).endObject()
                    .startObject("appeui").field("type", "keyword").field("index", true).endObject()
                    .startObject("datatype").field("type", "keyword").field("index", true).endObject()
                    .startObject("reserver").field("type", "text").field("index", true).endObject()
                    .startObject("parttype").field("type", "text").field("index", true).endObject()
                    .startObject("parttypetext").field("type", "text").field("index", true).endObject()
                    .startObject("uploadtime").field("type", "date").field("format", "strict_date_optional_time||epoch_millis").endObject()
                    .startObject("recievetime").field("type", "date").field("format", "strict_date_optional_time||epoch_millis").endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            client.admin().indices().prepareCreate(IndexName)
                    //.setSettings(Settings.builder().put("index.number_of_shards", index.getNumber_of_shards()).put("index.number_of_replicas", index.getNumber_of_replicas()))
                    .addMapping(TypeName, mapBuilder)
                    .get();
        } catch (IOException e) {
            log.error(e.toString());
        }
    }
}
