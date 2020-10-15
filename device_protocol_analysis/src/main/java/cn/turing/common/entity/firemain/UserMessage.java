package cn.turing.common.entity.firemain;


import cn.turing.common.base.Constant;
import cn.turing.common.entity.SensorDetail;
import cn.turing.common.util.ByteUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class UserMessage {

	/**
	 * 系统类型
	 */
	private String systemType;
	/**
	 * 报警主机主机号  等于 系统地址
	 */
	private String fireMainCode;
	/**
	 * 部件类型
	 */
	private String partType;
	/**
	 * 回路地址 根据部件地址拆分
	 */
	private String loopNo;
	/**
	 * 节点编号-根据部件地址拆分
	 */
	private String addressNo;
	/**
	 * 部件状态
	 */
	private String partStat;
	/**
	 * 部件说明
	 */
	private String partExplain;
	/**
	 * 发生时间
	 */
	private String upTime;

	private List<SensorDetail> list;
}
