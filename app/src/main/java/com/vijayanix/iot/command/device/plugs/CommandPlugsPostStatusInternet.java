package com.vijayanix.iot.command.device.plugs;


import com.vijayanix.iot.model.device.plugs.IStatusPlugs;
import com.vijayanix.iot.model.http.HeaderPair;
import com.vijayanix.iot.util.BaseApiUtil;
import com.vijayanix.iot.util.HttpStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CommandPlugsPostStatusInternet implements ICommandPlugsPostStatusInternet
{
    
    @Override
    public boolean doCommandPlugsPostStatusInternet(String deviceKey, IStatusPlugs status)
    {
        String headerKey = Authorization;
        String headerValue = Token + " " + deviceKey;
        HeaderPair header = new HeaderPair(headerKey, headerValue);
        
        JSONObject params = new JSONObject();
        JSONObject dataJSON = new JSONObject();
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
            dataJSON.put(X, valueSum);
            params.put(Datapoint, dataJSON);
        }
        catch (JSONException e1)
        {
            e1.printStackTrace();
        }
        
        String url = URL;
        JSONObject result = BaseApiUtil.Post(url, params, header);
        if (result == null)
        {
            return false;
        }
        
        try
        {
            int httpStatus = result.getInt(Status);
            return httpStatus == HttpStatus.SC_OK;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return false;
    }
    
}
