package com.vijayanix.iot.command.device.common;

import com.vijayanix.iot.common.net.rest2.MqttRequestUtil;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.mqtt.MqttConstant;
import com.vijayanix.iot.mqtt.Onenet;

import org.apache.log4j.Logger;

import java.util.List;

import static com.vijayanix.iot.mqtt.SubTopic.DEVICE_TYPE;

public class CommandDeviceSynchronizeInternet implements ICommandDeviceSynchronizeInternet
{
    private final static Logger log = Logger.getLogger(CommandDeviceSynchronizeInternet.class);




	@Override
    public List<IDevice> doCommandDeviceSynchronizeInternet(String userKey)
    {

	  return Onenet.getDevices();

    }

    @Override
	public void getDeviceType(String deviceKey){
		String topic = MqttConstant.getAppSubTopic(deviceKey,DEVICE_TYPE);
		MqttRequestUtil.Get(topic);
	}




}
