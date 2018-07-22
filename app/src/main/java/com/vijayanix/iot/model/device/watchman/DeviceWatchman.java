package com.vijayanix.iot.model.device.watchman;

import com.vijayanix.iot.model.device.Device;
import com.vijayanix.iot.mqtt.MqttConstant;
import com.vijayanix.iot.mqtt.SubTopic;

/**
 * Created by hxhoua on 2018/7/12.
 */

public class DeviceWatchman extends Device implements IDeviceWatchman {

	IDeviceWatchmanStatus mWatchmanStatus;

/*	public DeviceWatchman(IDeviceWatchmanStatus watchmanStatus) {
		mWatchmanStatus = watchmanStatus;
		this.addSubscribedTopics(MqttConstant.getTopicDevice(this.getKey()));
		this.addSubscribedTopics(MqttConstant.getTopicDeviceWildcards(this.getKey()));
	}*/

	public DeviceWatchman() {
		super();
		mWatchmanStatus = new DeviceWatchmanStatus();
	}

	@Override
	public IDeviceWatchmanStatus getWatchmanStatus() {
		return mWatchmanStatus;
	}

	@Override
	public void setWatchmanStatus(IDeviceWatchmanStatus watchmanStatus) {
		mWatchmanStatus = watchmanStatus;
	}


	@Override
	protected void addPowerSwitchTopic() {
		addReferencedTopic(MqttConstant.getDeviceSubTopic(this.getKey(), SubTopic.WATCHMAN_LIGHTA));
	}
}
