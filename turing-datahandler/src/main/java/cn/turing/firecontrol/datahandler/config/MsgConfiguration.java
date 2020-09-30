package cn.turing.firecontrol.datahandler.config;

import cn.turing.firecontrol.common.util.AliSmsUtil;
import cn.turing.firecontrol.common.util.AliVmsSent;
import cn.turing.firecontrol.common.util.FeiGeSmsUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2019/02/19 9:24
 *
 * @Description TODO
 * @Version V1.0
 */
@Configuration
@RefreshScope
public class MsgConfiguration {

    @Value("${vms.aliyun.accessKeyId}")
    private String aLiAccessKeyId;
    @Value("${vms.aliyun.accessKeySecret}")
    private String aLiAccessKeySecret;
    @Value("${vms.aliyun.calledNumber}")
    private String aLiCalledNumber;
    @Value("${sms.ailiyun.SignName}")
    private String SignName;
    @Value(("${sms.feige.account}"))
    private String feiGeAccount;
    @Value(("${sms.feige.pwd}"))
    private String feiGePwd;
    @Value(("${sms.feige.signId}"))
    private String feiGeSignId;




    @Bean
    public AliVmsSent aliVmsSent(){
        return new AliVmsSent(aLiAccessKeyId,aLiAccessKeySecret,aLiCalledNumber);
    }

    @Bean
    public FeiGeSmsUtil feiGeSmsUtil(){
        return new FeiGeSmsUtil(feiGeAccount,feiGePwd,feiGeSignId);
    }

    @Bean
    public AliSmsUtil aliSmsUtil(){
        return new AliSmsUtil(aLiAccessKeyId,aLiAccessKeySecret,SignName);
    }

}
