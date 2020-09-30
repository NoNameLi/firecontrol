package cn.turing.firecontrol.device.dto;

import lombok.Data;

@Data
public class TypeNumDto {
    //类型
    private String type;
    //数量
    private long num;
    private double percent;

    public TypeNumDto(String type, long num) {
        this.type = type;
        this.num = num;
    }

    public TypeNumDto(String type, double percent) {
        this.type = type;
        this.percent = percent;
    }
}
