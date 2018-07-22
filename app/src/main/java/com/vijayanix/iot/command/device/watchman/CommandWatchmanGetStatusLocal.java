package com.vijayanix.iot.command.device.watchman;


import com.google.gson.Gson;
import com.vijayanix.iot.command.device.ICommandGetStatusLocal;
import com.vijayanix.iot.command.device.ICommandWatchman;
import com.vijayanix.iot.model.device.watchman.DeviceWatchmanStatus;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchmanStatus;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

public class CommandWatchmanGetStatusLocal implements ICommandGetStatusLocal,ICommandWatchman
{
    private final static Logger log = Logger.getLogger(CommandWatchmanGetStatusLocal.class);
    
    @Override
    public String getLocalUrl(InetAddress inetAddress)
    {
        return "http://" + inetAddress.getHostAddress() + "/" + "config?command=watchman";
    }
    
    private IDeviceWatchmanStatus getCurrentPlugStatus2(InetAddress inetAddress, String deviceBssid, boolean isMeshDevice)
    {
        String uriString = getLocalUrl(inetAddress);
        JSONObject jo = null;
        if (deviceBssid == null || !isMeshDevice)
        {
            jo = BaseApiUtil.Get(uriString);
        }
        else
        {
            jo = BaseApiUtil.GetForJson(uriString, deviceBssid);
        }
        if (jo == null)
        {
            return null;
        }
        try
        {
            JSONObject responseJson;
            if (!jo.isNull(RESPONSE)) {
                responseJson = jo.getJSONObject(RESPONSE);
            } else {
               return null;
            }

	        Gson gson = new Gson();

	        IDeviceWatchmanStatus status = gson.fromJson(responseJson.toString(),DeviceWatchmanStatus.class);

/*            switch ((SubDatastreamType)datastreamType){

	            Gson gson = new Gson();

	            IDeviceWatchmanStatus status = gson.fromJson(responseJson.toString(),DeviceWatchmanStatus.class);

	            IDeviceWatchmanStatus status = new DeviceWatchmanStatus();

	            case LIGHTA:
                   int lightA = responseJson.getInt(LITHT_A);
		            gson.fromJson(responseJson.toString(),DeviceWatchmanStatus.class);
                    break;
                case LIGHTB:
                    int lightB = responseJson.getInt(LITHT_B);
                case POWER:
                    int power = responseJson.getInt(POWER);
	            case ALL:
                    int power = responseJson.getInt(POWER);
                    break;
            }*/

            return status;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public IDeviceWatchmanStatus doCommandGetStatusLocal(InetAddress inetAddress)
    {
        IDeviceWatchmanStatus result = getCurrentPlugStatus2(inetAddress, null, false);
        log.debug(Thread.currentThread().toString() + "##doCommandPlugGetStatusLocal(inetAddress=[" + inetAddress
            + "]): " + result);
        return result;
    }
    
    @Override
    public IDeviceWatchmanStatus doCommandGetStatusLocal(InetAddress inetAddress, String deviceBssid, boolean isMeshDevice)
    {
        IDeviceWatchmanStatus result = getCurrentPlugStatus2(inetAddress, deviceBssid, isMeshDevice);
        log.debug(Thread.currentThread().toString() + "##doCommandPlugGetStatusLocal(inetAddress=[" + inetAddress
            + "],deviceBssid=[" + deviceBssid + "],isMeshDevice=[" + isMeshDevice + "])");
        return result;
    }
    
}
