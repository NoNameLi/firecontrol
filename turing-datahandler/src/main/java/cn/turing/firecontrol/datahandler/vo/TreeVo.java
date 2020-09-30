package cn.turing.firecontrol.datahandler.vo;


import java.util.List;

public class TreeVo{

    private String code;
    private String pid;
    private String name;
    private List<TreeVo> childList;


    public TreeVo(String code, String pid, String name) {
        this.code = code;
        this.pid = pid;
        this.name = name;
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
