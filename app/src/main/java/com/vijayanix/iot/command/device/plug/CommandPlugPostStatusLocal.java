package com.vijayanix.iot.command.device.plug;


import com.vijayanix.iot.common.net.proxy.MeshCommunicationUtils;
import com.vijayanix.iot.model.device.plug.IStatusPlug;
import com.vijayanix.iot.model.http.HeaderPair;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class CommandPlugPostStatusLocal implements ICommandPlugPostStatusLocal
{
    
    private final static Logger log = Logger.getLogger(CommandPlugPostStatusLocal.class);
    
    @Override
    public String getLocalUrl(InetAddress inetAddress)
    {
        return "http://" + inetAddress.getHostAddress() + "/" + "config?command=switch";
    }
    
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
    
    private boolean postPlugStatus2(InetAddress inetAddress, JSONObject postJSON, String deviceBssid, boolean isMeshDevice)
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
    public boolean doCommandPlugPostStatusLocal(InetAddress inetAddress, IStatusPlug statusPlug)
    {
        JSONObject postJSON = getRequestJSONObject(statusPlug);
        boolean result = postPlugStatus2(inetAddress, postJSON, null, false);
        log.debug(Thread.currentThread().toString() + "##doCommandPlugPostStatusInternet(inetAddress=[" + inetAddress
            + "],statusPlug=[" + statusPlug + "]): " + result);
        return result;
    }
    
    @Override
    public boolean doCommandPlugPostStatusLocal(InetAddress inetAddress, IStatusPlug statusPlug, String deviceBssid,
                                                boolean isMeshDevice)
    {
        JSONObject postJSON = getRequestJSONObject(statusPlug);
        log.debug("postJSON is "+postJSON.toString());
        boolean result = postPlugStatus2(inetAddress, postJSON, deviceBssid, isMeshDevice);
        log.debug(Thread.currentThread().toString() + "##doCommandPlugPostStatusLocal(inetAddress=[" + inetAddress
            + "],statusPlug=[" + statusPlug + "],deviceBssid=[" + deviceBssid + "],isMeshDevice=[" + isMeshDevice
            + "])");
        return result;
    }
    
    @Override
    public boolean doCommandMulticastPostStatusLocal(InetAddress inetAddress, IStatusPlug statusPlug,
                                                     List<String> bssids)
    {
        if (bssids.size() == 1)
        {
            JSONObject postJSON = getRequestJSONObject(statusPlug);
            return postPlugStatus2(inetAddress, postJSON, bssids.get(0), true);
        }
        else
        {
            boolean result = true;
            List<String> macList = new ArrayList<String>();
            for (String bssid : bssids)
            {
                macList.add(bssid);
                if (macList.size() == MULTICAST_GROUP_LENGTH_LIMIT)
                {
                    if (!postMulticastCommand(inetAddress, statusPlug, macList))
                    {
                        result = false;
                    }
                    macList.clear();
                }
            }
            if (!macList.isEmpty())
            {
                if (!postMulticastCommand(inetAddress, statusPlug, macList))
                {
                    result = false;
                }
            }
            
            return result;
        }
    }
    
    private boolean postMulticastCommand(InetAddress inetAddress, IStatusPlug statusPlug, List<String> macList)
    {
        StringBuilder macs = new StringBuilder();
        for (String mac : macList) {
            macs.append(mac);
        }
        HeaderPair multicastHeader =
            new HeaderPair(MeshCommunicationUtils.HEADER_MESH_MULTICAST_GROUP, macs.toString());
        
        JSONObject postJSON = getRequestJSONObject(statusPlug);
        
        JSONObject respJSON = BaseApiUtil.PostForJson(getLocalUrl(inetAddress), MeshCommunicationUtils.MULTICAST_MAC, postJSON, multicastHeader);
        return respJSON != null;
    }
}
