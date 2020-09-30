package cn.turing.firecontrol.device;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableEurekaClient
// 开启事务
@EnableTransactionManagement
// 开启熔断监控
@EnableCircuitBreaker
// 开启服务鉴权
@EnableFeignClients({"cn.turing.firecontrol.auth.client.feign","cn.turing.firecontrol.device.feign"})
@SpringBootApplication
@MapperScan("cn.turing.firecontrol.device.mapper")
@ComponentScan("cn.turing.firecontrol")
@EnableSwagger2Doc
@EnableScheduling
public class DeviceBootstrap {

    public static void main(String[] args){
        SpringApplication.run(DeviceBootstrap.class,args);
    }



}