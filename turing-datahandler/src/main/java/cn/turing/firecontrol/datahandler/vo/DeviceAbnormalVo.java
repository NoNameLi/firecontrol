package cn.turing.firecontrol.datahandler.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "报警详情")
public class DeviceAbnormalVo {
    private Integer sensorId;
    private String equipmentType;
    private String alrmCategory;
    private String measuringPoint;
    private Integer buildId;
    private String bName;
    private Integer equId;
    private String alrmDate;
    private String positionDescription;
    private String linkman;
    private String linkphone;
    private String alrmType;
}
