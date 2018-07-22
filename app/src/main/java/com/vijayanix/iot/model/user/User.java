package com.vijayanix.iot.model.user;

import android.text.TextUtils;

import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.action.device.array.ActionDeviceArrayPostStatus;
import com.vijayanix.iot.action.device.array.IActionDeviceArrayPostStatus;
import com.vijayanix.iot.action.device.common.ActionDeviceGetStatusInternet;
import com.vijayanix.iot.action.device.common.ActionDeviceGetStatusLocal;
import com.vijayanix.iot.action.device.common.ActionDevicePostStatusInternet;
import com.vijayanix.iot.action.device.common.ActionDevicePostStatusLocal;
import com.vijayanix.iot.action.device.common.ActionDeviceSynchronizeInterentDiscoverLocal;
import com.vijayanix.iot.action.device.common.IActionDeviceGetStatusInternet;
import com.vijayanix.iot.action.device.common.IActionDeviceGetStatusLocal;
import com.vijayanix.iot.action.device.common.IActionDevicePostStatusInternet;
import com.vijayanix.iot.action.device.common.IActionDevicePostStatusLocal;
import com.vijayanix.iot.action.device.common.IActionDeviceSynchronizeInterentDiscoverLocal;
import com.vijayanix.iot.action.device.esptouch.ActionDeviceEsptouch;
import com.vijayanix.iot.action.device.esptouch.IActionDeviceEsptouch;
import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.db.UserDBManager;
import com.vijayanix.iot.db.interfa.IDeviceDB;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.IDeviceState;
import com.vijayanix.iot.model.device.IOTAddress;
import com.vijayanix.iot.model.device.array.IDeviceArray;
import com.vijayanix.iot.model.device.builder.BDevice;
import com.vijayanix.iot.model.device.configure.IDeviceConfigure;
import com.vijayanix.iot.model.statemachine.DeviceStateMachine;
import com.vijayanix.iot.model.statemachine.DeviceStateMachineHandler;
import com.vijayanix.iot.model.statemachine.IDeviceStateMachine;
import com.vijayanix.iot.model.statemachine.IDeviceStateMachine.Direction;
import com.vijayanix.iot.model.statemachine.IDeviceStateMachineHandler;
import com.vijayanix.iot.mqtt.ReceivedMessage;
import com.vijayanix.iot.util.BSSIDUtil;
import com.vijayanix.iot.util.BaseApiUtil;
import com.vijayanix.iot.util.RandomUtil;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class User implements IUser
{
    
    private final static Logger log = Logger.getLogger(User.class);
    
    private long mUserId;
    private String mUserKey;
    private String mUserName;
    private String mUserEmail;
    private boolean isFirstTime;
    
    private List<IDevice> mDeviceList = new ArrayList<IDevice>();
    private final List<IDevice> mStaDeviceList = new ArrayList<IDevice>();
    
    private final List<IDevice> mTempStaDeviceList = new ArrayList<IDevice>();
    private final List<IDevice> mGuestDeviceList = new ArrayList<IDevice>();
    private final ReentrantLock mDeviceListsLock = new ReentrantLock();
    
    private volatile IActionDeviceEsptouch mActionDeviceEsptouch = null;
    private final Object mEsptouchLock = new Object();
    private volatile boolean mIsEsptouchCancelled = false;

	private IActionDeviceSynchronizeInterentDiscoverLocal mEspActionDeviceSynchronizeInterentDiscoverLocal;

	public User(){
		this.mEspActionDeviceSynchronizeInterentDiscoverLocal = new ActionDeviceSynchronizeInterentDiscoverLocal();
	}
    
    //private List<IEspGroup> mGroupList = new ArrayList<IEspGroup>();
    
    @Override
    public String toString()
    {
        return "[id=" + mUserId + ",key=" + mUserKey + ",email=" + mUserEmail + "]";
    }
    


    @Override
    public void setUserEmail(String userEmail)
    {
        this.mUserEmail = userEmail;
    }
    
    @Override
    public String getUserEmail()
    {
        return this.mUserEmail;
    }
    
    @Override
    public void setUserId(long userId)
    {
        this.mUserId = userId;
    }
    
    @Override
    public long getUserId()
    {
        return this.mUserId;
    }
    
    @Override
    public void setUserKey(String userKey)
    {
        this.mUserKey = userKey;
    }
    
    @Override
    public String getUserKey()
    {
        return this.mUserKey;
    }
    
    @Override
    public void setUserName(String userName)
    {
        mUserName = userName;
    }
    
    @Override
    public String getUserName()
    {
        return mUserName;
    }
    
    @Override
    public boolean isLogin()
    {
        return mUserId > 0 && !TextUtils.isEmpty(mUserKey);
    }

    @Override
	public boolean isFirstTime() {
		return isFirstTime;
	}
	@Override
	public void setFirstTime(boolean firstTime) {
		isFirstTime = firstTime;
	}


    

    
    @Override
    public boolean doActionPostDeviceStatus(IDevice device, IDeviceStatus status)
    {
        if (device instanceof IDeviceArray)
        {
            return doActionPostDeviceArrayStatus((IDeviceArray)device, status);
        }

        boolean isLocal = device.getDeviceState().isStateLocal();
        if (isLocal)
        {
            IActionDevicePostStatusLocal actionLocal = new ActionDevicePostStatusLocal();
            return actionLocal.doActionDevicePostStatusLocal(device, status);
        }
        else
        {
            IActionDevicePostStatusInternet actionInternet = new ActionDevicePostStatusInternet();
            return actionInternet.doActionDevicePostStatusInternet(device, status);
        }
    }

    @Override
    public boolean doActionPostDeviceStatus(int datastreamType, IDevice device, IDeviceStatus status)
    {


        boolean isLocal = device.getDeviceState().isStateLocal();
        if (isLocal)
        {
            IActionDevicePostStatusLocal actionLocal = new ActionDevicePostStatusLocal();
            return actionLocal.doActionDevicePostStatusLocal(datastreamType,device, status);
        }
        else
        {
            IActionDevicePostStatusInternet actionInternet = new ActionDevicePostStatusInternet();
            return actionInternet.doActionDevicePostStatusInternet(datastreamType,device, status);
        }
    }

    private boolean doActionPostDeviceArrayStatus(IDeviceArray device, IDeviceStatus status)
    {
        IActionDeviceArrayPostStatus action = new ActionDeviceArrayPostStatus();
        action.doActionDeviceArrayPostStatus(device, status);
        return true;
    }
    
    @Override
    public boolean doActionGetDeviceStatus(IDevice device)
    {
        boolean isLocal = device.getDeviceState().isStateLocal();
        if (isLocal)
        {
            IActionDeviceGetStatusLocal actionLocal = new ActionDeviceGetStatusLocal();
            return actionLocal.doActionDeviceGetStatusLocal(device);
        }
        else
        {
            IActionDeviceGetStatusInternet actionInternet = new ActionDeviceGetStatusInternet();
            return actionInternet.doActionDeviceGetStatusInternet(device);
        }
    }
    @Override
    public boolean doActionGetDeviceStatus(int datastreamType,IDevice device)
    {
        boolean isLocal = device.getDeviceState().isStateLocal();
        if (isLocal)
        {
            IActionDeviceGetStatusLocal actionLocal = new ActionDeviceGetStatusLocal();
            return actionLocal.doActionDeviceGetStatusLocal(datastreamType,device);
        }
        else
        {
            IActionDeviceGetStatusInternet actionInternet = new ActionDeviceGetStatusInternet();
            return actionInternet.doActionDeviceGetStatusInternet(datastreamType,device);
        }
    }



    public IUser doActionUserLoginDB2() {
//        IActionUserLoginDB action = new ActionUserLoginDB();
//        IUser result = action.doActionUserLoginDB();
	     doActionLoginUser2();
	    return null;
    }

    private void doActionLoginUser2(){

	    boolean hasBeenLogin = IOTApplication.getAppPreferences().getHasBeenLogin();

	    setFirstTime(!hasBeenLogin);

	    if(isFirstTime()){
		  //queryFromServer
          UserDBManager.getInstance().changeUserInfo(mUserId,mUserEmail,mUserKey,mUserName);
		    IOTApplication.getAppPreferences().setBeenLogin(true);
	  }else{
		  __loadUserDeviceList2(mUserId,mDeviceList);
	  }

    }

	private void __loadUserDeviceList2(long userId, List<IDevice> deviceList) {
        UserDBManager userDBManager = UserDBManager.getInstance();
        List<IDeviceDB> deviceDBList = userDBManager.getUserDeviceList(userId);
		// add device into mDeviceList by deviceDBList
		for (IDeviceDB deviceDB : deviceDBList) {
			IDevice device = BDevice.getInstance().alloc(deviceDB);
			IDeviceState deviceState = device.getDeviceState();
			deviceState.clearState();
			deviceState.addStateOffline();
			deviceList.add(device);
			mEspActionDeviceSynchronizeInterentDiscoverLocal.putIntoDevice(device);
		}

	}


	public IDevice getFoundedDeviceByssid(String ssid){
		return mEspActionDeviceSynchronizeInterentDiscoverLocal.getFoundedDeviceByssid(ssid);
	}

    @Override
	public List<IDevice> loadAllDevices(){
		return mEspActionDeviceSynchronizeInterentDiscoverLocal.getFoundDevices();
	}


    @Override
    public void doActionRefreshDevices()
    {
	    mEspActionDeviceSynchronizeInterentDiscoverLocal.doActionDeviceSynchronizeInterentDiscoverLocal(mUserKey);
    }
    
    @Override
    public void doActionRefreshStaDevices(boolean isSyn)
    {
	    mEspActionDeviceSynchronizeInterentDiscoverLocal.doActionDeviceSynchronizeDiscoverLocal(isSyn);
    }

   @Override
	public void addLocalDeviceListAysnc(final List<IOTAddress> iotAddressList)
	{
		BaseApiUtil.submit(new Runnable() {
			@Override
			public void run() {
				log.debug("__addTempStaDeviceList() iotAddressList:" + iotAddressList);
				boolean isDeviceChanged = false;
				for (IOTAddress iotAddress : iotAddressList)
				{
					String rootBssid = iotAddress.getRootBssid();
					String bssid = iotAddress.getBSSID();
					String ssid = BSSIDUtil.genDeviceNameByBSSID(bssid);
					iotAddress.setSSID(ssid);
					IDevice tempStaDevice = BDevice.getInstance().createStaDevice(iotAddress);
					tempStaDevice.setRootDeviceBssid(rootBssid);
					tempStaDevice.saveInDB();
				}
			}
		});


	}



    // "espressif_" + MAC address's 6 places
    private boolean isESPMeshDevice(String SSID)
    {
        for (int i = 0; i < MESH_DEVICE_SSID_PREFIX.length; i++)
        {
            if (SSID.startsWith(MESH_DEVICE_SSID_PREFIX[i]))
            {
                return true;
            }
        }
        return false;
    }
    
    // "ESP_" + MAC address's 6 places, ordinary device
    // "espressif_" + MAC address's 6 places, mesh device
    private boolean isESPDevice(String SSID)
    {
        for (int i = 0; i < DEVICE_SSID_PREFIX.length; i++)
        {
            if (SSID.startsWith(DEVICE_SSID_PREFIX[i]))
            {
                return true;
            }
        }
        return false;
    }
    

    




     @Override
    public void processMqttMessage(ReceivedMessage receivedMessage){
         mEspActionDeviceSynchronizeInterentDiscoverLocal.processMqttMessage(receivedMessage);
     }





	private List<IOTAddress> configDeviceEsptouch(final String apSsid, final String apBssid, final String apPassword,
	                                final boolean isSsidHidden,  int expectTaskResultCount,
	                                IEsptouchListener esptouchListener)

	{
		log.debug("doEsptouchTaskSynAddDeviceAsyn entrance");
		List<IEsptouchResult> esptouchResultList =
				mActionDeviceEsptouch.doActionDeviceEsptouch(expectTaskResultCount,
						apSsid,
						apBssid,
						apPassword,
						isSsidHidden,
						esptouchListener);
		// when requiredActivate false, we should discover sta devices
		log.debug("doEsptouchTaskSynAddDeviceAsyn requiredActivate = true");


			log.debug("doEsptouchTaskSynAddDeviceAsyn add sta device list last discovered");
			if (!mActionDeviceEsptouch.isCancelled() || mActionDeviceEsptouch.isDone())
			{
				// clear the interrupted by esptouchResultList
				log.debug("doEsptouchTaskSynAddDeviceAsyn clear the interrupted set by esptouch");
				Thread.interrupted();
			}

			// add the esptouch devices
			log.debug("doEsptouchTaskSynAddDeviceAsyn add the remainder esptouchResultList");

			List<IOTAddress> iotAddresses = new ArrayList<>();

			for (IEsptouchResult esptouchResult : esptouchResultList)
			{
				// check whether the task is executed suc
				if (!esptouchResult.isSuc())
				{
					break;
				}
				// for doActionRefreshStaDevices() can't find them,
				// so we can't get the info like deviceType, etc.
				// thus we can't make them added into staDeviceList
				IOTAddress iotAddress = getIOTAddress(esptouchResult.getBssid());

				iotAddresses.add(iotAddress);
			}


		return iotAddresses;

	}


	private IOTAddress getIOTAddress(String bssid)
	{
		IOTAddress iotAddress = null;
		for (int retry = 0; retry < 10; retry++)
		{
			iotAddress = BaseApiUtil.discoverDevice(BSSIDUtil.restoreBSSID(bssid));
			if (iotAddress != null)
			{
				return iotAddress;
			}
		}
		return null;
	}


    
    
    public boolean addDeviceAsyn(final IDevice device)
    {
        log.info("addDeviceAsyn() device:" + device);
        IDeviceStateMachineHandler handler = DeviceStateMachineHandler.getInstance();
        // there's another task about the device is executing, so return false
        if (!handler.isTaskFinished(device.getBssid()))
        {
            return false;
        }
        log.info("addDeviceAsyn() device:" + device + " is finished");
        String randomToken = RandomUtil.random40();
        IDeviceConfigure deviceConfigure = BDevice.getInstance().createConfiguringDevice(device, randomToken);
        String deviceName = device.getName();
        DeviceType deviceType = device.getDeviceType();
        String romVersion = device.getRom_version();
        int rssi = device.getRssi();
        String info = device.getInfo();
        deviceConfigure.setName(deviceName);
        deviceConfigure.setDeviceType(deviceType);
        deviceConfigure.setRom_version(romVersion);
        deviceConfigure.setRssi(rssi);
        deviceConfigure.setInfo(info);
        deviceConfigure.setUserId(mUserId);
        log.info("addDeviceAsyn() device:" + device + " before stateMachine");
        IDeviceStateMachine stateMachine = DeviceStateMachine.getInstance();
        stateMachine.transformState(deviceConfigure, Direction.CONFIGURE);
        log.info("addDeviceAsyn() device:" + device + " after stateMachine");
        return true;
    }


    public List<IOTAddress> configDeviceEsptouch(String apSsid, String apBssid, String apPassword, boolean isSsidHidden,
                                                 IEsptouchListener esptouchListener)
    {

        if (!doEsptouchTaskPrepare())
        {
            log.debug("addDevicesSyn fail for doEsptouchTaskPrepare()");
            return Collections.emptyList();
        }
        if (mActionDeviceEsptouch.isExecuted())
        {
            log.debug("addDevicesSyn fail for mActionDeviceEsptouch.isExecuted()");
            return Collections.emptyList();
        }

        return configDeviceEsptouch(apSsid, apBssid, apPassword, isSsidHidden, 1, esptouchListener);

    }

	private boolean doEsptouchTaskPrepare()
	{
		synchronized (mEsptouchLock)
		{
			if (mIsEsptouchCancelled)
			{
				mIsEsptouchCancelled = false;
				return false;
			}
			mActionDeviceEsptouch = new ActionDeviceEsptouch();
			mIsEsptouchCancelled = false;
		}
		return true;
	}

	@Override
	public void cancelAllAddDevices()
	{
		synchronized (mEsptouchLock)
		{
			if (mActionDeviceEsptouch != null)
			{
				if (mActionDeviceEsptouch.isCancelled() || mActionDeviceEsptouch.isDone())
				{
					mIsEsptouchCancelled = true;
				}
				else
				{
					mActionDeviceEsptouch.cancel();
				}
			}
		}
		IDeviceStateMachineHandler handler = DeviceStateMachineHandler.getInstance();
		handler.cancelAllTasks();
	}


	@Override
	public void doneAllAddDevices()
	{
		synchronized (mEsptouchLock)
		{
			if (mActionDeviceEsptouch != null)
			{
				mActionDeviceEsptouch.done();
			}
		}
	}


	@Override
	public IDevice getUserDevice(String deviceKey)
	{   //claire comment this
		// Check Virtual Root router device
       /* IDevice deviceRoot = BEspDeviceRoot.getBuilder().getLocalRoot();
        if (deviceKey.equals(deviceRoot.getKey()))
        {
            return deviceRoot;
        }
        deviceRoot = BEspDeviceRoot.getBuilder().getInternetRoot();
        if (deviceKey.equals(deviceRoot.getKey()))
        {
            return deviceRoot;
        }
        deviceRoot = BEspDeviceRoot.getBuilder().getVirtualMeshRoot();
        if (deviceKey.equals(deviceRoot.getKey()))
        {
            return deviceRoot;
        }
        */
		List<IDevice> deviceList = loadAllDevices();
		for (IDevice device : deviceList)
		{
			// if the device is DELETED, ignore it
			if (device.getDeviceState().isStateDeleted())
			{
				continue;
			}
			if (deviceKey.equals(device.getKey()))
			{
				return device;
			}
		}

		return null;
	}


	@Override
	public List<IDevice> getUserDevices(String[] deviceKeys) {
		List<IDevice> result = new ArrayList<IDevice>();
		List<IDevice> devices = loadAllDevices();
		for (IDevice device : devices) {
			if (device.getDeviceState().isStateDeleted()) {
				continue;
			}

			for (String key : deviceKeys) {
				if (key.equals(device.getKey())) {
					result.add(device);
					break;
				}
			}
		}
		return result;
	}


}
