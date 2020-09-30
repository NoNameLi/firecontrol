package cn.turing.firecontrol.device.entity;

import cn.turing.firecontrol.device.util.Constants;
import com.google.common.collect.Maps;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Date;

/**
 * Created on 2019/02/25 14:43
 *
 * @Description
 * @Version V1.0
 */
@Data
public class DeviceVideoAnalysisData extends ElasticSearchEntity implements Serializable {

    public static final Integer ANALYSIS_RESULT_MALFUNCTION = 0;
    public static final Integer ANALYSIS_RESULT_ALARM = 1;
    public static final Integer ANALYSIS_RESULT_NORMAL = 2;

    //设备编号
    private String sensorNo;
    //分析结果值
    private Double analysisValue;
    //所分析的图片
    private String analysisPicture;
    //分析结果(0故障，1报警，2正常）
    private Integer analysisResult;
    //分析时间
    private Date analysisTime;
    //分站点ID
    private String tenantId;

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("sensorNo",sensorNo);
        map.put("analysisValue",analysisValue);
        map.put("analysisPicture",analysisPicture);
        map.put("analysisResult",analysisResult);
        map.put("tenantId",tenantId);
        map.put("analysisTime",dateFormat.format(analysisTime));
        return map;
    }
}
