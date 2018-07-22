package com.vijayanix.iot.model.device;



import java.net.InetAddress;
import java.util.List;

/**
 * the root interfa for all Esp Device
 * 
 * isStateEqual>equal>isSimilar isStateEqual: the device's id and state is equal equal: the device's id is equal
 * isSimilar: the device's bssid equal
 *

 * 
 */
public interface IDevice extends IDeviceTopic
{
    String DEFAULT_MESH_PASSWORD = "";
    /**
     * Set the bssid of the device
     * 
     * @param bssid
     */
    void setBssid(String bssid);
    
    /**
     * Get the bssid of the device
     * 
     * @return the bssid
     */
    String getBssid();
    
    /**
     * Get the device id
     * 
     * @return the device id
     */
    long getId();
    
    /**
     * Set the device id
     * 
     * @param id
     */
    void setId(long id);
    
    /**
     * Get the device key
     * 
     * @return device key
     */
    String getKey();
    
    /**
     * Set the device key
     * 
     * @param key
     */
    void setKey(String key);
    
    /**
     * Whether the user is owner of the device
     * 
     * @return user is owner or not
     */
    boolean getIsOwner();
    
    /**
     * Set whether the user is owner of the device
     * 
     * @param isOwner
     */
    void setIsOwner(boolean isOwner);
    
    /**
     * Get the device name
     * 
     * @return device name
     */
    String getName();
    
    /**
     * Set the device name
     * 
     * @param name
     */
    void setName(String name);
    
    /**
     * Get the rom version of the device
     * 
     * @return the rom version
     */
    String getRom_version();
    
    /**
     * Set the rom version of the device
     * 
     * @param rom_version
     */
    void setRom_version(String rom_version);
    
    /**
     * Get the latest rom version
     * 
     * @return the latest rom version
     */
    String getLatest_rom_version();
    
    /**
     * Set the latest rom version
     * 
     * @param latest_rom_version
     */
    void setLatest_rom_version(String latest_rom_version);
    
    /**
     * Get the timestamp
     * 
     * @return timestamp
     */
    long getTimestamp();
    
    /**
     * Set the timestamp
     * 
     * @param timestamp
     */
    void setTimestamp(long timestamp);
    
    /**
     * Get activated time on server
     * 
     * @return activate time
     */
    long getActivatedTime();
    
    /**
     * Set activated time on server
     * 
     * @param activatedAt
     */
    void setActivatedTime(long activatedAt);
    
    /**
     * Get the user id of this device
     * 
     * @return user id
     */
    long getUserId();
    
    /**
     * Set the user id of this device
     * 
     * @param userId
     */
    void setUserId(long userId);
    
    /**
     * Set the device InetAddress
     * 
     * @param inetAddress
     */
    void setInetAddress(InetAddress inetAddress);
    
    /**
     * Get the device InetAddress
     * 
     * @return the device InetAddress
     */
    InetAddress getInetAddress();
    
    /**
     * Set whether the device is mesh device
     * 
     * @param isMeshDevice the device is mesh device
     */
    void setIsMeshDevice(boolean isMeshDevice);
    
    /**
     * Get whether the device is mesh device
     * 
     * @return whether the device is mesh device
     */
    boolean getIsMeshDevice();
    
    /**
     * Set the parent device bssid in mesh device
     * 
     * @param parentBssid the parent device bssid(root's parent bssid is null)
     */
    void setParentDeviceBssid(String parentBssid);
    
    /**
     * Get the parent device bssid in mesh device
     * 
     * @return the parent device bssid in mesh device(root's parent bssid is null)
     */
    String getParentDeviceBssid();
    
    /**
     * Set root device bssid of device's mesh group
     * 
     * @param rootBssid
     */
    void setRootDeviceBssid(String rootBssid);
    
    /**
     * Get device's mesh group root device bssid. If the device is root device, return bssid of itself.
     * 
     * @return
     */
    String getRootDeviceBssid();
    
    /**
     * The value means can't get rssi vale of the device
     */
    int RSSI_NULL = 1;
    
    /**
     * Set device rssi
     */
    void setRssi(int rssi);
    
