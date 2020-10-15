package cn.turing.common.constant;

/**
 * 电信AEP平台，订阅推送
 */
public interface NbTelecomConstant {
	/**
	 * 设备数据变化
	 */
	String dataReport="dataReport";
	/**
	 * 设备命令响应
	 */
	String commandResponse="commandResponse";
	/**
	 * 设备事件上报
	 */
	String eventReport="eventReport";
	/**
	 * 设备上下线
	 */
	String deviceOnlineOfflineReport="deviceOnlineOfflineReport";
	/**
	 * 设备数据批量变化
	 */
	String dataReportTupUnion="dataReportTupUnion";


	//可燃气体-赛特威尔-定义
	/**
	 * 正常
	 */
	Integer gas_sensor_state_normal=0;
	/**
	 * 1--低浓度报警
	 */
	Integer gas_sensor_state_alarm_d=1;
	/**
	 * 2--高浓度报警
	 */
	Integer gas_sensor_state_alarm_g=2;
}
