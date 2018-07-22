package com.vijayanix.iot.command.device.plug;


import com.vijayanix.iot.model.device.plug.StatusPlug;
import com.vijayanix.iot.model.device.plug.IStatusPlug;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

public class CommandPlugGetStatusLocal implements ICommandPlugGetStatusLocal
{
    private final static Logger log = Logger.getLogger(CommandPlugGetStatusLocal.class);
    
    @Override
    public String getLocalUrl(InetAddress inetAddress)
    {
        return "http://" + inetAddress.getHostAddress() + "/" + "config?command=switch";
    }
    
    private IStatusPlug getCurrentPlugStatus2(InetAddress inetAddress, String deviceBssid, boolean isMeshDevice)
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
            if (!jo.isNull(Response)) {
                responseJson = jo.getJSONObject(Response);
            } else {
                return  null;
            }
            int on = responseJson.getInt(Status);
            IStatusPlug status = new StatusPlug();
            status.setIsOn(on == 1);
            return status;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public IStatusPlug doCommandPlugGetStatusLocal(InetAddress inetAddress)
    {
        IStatusPlug result = getCurrentPlugStatus2(inetAddress, null, false);
        log.debug(Thread.currentThread().toString() + "##doCommandPlugGetStatusLocal(inetAddress=[" + inetAddress
            + "]): " + result);
        return result;
    }
    
    @Override
    public IStatusPlug doCommandPlugGetStatusLocal(InetAddress inetAddress, String deviceBssid, boolean isMeshDevice)
    {
        IStatusPlug result = getCurrentPlugStatus2(inetAddress, deviceBssid, isMeshDevice);
        log.debug(Thread.currentThread().toString() + "##doCommandPlugGetStatusLocal(inetAddress=[" + inetAddress
            + "],deviceBssid=[" + deviceBssid + "],isMeshDevice=[" + isMeshDevice + "])");
        return result;
    }
    
}
