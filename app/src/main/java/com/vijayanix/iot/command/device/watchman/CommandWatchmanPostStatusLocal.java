package com.vijayanix.iot.command.device.watchman;

import com.vijayanix.iot.command.device.ICommandWatchman;
import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.command.device.ICommandPostStatusLocal;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchmanStatus;
import com.vijayanix.iot.model.device.watchman.SubDatastreamType;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;



/**
 * Created by hxhoua on 2018/7/12.
 */

@SuppressWarnings("DefaultFileTemplate")
public class CommandWatchmanPostStatusLocal implements ICommandPostStatusLocal,ICommandWatchman {

	private final static Logger log = Logger.getLogger(CommandWatchmanPostStatusLocal.class);

	@Override
	public String getLocalUrl(InetAddress inetAddress)
	{
		return "http://" + inetAddress.getHostAddress() + "/" + "config?command=watchman";
	}



	private JSONObject getRequestJSONObject(int datastreamType,IDeviceWatchmanStatus statusWatchman)
	{
		JSONObject request = new JSONObject();
		JSONObject body = new JSONObject();
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
			body.put(RESPONSE, response);
			request.put(BODY, body);
			request.put(SUB_DATASTREAM, datastreamType);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return request;
	}

	private boolean postStatus(InetAddress inetAddress, JSONObject postJSON, String deviceBssid, boolean isMeshDevice)
	{
		String uriString = getLocalUrl(inetAddress);
		JSONObject result = null;
		if (deviceBssid == null || !isMeshDevice)
		{
			result = BaseApiUtil.Post(uriString, postJSON);
		}
		else
		{
			result = BaseApiUtil.PostForJson(uriString, deviceBssid, postJSON);
		}
		return (result != null);
	}


	@Override
	public boolean doCommandPostStatusLocal(int datastreamType,InetAddress inetAddress, IDeviceStatus deviceStatus)
	{
		IDeviceWatchmanStatus statusWatchman = (IDeviceWatchmanStatus) deviceStatus;
		JSONObject postJSON = getRequestJSONObject(datastreamType,statusWatchman);
		boolean result = postStatus(inetAddress, postJSON, null, false);
		log.debug(Thread.currentThread().toString() + "##doCommandPlugPostStatusInternet(inetAddress=[" + inetAddress
				+ "],statusWatchman=[" + statusWatchman + "]): " + result);
		return result;
	}




	@Override
	public boolean doCommandPostStatusLocal(int datastreamType, InetAddress inetAddress, IDeviceStatus deviceStatus, String deviceBssid,
	                                        boolean isMeshDevice)
	{
		IDeviceWatchmanStatus statusWatchman = (IDeviceWatchmanStatus) deviceStatus;
		JSONObject postJSON = getRequestJSONObject(datastreamType,statusWatchman);
		log.debug("postJSON is "+postJSON.toString());
		boolean result = postStatus(inetAddress, postJSON, deviceBssid, isMeshDevice);
		log.debug(Thread.currentThread().toString() + "##doCommandPlugPostStatusLocal(inetAddress=[" + inetAddress
				+ "],statusWatchman=[" + statusWatchman + "],deviceBssid=[" + deviceBssid + "],isMeshDevice=[" + isMeshDevice
				+ "])");


		return result;
	}




}
