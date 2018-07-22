package com.vijayanix.iot.command.device.watchman;


import com.vijayanix.iot.command.device.ICommandWatchman;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchmanStatus;
import com.vijayanix.iot.util.DeviceDatastreamUtil;
import com.vijayanix.iot.command.device.ICommandPostStatusInternet;
import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.common.net.rest2.MqttRequestUtil;
import com.vijayanix.iot.model.device.watchman.SubDatastreamType;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class CommandWatchmanPostStatusInternet implements ICommandPostStatusInternet,ICommandWatchman
{
    private final static Logger log = Logger.getLogger(CommandWatchmanPostStatusInternet.class);

	private JSONObject getRequestJSONObject(int datastreamType, IDeviceWatchmanStatus statusWatchman)
	{
		JSONObject request = new JSONObject();
		JSONObject response = new JSONObject();

		try
		{
			switch (datastreamType){
				case SubDatastreamType.WATCHMAN_LIGHTA:
					response.put(LITHT_A, statusWatchman.getLightA());
					break;
				case SubDatastreamType.WATCHMAN_LIGHTB:
					response.put(LITHT_B, statusWatchman.getLightB());
					break;

			}

			request.put(RESPONSE, response);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return request;
	}




    
    private boolean postStatus(int datastreamType,String deviceKey, IDeviceWatchmanStatus statusWatchman)
    {
        JSONObject jsonObject = getRequestJSONObject(datastreamType,statusWatchman);

	    String topic = DeviceDatastreamUtil.status2Topic(datastreamType,deviceKey);


	    return MqttRequestUtil.Post(topic, jsonObject);

        
    }
    
    @Override
    public boolean doCommandPostStatusInternet(int datastreamType,String deviceKey, IDeviceStatus deviceStatus)
    {
	    IDeviceWatchmanStatus statusWatchman = (IDeviceWatchmanStatus) deviceStatus;

	    boolean result = postStatus(datastreamType,deviceKey, statusWatchman);
        log.debug(Thread.currentThread().toString() + "##doCommandPlugPostStatusInternet(deviceKey=[" + deviceKey
            + "],statusWatchman=[" + statusWatchman + "]): " + result);
        return result;
    }
    

}
