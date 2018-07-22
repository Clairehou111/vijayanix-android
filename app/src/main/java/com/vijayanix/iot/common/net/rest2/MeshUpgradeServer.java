package com.vijayanix.iot.common.net.rest2;


import com.vijayanix.iot.common.net.proxy.MeshCommunicationUtils;
import com.vijayanix.iot.model.http.HeaderPair;
import com.vijayanix.iot.util.Base64Util;
import com.vijayanix.iot.util.HttpStatus;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

public class MeshUpgradeServer
{
    private final Logger log = Logger.getLogger(MeshUpgradeServer.class);
    
    private final String ACTION = "action";

	private final String VERSION = "version";

	private final String GET = "get";
    
    private final byte[] mUser1Bin;
    
    private final byte[] mUser2Bin;
    
    private boolean mIsFirstPackage;
    
    private boolean mIsFinished;
    
    private final InetAddress mInetAddr;
    
    private final String mDeviceBssid;
    
    private boolean mIsSuc;
    
    // for mesh device upgrade local require the socket keep connection all the time,
    // mSerial is the tag to differ different sockets
    private volatile int mSerial;
    
    enum RequestType
    {
        INVALID, MESH_DEVICE_UPGRADE_LOCAL, MESH_DEVICE_UPGRADE_LOCAL_SUC, MESH_DEVICE_UPGRADE_LOCAL_FAIL
    }
    
    private MeshUpgradeServer(byte[] user1bin, byte[] user2bin, InetAddress inetAddr, String deviceBssid)
    {
        this.mUser1Bin = user1bin;
        this.mUser2Bin = user2bin;
        this.mInetAddr = inetAddr;
        this.mDeviceBssid = deviceBssid;
    }
    
    public static MeshUpgradeServer createInstance(byte[] user1bin, byte[] user2bin, InetAddress inetAddr,
                                                   String deviceBssid)
    {
        return new MeshUpgradeServer(user1bin, user2bin, inetAddr, deviceBssid);
    }
    
    /**
     * build mesh device upgrade request which is sent to mesh device
     * 
     * @param url the url of the request
     * @param version the version of the upgrade bin
     * @return the request which is sent to mesh device
     */
    private String buildMeshDeviceUpgradeRequest1(String url, String version)
    {
        HttpRequestBaseEntity requestEntity = new HttpRequestBaseEntity(GET, url);
	    String SYS_UPGRADE = "sys_upgrade";
	    requestEntity.putQueryParams(ACTION, SYS_UPGRADE);
        requestEntity.putQueryParams(VERSION, version);
	    String TRUE = "true";
	    String DELIVER_TO_DEVICE = "deliver_to_deivce";
	    requestEntity.putQueryParams(DELIVER_TO_DEVICE, TRUE);
        return requestEntity.toString();
    }
    
    /**
     * analyze mesh device upgrading response
     * 
     * @param response the response sent by mesh device
     * @return whether the mesh device is ready to upgrade
     */
    private boolean analyzeUpgradeResponse1(String response)
    {
        HttpResponseBaseEntity responseEntity = new HttpResponseBaseEntity(response);
        return responseEntity.isValid() && responseEntity.getStatus() == HttpStatus.SC_OK;
    }
    
    // generate long socket serial for long socket tag
    private void generateLongSocketSerial()
    {
        this.mSerial = MeshCommunicationUtils.generateLongSocketSerial();
    }
    
    /**
     * request mesh device upgrading
     * 
     * @param version the version of bin to be upgraded
     * @return whether the mesh device is ready to upgrade
     */
    public boolean requestUpgrade(String version)
    {
        generateLongSocketSerial();
        String url = "http://" + mInetAddr.getHostAddress() + "/v1/device/rpc/";
        // build request
        String request = buildMeshDeviceUpgradeRequest1(url, version);
        JSONObject postJSON = null;
        try
        {
            postJSON = new JSONObject(request);
        }
        catch (JSONException e)
        {
            throw new IllegalArgumentException("requestUpgrade() request isn't json :" + postJSON);
        }
        // send request to mesh device and receive the response
        int serial = mSerial;
        HeaderPair[] headers = null;
        JSONObject responseJson = MeshCommunicationUtils.JsonPost(url, mDeviceBssid, serial, postJSON, headers);
        if (responseJson == null)
        {
            log.warn("requestUpgrade() fail, return false");
            return false;
        }
        String responseStr = responseJson.toString();
        // analyze the response
        boolean isResponseSuc = analyzeUpgradeResponse1(responseStr);
        log.debug("requestUpgrade(): " + isResponseSuc);
        return isResponseSuc;
    }
    
