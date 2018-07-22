package com.vijayanix.iot.model.device.configure;



import com.vijayanix.iot.action.device.common.ActionDeviceConfigureLocal;
import com.vijayanix.iot.action.device.common.IActionDeviceConfigureLocal;
import com.vijayanix.iot.action.device.configure.ActionDeviceConfigureActivateInternet;
import com.vijayanix.iot.action.device.configure.IActionDeviceConfigureActivateInternet;
import com.vijayanix.iot.model.device.Device;
import com.vijayanix.iot.model.device.DeviceState;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.user.IUser;
import com.vijayanix.iot.model.user.builder.BUser;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.concurrent.Future;

public class DeviceConfigure extends Device implements IDeviceConfigure
{
    
    private final static Logger log = Logger.getLogger(DeviceConfigure.class);
    
    private Future<?> mFuture;
    
    public DeviceConfigure(String bssid, String randomToken)
    {
        // DeviceConfigure's deviceId should be 0
        // although the default value is 0, assign the value here just to make it significant
        this.mDeviceId = 0;
        this.mBssid = bssid;
        this.mDeviceType = DeviceType.NEW;
        this.mDeviceState = new DeviceState();
        this.mDeviceState.addStateNew();
        setKey(randomToken);
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        if (mFuture != null)
        {
            log.info(Thread.currentThread().toString() + "##cancel(mayInterruptIfRunning=[" + mayInterruptIfRunning
                + "])");
            boolean result = this.mFuture.cancel(true);
            this.mFuture = null;
            return result;
        }
        return true;
    }
    
    @Override
    public void setFuture(Future<?> future)
    {
        this.mFuture = future;
    }
    
    @Override
    public IDevice doActionDeviceConfigureActivateInternet(long userId, String userKey, String randomToken)
    {
        String deviceName = mDeviceName;
        IActionDeviceConfigureActivateInternet action = new ActionDeviceConfigureActivateInternet();
        IDevice newDevice = action.doActionDeviceConfigureActivateInternet(userId, userKey, randomToken);
        if (newDevice != null && !newDevice.getName().equals(deviceName))
        {
            newDevice.setName(deviceName);
            IUser user = BUser.getBuilder().getInstance();
           // user.doActionRename(newDevice, deviceName);
        }
        return newDevice;
    }
    
    @Override
    public boolean doActionDeviceConfigureLocal(boolean discoverRequired, InetAddress inetAddress, String apSsid,
                                                String apPassword, String randomToken, String deviceBssid)
    {
        IActionDeviceConfigureLocal action = new ActionDeviceConfigureLocal();
        return action.doActionDeviceConfigureLocal(discoverRequired,
            inetAddress,
            apSsid,
            apPassword,
            randomToken,
            deviceBssid);
    }
    
    @Override
    public boolean doActionDeviceConfigureLocal(boolean discoverRequired, InetAddress inetAddress, String apSsid,
                                                String apPassword, String deviceBssid)
    {
        IActionDeviceConfigureLocal action = new ActionDeviceConfigureLocal();
        return action.doActionDeviceConfigureLocal(discoverRequired, inetAddress, apSsid, apPassword, deviceBssid);
    }
    
    @Override
    public boolean doActionDeviceConfigureLocal(boolean discoverRequired, InetAddress inetAddress, String randomToken,
                                                String deviceBssid)
    {
        IActionDeviceConfigureLocal action = new ActionDeviceConfigureLocal();
        return action.doActionDeviceConfigureLocal(discoverRequired, inetAddress, randomToken, deviceBssid);
    }
    
    @Override
    public boolean doActionMeshDeviceConfigureLocal(boolean discoverRequired, String deviceBssid,
                                                    InetAddress inetAddress, String apSsid, String apPassword, String randomToken)
    {
        IActionDeviceConfigureLocal action = new ActionDeviceConfigureLocal();
        return action.doActionMeshDeviceConfigureLocal(discoverRequired,
            deviceBssid,
            inetAddress,
            apSsid,
            apPassword,
            randomToken);
    }
    
    @Override
    public boolean doActionMeshDeviceConfigureLocal(boolean discoverRequired, String deviceBssid,
                                                    InetAddress inetAddress, String apSsid, String apPassword)
    {
        IActionDeviceConfigureLocal action = new ActionDeviceConfigureLocal();
        return action.doActionMeshDeviceConfigureLocal(discoverRequired, deviceBssid, inetAddress, apSsid, apPassword);
    }
    
    @Override
    public boolean doActionMeshDeviceConfigureLocal(boolean discoverRequired, String deviceBssid,
                                                    InetAddress inetAddress, String randomToken)
    {
        IActionDeviceConfigureLocal action = new ActionDeviceConfigureLocal();
        return action.doActionMeshDeviceConfigureLocal(discoverRequired, deviceBssid, inetAddress, randomToken);
    }
    
}
