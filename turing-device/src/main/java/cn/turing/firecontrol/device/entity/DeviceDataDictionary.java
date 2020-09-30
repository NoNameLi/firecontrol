package cn.turing.firecontrol.device.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;


/**
 * 
 * 
 * @author HanYong
 * @email 3291659070@qq.com
 * @version 2018-07-26 09:17:57
 */
@Table(name = "device_data_dictionary")
public class DeviceDataDictionary implements Serializable {
	private static final long serialVersionUID = 1L;

		//主键id
	@GeneratedValue(generator = "JDBC")
	@Id
	private Integer id;
	
	    //参数类别
    @Column(name = "PARM_TYPE")
    private String parmType;
	
	    //参数说明
    @Column(name = "PARM_NAME")
    private String parmName;
	
	    //参数代号
    @Column(name = "PARM_VALUE")
    private String parmValue;
	
	    //父级参数类别
    @Column(name = "PARENT_TYPE")
    private String parentType;
	
	    //父级参数代号
    @Column(name = "PARENT_VALUE")
    private String parentValue;
	

	/**
	 * 设置：主键id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：主键id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：参数类别
	 */
	public void setParmType(String parmType) {
		this.parmType = parmType;
	}
	/**
	 * 获取：参数类别
	 */
	public String getParmType() {
		return parmType;
	}
	/**
	 * 设置：参数说明
	 */
	public void setParmName(String parmName) {
		this.parmName = parmName;
	}
	/**
	 * 获取：参数说明
	 */
	public String getParmName() {
		return parmName;
	}
	/**
	 * 设置：参数代号
	 */
	public void setParmValue(String parmValue) {
		this.parmValue = parmValue;
	}
	/**
	 * 获取：参数代号
	 */
	public String getParmValue() {
		return parmValue;
	}
	/**
	 * 设置：父级参数类别
	 */
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}
	/**
	 * 获取：父级参数类别
	 */
	public String getParentType() {
		return parentType;
	}
	/**
	 * 设置：父级参数代号
	 */
	public void setParentValue(String parentValue) {
		this.parentValue = parentValue;
	}
	/**
	 * 获取：父级参数代号
	 */
	public String getParentValue() {
		return parentValue;
	}
}
