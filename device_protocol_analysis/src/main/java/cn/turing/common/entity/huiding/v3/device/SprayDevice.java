package cn.turing.common.entity.huiding.v3.device;



import lombok.Data;



/**
 * 喷淋水压
 * @author TDS
 *
 */
@Data
public class SprayDevice {

    private String spray_value;

    public SprayDevice(String data,Double v){

        spray_value= String.format("%.2f", 0.125*(Integer.parseInt(data,16)
                *v*10)/4096-0.5);
    }
}
