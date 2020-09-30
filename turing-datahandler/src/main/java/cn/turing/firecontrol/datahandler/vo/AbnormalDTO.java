package cn.turing.firecontrol.datahandler.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data

public class  AbnormalDTO {
    private String alrmDate;
    private String category;
    private String handleFlag;
    private int num;
}
