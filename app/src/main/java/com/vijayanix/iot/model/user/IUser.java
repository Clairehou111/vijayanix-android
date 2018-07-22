package com.vijayanix.iot.model.user;

import com.espressif.iot.esptouch.IEsptouchListener;
import com.vijayanix.iot.base.object.ISingletonObject;
import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.IOTAddress;
import com.vijayanix.iot.mqtt.ReceivedMessage;

import java.util.List;

public interface IUser extends ISingletonObject
{
    String[] DEVICE_SSID_PREFIX = {"ESP_", "espressif_"};
    
    String[] MESH_DEVICE_SSID_PREFIX = {"espressif_"};
    
    long GUEST_USER_ID = -1;
    
    String GUEST_USER_KEY = "guest";
    
    String GUEST_USER_EMAIL = "guest";
    
    String GUEST_USER_NAME = "guest";
    
    /**
     * when the device is configured just now, the softap will be scanned, but it should be ignored
     */
    long SOFTAP_IGNORE_TIMESTAMP = 60 * 1000;
    
    /**
     * Set the email of the user
     * 
     * @param userEmail
     */
    void setUserEmail(final String userEmail);
    
    /**
     * Get the email of the user
     * 
     * @return email address
     */
    String getUserEmail();
    
    /**
     * Set the id of the user
     * 
     * @param userId
     */
    void setUserId(final long userId);
    
    /**
     * Get the id of the user
     * 
     * @return the user id
     */
    long getUserId();
    
    /**
     * Set the key of the user
     * 
     * @param userKey
     */
    void setUserKey(final String userKey);
    
    /**
     * Get the key of the user
     * 
     * @return user key
     */
    String getUserKey();
    
    /**
     * Set the user name
     * 
     * @param userName
     */
    void setUserName(String userName);
    
    /**
     * Get the user name
     * 
     * @return
     */
    String getUserName();
    
    /**
     * Check whether the user has login
     * 
     * @return whether the user has login
     */
    boolean isLogin();

    boolean isFirstTime();

    void setFirstTime(boolean firstTime);

    void cancelAllAddDevices();
    void doneAllAddDevices();

	List<IOTAddress> configDeviceEsptouch(String apSsid, String apBssid, String apPassword, boolean isSsidHidden,
	                                      IEsptouchListener esptouchListener);

	IUser doActionUserLoginDB2();

    IDevice getFoundedDeviceByssid(String ssid);

    List<IDevice> loadAllDevices();

    void addLocalDeviceListAysnc(final List<IOTAddress> iotAddressList);


    void processMqttMessage(ReceivedMessage receivedMessage);

	boolean doActionPostDeviceStatus(IDevice device, IDeviceStatus status);

	boolean doActionGetDeviceStatus(IDevice device);

	boolean doActionPostDeviceStatus(int datastreamType, IDevice device, IDeviceStatus status);

	boolean doActionGetDeviceStatus(int datastreamType,IDevice device);

	void doActionRefreshDevices();

	void doActionRefreshStaDevices(boolean isSyn);

	IDevice getUserDevice(String deviceKey);

	List<IDevice> getUserDevices(String[] deviceKeys);

}
