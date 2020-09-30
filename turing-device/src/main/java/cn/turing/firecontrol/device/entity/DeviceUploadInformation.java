package cn.turing.firecontrol.device.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 上传文件信息类
 */
@Table(name = "device_upload_information")
public class DeviceUploadInformation implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    @GeneratedValue(generator = "JDBC")
    @Id
    private Integer id;

    //文件名称
    @Column(name = "FILE_NAME")
    private String fileName;

    //文件路径
    @Column(name = "FILE_PATH")
    private String filePath;

    //文件类型
    @Column(name = "FILE_TYPE")
    private String fileType;

    //文件大小
    @Column(name = "FILE_SIZE")
    private long fileSize;

    //所属系统
    @Column(name = "SYSTEM")
    private String system;

    //删除标记[1=是/0=否（default）]
    @Column(name = "DEL_FLAG")
    private String delFlag;

    //上传用户名称
    @Column(name = "CRT_USER_NAME")
    private String crtUserName;

    //上传用户Id
    @Column(name = "CRT_USER_ID")
    private Integer crtUserId;

    //上传时间
    @Column(name = "CRT_TIME")
    private Date crtTime;

    //修改者名称
    @Column(name = "UPD_USER_NAME")
    private String updUserName;

    //修改者Id
    @Column(name = "UPD_USER_ID")
    private Integer updUserId;

    //修改时间
    @Column(name = "UPD_TIME")
    private Date updTime;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getCrtUserName() {
        return crtUserName;
    }

    public void setCrtUserName(String crtUserName) {
        this.crtUserName = crtUserName;
    }

    public Integer getCrtUserId() {
        return crtUserId;
    }

    public void setCrtUserId(Integer crtUserId) {
        this.crtUserId = crtUserId;
    }

    public Date getCrtTime() {
        return crtTime;
    }

    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    public String getUpdUserName() {
        return updUserName;
    }

    public void setUpdUserName(String updUserName) {
        this.updUserName = updUserName;
    }

    public Integer getUpdUserId() {
        return updUserId;
    }

    public void setUpdUserId(Integer updUserId) {
        this.updUserId = updUserId;
    }

    public Date getUpdTime() {
        return updTime;
    }

    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}
