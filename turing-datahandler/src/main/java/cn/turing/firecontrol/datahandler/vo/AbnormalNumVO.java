package cn.turing.firecontrol.datahandler.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="报警数量")
public class AbnormalNumVO {
    @ApiModelProperty(value="日期")
    private String alrmDate;
    @ApiModelProperty(value="故障数量")
    private int faultNum;
    @ApiModelProperty(value="火警数量")
    private int fireNum;
}
