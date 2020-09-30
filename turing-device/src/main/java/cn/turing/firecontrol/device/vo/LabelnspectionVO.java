package cn.turing.firecontrol.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
@ApiModel(value="巡检详情")
public class LabelnspectionVO {
    @ApiModelProperty("设施标签id")
    private Integer labelId;
    @ApiModelProperty("设施类型")
    private String equipmentType;
    @ApiModelProperty("位置描述")
    private String positionDescription;
    @ApiModelProperty("联系人")
    private String linkman;
    @ApiModelProperty("联系电话")
    private String linkphone;
    @ApiModelProperty("巡检状态")
    private String resultFlag;
    @ApiModelProperty("路线id")
    private Integer routeId;
    @ApiModelProperty("路线")
    private String routeName;
    @ApiModelProperty("巡检周期（单位：天）")
    private Integer patrolCycle;
    @ApiModelProperty("最近巡检时间")
    private Date lastInspectionTime;
    @ApiModelProperty("下次巡检时间")
    private String nextInspectionTime;
}
