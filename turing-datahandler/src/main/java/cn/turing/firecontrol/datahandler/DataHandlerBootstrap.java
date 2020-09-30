package cn.turing.firecontrol.datahandler;



import cn.turing.firecontrol.datahandler.util.ApplicationContextUtil;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@EnableScheduling
@EnableEurekaClient
// 开启事务
@EnableTransactionManagement
// 开启熔断监控
@EnableCircuitBreaker
// 开启服务鉴权
@EnableFeignClients({"cn.turing.firecontrol.auth.client.feign", "cn.turing.firecontrol.datahandler.feign"})
@SpringBootApplication
@ComponentScan({"cn.turing.firecontrol.datahandler","cn.turing.firecontrol.auth"})
@MapperScan("cn.turing.firecontrol.datahandler.mapper")
@EnableSwagger2Doc
public class DataHandlerBootstrap {

    public static void main(String[] args){
        ApplicationContextUtil.setApplicationContext(SpringApplication.run(DataHandlerBootstrap.class,args));
    }

}