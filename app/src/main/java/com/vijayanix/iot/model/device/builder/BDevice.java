package com.vijayanix.iot.model.device.builder;


import com.vijayanix.iot.db.interfa.IDeviceDB;
import com.vijayanix.iot.model.device.DeviceState;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.IDeviceState;
import com.vijayanix.iot.model.device.IOTAddress;
import com.vijayanix.iot.model.device.array.DevicePlugArray;
import com.vijayanix.iot.model.device.array.IDeviceArray;
import com.vijayanix.iot.model.device.configure.IDeviceConfigure;
import com.vijayanix.iot.model.device.plug.DevicePlug;
import com.vijayanix.iot.model.device.plugs.DevicePlugs;
import com.vijayanix.iot.model.device.watchman.DeviceWatchman;
import com.vijayanix.iot.model.user.builder.BUser;
import com.vijayanix.iot.util.BSSIDUtil;
import com.vijayanix.iot.util.RandomUtil;

public class BDevice implements IBDevice
{
    /*
     * Singleton lazy initialization start
     */
    private BDevice()
    {
    }
    
    private static class InstanceHolder
    {
        static BDevice instance = new BDevice();
    }
    
    public static BDevice getInstance()
    {
        return InstanceHolder.instance;
    }
    
    /*
     * Singleton lazy initialization end
     */
    
    @Override
    public IDevice alloc(String deviceName, long deviceId, String deviceKey, boolean isOwner, String bssid,
                         int state, int ptype, String rom_version, String latest_rom_version, long userId, long... timestamp)
    {
        IDevice device = null;
        IDeviceState deviceState = new DeviceState(state);
        DeviceType deviceType = DeviceType.getEspTypeEnumBySerial(ptype);
        switch (deviceType)
        {
             case PLUG:
                device = new DevicePlug();
                break;

            case PLUGS:
                device = new DevicePlugs();
                break;

	        case WATCHMAN:
		        device = new DeviceWatchman();
		        break;
        }
        device.setId(deviceId);
        device.setKey(deviceKey);
        device.setIsOwner(isOwner);
        device.setBssid(bssid);
        device.setDeviceState(deviceState);
        device.setDeviceType(deviceType);
        device.setRom_version(rom_version);
        device.setLatest_rom_version(latest_rom_version);
        device.setUserId(userId);
        device.setName(deviceName);
        if (timestamp.length != 0)
        {
            device.setTimestamp(timestamp[0]);
        }
        return device;
    }
    
    @Override
    public IDevice alloc(IDeviceDB deviceDB)
    {
        String deviceName = deviceDB.getName();
        long deviceId = deviceDB.getId();
        String deviceKey = deviceDB.getKey();
        boolean isOwner = deviceDB.getIsOwner();
        String bssid = deviceDB.getBssid();
        int state = deviceDB.getState();
        int ptype = deviceDB.getType();
        String rom_version = deviceDB.getRom_version();
        String latest_rom_version = deviceDB.getLatest_rom_version();
        long userId = deviceDB.getUserId();
        long timestamp = deviceDB.getTimestamp();
        long activatedTime = deviceDB.getActivatedTime();
        IDevice device =
            alloc(deviceName,
                deviceId,
                deviceKey,
                isOwner,
                bssid,
                state,
                ptype,
                rom_version,
                latest_rom_version,
                userId,
                timestamp);
        device.setActivatedTime(activatedTime);
        
        return device;
    }
    
    public static IDeviceArray createDeviceArray(DeviceType deviceType)
    {
        switch(deviceType)
        {

            case PLUG:
                DevicePlugArray plugArray = new DevicePlugArray();
                plugArray.setName(deviceType.toString());
                plugArray.setDeviceType(deviceType);
                plugArray.setKey(RandomUtil.randomString(41));
                plugArray.setIsMeshDevice(false);
                IDeviceState plugState = new DeviceState();
                plugState.addStateInternet();
                plugState.addStateLocal();
                plugArray.setDeviceState(plugState);
                return plugArray;
                
            case FLAMMABLE:
            case HUMITURE:
            case NEW:
            case PLUGS:
            case REMOTE:
            case ROOT:
            case VOLTAGE:
            case SOUNDBOX:
                break;
        }
        
        return null;
    }

