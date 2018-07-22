package com.vijayanix.iot.model.device.cache;



import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.IOTAddress;

import java.util.List;

/**
 * the cache to store devices to be processed by others
 * 
 * onhand: indicate that the device require handling, which is used by @see IDeviceCacheHandler. worked: indicate
 * that the device has been handled. which is used by @see IUser to diplay on UI
 * 

 * 
 */
public interface IDeviceCache
{
    enum NotifyType
    {
        PULL_REFRESH, STATE_MACHINE_BACKSTATE, STATE_MACHINE_UI
    }

    void clear();
    
    boolean addTransformedDeviceCache(IDevice device);
    
    boolean addTransformedDeviceCacheList(List<IDevice> devieList);

    List<IDevice> pollTransformedDeviceCacheList();
    
    boolean addServerLocalDeviceCache(IDevice device);
    
    boolean addServerLocalDeviceCacheList(List<IDevice> deviceList);

    List<IDevice> pollServerLocalDeviceCacheList();
    
    boolean addLocalDeviceCacheList(List<IOTAddress> deviceIOTAddressList);
    
    List<IOTAddress> pollLocalDeviceCacheList();
    
    boolean addStatemahchineDeviceCache(IDevice device);
    
    IDevice pollStatemachineDeviceCache();
    
    boolean addSharedDeviceCache(IDevice device);
    
    IDevice pollSharedDeviceCache();
    
    boolean addUpgradeSucLocalDeviceCacheList(List<IOTAddress> deviceIOTAddressList);
    
    List<IOTAddress> pollUpgradeSucLocalDeviceCacheList();
    
    boolean addStaDeviceCache(IOTAddress deviceStaDevice);
    
    boolean addStaDeviceCacheList(List<IOTAddress> deviceStaDeviceList);

    List<IOTAddress> pollStaDeviceCacheList();
    /**
     * notify the user that device cache has been changed
     */
    void notifyIUser(NotifyType type);
}
