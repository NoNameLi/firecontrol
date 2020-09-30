package cn.turing.firecontrol.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="设备状态")
public class SensorStatusVO {
    @ApiModelProperty(value="系统名称")
    private String channelName;
    @ApiModelProperty(value="系统id")
    private Integer channelId;
    @ApiModelProperty(value="类型")
    private String equipmentType;
    private String status;
    @ApiModelProperty(value="正常数量")
    private int normalNum;
    @ApiModelProperty(value="报警数量")
    private int warningNum;
    @ApiModelProperty(value="故障数量")
    private int faultNum;
    @ApiModelProperty(value="离线数量")
    private int offLineNum;
    @ApiModelProperty(value="未启用数量")
    private int notUsedNum;

}
