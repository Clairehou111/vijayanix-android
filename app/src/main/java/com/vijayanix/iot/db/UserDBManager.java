package com.vijayanix.iot.db;


import com.vijayanix.iot.db.bean.DeviceDB;
import com.vijayanix.iot.db.bean.UserDB;
import com.vijayanix.iot.db.greendao.gen.DaoSession;
import com.vijayanix.iot.db.greendao.gen.UserDBDao;
import com.vijayanix.iot.db.greendao.gen.UserDBDao.Properties;
import com.vijayanix.iot.base.object.ISingletonObject;
import com.vijayanix.iot.db.interfa.IDeviceDB;
import com.vijayanix.iot.db.interfa.IUserDB;

import org.apache.log4j.Logger;
import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserDBManager implements IUserDBManager, ISingletonObject
{
    private static final Logger log = Logger.getLogger(UserDBManager.class);
    
    private UserDBDao userDao;
    
    // Singleton Pattern
    private static UserDBManager instance = null;
    
    private UserDBManager(DaoSession daoSession)
    {
        this.userDao = daoSession.getUserDBDao();
    }
    
    public static void init(DaoSession daoSession)
    {
        instance = new UserDBManager(daoSession);
    }
    
    public static UserDBManager getInstance()
    {
        return instance;
    }
    
    /**
     * change User info in local db
     * 
     * @param id user's id
     * @param email user's email
     * @param key user's key
     * @param name user name
     */
    @Override
    public void changeUserInfo(long id, String email, String key, String name)
    {
        log.info(Thread.currentThread().toString() + "##changeUserInfo(id=[" + id + "],email=[" + email
            + "],key=[" + key + "]");
        Query<UserDB> query = userDao.queryBuilder().where(UserDBDao.Properties.IsLastLogin.eq(true)).build();
        UserDB result = query.unique();
        if (result != null)
        {
            // clear the old login info
            result.setIsLastLogin(false);
            userDao.update(result);
        }
        result = new UserDB(id, email, key, name, true);
        userDao.insertOrReplace(result);
    }
    
    /**
     * load the UserDB from local db
     * 
     * @return UserDB info
     */
    @Override
    public IUserDB load()
    {
        UserDB result = null;
        
        Query<UserDB> query = userDao.queryBuilder().where(Properties.IsLastLogin.eq(true)).build();
        result = query.unique();
        if (result != null)
        {
            log.debug(Thread.currentThread().toString() + "##load(): " + result);
            return result;
        }
        // the app hasn't been login yet
        log.debug(Thread.currentThread().toString() + "##load(): " + result);
        return result;
    }
    
    private List<IDeviceDB> __getUserDeviceList(long userId)
    {
        Query<UserDB> query = userDao.queryBuilder().where(UserDBDao.Properties.Id.eq(userId)).build();
        UserDB user = query.unique();
        List<DeviceDB> result = null;
        if (user != null)
        {   //claire comment this
            user.resetDevices();
            result = user.getDevices();
        }
        log.debug(Thread.currentThread().toString() + "##getUserDeviceList(userId=[" + userId + "]): " + result);
        if (result != null)
        {
            List<IDeviceDB> deviceList = new ArrayList<IDeviceDB>();
            deviceList.addAll(result);
            return deviceList;
        }
        else
        {
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<IDeviceDB> getUserDeviceList(long userId)
    {
        List<IDeviceDB> deviceList = new ArrayList<IDeviceDB>();
//        if (userId > 0)
//        {
//            List<IDeviceDB> userDeviceList = __getUserDeviceList(userId);
//            deviceList.addAll(userDeviceList);
//            List<IDeviceDB> guestDeviceList = __getUserDeviceList(IUser.GUEST_USER_ID);
//            deviceList.addAll(guestDeviceList);
//        }
//        else
//        {
            List<IDeviceDB> userDeviceList = __getUserDeviceList(userId);
            deviceList.addAll(userDeviceList);
//        }
        return deviceList;
    }
}
