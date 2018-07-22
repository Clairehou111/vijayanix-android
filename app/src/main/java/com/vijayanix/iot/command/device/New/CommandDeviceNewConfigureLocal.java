package com.vijayanix.iot.command.device.New;


import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.common.net.wifi.WifiCipherType;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommandDeviceNewConfigureLocal implements ICommandDeviceNewConfigureLocal
{
    private final static Logger log = Logger.getLogger(CommandDeviceNewConfigureLocal.class);
    
    @Override
    public String getLocalUrl(InetAddress inetAddress)
    {
        return "http://" + inetAddress.getHostAddress() + "/" + "config?command=wifi";
    }
    
    @Override
    public boolean doCommandDeviceNewConfigureLocal(String deviceSsid, WifiCipherType deviceWifiCipherType,
                                                    String devicePassword, String apSsid, WifiCipherType apWifiCipherType, String apPassword, String randomToken)
    {
        JSONObject Content = new JSONObject();
        JSONObject Connect_Station = new JSONObject();
        JSONObject Station = new JSONObject();
        JSONObject Request = new JSONObject();
        try
        {
            Content.put("token", randomToken);
            Content.put("password", apPassword);
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
        JSONObject result = BaseApiUtil.Post(urlString, Request);
        log.debug(Thread.currentThread().toString() + "##doCommandDeviceNewConfigureLocal(deviceSsid=[" + deviceSsid
            + "],deviceWifiCipherType=[" + deviceWifiCipherType + "],devicePassword=[" + devicePassword + "],apSsid=["
            + apSsid + "],apWifiCipherType=[" + apWifiCipherType + "],apPassword=[" + apPassword + "],randomToken=["
            + randomToken + "]): " + result);
        return true;
    }
    
    @Override
    public boolean doCommandMeshDeviceNewConfigureLocal(String deviceBssid, String deviceSsid,
                                                        WifiCipherType deviceWifiCipherType, String devicePassword, String randomToken)
    {
        JSONObject Content = new JSONObject();
        JSONObject Connect_Station = new JSONObject();
        JSONObject Station = new JSONObject();
        JSONObject Request = new JSONObject();
        try
        {
            Content.put("token", randomToken);
            
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
        log.debug(Thread.currentThread().toString() + "##doCommandDeviceNewConfigureLocal(deviceBssid=[" + deviceBssid
            + "],deviceSsid=[" + deviceSsid + "],deviceWifiCipherType=[" + deviceWifiCipherType + "],devicePassword=["
            + devicePassword + "],randomToken=[" + randomToken + "]): " + result);
        return true;
    }
    
}
