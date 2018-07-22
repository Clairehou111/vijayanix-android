package com.vijayanix.iot.util;

import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchmanStatus;
import com.vijayanix.iot.model.device.watchman.SubDatastreamType;
import com.vijayanix.iot.mqtt.MqttConstant;
import com.vijayanix.iot.mqtt.SubTopic;

import static com.vijayanix.iot.mqtt.SubTopic.ALL;
import static com.vijayanix.iot.mqtt.SubTopic.DEVICE_TYPE;
import static com.vijayanix.iot.mqtt.SubTopic.WATCHMAN_LIGHTA;
import static com.vijayanix.iot.mqtt.SubTopic.WATCHMAN_LIGHTB;


/**
 * Created by hxhoua on 2018/7/12.
 */

public class DeviceDatastreamUtil {

	public static void copyDeviceStatus(int datastreamType, IDeviceStatus sourceStatus, IDeviceStatus targetStatus){

		IDeviceWatchmanStatus source = (IDeviceWatchmanStatus) sourceStatus;
		IDeviceWatchmanStatus target = (IDeviceWatchmanStatus) targetStatus;

		switch (datastreamType){
			case SubDatastreamType.WATCHMAN_LIGHTA:
				target.setLightA(source.getLightA());
				break;
			case SubDatastreamType.WATCHMAN_LIGHTB:
				target.setLightB(source.getLightB());
				break;
			case SubDatastreamType.ALL:
				target.setLightA(source.getLightA());
				target.setLightB(source.getLightB());
				break;
			case SubDatastreamType.DEVICE_TYPE:
				target.setLightA(source.getLightA());
				target.setLightB(source.getLightB());
				break;
			default:
				throw new IllegalArgumentException();
		}
	}


	public static String status2Topic(int datastreamType, String deviceKey) {
		switch (datastreamType) {
			case SubDatastreamType.ALL:
				return MqttConstant.getTopicApp(deviceKey);

/*			case DEVICE_TYPE:
				return TOPIC_APP + deviceKey+ TOPIC_SPERATOR+ SubTopic.LIGHTA;*/

			case SubDatastreamType.WATCHMAN_LIGHTA:
				return MqttConstant.getAppSubTopic(deviceKey, SubTopic.WATCHMAN_LIGHTA);
			case SubDatastreamType.WATCHMAN_LIGHTB:
				return MqttConstant.getAppSubTopic(deviceKey,SubTopic.WATCHMAN_LIGHTB);


		}
		throw new IllegalArgumentException();
	}




	public static int topic2status(String topic) {

		int subTopic = MqttConstant.getSubTopic(topic);
		switch (subTopic) {
			case ALL:
				return  SubDatastreamType.ALL;
			case DEVICE_TYPE:
				return  SubDatastreamType.DEVICE_TYPE;
			case WATCHMAN_LIGHTA:
				return SubDatastreamType.WATCHMAN_LIGHTA;
			case WATCHMAN_LIGHTB:
				return SubDatastreamType.WATCHMAN_LIGHTB;

		}

		throw new IllegalArgumentException();
	}
}
