package com.vijayanix.iot.db;

import com.vijayanix.iot.db.interfa.IDeviceDB;
import com.vijayanix.iot.db.interfa.IUserDB;

import java.util.List;

public interface IUserDBManager extends IDBManager
{
    /**
     * load the user info from db (it is called when the application start launching)
     * 
     * @return @see IUserDB
     */
    IUserDB load();
    
    /**
     * change the user info in local db
     * 
     * @param id user id
     * @param email user email
     * @param key user key
     * @param name user name
     */
    void changeUserInfo(long id, String email, String key, String name);
    
    /**
     * get the user's device list (for greenDao use List<DeviceDB> as the return value, we have to use DeviceDB instead
     * of IDeviceDB)
     * 
     * @param userId the user Id
     * @return the device list
     */
    List<IDeviceDB> getUserDeviceList(long userId);
}
