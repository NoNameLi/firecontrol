package cn.turing.firecontrol.monitor;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class MonitorApp {
    public static void main(String[] args) {
        SpringApplication.run(MonitorApp.class, args);
    }
}