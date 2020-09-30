package cn.turing.firecontrol.server.bean;

import lombok.Data;

@Data
public class Rabbit {
    private String sensor_id;
    private String exchange;
    private String routingkey;
}
