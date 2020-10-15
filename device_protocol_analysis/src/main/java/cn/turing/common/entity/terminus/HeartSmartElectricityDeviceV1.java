package cn.turing.common.entity.terminus;

import cn.turing.common.util.ByteUtil;
import lombok.Data;

@Data
public class HeartSmartElectricityDeviceV1 extends SmartElectricityDeviceV1{

    private String replyPackage;//回复包

    public HeartSmartElectricityDeviceV1(String data) {
        super(data);
        if (this.getEffectiveData().equals("02") && this.isFlag()){

            String rawCrc="1104"+this.getPackageNum()+"01"+"00";
            replyPackage="401104"+this.getPackageNum()+"01"+"00"+ ByteUtil.hexSum(rawCrc)+"23";
        }else{
            String rawCrc="1104"+this.getPackageNum()+"01"+"01";
            replyPackage="401104"+this.getPackageNum()+"01"+"01"+ ByteUtil.hexSum(rawCrc)+"23";
        }
    }
}
