package cn.turing.firecontrol.server.base;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 如果布置tzj 这个类需要毙掉，
 */
@Configuration
public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);
    //ES配置
    @Value("${spring.elasticsearch.cluster-nodes}")
    private String clusterNodes;
    @Value("${spring.elasticsearch.cluster-name}")
    private String clusterName;

    @Value("${es.url}")
    private String esurl;
    @Value("${es.port}")
    private Integer port;
    @Value("${es.userName}")
    private String userName;
    @Value("${es.password}")
    private String password;
    @Value("${es.http}")
    private String http;
    @Value("${es.alias}")
    private String alias = "";
    private Integer shards = 1;
    private Integer replicas = 1;

    @Bean
    public RestHighLevelClient getClient() {
        log.info("获得ESClient开始");
        RestHighLevelClient client = null;
        try {
            if (userName == null) {
                client = new RestHighLevelClient(RestClient.builder(
                        new HttpHost(esurl, port, http)));
            } else {
                //需要用户名和密码的认证
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
                RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(esurl, port, http))
                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                            @Override
                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                                return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                            }
                        });
                client = new RestHighLevelClient(restClientBuilder);
                //检查是否存在 两个基本的索引，如果没有就创建
                GetIndexRequest getIndexRequest = new GetIndexRequest(Constant.ESConstant.ES_INDEX);
                if (!client.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
                    log.info("不存在 " + Constant.ESConstant.ES_INDEX + " 开始创建");
                    defineIndexTypeMapping(client);
                }
                getIndexRequest = new GetIndexRequest(Constant.ESConstant.ES_INDEX_SENSOR);
                if (!client.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
                    log.info("不存在 " + Constant.ESConstant.ES_INDEX_SENSOR + " 开始创建");
                    defineSensorIndexTypeMapping(client);
                }
            }
        } catch (Exception e) {
            log.error("ESClient初始化错误", e);
        }
        log.info("获得ESClient结束");
        return client;
    }

    //    @Bean
//    public TransportClient getClient() {
//        log.info("获得ESClient开始");
//        TransportClient client = null;
//        try {
//            Settings settings = Settings.builder()
//                    .put("cluster.name", clusterName)//设置集群名称
//                    .put("client.transport.sniff", true).build();//自动嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中
//            // .put("thread_pool.search.size", Integer.parseInt(5))//增加线程池个数，暂时设为5
//            client=new PreBuiltTransportClient(settings);
//            String[] nodes = clusterNodes.split(",");
//            for (String node : nodes) {
//                if (node.length() > 0) {//跳过为空的node（当开头、结尾有逗号或多个连续逗号时会出现空node）
//                    String[] hostPort = node.split(":");
//                    client.addTransportAddress(new TransportAddress(InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1])));
//                }
//            }
//            //检查是否存在 两个基本的索引，如果没有就创建
//            IndicesExistsRequest request = new IndicesExistsRequest(Constant.ESConstant.ES_INDEX);
//            IndicesExistsResponse response = client.admin().indices().exists(request).actionGet();
//            if (response.isExists() == false) {
//                log.info("不存在 "+Constant.ESConstant.ES_INDEX+" 开始创建");
//                Config.defineIndexTypeMapping(client);
//            }
//            //
//            request = new IndicesExistsRequest(Constant.ESConstant.ES_INDEX_SENSOR);
//            response = client.admin().indices().exists(request).actionGet();
//            if (response.isExists() == false) {
//                log.info("不存在 "+Constant.ESConstant.ES_INDEX_SENSOR+" 开始创建");
//                Config.defineSensorIndexTypeMapping(client);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e.toString());
//        }
//        log.info("获得ESClient完成");
//        return client;
//    }
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
                    .addMapping(TypeName, mapBuilder)
                    .get();
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
                    .addMapping(TypeName, mapBuilder)
                    .get();
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public void defineIndexTypeMapping(RestHighLevelClient client) {
        try {
            XContentBuilder mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject()
                    .startObject(Constant.ESConstant.ES_STRING)
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
            //索引名称
            CreateIndexRequest request = new CreateIndexRequest(Constant.ESConstant.ES_INDEX)
                    .settings(Settings.builder().put("index.number_of_shards", shards)
                            .put("index.number_of_replicas", replicas))
                    .alias(new Alias(alias + "_sensor"))
                    .mapping(mapBuilder);
            client.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public void defineSensorIndexTypeMapping(RestHighLevelClient client) {
        try {
            XContentBuilder mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject()
                    .startObject(Constant.ESConstant.ES_SOURCE_TYPE)
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
            CreateIndexRequest request = new CreateIndexRequest(Constant.ESConstant.ES_INDEX_SENSOR)
                    .settings(Settings.builder().put("index.number_of_shards", shards)
                            .put("index.number_of_replicas", replicas))
                    .alias(new Alias(alias + "_sensor"))
                    .mapping(mapBuilder);
            client.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }
}
