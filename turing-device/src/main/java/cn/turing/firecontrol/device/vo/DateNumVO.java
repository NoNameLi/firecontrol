package cn.turing.firecontrol.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="日期数量(通用)")
public class DateNumVO {
    @ApiModelProperty(value="日期")
    private String dateValue;
    @ApiModelProperty(value="数量")
    private int num;
    @ApiModelProperty(value="数量1")
    private int num1;

}
