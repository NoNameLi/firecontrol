package cn.turing.firecontrol.datahandler.base;

import cn.turing.firecontrol.datahandler.listener.ExpireMessageListener;
import cn.turing.firecontrol.datahandler.util.ESTransportUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

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

    @Autowired
    ExpireMessageListener expireMessageListener;
//redis配置
//    @Value("${spring.redis.host}")
//    private String host;
//    @Value("${spring.redis.pool.maxActive}")
//    private Integer maxActive;
//    @Value("${spring.redis.password}")
//    private String password;
//    @Value("${spring.redis.pool.maxIdle}")
//    private Integer maxIdle;
//    @Value("${spring.redis.pool.minIdle}")
//    private Integer minIdle;
//    @Value("${spring.redis.pool.maxWait}")
//    private Integer maxWait;

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
                GetIndexRequest getIndexRequest = new GetIndexRequest(Constant.ESConstant.ES_INDEX_WARNING);
                if (!client.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
                    defineAlarmIndexTypeMapping(client);
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
//                    .put("client.transport.ignore_cluster_name", true)
//                    .put("client.transport.ping_timeout", "30s")
////                    .put("cluster.name", clusterName)//设置集群名称
//                    .put("client.transport.sniff", false).build();//自动嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中
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
//            IndicesExistsRequest request = new IndicesExistsRequest(Constant.ESConstant.ES_INDEX_WARNING);
//            IndicesExistsResponse response = client.admin().indices().exists(request).actionGet();
//            if (response.isExists() == false) {
//                Config.defineAlarmIndexTypeMapping(client);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e.toString());
//        }
//        log.info("获得ESClient结束");
//        return client;
//    }

//    @Bean
//    public JedisCluster getJedisCluster() {
//        log.info("获得redis连接开始");
//        JedisCluster jedisCluster=null;
//        String[] cNodes = host.split(",");
//        Set<HostAndPort> nodes =new HashSet<>();
//        for(String node : cNodes) {
//            String[] hp = node.split(":");
//            nodes.add(new HostAndPort(hp[0],Integer.parseInt(hp[1])));
//        }
//        JedisPoolConfig jedisPoolConfig =new JedisPoolConfig();
//        jedisPoolConfig.setMaxIdle(maxIdle);
//        jedisPoolConfig.setMaxWaitMillis(maxWait);
//        jedisPoolConfig.setMaxTotal(maxActive);
//        jedisPoolConfig.setBlockWhenExhausted(true);
//        jedisPoolConfig.setTestOnBorrow(true);
//        jedisCluster= new JedisCluster(nodes,5000,10000,10,password,jedisPoolConfig);
//        //jedisCluster=new JedisCluster(nodes,5000,10000,10,jedisPoolConfig);
//        log.info("获得redis连接结束");
//        return jedisCluster;
//    }

    public static void defineAlarmIndexTypeMapping(TransportClient client) {
        try {
            String TypeName = Constant.ESConstant.ES_STRING;
            String IndexName = Constant.ESConstant.ES_INDEX_WARNING;
            XContentBuilder mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject()
                    .startObject(TypeName)
                    .startObject("_all").field("enabled", false).endObject()
                    .startObject("properties")
                    .startObject("deviceid").field("type", "keyword").field("index", true).endObject()
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

    public void defineAlarmIndexTypeMapping(RestHighLevelClient client) {
        try {
            XContentBuilder mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject()
                    .startObject(Constant.ESConstant.ES_STRING)
                    .startObject("_all").field("enabled", false).endObject()
                    .startObject("properties")
                    .startObject("deviceid").field("type", "keyword").field("index", true).endObject()
                    .startObject("uploadtime").field("type", "date").field("format", "strict_date_optional_time||epoch_millis").endObject()
                    .startObject("recievetime").field("type", "date").field("format", "strict_date_optional_time||epoch_millis").endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            CreateIndexRequest request = new CreateIndexRequest(Constant.ESConstant.ES_INDEX_WARNING)
                    .settings(Settings.builder().put("index.number_of_shards", shards)
                            .put("index.number_of_replicas", replicas))
                    .alias(new Alias(alias)).mapping(mapBuilder);
            client.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    @Bean
    public ESTransportUtil esTransportUtil() {
        ESTransportUtil util = new ESTransportUtil(getClient(), alias, shards, replicas);
        return util;
    }

}
