package cn.turing.common.entity.huiding.v3.device;

import lombok.Data;


@Data
public class TemperatureAndHumidity  {
    /**
     * 温度
     */
    private String temp_value;
    /**
     * 湿度
     */
    private String hum_value;

    public TemperatureAndHumidity(String data){
      hum_value=String.format("%.2f",Integer.parseInt(data.substring(0,4),16)*0.1);

      if (data.substring(4,5).equals("1")){
          temp_value=String.format("%.2f",-Integer.parseInt(data.substring(5),16)*0.1);
      }else{
          temp_value=String.format("%.2f",Integer.parseInt(data.substring(5),16)*0.1);
      }

    }
}
