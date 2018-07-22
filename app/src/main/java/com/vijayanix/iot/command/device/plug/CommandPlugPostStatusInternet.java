package com.vijayanix.iot.command.device.plug;


import com.vijayanix.iot.common.net.rest2.MqttRequestUtil;
import com.vijayanix.iot.model.device.plug.IStatusPlug;
import com.vijayanix.iot.model.http.HeaderPair;
import com.vijayanix.iot.util.BaseApiUtil;
import com.vijayanix.iot.util.HttpStatus;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.vijayanix.iot.mqtt.MqttConstant.TOPIC_APP;

public class CommandPlugPostStatusInternet implements ICommandPlugPostStatusInternet
{
    private final static Logger log = Logger.getLogger(CommandPlugPostStatusInternet.class);





	private JSONObject getRequestJSONObject(IStatusPlug statusPlug)
	{
		JSONObject request = new JSONObject();
		JSONObject response = new JSONObject();
		try
		{
			int status = 0;
			if (statusPlug.isOn())
			{
				status = 1;
			}
			response.put(Status, status);
			request.put(Response, response);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return request;
	}


    
    private boolean postPlugStatus(String deviceKey, IStatusPlug statusPlug)
    {
        JSONObject jsonObject = getRequestJSONObject(statusPlug);

	    String topic = TOPIC_APP + deviceKey;
        
        return MqttRequestUtil.Post(topic, jsonObject);

        
    }
    
    @Override
    public boolean doCommandPlugPostStatusInternet(String deviceKey, IStatusPlug statusPlug)
    {
        boolean result = postPlugStatus(deviceKey, statusPlug);
        log.debug(Thread.currentThread().toString() + "##doCommandPlugPostStatusInternet(deviceKey=[" + deviceKey
            + "],statusPlug=[" + statusPlug + "]): " + result);
        return result;
    }
    
    @Override
    public boolean doCommandMulticastPostStatusInternet(String deviceKey, IStatusPlug statusPlug, List<String> bssids)
    {
        boolean result = true;
        List<String> macList = new ArrayList<String>();
        for (String bssid : bssids)
        {
            macList.add(bssid);
            if (macList.size() == MULTICAST_GROUP_LENGTH_LIMIT)
            {
                if (!postMulticastCommand(deviceKey, statusPlug, macList))
                {
                    result = false;
                }
                macList.clear();
            }
        }
        if (!macList.isEmpty())
        {
            if (!postMulticastCommand(deviceKey, statusPlug, macList))
            {
                result = false;
            }
        }
        return result;
    }
    
    private boolean postMulticastCommand(String deviceKey, IStatusPlug statusPlug, List<String> macList)
    {
        String headerKey = Authorization;
        String headerValue = Token + " " + deviceKey;
        HeaderPair header = new HeaderPair(headerKey, headerValue);
        
        StringBuilder urlBuilder = new StringBuilder(URL_MULTICAST);
        for (int i = 0; i < macList.size(); i++) {
            String mac = macList.get(i);
            urlBuilder.append(mac);
            if (i < macList.size() - 1) {
                urlBuilder.append(",");
            }
        }
        
        try
        {
            JSONObject postJSON = getRequestJSONObject(statusPlug);
            JSONObject result = BaseApiUtil.Post(urlBuilder.toString(), postJSON, header);
            if (result != null)
            {
                int httpStatus = result.getInt(Status);
                return httpStatus == HttpStatus.SC_OK;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
        
        return false;
    }
}
