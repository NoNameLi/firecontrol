package cn.turing.common.entity.huiding.v1;

import lombok.Data;

@Data
public class DChannelSensor {
    private Double vol1;
    private Double vol2;
    private Double vol3;
    private Double vol4;
    public DChannelSensor(String bdata) {
        try{
            vol1=  Integer.parseInt(bdata.substring(8,12),16)*0.01;
        }catch (Exception e){
            vol1=0.0;
        }
        try{
            vol2=  Integer.parseInt(bdata.substring(12,16),16)*0.01;
        }catch (Exception e){
            vol2=0.0;
        }
        try{
            vol3=  Integer.parseInt(bdata.substring(16,20),16)*0.01;
        }catch (Exception e){
            vol3=0.0;
        }
        try{
            vol4=  Integer.parseInt(bdata.substring(20,24),16)*0.01;
        }catch (Exception e){
            vol4=0.0;
        }




    }

}
