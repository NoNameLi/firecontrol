package cn.turing.firecontrol.device.entity;

import cn.turing.firecontrol.device.util.Constants;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created on 2019/02/22 10:23
 *
 * @Description
 * @Version V1.0
 */
@Data
public abstract class ElasticSearchEntity {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.ES_DATE_FORMAT);

    //数据ID
    private String id;

    public abstract Map<String,Object> toMap();

}
