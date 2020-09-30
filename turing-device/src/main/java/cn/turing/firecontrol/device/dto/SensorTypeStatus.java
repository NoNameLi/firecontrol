package cn.turing.firecontrol.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="设备状态")
public class SensorTypeStatus {
    @ApiModelProperty(value="系统id")
    private Integer channelId;
    @ApiModelProperty(value="类型")
    private String equipmentType;
    @ApiModelProperty(value="'状态[0=故障/1=报警/2=正常/3=未启用/4=离线]")
    private String status;
    @ApiModelProperty(value="'状态名称")
    private String statusName;
    @ApiModelProperty(value="数量")
    private int num;
    @ApiModelProperty(value="占比")
    private double percent;
}
