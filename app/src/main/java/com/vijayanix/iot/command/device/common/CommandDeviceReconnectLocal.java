package com.vijayanix.iot.command.device.common;



import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.common.net.wifi.WifiCipherType;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommandDeviceReconnectLocal implements ICommandDeviceReconnectLocal
{
    private final static Logger log = Logger.getLogger(CommandDeviceReconnectLocal.class);
    
    @Override
    public String getLocalUrl(InetAddress inetAddress)
    {
        return "http://" + inetAddress.getHostAddress() + "/" + "config?command=wifi";
    }
    
    @Override
    public boolean doCommandReconnectLocal(String deviceBssid, String apSsid, WifiCipherType type,
                                           String... apPassword)
    {
        JSONObject Content = new JSONObject();
        JSONObject Connect_Station = new JSONObject();
        JSONObject Station = new JSONObject();
        JSONObject Request = new JSONObject();
        try
        {
            if (apPassword != null)
            {
                Content.put("password", apPassword[0]);
            }
            Content.put("ssid", apSsid);
            
            Connect_Station.put("Connect_Station", Content);
            
            Station.put("Station", Connect_Station);
            
            Request.put("Request", Station);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        String gateWay = IOTApplication.sharedInstance().getGateway();
        InetAddress inetAddress = null;
        try
        {
            inetAddress = InetAddress.getByName(gateWay);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        String urlString = getLocalUrl(inetAddress);
        JSONObject result = BaseApiUtil.PostForJson(urlString, deviceBssid, Request);
        log.debug(Thread.currentThread().toString() + "##doCommandReconnectLocal(deviceBssid=[" + deviceBssid
            + "],apSsid=[" + apSsid + "],apPassword=[" + apPassword + "]): " + result);
        return result != null;
    }
}
