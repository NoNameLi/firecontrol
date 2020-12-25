package cn.turing.firecontrol.device.config;

import cn.turing.firecontrol.device.util.ESTransportUtil;
import cn.turing.firecontrol.device.util.FireRecognitionUtil;
import cn.turing.firecontrol.device.util.YingShiUtil;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    @Value("${spring.elasticsearch.cluster-nodes}")
    private String clusterNodes;
    @Value("${spring.elasticsearch.cluster-name}")
    private String clusterName;

    @Value("${yingShi.appKey}")
    private String yingShiAppKey;
    @Value("${yingShi.appSecret}")
    private String yingShiAppSecret;

    @Value("${xiaoHuaErAI.appCode}")
    private String xiaoHuaErAIAppCode;
    @Value("${xiaoHuaErAI.accuracyThreshold}")
    private Double xiaoHuaErAIAccuracyThreshold;

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
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("ESClient error!");
//            log.error(e.toString());
//        }
//        log.info("获得ESClient结束");
//        return client;
//    }

    @Bean
    public ESTransportUtil esTransportUtil() {
        ESTransportUtil util = new ESTransportUtil(getClient(), alias, shards, replicas);
        return util;
    }

    //萤石开放平台调用工具类
    @Bean
    public YingShiUtil yingShiUtil() {
        return new YingShiUtil(yingShiAppKey, yingShiAppSecret);
    }

    //小花儿人工智能火焰识别调用工具类
    @Bean
    public FireRecognitionUtil fireRecognitionUtil() {
        return new FireRecognitionUtil(xiaoHuaErAIAppCode, xiaoHuaErAIAccuracyThreshold);
    }

}