    /**
     * 
     * @return rssi of the device
     */
    int getRssi();
    
    /**
     * Set device info
     * 
     * @param info
     */
    void setInfo(String info);
    
    /**
     * 
     * @return device info
     */
    String getInfo();
    
    /*
     * refreshed means the device on Server maybe dirty, so ignore Server info about the device until refreshed is
     * cleared
     */
    void __setDeviceRefreshed();
    
    void __clearDeviceRefreshed();
    
    boolean __isDeviceRefreshed();
    
    /**
     * @see DeviceType set the device type
     * @param deviceType
     */
    void setDeviceType(DeviceType deviceType);
    
    /**
     * @see DeviceType
     * @return the device type
     */
    DeviceType getDeviceType();
    
    /**
     * @see IDeviceState set the device state
     * @param deviceState the device state
     */
    void setDeviceState(IDeviceState deviceState);
    
    /**
     * @see IDeviceState
     * @return the device state
     */
    IDeviceState getDeviceState();
    

    
    /**
     * Whether the device support timer
     * 
     * @return
     */
    boolean isSupportTimer();
    
    /**
     * Whether the device support trigger
     * 
     * @return
     */
    boolean isSupportTrigger();
    
    /**
     * save the IDevice in local db
     */
    void saveInDB();
    
    /**
     * delete the IDevice in local db
     */
    void deleteInDB();
    
    /**
     * clear the device related resources
     */
    void clear();
    
    /**
     * whether the device is similar(the same bssid is equal)
     * 
     * @param device
     * @return whether the device is similar
     */
    boolean isSimilar(IDevice device);
    
    /**
     * 
     * whether both of device id and state are equal
     * 
     * @param device
     * @return whether both of device id and state are equal
     */
    boolean isStateEqual(IDevice device);
    
    /**
     * Override the Object method equals
     * 
     * @param o the object to compare this instance with.
     * @return {@code true} if the specified object is equal to this {@code Object}; {@code false} otherwise.
     */
    boolean equals(Object o);
    
    /**
     * Override the Object method hashCode
     * 
     * @return the hasCode
     */
    int hashCode();
    
    /**
     * Override the Object method clone
     * 
     * @return the cloned Object
     */
    Object clone()
        throws CloneNotSupportedException;
    
    /**
     * deep clone the IDevice
     * 
     * @return the cloned IDevice
     */
    IDevice cloneDevice();
    
    /**
     * Copy the device State from another device
     * 
     * @param device another device
     */
    void copyDeviceState(IDevice device);
    
    /**
     * Copy the device rom version(including current and latest) from another device
     * 
     * @param deivce another device
     */
    void copyDeviceRomVersion(IDevice deivce);
    
    /**
     * Copy the device rssi
     * 
     * @param device another
     */
    void copyDeviceRssi(IDevice device);
    
    /**
     * Copy the device name from another device
     * 
     * @param device another device
     */
    void copyDeviceName(IDevice device);
    
    /**
     * Copy the device InetAddress from another device
     * 
     * @param device another device
     */
    void copyInetAddress(IDevice device);
    
    /**
     * Copy IsMeshDevice from another device
     * @param device  another device
     */
    void copyIsMeshDevice(IDevice device);
    
    /**
     * Copy parent device bssid from another device
     * 
     * @param device another device
     */
    void copyParentDeviceBssid(IDevice device);
    
    /**
     * Copy the device activating timestamp from another device
     * 
     * @param device
     */
    void copyTimestamp(IDevice device);
    
    /**
     * Copy the device activated time on server from another device
     * 
     * @param device
     */
    void copyActivatedTime(IDevice device);
    
    /**
     * Copy the device info from another device
     * 
     * @param device
     */
    void copyDeviceInfo(IDevice device);
    
    /**
     * Get the device's device tree element list
     * @param allDeviceList the list of all device belong to the IUser
     * @return the device's device tree element list
     */
    //List<IEspDeviceTreeElement> getDeviceTreeElementList(List<IDevice> allDeviceList);
    
    /**
     * Whether the device has activated on server
     * @return
     */
    boolean isActivated();

    List<String> getPrefrenceTopics();

}
