package cn.turing.firecontrol.device.vo;


import java.util.List;

public class TreeVo{

    private String code;
    private String pid;
    private String name;
    private List<TreeVo> childList;
    private String status;

    public TreeVo(String code, String pid, String name,String status) {
        this.code = code;
        this.pid = pid;
        this.name = name;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TreeVo> getChildList() {
        return childList;
    }

    public void setChildList(List<TreeVo> childList) {
        this.childList = childList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TreeVo{" +
                "code='" + code + '\'' +
                ", pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", childList=" + childList +
                '}';
    }
}