    // -1, -2, ... is used to activate softap device by direct connect,
    // 1, 2, ... is used by server
    private static long mIdCreator = -Long.MAX_VALUE / 2;

    private synchronized long getNextId() {
        return --mIdCreator;
    }

    public IDevice createStaDevice(IOTAddress iotAddress) {
        IDevice device = null;
        switch (iotAddress.getDeviceTypeEnum()) {
            case PLUG:
                device = new DevicePlug();
                break;

            case PLUGS:
                device = new DevicePlugs();
                break;
	        case WATCHMAN:
		        device = new DeviceWatchman();
		        break;
        }

        if (device != null) {
            DeviceState stateLocal = new DeviceState();
            stateLocal.addStateLocal();
            device.setDeviceState(stateLocal);
            device.setName(iotAddress.getSSID());
            device.setBssid(iotAddress.getBSSID());
            device.setInetAddress(iotAddress.getInetAddress());
            device.setDeviceType(iotAddress.getDeviceTypeEnum());
            device.setParentDeviceBssid(iotAddress.getParentBssid());
            device.setIsMeshDevice(iotAddress.isMeshDevice());
            //device.setKey(iotAddress.getBSSID());
            device.setId(getNextId());
            device.setIsOwner(false);
            device.setRom_version(iotAddress.getRomVersion());
            device.setRssi(iotAddress.getRssi());
            device.setInfo(iotAddress.getInfo());
            device.setUserId(BUser.getBuilder().getInstance().getUserId());
        }

        return device;
    }

    public IDevice createClearDevice(IOTAddress iotAddress) {
        IDevice device = null;
        switch (iotAddress.getDeviceTypeEnum()) {
            case PLUG:
                device = new DevicePlug();
                break;

            case PLUGS:
                device = new DevicePlugs();
                break;

            case WATCHMAN:
                device = new DeviceWatchman();
                break;

        }

        if (device != null) {
            DeviceState stateLocal = new DeviceState();
            device.setDeviceState(stateLocal);
            device.setName(iotAddress.getSSID());
            device.setBssid(iotAddress.getBSSID());
            device.setInetAddress(iotAddress.getInetAddress());
            device.setDeviceType(iotAddress.getDeviceTypeEnum());
            device.setParentDeviceBssid(iotAddress.getParentBssid());
            device.setIsMeshDevice(iotAddress.isMeshDevice());
            //device.setKey(iotAddress.getBSSID());
            device.setId(getNextId());
            device.setIsOwner(false);
            device.setRom_version(iotAddress.getRomVersion());
            device.setRssi(iotAddress.getRssi());
            device.setInfo(iotAddress.getInfo());
            device.setUserId(BUser.getBuilder().getInstance().getUserId());
        }

        return device;
    }

    public IDevice createClearDevice(DeviceType deviceType, String deviceKey, String bssid) {
        IDevice device = null;
        switch (deviceType) {
            case PLUG:
                device = new DevicePlug();
                break;

            case PLUGS:
                device = new DevicePlugs();
                break;

	        case WATCHMAN:
		        device = new DeviceWatchman();
		        break;
        }

        if (device != null) {
            DeviceState stateLocal = new DeviceState();
            device.setDeviceState(stateLocal);
	        device.setName(BSSIDUtil.genDeviceNameByBSSID(bssid));
            device.setBssid(bssid);
            device.setDeviceType(deviceType);
            device.setKey(deviceKey);
            device.setId(getNextId());
            device.setIsOwner(false);
            device.setUserId(BUser.getBuilder().getInstance().getUserId());
        }

        return device;
    }

    public IDeviceConfigure createConfiguringDevice(IDevice staDevice, String random40) {
        IDeviceConfigure device = BDeviceConfigure.getInstance().alloc(staDevice.getBssid(), random40);
        device.setInetAddress(staDevice.getInetAddress());
        device.setIsMeshDevice(staDevice.getIsMeshDevice());
        device.setParentDeviceBssid(staDevice.getParentDeviceBssid());
        device.setName(staDevice.getName());
        return device;
    }
}
