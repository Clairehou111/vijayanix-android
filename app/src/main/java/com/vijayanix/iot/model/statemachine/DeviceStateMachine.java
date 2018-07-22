package com.vijayanix.iot.model.statemachine;


import com.vijayanix.iot.base.object.ISingletonObject;
import com.vijayanix.iot.model.device.DeviceState;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.IDeviceState;
import com.vijayanix.iot.model.device.IOTAddress;
import com.vijayanix.iot.common.net.wifi.WifiCipherType;
import com.vijayanix.iot.model.device.cache.DeviceCache;
import com.vijayanix.iot.model.device.configure.IDeviceConfigure;
import com.vijayanix.iot.model.device.newd.IDeviceNew;
import com.vijayanix.iot.model.device.cache.IDeviceCache;
import com.vijayanix.iot.model.user.IUser;
import com.vijayanix.iot.model.user.builder.BUser;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class DeviceStateMachine implements IDeviceStateMachine, ISingletonObject
{
    private final static Logger log = Logger.getLogger(DeviceStateMachine.class);
    
    /*
     * Singleton lazy initialization start
     */
    private DeviceStateMachine()
    {
        _bssidCallableFutureMap = new ConcurrentHashMap<String, CallableFuture>();
    }
    
    private static class InstanceHolder
    {
        static DeviceStateMachine instance = new DeviceStateMachine();
    }
    
    public static DeviceStateMachine getInstance()
    {
        return InstanceHolder.instance;
    }
    
    /*
     * Singleton lazy initialization end
     */
    
    // store device's bssid and its task
    private final Map<String, CallableFuture> _bssidCallableFutureMap;
    
    private class CallableFuture
    {
        public Callable<?> _callable;
        
        public Future<?> _future;
        
        CallableFuture(Callable<?> callable, Future<?> future)
        {
            _callable = callable;
            _future = future;
        }
    }
    
    private Future<?> __submitTask(final Callable<?> task, final Runnable taskSuc, final Runnable taskFail,
                                   final Runnable taskCancel)
    {
        return BaseApiUtil.submit(task, taskSuc, taskFail, taskCancel);
    }
    
    private void __resumeAllTasks()
    {
        Set<String> bssidSet = _bssidCallableFutureMap.keySet();
        CallableFuture callableFuture = null;
        for (String bssidInSet : bssidSet)
        {
            callableFuture = _bssidCallableFutureMap.get(bssidInSet);
            // if the future isDone() means the action fail, it needs redoing
            if (callableFuture._future.isDone())
            {
                // execute the old callable
                __submitTask(callableFuture._callable, null, null, null);
                log.info(Thread.currentThread().toString()
                    + "##__resumeOldTasks(): old task is submitted again.(bssid=" + bssidInSet + ")");
            }
        }
    }
    
    private void __cancelAllTasks()
    {
        Set<String> bssidSet = _bssidCallableFutureMap.keySet();
        CallableFuture callableFuture = null;
        for (String bssidInSet : bssidSet)
        {
            callableFuture = _bssidCallableFutureMap.get(bssidInSet);
            if (callableFuture._future != null)
            {
                callableFuture._future.cancel(true);
                log.info(Thread.currentThread().toString()
                    + "##__cancelOldTasks(): old task is submitted again.(bssid=" + bssidInSet + ")");
            }
        }
    }
    
    private void __addBssidTask(final String bssid, final Callable<?> task)
    {
        // cancel the executing old task
        __cancelAllTasks();
        Future<?> future = __submitTask(task, null, null, null);
        log.info(Thread.currentThread().toString() + "##__addBssidTask(bssid=[" + bssid
            + "]): task is submitted.(bssid=" + bssid + ")");
        CallableFuture callableFuture = new CallableFuture(task, future);
        // put into _bssidCallableFutureMap
        _bssidCallableFutureMap.put(bssid, callableFuture);
        // execute all of the old task and new task
        __resumeAllTasks();
    }
    
    private class DefaultTaskFail implements Runnable
    {
        private final IDevice _device;
        
        DefaultTaskFail(final IDevice device)
        {
            _device = device;
        }
        
        @Override
        public void run()
        {
            transformState(_device, Direction.FAIL);
        }
    }
    

    
    private void __configure(final IDevice device, final IDevice deviceStateMachine)
    {
        if (device instanceof IDeviceConfigure)
        {
            log.debug(Thread.currentThread().toString() + "##__configure IDeviceConfigure");
            IDeviceStateMachineHandler handler = DeviceStateMachineHandler.getInstance();
            IDeviceStateMachineHandler.ITaskActivateLocal task = handler.createTaskActivateLocal((IDeviceConfigure)device);
            handler.addTask(task);
            return;
        }
        log.debug(Thread.currentThread().toString() + "##__configure(deviceStateMachine=[" + deviceStateMachine + "])");
        Callable<?> task = new Callable<Boolean>()
        {
            @Override
            public Boolean call()
                throws Exception
            {
                log.debug("__configure start");
                DeviceStateMachine.this.__cancelAllTasks();
                IDeviceNew deviceNew = (IDeviceNew)deviceStateMachine;
                // do configure device command(connect to the device and configure it)
                String deviceBssid = deviceNew.getBssid();
                String deviceSsid = deviceNew.getSsid();
                WifiCipherType deviceWifiCipherType = deviceNew.getWifiCipherType();
                String devicePassword = deviceNew.getDefaultPassword();
                String apSsid = deviceNew.getApSsid();
                WifiCipherType apWifiCipherType = deviceNew.getApWifiCipherType();
                String apPassword = deviceNew.getApPassword();
                String randomToken = deviceNew.getKey();
                long deviceId =
                    deviceNew.doActionDeviceNewConfigureLocal(deviceBssid,
                        deviceSsid,
                        deviceWifiCipherType,
                        devicePassword,
                        apSsid,
                        apWifiCipherType,
                        apPassword,
                        randomToken);
                if (deviceId < 0)
                {
                    log.info("__configure suc");
                    return true;
                }
                // note: if fail, must return null instead of False
                else
                {
                    log.warn("__configure fail");
                    return null;
                }
            }
        };
        Runnable taskSuc = new Runnable()
        {
            @Override
            public void run()
            {
                DeviceStateMachine.this.__resumeAllTasks();
                transformState(deviceStateMachine, Direction.ACTIVATE);
            }
        };
        Runnable taskFail = new Runnable()
        {
            @Override
            public void run()
            {
                DeviceStateMachine.this.__resumeAllTasks();
                transformState(deviceStateMachine, Direction.FAIL);
            }
        };
        Runnable taskCancel = new Runnable()
        {
            @Override
            public void run()
            {
                DeviceStateMachine.this.__resumeAllTasks();
                /**
                 * restore the device's state to the New State and remove it from IUser's device list, just like get it
                 * from @see User's scanSoftapDeviceList()
                 */
                device.getDeviceState().clearState();
                device.getDeviceState().addStateNew();
                IUser user = BUser.getBuilder().getInstance();
                boolean suc = user.loadAllDevices().remove(device);
                log.warn("cancel suc:" + suc);
            }
        };
        Future<?> future = __submitTask(task, taskSuc, taskFail, taskCancel);
        IDeviceNew deviceNew = (IDeviceNew)device;
        deviceNew.setFuture(future);
    }
    

    

    private void __transformStateMeshUpgradeLocalSuc(final IDevice upgradeLoalSucDevice,
                                                     final Collection<IOTAddress> localDeviceList, final Direction direction)
    {
        DeviceCache deviceCache = DeviceCache.getInstance();
        deviceCache.addUpgradeSucLocalDeviceCacheList((List<IOTAddress>)localDeviceList);
        transformState(upgradeLoalSucDevice, direction);
    }
    
    @Override
    public void transformState(final Collection<IDevice> deviceList, final Direction direction)
    {
        DeviceCache deviceCache = DeviceCache.getInstance();
        for (IDevice device : deviceList)
        {
            transformState(device, direction, IDeviceCache.NotifyType.STATE_MACHINE_BACKSTATE);
        }
        
        deviceCache.notifyIUser(IDeviceCache.NotifyType.STATE_MACHINE_UI);
    }
    
    @Override
    public void transformState(final IDevice device, final Direction direction)
    {
        transformState(device, direction, IDeviceCache.NotifyType.STATE_MACHINE_UI);
    }
    
    private void transformState(final IDevice device, final Direction direction, IDeviceCache.NotifyType notifyType)
    {
        __checkValid(device, direction);
        log.debug(Thread.currentThread().toString() + "##transformState(): pass __checkValid");
        DeviceCache deviceCache = DeviceCache.getInstance();
        
        IDevice stateMachineDevice = null;
        if (direction == Direction.CONFIGURE)
        {
            // for Direction.CONFIGURE isn't in IUser's device list,
            // and the configure UI use it to check the device configuring status, so don't copy it
            stateMachineDevice = device;
        }
        else
        {
            // clone a device in statemachine
            // the life cycle of it is here and IDeviceCache
            stateMachineDevice = device.cloneDevice();
        }
        
        // the state of device in statemachine
        IDeviceState state = stateMachineDevice.getDeviceState();
        
        switch (direction)
        {
            case ACTIVATE:
                state.clearStateConfiguring();
                state.addStateActivating();
                if (!DeviceState.checkValidWithSpecificStates(state, DeviceState.ACTIVATING))
                {
                    throw new IllegalStateException("device: " + device + ",  case ACTIVATE");
                }
                //__activate(device, stateMachineDevice);
                break;
            case CONFIGURE:
                state.clearStateNew();
                // if the device is configuring fail and try configure again, clear the DELETED state
                state.clearStateDeleted();
                state.addStateConfiguring();
                if (!DeviceState.checkValidWithSpecificStates(state, DeviceState.CONFIGURING))
                {
                    throw new IllegalStateException("device: " + device + ",  case CONFIGURE");
                }
                __configure(device, stateMachineDevice);
                break;
            case DELETE:
                state.clearState();
                state.addStateDeleted();
                // it is obvious the state is DeviceState.DELETED now
                //__delete(stateMachineDevice);
                break;
            case FAIL:
                if (state.isStateConfiguring())
                {
                    // when configure using esptouch, clearState() instead of addStateDeleted()
                    if ((device instanceof IDeviceConfigure))
                    {
                        state.clearState();
                    }
                    else
                    {
                        state.addStateDeleted();
                    }
                }
                if (state.isStateActivating())
                {
                    state.addStateDeleted();
                }
                if (state.isStateUpgradingInternet())
                {
                    state.clearStateUpgradingInternet();
                    state.clearStateInternet();
                    if (!state.isStateLocal())
                    {
                        state.addStateOffline();
                    }
                }
                if (state.isStateUpgradingLocal())
                {
                    state.clearStateUpgradingLocal();
                    state.clearStateLocal();
                    if (!state.isStateInternet())
                    {
                        state.addStateOffline();
                    }
                }
                break;
            case RENAME:
                state.addStateRenamed();
                if (!DeviceState.checkValidWithPermittedStates(state,
                    DeviceState.RENAMED,
                    DeviceState.INTERNET,
                    DeviceState.LOCAL,
                    DeviceState.OFFLINE))
                {
                    throw new IllegalStateException("device: " + device + ",  case RENAME");
                }
               // __rename(stateMachineDevice);
                break;
            case SUC:
                // activate suc
                if (DeviceState.checkValidWithSpecificStates(state, DeviceState.OFFLINE)
                    || DeviceState.checkValidWithSpecificStates(state, DeviceState.INTERNET))
                {
                    break;
                }
                // for upgradingInternet suc, it will return INTERNET state
                // it won't be executed now, for it will break in activate suc
                else if (DeviceState.checkValidWithNecessaryStates(state, DeviceState.INTERNET)
                    && DeviceState.checkValidWithPermittedStates(state,
                        DeviceState.INTERNET,
                        DeviceState.RENAMED))
                {
                    break;
                }
                // upgrading local suc
                else if (DeviceState.checkValidWithNecessaryStates(state,
                    DeviceState.UPGRADING_LOCAL,
                    DeviceState.LOCAL)
                    && DeviceState.checkValidWithPermittedStates(state,
                        DeviceState.UPGRADING_LOCAL,
                        DeviceState.LOCAL,
                        DeviceState.INTERNET,
                        DeviceState.RENAMED))
                {
                    state.clearStateUpgradingLocal();
                    break;
                }
                // rename suc
                else if (DeviceState.checkValidWithNecessaryStates(state, DeviceState.RENAMED)
                    && DeviceState.checkValidWithPermittedStates(state,
                        DeviceState.RENAMED,
                        DeviceState.LOCAL,
                        DeviceState.INTERNET,
                        DeviceState.OFFLINE,
                        DeviceState.UPGRADING_INTERNET,
                        DeviceState.UPGRADING_LOCAL))
                {
                    state.clearStateRenamed();
                    device.__setDeviceRefreshed();
                    log.info(Thread.currentThread().toString() + "##transformState device:[" + device
                        + "] __setDeviceRefreshed");
                    break;
                }
                // delete suc
                else if (DeviceState.checkValidWithSpecificStates(state, DeviceState.DELETED))
                {
                    state.clearStateDeleted();
                    break;
                }
                else
                {
                    throw new IllegalStateException("device: " + device + ",  case SUC");
                }
            case UPGRADE_INTERNET:
                state.addStateUpgradingInternet();
                if (!(DeviceState.checkValidWithPermittedStates(state,
                    DeviceState.UPGRADING_INTERNET,
                    DeviceState.INTERNET,
                    DeviceState.LOCAL) && DeviceState.checkValidWithNecessaryStates(state,
                    DeviceState.UPGRADING_INTERNET,
                    DeviceState.INTERNET)))
                {
                    throw new IllegalStateException("device: " + device + ",  case UPGRADE_INTERNET");
                }
                //__upgradeInternet(stateMachineDevice);
                break;
            case UPGRADE_LOCAL:
                state.addStateUpgradingLocal();
                if (!(DeviceState.checkValidWithPermittedStates(state,
                    DeviceState.UPGRADING_LOCAL,
                    DeviceState.INTERNET,
                    DeviceState.LOCAL) && DeviceState.checkValidWithNecessaryStates(state,
                    DeviceState.UPGRADING_LOCAL,
                    DeviceState.LOCAL)))
                {
                    throw new IllegalStateException("device: " + device + ",  case UPGRADE_LOCAL");
                }
               // __upgradeLocal(stateMachineDevice);
                break;
        }
        log.debug(Thread.currentThread().toString() + "##transformState(device=[" + device + "],stateMachineDevice=["
            + stateMachineDevice + "],direction=[" + direction + "])");
        // update device in local db
        // stateMachineDevice.saveInDB();
        // put the device into deviceCache
        log.error("stateMachineDevice name:" + stateMachineDevice.getName());
        deviceCache.addStatemahchineDeviceCache(stateMachineDevice);
        
        // notify the IUser
        deviceCache.notifyIUser(notifyType);
    }
    
    private void __checkValid(IDevice device, Direction direction)
    {
        IDeviceState currentState = device.getDeviceState();
        // graph 1
        // start configure
        if (DeviceState.checkValidWithSpecificStates(currentState, DeviceState.NEW))
        {
            if (direction == Direction.CONFIGURE)
            {
                return;
            }
        }
        // CONFIGURING and DELETED: means the device is configured fail last time, and device is configured again
        if (DeviceState.checkValidWithSpecificStates(currentState,
            DeviceState.CONFIGURING,
            DeviceState.DELETED))
        {
            if (direction == Direction.CONFIGURE)
            {
                return;
            }
        }
        
        // configure fail or configure suc(start activate)
        if (DeviceState.checkValidWithSpecificStates(currentState, DeviceState.CONFIGURING))
        {
            if (direction == Direction.ACTIVATE || direction == Direction.FAIL)
            {
                return;
            }
        }
        
        // activate fail
        if (DeviceState.checkValidWithSpecificStates(currentState, DeviceState.ACTIVATING))
        {
            if (direction == Direction.FAIL)
            {
                return;
            }
        }
        // activate suc
        if (DeviceState.checkValidWithNecessaryStates(currentState, DeviceState.OFFLINE)
            || DeviceState.checkValidWithPermittedStates(currentState,
                DeviceState.OFFLINE,
                DeviceState.INTERNET))
        {
            if (direction == Direction.SUC)
            {
                return;
            }
        }
        // start upgrade local
        if (DeviceState.checkValidWithNecessaryStates(currentState, DeviceState.LOCAL)
            && DeviceState.checkValidWithPermittedStates(currentState,
                DeviceState.LOCAL,
                DeviceState.INTERNET,
                DeviceState.RENAMED))
        {
            if (direction == Direction.UPGRADE_LOCAL)
            {
                return;
            }
        }
        // start upgrade internet
        if (DeviceState.checkValidWithNecessaryStates(currentState, DeviceState.INTERNET)
            && DeviceState.checkValidWithPermittedStates(currentState,
                DeviceState.INTERNET,
                DeviceState.LOCAL,
                DeviceState.RENAMED))
        {
            if (direction == Direction.UPGRADE_INTERNET)
            {
                return;
            }
        }
        // upgrade local suc or fail
        if (DeviceState.checkValidWithNecessaryStates(currentState, DeviceState.UPGRADING_LOCAL)
            && DeviceState.checkValidWithPermittedStates(currentState,
                DeviceState.UPGRADING_LOCAL,
                DeviceState.LOCAL,
                DeviceState.INTERNET,
                DeviceState.RENAMED))
        {
            if (direction == Direction.SUC || direction == Direction.FAIL)
            {
                return;
            }
        }
        // upgrade internet suc or fail
        if (DeviceState.checkValidWithNecessaryStates(currentState, DeviceState.UPGRADING_INTERNET)
            && DeviceState.checkValidWithPermittedStates(currentState,
                DeviceState.UPGRADING_INTERNET,
                DeviceState.LOCAL,
                DeviceState.INTERNET,
                DeviceState.RENAMED))
        {
            if (direction == Direction.SUC || direction == Direction.FAIL)
            {
                return;
            }
        }
        // graph 2
        // start rename or delete
        if (DeviceState.checkValidWithNecessaryStates(currentState, DeviceState.OFFLINE)
            || DeviceState.checkValidWithNecessaryStates(currentState, DeviceState.LOCAL)
            || DeviceState.checkValidWithNecessaryStates(currentState, DeviceState.INTERNET)
            && DeviceState.checkValidWithPermittedStates(currentState,
                DeviceState.OFFLINE,
                DeviceState.LOCAL,
                DeviceState.INTERNET,
                DeviceState.RENAMED))
        {
            if (direction == Direction.DELETE || direction == Direction.RENAME)
            {
                return;
            }
        }
        // rename suc or rename fail or delete suc or delete fail
        if (DeviceState.checkValidWithNecessaryStates(currentState, DeviceState.RENAMED)
            || DeviceState.checkValidWithNecessaryStates(currentState, DeviceState.DELETED)
            && DeviceState.checkValidWithPermittedStates(currentState,
                DeviceState.RENAMED,
                DeviceState.DELETED,
                DeviceState.CONFIGURING,
                DeviceState.ACTIVATING,
                DeviceState.OFFLINE,
                DeviceState.LOCAL,
                DeviceState.INTERNET))
        {
            if (direction == Direction.SUC)
            {
                return;
            }
        }
        // when login, resume delete action(the delete action is fail last time,but device's state is DELETED)
        if (DeviceState.checkValidWithSpecificStates(currentState, DeviceState.DELETED))
        {
            if (direction == Direction.DELETE)
            {
                return;
            }
        }
        log.error(Thread.currentThread().toString() + "##__checkValid(device=[" + device + "],direction=[" + direction
            + "]) IllegalStateException");
        throw new IllegalStateException("device: " + device + ",direction:" + direction);
    }
}
