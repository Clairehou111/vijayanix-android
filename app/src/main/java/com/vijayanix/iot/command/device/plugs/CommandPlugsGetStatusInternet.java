package com.vijayanix.iot.command.device.plugs;



import com.vijayanix.iot.model.device.plugs.PlugsAperture;
import com.vijayanix.iot.model.device.plugs.StatusPlugs;
import com.vijayanix.iot.model.device.plugs.IStatusPlugs;
import com.vijayanix.iot.model.http.HeaderPair;
import com.vijayanix.iot.util.BaseApiUtil;
import com.vijayanix.iot.util.HttpStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommandPlugsGetStatusInternet implements ICommandPlugsGetStatusInternet
{
    
    @Override
    public IStatusPlugs doCommandPlugsGetStatusInternet(String deviceKey)
    {
        String headerKey = Authorization;
        String headerValue = Token + " " + deviceKey;
        HeaderPair header = new HeaderPair(headerKey, headerValue);
        
        JSONObject resultJSON = BaseApiUtil.Get(URL, header);
        if (resultJSON == null)
        {
            return null;
        }
        
        try
        {
            int status = resultJSON.getInt(Status);
            if (status != HttpStatus.SC_OK)
            {
                return null;
            }
            
            IStatusPlugs plugsStatus = new StatusPlugs();
            List<IStatusPlugs.IAperture> apertures = new ArrayList<IStatusPlugs.IAperture>();
            JSONObject dataJSON = resultJSON.getJSONObject(Datapoint);
            int valueSum = dataJSON.getInt(X);
            int count = dataJSON.getInt(Y);
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
