package cn.turing.firecontrol.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
@Data
@ApiModel(value="巡检结果")
public class InspectionResultsVO implements Serializable {


    @ApiModelProperty("设施类型")
    private String equipmentType;


    @ApiModelProperty("位置描述")
    private String positionDescription;



    @ApiModelProperty("问题描述")
    private String problemDescription;


    @ApiModelProperty("巡检人")
    private String inspectionPerson;

    @ApiModelProperty("巡检时间")
    private String inspectionDate;

}