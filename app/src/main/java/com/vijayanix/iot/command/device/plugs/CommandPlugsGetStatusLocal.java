package com.vijayanix.iot.command.device.plugs;



import com.vijayanix.iot.model.device.plugs.PlugsAperture;
import com.vijayanix.iot.model.device.plugs.StatusPlugs;
import com.vijayanix.iot.model.device.plugs.IStatusPlugs;
import com.vijayanix.iot.util.BaseApiUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class CommandPlugsGetStatusLocal implements ICommandPlugsGetStatusLocal
{
    @Override
    public String getLocalUrl(InetAddress inetAddress)
    {
        return "http://" + inetAddress.getHostAddress() + "/" + "config?command=plugs";
    }
    
    @Override
    public IStatusPlugs doCommandPlugsGetStatusLocal(InetAddress inetAddress, String deviceBssid,
                                                     boolean isMeshDevice)
    {
        String url = getLocalUrl(inetAddress);
        JSONObject resultJSON = null;
        if (deviceBssid == null || !isMeshDevice)
        {
            resultJSON = BaseApiUtil.Get(url);
        }
        else
        {
            resultJSON = BaseApiUtil.GetForJson(url, deviceBssid);
        }
        
        if (resultJSON == null)
        {
            return null;
        }
        
        try
        {
            IStatusPlugs plugsStatus = new StatusPlugs();
            List<IStatusPlugs.IAperture> apertures = new ArrayList<IStatusPlugs.IAperture>();
            JSONObject statusJSON = resultJSON.getJSONObject(KEY_PLUGS_STATUS);
            int count = statusJSON.getInt(KEY_APERTURE_COUNT);
            int valueSum = statusJSON.getInt(KEY_PLUGS_VALUE);
            for (int i = 0; i < count; i++)
            {
                IStatusPlugs.IAperture aperture = new PlugsAperture(i);
                aperture.setTitle("Plug " + i);
                boolean isOn = (valueSum >> i) % 2 == 1;
                aperture.setOn(isOn);
                
                apertures.add(aperture);
            }
            
            plugsStatus.setStatusApertureList(apertures);
            return plugsStatus;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
}
