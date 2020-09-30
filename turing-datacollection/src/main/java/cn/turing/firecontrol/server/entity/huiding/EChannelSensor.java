package cn.turing.firecontrol.server.entity.huiding;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class EChannelSensor {
    private Double vol1;
    private Double vol2;
    private Double vol3;
    public EChannelSensor(String bdata) {
        try{
            vol1=  Integer.parseInt(bdata.substring(8,12),16)*0.01;
        }catch (Exception e){
            vol1=0.0;
            log.error(e.toString());
        }
        try{
            vol2=  Integer.parseInt(bdata.substring(12,16),16)*0.01;
        }catch (Exception e){
            vol2=0.0;
            log.error(e.toString());
        }
        try{
            vol3=  Integer.parseInt(bdata.substring(16,20),16)*0.01;
        }catch (Exception e){
            vol3=0.0;
            log.error(e.toString());

        }



    }
}
