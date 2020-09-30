package cn.turing.firecontrol.datahandler.base;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("jpushConfig")
@Data
public class JpushConfig {

    // 读取极光配置信息中的用户名密码
    @Value("${jpush.appKey}")
    private String appkey;
    @Value("${jpush.masterSecret}")
    private String masterSecret;
    @Value("${jpush.liveTime}")
    private String liveTime;
    @Value("${jpush.title}")
    private String title;

}
