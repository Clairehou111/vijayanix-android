package com.vijayanix.iot.action.device.common;

import com.vijayanix.iot.model.device.IDevice;

/**
 * Created by hxhoua on 2018/7/12.
 */

public interface IActionDeviceStatusMqttSubscriber {
	/**
	 * process get datapoints topic
	 * @param device
	 * @param topic
	 * @param payload
	 */
	void doActionDeviceGetStatus(IDevice device, String topic, String payload);

	/**
	 * process device type sub topic
	 * @param device
	 * @param topic
	 * @param payload
	 * @return
	 */
	IDevice doActionDeviceType(IDevice device, String topic, String payload);

	/**
	 * process will topic
	 * @param device
	 */
	void doActionDeviceWill(IDevice device);

}
