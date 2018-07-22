package com.vijayanix.iot.action.device.New;


import com.vijayanix.iot.command.device.New.CommandDeviceNewConfigureLocal;
import com.vijayanix.iot.command.device.New.ICommandDeviceNewConfigureLocal;
import com.vijayanix.iot.model.device.DeviceState;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.common.net.wifi.WifiCipherType;
import com.vijayanix.iot.db.DeviceDBManager;
import com.vijayanix.iot.model.user.builder.BUser;
import com.vijayanix.iot.util.BSSIDUtil;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;

public class ActionDeviceNewConfigureLocal implements IActionDeviceNewConfigureLocal
{
    private final static Logger log = Logger.getLogger(ActionDeviceNewConfigureLocal.class);
    
    @Override
    public long doActionDeviceNewConfigureLocal(String deviceBssid, String deviceSsid,
                                                WifiCipherType deviceWifiCipherType, String devicePassword, String apSsid, WifiCipherType apWifiCipherType,
                                                String apPassword, String randomToken)
        throws InterruptedException
    {
        boolean result = false;
        long deviceId = 0;
        // 1. connect to device
        boolean connectDeviceResult = BaseApiUtil.connect(deviceSsid, deviceWifiCipherType, devicePassword);
        // 2. post configure info(do CommandDeviceNewConfigureLocal)
        if (connectDeviceResult)
        {
            ICommandDeviceNewConfigureLocal command = new CommandDeviceNewConfigureLocal();
            result =
                command.doCommandDeviceNewConfigureLocal(deviceSsid,
                    deviceWifiCipherType,
                    devicePassword,
                    apSsid,
                    apWifiCipherType,
                    apPassword,
                    randomToken);
        }
        
        if (result)
        {
            String key = randomToken;
            String bssid = deviceBssid;
            int type = DeviceType.NEW.getSerial();
            DeviceState deviceState = new DeviceState();
            deviceState.addStateActivating();
            int state = deviceState.getStateValue();
            String name = BSSIDUtil.genDeviceNameByBSSID(deviceBssid);
            String rom_version = "";
            String latest_rom_version = "";
            long timestamp = System.currentTimeMillis();
            long userId = BUser.getBuilder().getInstance().getUserId();
            deviceId =
                DeviceDBManager.getInstance().insertActivatingDevice(key,
                    bssid,
                    type,
                    state,
                    name,
                    rom_version,
                    latest_rom_version,
                    timestamp,
                    userId);
        }
//        Thread.sleep(500);
        log.debug(Thread.currentThread().toString() + "##doActionDeviceNewConfigureLocal(deviceBssid=[" + deviceBssid
            + "],deviceSsid=[" + deviceSsid + "],deviceWifiCipherType=[" + deviceWifiCipherType + "],devicePassword=["
            + devicePassword + "],apSsid=[" + apSsid + "],apWifiCipherType=[" + apWifiCipherType + "],apPassword=["
            + apPassword + "],randomToken=[" + randomToken + "]): " + deviceId);
        return deviceId;
    }
    
}
