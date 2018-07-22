package com.vijayanix.iot.model.device.cache;

import android.text.TextUtils;
import android.util.Log;

import com.vijayanix.iot.model.device.Device;
import com.vijayanix.iot.model.device.DeviceState;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.IDeviceState;
import com.vijayanix.iot.model.device.IOTAddress;
import com.vijayanix.iot.model.device.builder.BDevice;
import com.vijayanix.iot.model.device.newd.IDeviceNew;
import com.vijayanix.iot.base.object.ISingletonObject;
import com.vijayanix.iot.model.user.IUser;
import com.vijayanix.iot.util.BSSIDUtil;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DeviceCacheHandler implements ISingletonObject, IDeviceCacheHandler
{
    private final static Logger log = Logger.getLogger(DeviceCacheHandler.class);
    
    /*
     * Singleton lazy initialization start
     */
    private DeviceCacheHandler()
    {
        mDeviceDBList = new LinkedBlockingQueue<IDevice>();
        __executeInsertDeviceListAsyn();
    }
    
    private static class InstanceHolder
    {
        static DeviceCacheHandler instance = new DeviceCacheHandler();
    }
    
    public static DeviceCacheHandler getInstance()
    {
        return InstanceHolder.instance;
    }
    
    /*
     * Singleton lazy initialization end
     */
    
    private BlockingQueue<IDevice> mDeviceDBList;
    
    private void __executeInsertDeviceListAsyn()
    {
        BaseApiUtil.submit(new Runnable()
        {
            
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        IDevice device = mDeviceDBList.take();
                        if (device.getDeviceState().isStateClear())
                        {
                            device.deleteInDB();
                        }
                        else
                        {
                            device.saveInDB();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            
        });
    }
    
    private void __putDeviceInDBList(IDevice device)
    {
        mDeviceDBList.add(device);
    }
    
    private void __handleClearList(List<IDevice> userDeviceList)
    {
        for (int i = 0; i < userDeviceList.size(); i++)
        {
            IDevice device = userDeviceList.get(i);
            if (device.getDeviceState().isStateClear())
            {
                log.info(Thread.currentThread().toString() + "##__handleClearList device:[" + device
                    + "] is removed from local db");
                __putDeviceInDBList(device);
                log.info(Thread.currentThread().toString() + "##__handleClearList device:[" + device
                    + "] is removed from IUser list");
                userDeviceList.remove(i--);
            }
        }
    }
    
    private boolean handleServerLocal(List<IDevice> userDeviceList)
    {
        // poll devices from DeviceCache, the state should be OFFLINE,LOCAL,INTERNET or LOCAL,INTERNET coexist
        List<IDevice> serverLocaldeviceList = DeviceCache.getInstance().pollServerLocalDeviceCacheList();
        boolean isEmptyDevice1Exist = false;
        if (serverLocaldeviceList.isEmpty())
        {
            // Internet unaccessible, don't handleServerLocal
            return false;
        }
        // delete the EmptyDevice1 and EmptyDevice2 from serverLocaldeviceList
        for (int i = 0; i < serverLocaldeviceList.size(); i++)
        {
            if (serverLocaldeviceList.get(i) == Device.EmptyDevice1)
            {
                isEmptyDevice1Exist = true;
                serverLocaldeviceList.remove(i--);
            }
            else if (serverLocaldeviceList.get(i) == Device.EmptyDevice2)
            {
                serverLocaldeviceList.remove(i--);
            }
        }
        
        if (serverLocaldeviceList.isEmpty() && isEmptyDevice1Exist)
        {
            // Keep the userDeviceList in the device list
            return true;
        }
        
        // 1. IUser has, ServerLocal don't have, make IUser's state CLEAR(delete from IUser and delete from local db)
        // 2. IUser doesn't have, ServerLocal have, add into IUser's list(if it doesn't need ignoring)
        // 3. both of IUser and ServerLocal have, but IUser need to ignore(e.g. device is DELETED or ACTIVATING state
        // etc.)
        // 4. both of IUser and ServerLocal have, and IUser don't need to ignore
        for (IDevice deviceInUser : userDeviceList)
        {
            IDeviceState deviceInUserState = deviceInUser.getDeviceState();
            // 1. IUser has, ServerLocal don't have, make IUser's state CLEAR(delete from IUser and delete from local
            // db)
            if (!serverLocaldeviceList.contains(deviceInUser))
            {
                // if device is configuring, activating, new, don't clear the state
                if ((!deviceInUserState.isStateConfiguring()) && (!deviceInUserState.isStateActivating())
                    && (!deviceInUserState.isStateNew()))
                {
                    log.info(Thread.currentThread().toString() + "##handleServerLocal1 deviceInUser:[" + deviceInUser
                        + "] is clearState");
                    deviceInUserState.clearState();
                }
            }
            else
            {
                // 3. both of IUser and ServerLocal have, but IUser need to ignore(e.g. device is DELETED or ACTIVATING
                // state etc.)
                if (deviceInUserState.isStateActivating() || deviceInUserState.isStateClear()
                    || deviceInUserState.isStateConfiguring() || deviceInUserState.isStateDeleted()
                    || deviceInUserState.isStateUpgradingInternet() || deviceInUserState.isStateUpgradingLocal())
                {
                    // ignore
                    log.info(Thread.currentThread().toString() + "##handleServerLocal3 deviceInUser:[" + deviceInUser
                        + "] is ignored");
                }
                // 4. both of IUser and ServerLocal have, and IUser don't need to ignore
                else
                {
                    IDevice serverLocalDevice =
                        serverLocaldeviceList.get(serverLocaldeviceList.indexOf(deviceInUser));
                    log.info(Thread.currentThread().toString() + "##handleServerLocal4 deviceInUser:[" + deviceInUser
                        + "] is update(including name), serverLocalDevice:[" + serverLocalDevice + "]");
                    deviceInUser.copyInetAddress(serverLocalDevice);
                    deviceInUser.copyIsMeshDevice(serverLocalDevice);
                    deviceInUser.copyParentDeviceBssid(serverLocalDevice);
                    
                    // it must before deviceInUser.copyDeviceState, or the Renamed state will be cleared
                    boolean isRenamed = deviceInUserState.isStateRenamed();
                    if (isRenamed || deviceInUser.__isDeviceRefreshed())
                    {
                        deviceInUser.__clearDeviceRefreshed();
                    }
                    else
                    {
                        deviceInUser.copyDeviceName(serverLocalDevice);
                        deviceInUser.copyActivatedTime(serverLocalDevice);
                    }
                    deviceInUser.copyDeviceState(serverLocalDevice);
                    // don't forget to add Renamed State
                    if (isRenamed)
                    {
                        deviceInUser.getDeviceState().addStateRenamed();
                    }
                    deviceInUser.copyDeviceRomVersion(serverLocalDevice);
                    deviceInUser.copyDeviceRssi(serverLocalDevice);
                    deviceInUser.copyDeviceInfo(serverLocalDevice);
                    __putDeviceInDBList(deviceInUser);
                }
            }
        }
        // 2. IUser doesn't have, ServerLocal have, add into IUser's list(if it doesn't need ignoring)
        for (IDevice serverLocalUser : serverLocaldeviceList)
        {
            if (!userDeviceList.contains(serverLocalUser))
            {
                // if the device is activating or activating fail, don't add it into IUser
                // activating device's state: ACTIVATING
                // activating fail device's state: ACTIVATING DELETED
                IDevice similarDevice = null;
                for (IDevice device2 : userDeviceList)
                {
                    if (device2.isSimilar(serverLocalUser))
                    {
                        similarDevice = device2;
                        log.info(Thread.currentThread().toString() + "##handleServerLocal2 serverLocalUser:["
                            + serverLocalUser + "] is ignored");
                        break;
                    }
                }
                if (similarDevice == null)
                {
                    log.info(Thread.currentThread().toString() + "##handleServerLocal2 serverLocalUser:["
                        + serverLocalUser + "] is added in IUser list and local db");
                    userDeviceList.add(serverLocalUser);
                    __putDeviceInDBList(serverLocalUser);
                }
            }
        }
        return true;
    }
    
    private void __handleLocal(List<IDevice> userDeviceList, List<IOTAddress> localIOTAddressList,
                               boolean clearInternetState)
    {
        // make userDeviceList device state OFFLINE if LOCAL or INTERNET
        for (IDevice userDevice : userDeviceList)
        {
            IDeviceState deviceState = userDevice.getDeviceState();
            // only process INTERNET and LOCAL
            if (deviceState.isStateLocal() || deviceState.isStateInternet())
            {
                if (clearInternetState)
                {
                    deviceState.clearStateInternet();
                    deviceState.clearStateLocal();
                    deviceState.addStateOffline();
                }
                else
                {
                    deviceState.clearStateLocal();
                    if (!deviceState.isStateInternet())
                    {
                        deviceState.addStateOffline();
                    }
                }
            }
        }
        // only process OFFLINE(when clearInternetState is true)
        // only process OFFLINE and INTERNET(when clearInternetState is false)
        for (IOTAddress localIOTAddress : localIOTAddressList)
        {
            for (IDevice userDevice : userDeviceList)
            {
                IDeviceState deviceState = userDevice.getDeviceState();
                if (clearInternetState)
                {
                    if (!deviceState.isStateOffline())
                    {
                        // ignore
                        continue;
                    }
                }
                else
                {
                    if (!deviceState.isStateOffline() && !deviceState.isStateInternet())
                    {
                        // ignore
                        continue;
                    }
                }
                
                String bssid1 = localIOTAddress.getBSSID();
                String bssid2 = userDevice.getBssid();
                if (bssid1.equals(bssid2))
                {
                    deviceState.clearStateOffline();
                    deviceState.addStateLocal();
                    // fix the bug when device is only local, the apk will crash
                    userDevice.setInetAddress(localIOTAddress.getInetAddress());
                    userDevice.setIsMeshDevice(localIOTAddress.isMeshDevice());
                    userDevice.setParentDeviceBssid(localIOTAddress.getParentBssid());
                    // set current version by local
                    String romVersionLocal = localIOTAddress.getRomVersion();
                    String romVersionInternet = userDevice.getRom_version();
                    if (romVersionLocal != null && romVersionLocal.equals(romVersionInternet))
                    {
                        Log.w("DeviceCacheHandler", "romVersionLocal=" + romVersionLocal + ",romVersionInternet="
                            + romVersionInternet + " is different");
                        userDevice.setRom_version(romVersionLocal);
                    }
                    if (localIOTAddress.getRssi() != IDevice.RSSI_NULL) {
                        userDevice.setRssi(localIOTAddress.getRssi());
                    }
                    if (!TextUtils.isEmpty(localIOTAddress.getInfo())) {
                        userDevice.setInfo(localIOTAddress.getInfo());
                    }
                }
            }
        }
    }
    
    private void handleLocal(List<IDevice> userDeviceList)
    {
        log.debug(Thread.currentThread().toString() + "##handleLocal()");
        List<IOTAddress> localIOTAddressList = DeviceCache.getInstance().pollLocalDeviceCacheList();
        __handleLocal(userDeviceList, localIOTAddressList, true);
    }
    
    private void handleUpgradeLocalSuc(List<IDevice> userDeviceList)
    {
        log.debug(Thread.currentThread().toString() + "##handleUpgradeLocalSuc()");
        List<IOTAddress> localUpgradeSucIOTAddressList =
            DeviceCache.getInstance().pollUpgradeSucLocalDeviceCacheList();
        if (localUpgradeSucIOTAddressList.isEmpty())
        {
            return;
        }
        IDeviceState deviceState;
        // clear all devices of userDeviceList local state
        for (IDevice userDevice : userDeviceList)
        {
            deviceState = userDevice.getDeviceState();
            if (deviceState.isStateLocal())
            {
                deviceState.clearStateLocal();
                if (!deviceState.isStateInternet())
                {
                    // don't forget to make device offline
                    deviceState.addStateOffline();
                }
            }
        }
        // add local state to the devices of userDeviceList, which device is in localUpgradeSucIOTAddressList
        for (IOTAddress localUpgradeSucIOTAddress : localUpgradeSucIOTAddressList)
        {
            for (IDevice userDevice : userDeviceList)
            {
                deviceState = userDevice.getDeviceState();
                // only process OFFLINE or INTERNET
                if (!(deviceState.isStateOffline() || deviceState.isStateInternet()))
                {
                    // ignore
                    continue;
                }
                String bssid1 = localUpgradeSucIOTAddress.getBSSID();
                String bssid2 = userDevice.getBssid();
                if (bssid1.equals(bssid2))
                {
                    deviceState.clearStateOffline();
                    deviceState.addStateLocal();
                    userDevice.setInetAddress(localUpgradeSucIOTAddress.getInetAddress());
                    userDevice.setIsMeshDevice(localUpgradeSucIOTAddress.isMeshDevice());
                    userDevice.setParentDeviceBssid(localUpgradeSucIOTAddress.getParentBssid());
                    // set current version by local
                    String romVersionLocal = localUpgradeSucIOTAddress.getRomVersion();
                    String romVersionInternet = userDevice.getRom_version();
                    if (romVersionLocal != null && romVersionLocal.equals(romVersionInternet))
                    {
                        Log.w("DeviceCacheHandler", "romVersionLocal=" + romVersionLocal + ",romVersionInternet="
                            + romVersionInternet + " is different");
                        userDevice.setRom_version(romVersionLocal);
                    }
                    if (localUpgradeSucIOTAddress.getRssi() != IDevice.RSSI_NULL) {
                        userDevice.setRssi(localUpgradeSucIOTAddress.getRssi());
                    }
                    if (!TextUtils.isEmpty(localUpgradeSucIOTAddress.getInfo())) {
                        userDevice.setInfo(localUpgradeSucIOTAddress.getInfo());
                    }
                    break;
                }
            }
        }
    }
    
    private void handleStatemachine(List<IDevice> userDeviceList, List<IDevice> userStaDeviceList,
                                    List<IDevice> guestDeviceList)
    {
        // poll devices from DeviceCache
        List<IDevice> stateMachineDeviceList = new ArrayList<IDevice>();
        IDevice device = DeviceCache.getInstance().pollStatemachineDeviceCache();
        while (device != null)
        {
            stateMachineDeviceList.add(device);
            device = DeviceCache.getInstance().pollStatemachineDeviceCache();
        }
        boolean isExecuted = false;
        // a. handle DELETED(&&(CONFIGURING||ACTIVATING)), CONFIGURING, ACTIVATING
        // b. handle IUser activating
        // c. handle others (just copy device state, rom version and device name)
        
        // a. handle DELETED(&&(CONFIGURING||ACTIVATING)), CONFIGURING, ACTIVATING
        for (int i = 0; i < stateMachineDeviceList.size(); i++)
        {
            isExecuted = false;
            IDevice stateMachineDevice = stateMachineDeviceList.get(i);
            IDeviceState deviceStateMachineState = stateMachineDevice.getDeviceState();
            if (DeviceState.checkValidWithSpecificStates(deviceStateMachineState,
                DeviceState.DELETED,
                DeviceState.CONFIGURING)
                || DeviceState.checkValidWithSpecificStates(deviceStateMachineState,
                    DeviceState.DELETED,
                    DeviceState.ACTIVATING))
            {
                log.debug("handleStatemachine() deviceStateMachineState.isStateDeleted() and (isStateConfiguring() or isStateActivating()");
                isExecuted = true;
                // configuring or activating fail
                for (IDevice deviceInUser : userDeviceList)
                {
                    // if (deviceInUser.getId() == stateMachineDevice.getId())
                    if (deviceInUser.equals(stateMachineDevice))
                    {
                        deviceInUser.copyDeviceState(stateMachineDevice);
                        break;
                    }
                }
            }
            else if (DeviceState.checkValidWithSpecificStates(deviceStateMachineState, DeviceState.CONFIGURING))
            {
                log.debug("handleStatemachine() deviceStateMachineState.isStateConfiguring()");
                isExecuted = true;
                // clear device if the similar but not equal device is exist in IUser
                // clear the device which isn't activating
                for (IDevice deviceInUser : userDeviceList)
                {
                    if (deviceInUser.isSimilar(stateMachineDevice) && !deviceInUser.equals(stateMachineDevice))
                    {
                        deviceInUser.getDeviceState().clearState();
                    }
                }
                for (IDevice deviceInSta : userStaDeviceList)
                {
                    if (deviceInSta.isSimilar(stateMachineDevice) && !deviceInSta.equals(stateMachineDevice))
                    {
                        deviceInSta.getDeviceState().clearState();
                    }
                }
                for (IDevice deviceInGuest : guestDeviceList)
                {
                    if (deviceInGuest.isSimilar(stateMachineDevice) && !deviceInGuest.equals(stateMachineDevice))
                    {
                        deviceInGuest.getDeviceState().clearState();
                    }
                }
                // stop all activating tasks
                for (IDevice deviceInUser : userDeviceList)
                {
                    if (DeviceState.checkValidWithSpecificStates(deviceInUser.getDeviceState(),
                        DeviceState.ACTIVATING))
                    {
                        // when adding device by esptouch, deviceInUser is IDeviceConfigure
                        if(deviceInUser instanceof IDeviceNew)
                        {
                            IDeviceNew deviceNew = (IDeviceNew)deviceInUser;
                            deviceNew.cancel(true);
                        }
                    }
                }
                // delete the same activating device from userDeviceList
                for (int j = 0; j < userDeviceList.size(); j++)
                {
                    IDevice deviceInUser = userDeviceList.get(j);
                    if (deviceInUser.equals(stateMachineDevice))
                    {
                        userDeviceList.remove(j--);
                        break;
                    }
                }
                // add device in IUser
                userDeviceList.add(stateMachineDevice);
            }
            else if (DeviceState.checkValidWithSpecificStates(deviceStateMachineState, DeviceState.ACTIVATING))
            {
                log.debug("handleStatemachine() deviceStateMachineState.isStateActivating()");
                isExecuted = true;
                // change CONFIRURING to ACTIVATING
                for (IDevice deviceInUser : userDeviceList)
                {
                    // IDeviceNew support only one CONFIGURING device,
                    // IDeviceConfigure support more than one CONFIGURING device
                    if (DeviceState.checkValidWithSpecificStates(deviceInUser.getDeviceState(),
                        DeviceState.CONFIGURING) && deviceInUser.isSimilar(stateMachineDevice))
                    {
                        deviceInUser.copyDeviceState(stateMachineDevice);
                        deviceInUser.copyTimestamp(stateMachineDevice);
                        break;
                    }
                }
                // resume all activating tasks
                for (IDevice deviceInUser : userDeviceList)
                {
                    // only IDeviceNew require resume
                    if (deviceInUser instanceof IDeviceNew)
                    {
                        if (DeviceState.checkValidWithSpecificStates(deviceInUser.getDeviceState(),
                            DeviceState.ACTIVATING) && deviceInUser.isSimilar(stateMachineDevice))
                        {
                            IDeviceNew deviceNew = (IDeviceNew)deviceInUser;
                            deviceNew.resume();
                        }
                    }
                }
                
            }
            if (isExecuted)
            {
                stateMachineDeviceList.remove(i--);
            }
        }
        
        // b. handle IUser activating
        for (int i = 0; i < userDeviceList.size(); i++)
        {
            IDevice deviceInUser = userDeviceList.get(i);
            if (DeviceState.checkValidWithSpecificStates(deviceInUser.getDeviceState(), DeviceState.ACTIVATING))
            {
                for (int j = 0; j < stateMachineDeviceList.size(); j++)
                {
                    IDevice deviceInStateMachine = stateMachineDeviceList.get(j);
                    if (deviceInUser.isSimilar(deviceInStateMachine))
                    {
                        // replace deviceInUser with deviceInStateMachine
                        userDeviceList.remove(i--);
                        userDeviceList.add(deviceInStateMachine);
                        // delete the device from deviceInStateMachine
                        stateMachineDeviceList.remove(j--);
                        // don't forget to save in db,
                        deviceInStateMachine.saveInDB();
                    }
                }
            }
        }
        for (int i = 0; i < userStaDeviceList.size(); i++)
        {
            IDevice deviceInSta = userStaDeviceList.get(i);
            if (DeviceState.checkValidWithSpecificStates(deviceInSta.getDeviceState(), DeviceState.ACTIVATING))
            {
                for (int j = 0; j < stateMachineDeviceList.size(); j++)
                {
                    IDevice deviceInStateMachine = stateMachineDeviceList.get(j);
                    if (deviceInSta.isSimilar(deviceInStateMachine))
                    {
                        // replace deviceInUser with deviceInStateMachine
                        userStaDeviceList.remove(i--);
//                        userStaDeviceList.add(deviceInStateMachine);
                        // delete the device from deviceInStateMachine
                        stateMachineDeviceList.remove(j--);
                        // don't forget to save in db,
                        deviceInStateMachine.saveInDB();
                    }
                }
            }
        }
        for (int i = 0; i < guestDeviceList.size(); i++)
        {
            IDevice deviceInGuest = guestDeviceList.get(i);
            if (DeviceState.checkValidWithSpecificStates(deviceInGuest.getDeviceState(), DeviceState.ACTIVATING))
            {
                for (int j = 0; j < stateMachineDeviceList.size(); j++)
                {
                    IDevice deviceInStateMachine = stateMachineDeviceList.get(j);
                    if (deviceInGuest.isSimilar(deviceInStateMachine))
                    {
                        // replace deviceInUser with deviceInStateMachine
                        guestDeviceList.remove(i--);
//                        guestDeviceList.add(deviceInStateMachine);
                        // delete the device from deviceInStateMachine
                        stateMachineDeviceList.remove(j--);
                        // don't forget to save in db,
                        deviceInStateMachine.saveInDB();
                    }
                }
            }
        }
        for (int i = 0; i < userDeviceList.size(); i++)
        {
            IDevice deviceInUser = userDeviceList.get(i);
            if (DeviceState.checkValidWithSpecificStates(deviceInUser.getDeviceState(), DeviceState.ACTIVATING))
            {
                for (int j = 0; j < stateMachineDeviceList.size(); j++)
                {
                    IDevice deviceInStateMachine = stateMachineDeviceList.get(j);
                    if (deviceInUser.isSimilar(deviceInStateMachine))
                    {
                        // replace deviceInUser with deviceInStateMachine
                        userDeviceList.remove(i--);
                        userDeviceList.add(deviceInStateMachine);
                        // delete the device from deviceInStateMachine
                        stateMachineDeviceList.remove(j--);
                        // don't forget to save in db,
                        deviceInStateMachine.saveInDB();
                    }
                }
            }
        }
        
        // c. handle others (just copy device state, rom version and device name)
        for (IDevice stateMachineDevice : stateMachineDeviceList)
        {
            if (userDeviceList.contains(stateMachineDevice))
            {
                IDevice userDevice = userDeviceList.get(userDeviceList.indexOf(stateMachineDevice));
                userDevice.copyDeviceState(stateMachineDevice);
                userDevice.copyDeviceRomVersion(stateMachineDevice);
                userDevice.copyDeviceRssi(stateMachineDevice);
                userDevice.copyDeviceInfo(stateMachineDevice);
                userDevice.copyDeviceName(stateMachineDevice);
                userDevice.copyActivatedTime(stateMachineDevice);
                if (!stateMachineDevice.getDeviceState().isStateClear())
                {
                    log.error("userDevice: " + userDevice + ",stateMachineDevice: " + stateMachineDevice);
                    // don't forget to save in db
                    userDevice.saveInDB();
                }
            }
            if (userStaDeviceList.contains(stateMachineDevice))
            {
                IDevice userStaDevice = userStaDeviceList.get(userStaDeviceList.indexOf(stateMachineDevice));
                userStaDevice.copyDeviceState(stateMachineDevice);
                userStaDevice.copyDeviceRomVersion(stateMachineDevice);
                userStaDevice.copyDeviceRssi(stateMachineDevice);
                userStaDevice.copyDeviceInfo(userStaDevice);
                userStaDevice.copyDeviceName(stateMachineDevice);
                userStaDevice.copyActivatedTime(stateMachineDevice);
                if (!stateMachineDevice.getDeviceState().isStateClear())
                {
                    log.error("userStaDevice: " + userStaDevice + ",stateMachineDevice: " + stateMachineDevice);
                    // don't forget to save in db
                    userStaDevice.saveInDB();
                }
            }
            if (guestDeviceList.contains(stateMachineDevice))
            {
                IDevice guestDevice = guestDeviceList.get(guestDeviceList.indexOf(stateMachineDevice));
                guestDevice.copyDeviceState(stateMachineDevice);
                guestDevice.copyDeviceRomVersion(stateMachineDevice);
                guestDevice.copyDeviceRssi(stateMachineDevice);
                guestDevice.copyDeviceInfo(stateMachineDevice);
                guestDevice.copyDeviceName(stateMachineDevice);
                guestDevice.copyActivatedTime(stateMachineDevice);
                if (!stateMachineDevice.getDeviceState().isStateClear())
                {
                    log.error("guestDevice: " + guestDevice + ",stateMachineDevice: " + stateMachineDevice);
                    // don't forget to save in db
                    guestDevice.saveInDB();
                }
            }
        }
    }
    
    private void handleShared(List<IDevice> userDeviceList)
    {
        log.debug(Thread.currentThread().toString() + "##handlerShared()");
        IDevice device = DeviceCache.getInstance().pollSharedDeviceCache();
        while (device != null)
        {
            for (int i = 0; i < userDeviceList.size(); i++)
            {
                IDevice deviceInUser = userDeviceList.get(i);
                if (deviceInUser.equals(device))
                {
                    device = null;
                    break;
                }
            }
            
            if (device != null)
            {
                userDeviceList.add(device);
                __putDeviceInDBList(device);
            }
            device = DeviceCache.getInstance().pollSharedDeviceCache();
        }
    }
    
    private void handleTransformed(List<IDevice> userDeviceList)
    {
        log.debug(Thread.currentThread().toString() + "##handleTransformed()");
        List<IDevice> deviceList = DeviceCache.getInstance().pollTransformedDeviceCacheList();
        for (IDevice deviceInList : deviceList)
        {
            for (IDevice deviceInUserList : userDeviceList)
            {
                if (deviceInUserList.equals(userDeviceList))
                {
                    deviceInUserList = deviceInList;
                    break;
                }
            }
        }
    }
    
    private void handleSta(List<IDevice> userDeviceList, List<IDevice> userStaDeviceList)
    {
        log.debug(Thread.currentThread().toString() + "##handleSta()");
        List<IOTAddress> staDeviceList = DeviceCache.getInstance().pollStaDeviceCacheList();
        // refresh local device state
        __handleLocal(userDeviceList, staDeviceList, false);
        // check whether it's necessary to handle sta
        if (staDeviceList.isEmpty())
        {
            log.info("##handleSta() staDeviceList is empty, return");
            return;
        }
        
        userStaDeviceList.clear();
        boolean isExist;
        for (int index = 0;index < staDeviceList.size(); ++index)
        {
            IOTAddress staDevice = staDeviceList.get(index);
            // when receive IOTAddress.EmptyIOTAddress means that the lately discover local result is empty,
            // so we should clear the added device in userStaDeviceList
            if (staDevice == IOTAddress.EmptyIOTAddress)
            {
                userStaDeviceList.clear();
                staDeviceList.remove(index--);
                continue;
            }
            isExist = false;
            for (IDevice userDevice : userDeviceList)
            {
                if (userDevice.getDeviceState().isStateDeleted())
                {
                    log.debug("#handleSta() ignore deleted userDevice: " + userDevice.getBssid());
                    continue;
                }
                if (userDevice.getBssid().equals(staDevice.getBSSID()))
                {
                    log.debug("##handleSta() device: " + userDevice.getBssid() + " is exist already");
                    isExist = true;
                    break;
                }
            }
            if (!isExist)
            {
                // generate ssid by bssid and whether the device is mesh
                String bssid = staDevice.getBSSID();
//                String prefix = staDevice.isMeshDevice() ? "espressif_" : "ESP_";
                String prefix = "ESP_";
                String ssid = BSSIDUtil.genDeviceNameByBSSID(prefix, bssid);
                staDevice.setSSID(ssid);
                // generate IEspDeviceSSS and add it into userStaDeviceList
                IDevice userStaDevice = BDevice.getInstance().createStaDevice(staDevice);
                log.info("##handleSta() add device: " + userStaDevice);
                userStaDeviceList.add(userStaDevice);
            }
        }
    }
    
    private void handleUserDevices(List<IDevice> userDeviceList)
    {
        log.debug(Thread.currentThread().toString() + "##handleUserDevices()");
        for (IDevice device : userDeviceList)
        {
            IDeviceState deviceState = device.getDeviceState();
            if ((!deviceState.isStateLocal()) && (!deviceState.isStateInternet()))
            {
                // clear parent device bssid
                device.setParentDeviceBssid(null);
                device.setIsMeshDevice(false);
            }
        }
    }
    
    private void removeRedundantUserStaDevices(List<IDevice> userDeviceList, List<IDevice> userStaDeviceList)
    {
        // when userDeviceList and userStaDeviceList has the same bssid device,
        // remove the device from userStaDeviceList
        for (int indexDevice = 0; indexDevice < userDeviceList.size(); ++indexDevice)
        {
            IDevice userDevice = userDeviceList.get(indexDevice);
            String deviceBssid = userDevice.getBssid();
            for (int indexStaDevice = 0; indexStaDevice < userStaDeviceList.size(); ++indexStaDevice)
            {
                IDevice userStaDevice = userStaDeviceList.get(indexStaDevice);
                String staDeviceBssid = userStaDevice.getBssid();
                if (deviceBssid.equals(staDeviceBssid))
                {
                    userStaDeviceList.remove(indexStaDevice--);
                    break;
                }
            }
        }
    }
    
    private IDevice getDeviceByBssid(List<IDevice> deviceList, String bssid)
    {
        for (IDevice device : deviceList)
        {
            if (device.getBssid().equals(bssid))
            {
                return device;
            }
        }
        
        return null;
    }

    
    private void setAllDevicesRootBssid(List<IDevice> allDeviceList)
    {
        for (int i = 0; i < allDeviceList.size(); i++)
        {
            IDevice device = allDeviceList.get(i);
            IDevice parentDevice = null;
            String bssid = device.getBssid();
            String parentBssid = device.getParentDeviceBssid();
            String rootBssid = bssid;
            
            int limitLevel = 20;
            do
            {
                // next
                if (parentBssid != null)
                {
                    parentDevice = getDeviceByBssid(allDeviceList, parentBssid);
                }
                else
                {
                    break;
                }
                
                // process
                if (parentDevice != null)
                {
                    parentBssid = parentDevice.getParentDeviceBssid();
                    rootBssid = parentDevice.getBssid();
                }
                if (limitLevel-- < 0) {
                    log.warn("setAllDevicesRootBssid: find parent and root bssid warning");
                    parentDevice.setParentDeviceBssid(null);
                    rootBssid = parentDevice.getBssid();
                    break;
                }
            } while (parentDevice != null);
            // set root bssid
            device.setRootDeviceBssid(rootBssid);
        }
    }
    
    private void handleStaGuestDevices(IUser user, List<IDevice> staDeviceList, List<IDevice> guestDeviceList)
    {
        for (IDevice staDevice : staDeviceList)
        {
            String staBssid = staDevice.getBssid();
            IDevice guestDevice = getDeviceByBssid(guestDeviceList, staBssid);
            if (guestDevice != null)
            {
                staDevice.copyDeviceName(guestDevice);
            }
            else
            {
                staDevice.saveInDB();
                guestDeviceList.add(staDevice);
            }
        }
    }

    @Override
    public synchronized Void handleUninterruptible(boolean isStateMachine)
    {
       /* IUser user = BUser.getBuilder().getInstance();
        user.lockUserDeviceLists();
        user.clearTempStaDeviceList();
        List<IDevice> userDeviceList = user.__getOriginDeviceList();
        List<IDevice> userStaDeviceList = user.__getOriginStaDeviceList();
        List<IDevice> userGuestDeviceList = user.getGuestDeviceList();
        if (isStateMachine)
        {
            handleStatemachine(userDeviceList, userStaDeviceList, userGuestDeviceList);
        }
        else
        {
            boolean isExecuted = handleServerLocal(userDeviceList);
            if (!isExecuted)
            {
                handleLocal(userDeviceList);
            }
        }
        handleShared(userDeviceList);
        handleUpgradeLocalSuc(userDeviceList);
        handleTransformed(userDeviceList);

        // handle device in CLEAR state
        __handleClearList(userDeviceList);
        __handleClearList(userStaDeviceList);
        __handleClearList(userGuestDeviceList);
        // TODO
//        __handleClearList2(userGuestDeviceList,userStaDeviceList);
        if (!isStateMachine)
        {
            // handle user's sta device list
            handleSta(userDeviceList, userStaDeviceList);
        }
        // clear parent device bssid if it isn't local or internet
        handleUserDevices(userDeviceList);

        // remove the redundant user sta devices
        removeRedundantUserStaDevices(userDeviceList, userStaDeviceList);

        List<IDevice> allDeviceList = user.getAllDeviceList();
        setAllDevicesRootBssid(allDeviceList);

        handleStaGuestDevices(user, userStaDeviceList, userGuestDeviceList);
        user.unlockUserDeviceLists();*/
        return null;
    }
}
