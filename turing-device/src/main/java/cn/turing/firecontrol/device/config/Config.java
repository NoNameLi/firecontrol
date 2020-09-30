package cn.turing.firecontrol.device.config;

import cn.turing.firecontrol.device.util.ESTransportUtil;
import cn.turing.firecontrol.device.util.FireRecognitionUtil;
import cn.turing.firecontrol.device.util.YingShiUtil;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
public class Config {
    private  static final Logger log = LoggerFactory.getLogger(Config.class);

    @Value("${spring.elasticsearch.cluster-nodes}")
    private  String clusterNodes;
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

    @Bean
    public TransportClient getClient() {
        log.info("获得ESClient开始");
        TransportClient client = null;
        try {
            Settings settings = Settings.builder()
                    .put("client.transport.ignore_cluster_name", true)
                    .put("client.transport.ping_timeout", "30s")
//                    .put("cluster.name", clusterName)//设置集群名称
                    .put("client.transport.sniff", false).build();//自动嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中
            // .put("thread_pool.search.size", Integer.parseInt(5))//增加线程池个数，暂时设为5
            client=new PreBuiltTransportClient(settings);
            String[] nodes = clusterNodes.split(",");
            for (String node : nodes) {
                if (node.length() > 0) {//跳过为空的node（当开头、结尾有逗号或多个连续逗号时会出现空node）
                    String[] hostPort = node.split(":");
                    client.addTransportAddress(new TransportAddress(InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1])));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("ESClient error!");
            log.error(e.toString());
        }
        log.info("获得ESClient结束");
        return client;
    }

    @Bean
    public ESTransportUtil esTransportUtil(){
        ESTransportUtil util = new ESTransportUtil(getClient());
        return util;
    }

    //萤石开放平台调用工具类
    @Bean
    public YingShiUtil yingShiUtil(){
        return new YingShiUtil(yingShiAppKey,yingShiAppSecret);
    }

    //小花儿人工智能火焰识别调用工具类
    @Bean
    public FireRecognitionUtil fireRecognitionUtil(){
        return new FireRecognitionUtil(xiaoHuaErAIAppCode,xiaoHuaErAIAccuracyThreshold);
    }

}
