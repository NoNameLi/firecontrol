package cn.turing.firecontrol.server.entity.huiding;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 电表
 */
@Slf4j
@Data
public class BChannelSensor {
    private Double  positiveActive;//正向有功
    private Double  instantaneousActive;//瞬时总有功功率
    private Double  volA;//A相电压
    private Double  volB;//B相电压
    private Double  volC;//C相电压
    private Double  eleA;//A相电流
    private Double  eleB;//b相电流
    private Double  eleC;//c相电流
    public BChannelSensor(String bdata) {

            positiveActive = Integer.parseInt(bdata.substring(8, 16)) * 0.01;
            instantaneousActive = Integer.parseInt(bdata.substring(16, 24)) * 0.01;
        try {
            volA = Integer.parseInt(bdata.substring(24, 28)) * 0.01;
        }catch (Exception e){
            volA=0.0;
            log.error(e.toString());
        }
        try{
            volB = Integer.parseInt(bdata.substring(28, 32)) * 0.01;
        }catch (Exception e){
            volB=0.0;
            log.error(e.toString());
        }
        try{
            volC = Integer.parseInt(bdata.substring(32, 36)) * 0.01;
        }catch (Exception e){
            volC=0.0;
            log.error(e.toString());
        }
        try{
            eleA = Integer.parseInt(bdata.substring(36, 42)) * 0.01;
        }catch (Exception e){
            eleA=0.0;
            log.error(e.toString());
        }
        try{
            eleB = Integer.parseInt(bdata.substring(42, 48)) * 0.01;
        }catch (Exception e){
            eleB=0.0;
            log.error(e.toString());
        }
        try{
            eleC = Integer.parseInt(bdata.substring(48, 54)) * 0.01;
        }catch (Exception e){
            eleC=0.0;
            log.error(e.toString());
        }






    }

}
