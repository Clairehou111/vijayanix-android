package com.vijayanix.iot.command.device.common;


import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

public class CommandDeviceConfigureLocal implements ICommandDeviceConfigureLocal
{
    
    private final static Logger log = Logger.getLogger(CommandDeviceConfigureLocal.class);
    
    @Override
    public String getLocalUrl(InetAddress inetAddress)
    {
        return "http://" + inetAddress.getHostAddress() + "/" + "config?command=wifi";
    }
    
    private JSONObject createJSONReq(String apSsid, String apPassword, String randomToken)
    {
        JSONObject Content = new JSONObject();
        JSONObject Connect_Station = new JSONObject();
        JSONObject Station = new JSONObject();
        JSONObject Request = new JSONObject();
        try
        {
            if (randomToken != null)
            {
                Content.put("token", randomToken);
            }
            if (apSsid != null)
            {
                Content.put("password", apPassword);
                Content.put("ssid", apSsid);
            }
            
            Connect_Station.put("Connect_Station", Content);
            
            Station.put("Station", Connect_Station);
            
            Request.put("Request", Station);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return Request;
    }
    
    @Override
    public boolean doCommandDeviceConfigureLocal(InetAddress inetAddress, String apSsid, String apPassword,
                                                 String randomToken)
    {
        String urlString = getLocalUrl(inetAddress);
        JSONObject request = createJSONReq(apSsid, apPassword, randomToken);
        JSONObject result = BaseApiUtil.Post(urlString, request);
        boolean isSuc = result != null;
        log.debug(Thread.currentThread().toString() + "##doCommandDeviceConfigureLocal(inetAddress=[" + inetAddress
            + "],apSsid=[" + apSsid + "],apPassword=[" + apPassword + "],randomToken=[" + randomToken + "]): " + isSuc);
        return isSuc;
    }
    
    @Override
    public boolean doCommandDeviceConfigureLocal(InetAddress inetAddress, String apSsid, String apPassword)
    {
        String urlString = getLocalUrl(inetAddress);
        JSONObject request = createJSONReq(apSsid, apPassword, null);
        JSONObject result = BaseApiUtil.Post(urlString, request);
        boolean isSuc = result != null;
        log.debug(Thread.currentThread().toString() + "##doCommandDeviceConfigureLocal(inetAddress=[" + inetAddress
            + "],apSsid=[" + apSsid + "],apPassword=[" + apPassword + "]): " + isSuc);
        return isSuc;
    }
    
    @Override
    public boolean doCommandDeviceConfigureLocal(InetAddress inetAddress, String randomToken)
    {
        String urlString = getLocalUrl(inetAddress);
        JSONObject request = createJSONReq(null, null, randomToken);
        JSONObject result = BaseApiUtil.Post(urlString, request);
        boolean isSuc = result != null;
        log.debug(Thread.currentThread().toString() + "##doCommandDeviceConfigureLocal(inetAddress=[" + inetAddress
            + "],randomToken=[" + randomToken + "]): " + isSuc);
        return isSuc;
    }
    
    @Override
    public boolean doCommandMeshDeviceConfigureLocal(String deviceBssid, InetAddress inetAddress, String apSsid,
                                                     String apPassword, String randomToken)
    {
        String urlString = getLocalUrl(inetAddress);
        JSONObject request = createJSONReq(apSsid, apPassword, randomToken);
        JSONObject result = BaseApiUtil.PostForJson(urlString, deviceBssid, request);
        boolean isSuc = result != null;
        log.debug(Thread.currentThread().toString() + "##doCommandMeshDeviceConfigureLocal(deviceBssid=[" + deviceBssid
            + "],inetAddress=[" + inetAddress + "],apSsid=[" + apSsid + "],apPassword=[" + apPassword
            + "],randomToken=[" + randomToken + "]): " + isSuc);
        return isSuc;
    }
    
    @Override
    public boolean doCommandMeshDeviceConfigureLocal(String deviceBssid, InetAddress inetAddress, String apSsid,
                                                     String apPassword)
    {
        String urlString = getLocalUrl(inetAddress);
        JSONObject request = createJSONReq(apSsid, apPassword, null);
        JSONObject result = BaseApiUtil.PostForJson(urlString, deviceBssid, request);
        boolean isSuc = result != null;
        log.debug(Thread.currentThread().toString() + "##doCommandMeshDeviceConfigureLocal(deviceBssid=[" + deviceBssid
            + "],inetAddress=[" + inetAddress + "],apSsid=[" + apSsid + "],apPassword=[" + apPassword + "]): " + isSuc);
        return isSuc;
    }
    
    @Override
    public boolean doCommandMeshDeviceConfigureLocal(String deviceBssid, InetAddress inetAddress, String randomToken)
    {
        String urlString = getLocalUrl(inetAddress);
        JSONObject request = createJSONReq(null, null, randomToken);
        JSONObject result = BaseApiUtil.PostForJson(urlString, deviceBssid, request);
        boolean isSuc = result != null;
        log.debug(Thread.currentThread().toString() + "##doCommandMeshDeviceConfigureLocal(deviceBssid=[" + deviceBssid
            + "],inetAddress=[" + inetAddress + "],randomToken=[" + randomToken + "]): " + isSuc);
        return isSuc;
    }
    
}
