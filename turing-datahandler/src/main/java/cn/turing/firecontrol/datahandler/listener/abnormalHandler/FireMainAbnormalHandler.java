package cn.turing.firecontrol.datahandler.listener.abnormalHandler;

import cn.turing.firecontrol.datahandler.business.BusinessI;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
@Slf4j
@Data
public class FireMainAbnormalHandler extends  AbstractAbnormalHandler{

    @Autowired
    BusinessI businessI;
    public void handleAbnormal(String data) throws ParseException{
        try {
            businessI.alertFireMainMSG(data);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
