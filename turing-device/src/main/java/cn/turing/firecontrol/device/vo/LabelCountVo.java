package cn.turing.firecontrol.device.vo;

/**
 * @author: zhangpeng
 * @email: 723308025@qq.com
 * @create: 2018-11-30 11:13
 **/
public class LabelCountVo {
    private Integer uncheckedCount;
    private Integer normalCount;
    private Integer abnormalCount;
    private Integer jumpCount;

    public Integer getUncheckedCount() {
        return uncheckedCount;
    }

    public void setUncheckedCount(Integer uncheckedCount) {
        this.uncheckedCount = uncheckedCount;
    }

    public Integer getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(Integer normalCount) {
        this.normalCount = normalCount;
    }

    public Integer getAbnormalCount() {
        return abnormalCount;
    }

    public void setAbnormalCount(Integer abnormalCount) {
        this.abnormalCount = abnormalCount;
    }

    public Integer getJumpCount() {
        return jumpCount;
    }

    public void setJumpCount(Integer jumpCount) {
        this.jumpCount = jumpCount;
    }
}
