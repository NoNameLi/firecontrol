package cn.turing.firecontrol.device.vo;

/**
 * @author: zhangpeng
 * @email: 723308025@qq.com
 * @create: 2018-11-14 14:44
 **/
public class CountVo {
    private Integer faultCount;
    private Integer callCount;
    private Integer normalCount;
    private Integer offCount;

    public Integer getFaultCount() {
        return faultCount;
    }

    public void setFaultCount(Integer faultCount) {
        this.faultCount = faultCount;
    }

    public Integer getCallCount() {
        return callCount;
    }

    public void setCallCount(Integer callCount) {
        this.callCount = callCount;
    }

    public Integer getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(Integer normalCount) {
        this.normalCount = normalCount;
    }

    public Integer getOffCount() {
        return offCount;
    }

    public void setOffCount(Integer offCount) {
        this.offCount = offCount;
    }
}
