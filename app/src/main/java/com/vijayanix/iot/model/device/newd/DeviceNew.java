package com.vijayanix.iot.model.device.newd;

import android.text.TextUtils;

import com.vijayanix.iot.action.device.New.ActionDeviceNewConfigureLocal;
import com.vijayanix.iot.action.device.New.IActionDeviceNewConfigureLocal;
import com.vijayanix.iot.model.device.Device;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.DeviceState;
import com.vijayanix.iot.common.net.wifi.WifiCipherType;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.statemachine.DeviceStateMachine;
import com.vijayanix.iot.model.statemachine.IDeviceStateMachine;
import com.vijayanix.iot.model.statemachine.IDeviceStateMachine.Direction;
import com.vijayanix.iot.model.user.IUser;
import com.vijayanix.iot.model.user.builder.BUser;
import com.vijayanix.iot.util.BSSIDUtil;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class DeviceNew extends Device implements IDeviceNew
{
    private final static Logger log = Logger.getLogger(DeviceNew.class);
    
    private int mRssi;
    
    private WifiCipherType mWifiCipherType;
    
    private String mSsid;
    
    private String mApSsid;
    
    private WifiCipherType mApWifiCipherType;
    
    private String mApPassword;
    
    private Future<?> mFuture;
    
    public DeviceNew(String ssid, String bssid, WifiCipherType wifiCipherType, int rssi)
    {
        // DeviceNew's deviceId should be 0
        // although the default value is 0, assign the value here just to make it significant
        this.mDeviceId = 0;
        this.mSsid = ssid;
        this.mBssid = bssid;
        this.mWifiCipherType = wifiCipherType;
        this.mRssi = rssi;
        this.mDeviceName = BSSIDUtil.genDeviceNameByBSSID(bssid);
        this.mDeviceType = DeviceType.NEW;
    }
    
    public DeviceNew(String ssid, String bssid, WifiCipherType wifiCipherType, int rssi, int state)
    {
        // DeviceNew's deviceId should be 0
        // although the default value is 0, assign the value here just to make it significant
        this.mDeviceId = 0;
        this.mDeviceState = new DeviceState(state);
        this.mSsid = ssid;
        this.mBssid = bssid;
        this.mWifiCipherType = wifiCipherType;
        this.mRssi = rssi;
        this.mDeviceName = BSSIDUtil.genDeviceNameByBSSID(bssid);
        this.mDeviceType = DeviceType.NEW;
    }
    
    @Override
    public void setFuture(Future<?> future)
    {
        this.mFuture = future;
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        if (this.mFuture != null)
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
    public void resume()
    {
        // mFuture!=null means that the task is executing
        if (mFuture == null)
        {
            log.info(Thread.currentThread().toString() + "##resume(): start");
            final IDeviceStateMachine stateMachine = DeviceStateMachine.getInstance();
            Callable<?> task = new Callable<IDevice>()
            {
                @Override
                public IDevice call()
                    throws Exception
                {
                    IUser user = BUser.getBuilder().getInstance();
                    long userId = user.getUserId();
                    String userKey = user.getUserKey();
                    String randomToken = DeviceNew.this.getKey();
                    long negativeDeviceId = DeviceNew.this.getId();
                    IDevice result =
                        DeviceNew.this.doActionDeviceNewActivateInternet(userId,
                            userKey,
                            randomToken,
                            negativeDeviceId);
                    if (result != null)
                    {
                        stateMachine.transformState(result, Direction.SUC);
                        return result;
                    }
                    // note: if fail, must return null instead of False
                    else
                    {
                        return null;
                    }
                }
            };
            Runnable taskSuc = null;
            Runnable taskFail = new Runnable()
            {
                
                @Override
                public void run()
                {
                    log.info(Thread.currentThread().toString() + "##resume(): fail");
                    stateMachine.transformState(DeviceNew.this, Direction.FAIL);
                }
                
            };
            Future<?> future = BaseApiUtil.submit(task, taskSuc, taskFail, null);
            DeviceNew.this.setFuture(future);
        }
    }
    
    @Override
    public void setRssi(int rssi)
    {
        this.mRssi = rssi;
    }
    
    @Override
    public int getRssi()
    {
        return this.mRssi;
    }
    
    @Override
    public WifiCipherType getWifiCipherType()
    {
        return this.mWifiCipherType;
    }
    
    @Override
    public String getDefaultPassword()
    {
        if (getIsMeshDevice())
        {
            return "vijayanix";
        }
        else
        {
            final String softap_pwd_first_half = BSSIDUtil.restoreSoftApBSSID(mBssid);
            final String softap_pwd_second_half = "_v*%W>L<@i&Nxe!";
            return softap_pwd_first_half + softap_pwd_second_half;
        }
    }
    
    @Override
    public void setSsid(String ssid)
    {
        this.mSsid = ssid;
    }
    
    @Override
    public String getSsid()
    {
        return this.mSsid;
    }
    
    @Override
    public void setApSsid(String apSsid)
    {
        this.mApSsid = apSsid;
    }
    
    @Override
    public String getApSsid()
    {
        return this.mApSsid;
    }
    
    @Override
    public void setApWifiCipherType(WifiCipherType apWifiCipherType)
    {
        this.mApWifiCipherType = apWifiCipherType;
    }
    
    @Override
    public WifiCipherType getApWifiCipherType()
    {
        return this.mApWifiCipherType;
    }
    
    @Override
    public void setApPassword(String apPassword)
    {
        this.mApPassword = apPassword;
    }
    
    @Override
    public String getApPassword()
    {
        return this.mApPassword;
    }
    
    @Override
    public void saveInDB()
    {
        // ignore
    }
    
    @Override
    public long doActionDeviceNewConfigureLocal(String deviceBssid, String deviceSsid,
                                                WifiCipherType deviceWifiCipherType, String devicePassword, String apSsid, WifiCipherType apWifiCipherType,
                                                String apPassword, String randomToken)
        throws InterruptedException
    {
        IActionDeviceNewConfigureLocal action = new ActionDeviceNewConfigureLocal();
        this.mDeviceId =
            action.doActionDeviceNewConfigureLocal(deviceBssid,
                deviceSsid,
                deviceWifiCipherType,
                devicePassword,
                apSsid,
                apWifiCipherType,
                apPassword,
                randomToken);
        // if device configure suc, save random token as device key
        if (this.mDeviceId != 0)
        {
            this.mDeviceKey = randomToken;
        }
        return this.mDeviceId;
    }
    
    @Override
    public IDevice doActionDeviceNewActivateInternet(long userId, String userKey, String randomToken,
                                                     long negativeDeviceId)
        throws InterruptedException
    {
//        String deviceName = mDeviceName;
//        IActionDeviceNewActivateInternet action = new EspActionDeviceNewActivateInternet();
//        IDevice device = action.doActionDeviceNewActivateInternet(userId, userKey, randomToken, this.mDeviceId);
//        if (!device.getName().equals(mDeviceName))
//        {
//            device.setName(deviceName);
//            IUser user = BUser.getBuilder().getInstance();
//           // user.doActionRename(device, deviceName);
//        }
//        return device;
        return  null;
    }
    
    @Override
    public boolean getIsMeshDevice()
    {
        if (TextUtils.isEmpty(mSsid))
        {
            return false;
        }
        
        for (String meshPrefix : IUser.MESH_DEVICE_SSID_PREFIX)
        {
            if (mSsid.startsWith(meshPrefix))
            {
                return true;
            }
        }
        
        return false;
    }
    
}
