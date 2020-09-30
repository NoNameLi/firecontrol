package cn.turing.firecontrol.datahandler.vo;

import lombok.Data;

@Data
public class TypeNumVO {
    //类型
    private String type;
    //类型
    private String typeName;
    //数量
    private long num;
    private double percent;

    public TypeNumVO() {
    }

    public TypeNumVO(String type, long num) {
        this.type = type;
        this.num = num;
    }

    public TypeNumVO(String type, double percent) {
        this.type = type;
        this.percent = percent;
    }

    public TypeNumVO(String type, String typeName, double percent) {
        this.type = type;
        this.typeName = typeName;
        this.percent = percent;
    }
}
