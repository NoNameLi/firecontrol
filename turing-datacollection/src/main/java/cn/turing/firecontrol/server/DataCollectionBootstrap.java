package cn.turing.firecontrol.server;


import cn.turing.firecontrol.server.netty.NettyServer;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@EnableSwagger2Doc
@EnableEurekaClient
@EnableTransactionManagement //开启事务
@EnableCircuitBreaker // 开启熔断监控
 //开启服务鉴权
@EnableFeignClients({"cn.turing.firecontrol.auth.client.feign", "cn.turing.firecontrol.server.feign"})
@SpringBootApplication
@ComponentScan({"cn.turing.firecontrol","cn.turing.firecontrol.auth"})
@EnableScheduling
public class DataCollectionBootstrap implements CommandLineRunner {

    @Autowired
    NettyServer nettyServer;

    public static void main(String[] args){
        SpringApplication.run(DataCollectionBootstrap.class,args);
    }


    @Override
    public void run(String... strings) throws Exception {
        Thread thread2=new Thread(new netty3(8890));
        thread2.start();
        Thread thread3=new Thread(new netty3(8891));
        thread3.start();
        Thread thread4=new Thread(new netty5(8892));
        thread4.start();
        Thread thread=new Thread(new netty2(8893));
        thread.start();
        Thread thread5=new Thread(new netty4(8894));
        thread5.start();
    }

    private class  netty3 implements Runnable{
        private  int port;

        public netty3(int i) {
            port=i;
        }

        @Override
        public void run() {
            try {
                nettyServer.wisdomFactoryServer(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class  netty2 implements Runnable{
        private int port;
        public netty2(int i) {
            port=i;
        }

        @Override
        public void run() {
            try {
                nettyServer.sanjiangSeaServer(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private class  netty4 implements Runnable{
        private int port;
        public netty4(int i) {
            port=i;
        }

        @Override
        public void run() {
            try {
                nettyServer.sanjiangSeaServer8894(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private class  netty5 implements Runnable{
        private int port;
        public netty5(int i) {
            port=i;
        }

        @Override
        public void run() {
            try {
                nettyServer.sanjiangSeaServer8892(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Bean(name = "executorGroup",destroyMethod ="shutdownGracefully" )
    public EventExecutorGroup eventExecutors(){
        return  new DefaultEventExecutorGroup(5);
    }
}