    /**
     * analyze mesh device upgrading request
     * 
     * @param requestJson the request sent by mesh device
     * @return the request type
     */
    private RequestType analyzeUpgradeRequest1(JSONObject requestJson)
    {
        try
        {
            JSONObject jsonGet = requestJson.getJSONObject(GET);
            String action = jsonGet.getString(ACTION);
	        String DEVICE_UPGRADE_FAIL = "device_upgrade_failed";
	        String DEVICE_UPGRADE_SUC = "device_upgrade_success";
	        String DOWNLOAD_ROM_BASE64 = "download_rom_base64";
	        if (action.equals(DOWNLOAD_ROM_BASE64))
            {
                return RequestType.MESH_DEVICE_UPGRADE_LOCAL;
            }
            else if (action.equals(DEVICE_UPGRADE_SUC))
            {
                return RequestType.MESH_DEVICE_UPGRADE_LOCAL_SUC;
            }
            else if (action.equals(DEVICE_UPGRADE_FAIL))
            {
                return RequestType.MESH_DEVICE_UPGRADE_LOCAL_FAIL;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return RequestType.INVALID;
    }
    
    /**
     * execute mesh device upgrade local
     * 
     * @param requestJson the request from mesh device
     * @return the response to be sent to mesh device
     */
    private String executeMeshDeviceUpgradeLocal(JSONObject requestJson)
    {
        try
        {
            JSONObject jsonGet = requestJson.getJSONObject(GET);
            String action = jsonGet.getString(ACTION);
	        String FILE_NAME = "filename";
	        String filename = jsonGet.getString(FILE_NAME);
            String version = jsonGet.getString(VERSION);
            byte[] bin = null;
	        String USER2_BIN = "user2.bin";
	        String USER1_BIN = "user1.bin";
	        if (filename.equals(USER1_BIN))
            {
                bin = mUser1Bin;
            }
            else if (filename.equals(USER2_BIN))
            {
                bin = mUser2Bin;
            }
            else
            {
                log.warn("filename is invalid, it isn't 'user1.bin' or 'user2.bin'.");
                return null;
            }
            int total = bin.length;
	        String OFFSET = "offset";
	        int offset = jsonGet.getInt(OFFSET);
            log.debug("__executeMeshDeviceUpgradeLocal(): offset = " + offset);
	        String SIZE = "size";
	        int size = jsonGet.getInt(SIZE);
            log.debug("__executeMeshDeviceUpgradeLocal(): size = " + size);
            if (offset + size > total)
            {
                size = total - offset;
            }
            byte[] encoded = Base64Util.encode(bin, offset, size);
            int size_base64 = encoded.length;
            // Response is like this:
            // {"status": 200, "device_rom": {"rom_base64":
            // "6QMAAAQAEEAAABBAQGYAAAQOAEASwfAJAw==",
            // "filename": "user1.bin", "version": "v1.2", "offset": 0, "action":
            JSONObject jsonResponse = new JSONObject();
            JSONObject jsonDeviceRom = new JSONObject();
            jsonDeviceRom.put(FILE_NAME, filename);
            jsonDeviceRom.put(VERSION, version);
            jsonDeviceRom.put(OFFSET, offset);
	        String TOTAL = "total";
	        jsonDeviceRom.put(TOTAL, total);
            jsonDeviceRom.put(SIZE, size);
	        String SIZE_BASE64 = "size_base64";
	        jsonDeviceRom.put(SIZE_BASE64, size_base64);
            jsonDeviceRom.put(ACTION, action);
	        String ROM_BASE64 = "rom_base64";
	        jsonDeviceRom.put(ROM_BASE64, "__rombase64");
	        String DEVICE_ROM = "device_rom";
	        jsonResponse.put(DEVICE_ROM, jsonDeviceRom);
	        String STATUS = "status";
	        jsonResponse.put(STATUS, 200);
            return jsonResponse.toString().replace("__rombase64", new String(encoded));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * execute mesh device upgrade local suc
     * 
     * @return the response to be sent to mesh device
     */
    private String executeMeshDeviceUpgradeLocalSuc()
    {
        // set mIsFinished and mIsSuc
        mIsFinished = true;
        mIsSuc = true;
        // build reset request as the mesh device's response
        String uriStr = "http://" + mInetAddr.getHostAddress() + "/upgrade?action=sys_reboot";
        String method = "POST";
        HttpRequestBaseEntity requestEntity = new HttpRequestBaseEntity(method, uriStr);
        return requestEntity.toString();
    }
    
    /**
     * execute mesh device upgrade local fail
     */
    private void executeMeshDeviceUpgradeLocalFail()
    {
        // set mIsFinished and mIsSuc
        mIsFinished = true;
        mIsSuc = false;
    }
    
    /**
     * handle one request
     * 
     * @return whether handle suc
     */
    private boolean handle()
    {
        String url = "http://" + mInetAddr.getHostAddress() + "/v1/device/rpc/";
        String bssid = mDeviceBssid;
        int serial = mSerial;
        HeaderPair[] headers = null;
        // for device requirement, taskTimeout has to be set 15 seconds, but it won't take so much time except first 2
        // packages
        int taskTimeout = 15000;
        if (mIsFirstPackage)
        {
            taskTimeout = 15000;
            mIsFirstPackage = false;
        }
        // receive request from the mesh device
        JSONObject requestJson = MeshCommunicationUtils.JsonReadOnly(url, bssid, serial, taskTimeout, headers);
        if (requestJson == null)
        {
            log.warn("hancle(): requestJson is null, return false");
            return false;
        }
        log.debug("handle(): receive request from mesh device:" + requestJson);
        // analyze the request and build the response
        RequestType requestType = analyzeUpgradeRequest1(requestJson);
        String response = null;
        switch (requestType)
        {
            case INVALID:
                log.warn("handle(): requestType is INVALID");
                return false;
            case MESH_DEVICE_UPGRADE_LOCAL:
                log.debug("handle(): requestType is LOCAL");
                response = executeMeshDeviceUpgradeLocal(requestJson);
                break;
            case MESH_DEVICE_UPGRADE_LOCAL_FAIL:
                log.debug("handle(): requestType is LOCAL FAIL");
                executeMeshDeviceUpgradeLocalFail();
                break;
            case MESH_DEVICE_UPGRADE_LOCAL_SUC:
                log.debug("handle(): requestType is LOCAL SUC");
                response = executeMeshDeviceUpgradeLocalSuc();
                break;
        }
        // send the response to the mesh device
        if (response != null)
        {
            log.debug("handle(): send response to mesh device:" + response);
            JSONObject postJSON = null;
            try
            {
                postJSON = new JSONObject(response);
            }
            catch (JSONException e)
            {
                throw new IllegalArgumentException("response isn't json: " + postJSON);
            }
            boolean isWriteSuc = MeshCommunicationUtils.JsonNonResponsePost(url, bssid, serial, postJSON) != null;
            log.debug("handle(): send response to mesh device isSuc:" + isWriteSuc);
            return isWriteSuc;
        }
        return false;
    }
    
    public boolean listen(long timeout)
    {
        // clear mIsSuc and mIsFinished
        mIsSuc = false;
        mIsFinished = false;
        mIsFirstPackage = true;
        
        long start = System.currentTimeMillis();
        while (!mIsFinished && System.currentTimeMillis() - start < timeout)
        {
            if (!handle())
            {
                log.warn("listen() handle() fail");
                executeMeshDeviceUpgradeLocalFail();
                break;
            }
        }
        
        if (!mIsFinished && !mIsSuc)
        {
            log.warn("listen fail for timeout:" + timeout + " ms");
        }
        
        return mIsSuc;
    }
}
