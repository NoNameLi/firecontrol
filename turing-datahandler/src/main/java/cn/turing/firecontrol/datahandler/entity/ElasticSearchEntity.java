package cn.turing.firecontrol.datahandler.entity;

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

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //数据ID
    private String id;

    public abstract Map<String,Object> toMap();

}
