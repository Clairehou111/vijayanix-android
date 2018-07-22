package com.vijayanix.iot.command.device.plugs;



import com.vijayanix.iot.model.device.plugs.IStatusPlugs;
import com.vijayanix.iot.util.BaseApiUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;

public class CommandPlugsPostStatusLocal implements ICommandPlugsPostStatusLocal
{
    @Override
    public String getLocalUrl(InetAddress inetAddress)
    {
        return "http://" + inetAddress.getHostAddress() + "/" + "config?command=plugs";
    }
    
    @Override
    public boolean doCommandPlugsPostStatusLocal(InetAddress inetAddress, IStatusPlugs status, String deviceBssid,
                                                 boolean isMeshDevice)
    {
        String url = getLocalUrl(inetAddress);
        
        JSONObject params = new JSONObject();
        JSONObject statusJSON = new JSONObject();
        try
        {
            List<IStatusPlugs.IAperture> apertures = status.getStatusApertureList();
            int valueSum = 0;
            for (IStatusPlugs.IAperture aperture : apertures)
            {
                int value;
                if (aperture.isOn())
                {
                    value = 1 << aperture.getId();
                }
                else
                {
                    value = 0;
                }
                valueSum += value;
            }
            statusJSON.put(KEY_PLUGS_VALUE, valueSum);
            statusJSON.put(KEY_APERTURE_COUNT, apertures.size());
            params.put(KEY_PLUGS_STATUS, statusJSON);
        }
        catch (JSONException e1)
        {
            e1.printStackTrace();
        }
        
        JSONObject result;
        if (deviceBssid == null || !isMeshDevice)
        {
            result = BaseApiUtil.Post(url, params);
        }
        else
        {
            result = BaseApiUtil.PostForJson(url, deviceBssid, params);
        }
        
        return result != null;
    }
}
