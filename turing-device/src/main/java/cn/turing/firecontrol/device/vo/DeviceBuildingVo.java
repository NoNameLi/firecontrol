package cn.turing.firecontrol.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "建筑物")
public class DeviceBuildingVo  {
    @ApiModelProperty(value = "建筑物或者消火栓id")
    private Integer id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "报警数量")
    private int num;
    @ApiModelProperty(value = "建筑物经纬度")
    private String gis;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "类型 1建筑物  2 消火栓")
    private String type;
}
