package cn.turing.firecontrol.datahandler.vo;

import lombok.Data;

import java.util.List;

@Data
public class AbnormalDateVo {
    private String alrmDate;
    private List<TypeNumVO> list;
}
