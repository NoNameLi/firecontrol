package cn.turing.common.entity.acrel.electricalFireV1;


import cn.turing.common.entity.acrel.AcrelDeviceInfo;
import com.alibaba.fastjson.JSONObject;

/**
 * 对时数据解析
 */
public class PairSensor extends AcrelDeviceInfo {
    public PairSensor(String s) {
        super(s);
    }

    @Override
    public JSONObject toDeviceJSON() {
        return null;
    }
}
