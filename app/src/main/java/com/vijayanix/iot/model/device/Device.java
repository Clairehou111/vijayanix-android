package com.vijayanix.iot.model.device;

import android.text.TextUtils;

import com.vijayanix.iot.db.DeviceDBManager;
import com.vijayanix.iot.mqtt.MqttConstant;
import com.vijayanix.iot.mqtt.SubTopic;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Device implements IDevice, Cloneable
{
    protected String mBssid;
    
    protected long mDeviceId;
    
    protected String mDeviceKey;
    
    protected boolean mIsOwner;
    
    protected String mDeviceName;
    
    protected String mRomVersion;
    
    protected String mLatestRomVersion;
    
    protected long mTimeStamp;
    
    protected long mActivatedTime;
    
    protected long mUserId;
    
    protected boolean _isDeviceRefreshed;
    
    protected DeviceType mDeviceType;
    
    protected IDeviceState mDeviceState;
    
    protected InetAddress mInetAddress;
    
    protected boolean mIsMeshDevice;
    
    protected String mParentDeviceBssid;
    
    protected String mRootDeviceBssid;
    

    protected int mRssi = RSSI_NULL;
    
    protected String mInfo;
    
    private List<String> mSubscribedTopics = new ArrayList<String>();

    private List<String> mPrefrenceTopics = new ArrayList<String>();


    public Device(){
    }

    /**
     * empty device1 is used by DeviceCacheHandler to distinguish between Internet unaccessible and user with no
     * devices
     */
    public static Device EmptyDevice1 = new Device();
    
    /**
     * empty device2 is used by DeviceCacheHandler to distinguish between Internet unaccessible and user's device
     * list is empty
     */
    public static Device EmptyDevice2 = new Device();
    
    @Override
    public void setBssid(String bssid)
    {
        this.mBssid = bssid;
    }
    
    @Override
    public String getBssid()
    {
        return this.mBssid;
    }
    
    @Override
    public long getId()
    {
        return this.mDeviceId;
    }
    
    @Override
    public void setId(long id)
    {
        this.mDeviceId = id;
    }
    
    @Override
    public String getKey()
    {
        return this.mDeviceKey;
    }
    
    @Override
    public void setKey(String key)
    {
        this.mDeviceKey = key;
	    resetSubscribedTopics();
	    resetReferenceTopics();
    }
    
    @Override
    public boolean getIsOwner()
    {
        return this.mIsOwner;
    }
    
    @Override
    public void setIsOwner(boolean isOwner)
    {
        this.mIsOwner = isOwner;
    }
    
    @Override
    public String getName()
    {
        return this.mDeviceName;
    }
    
    @Override
    public void setName(String name)
    {
        this.mDeviceName = name;
    }
    
    @Override
    public void setRom_version(String rom_version)
    {
        this.mRomVersion = rom_version;
    }
    
    @Override
    public String getRom_version()
    {
        return this.mRomVersion;
    }
    
    @Override
    public String getLatest_rom_version()
    {
        return this.mLatestRomVersion;
    }
    
    @Override
    public void setLatest_rom_version(String latest_rom_version)
    {
        this.mLatestRomVersion = latest_rom_version;
    }
    
    @Override
    public long getTimestamp()
    {
        return this.mTimeStamp;
    }
    
    @Override
    public void setTimestamp(long timestamp)
    {
        this.mTimeStamp = timestamp;
    }
    
    @Override
    public long getActivatedTime()
    {
        return mActivatedTime;
    }
    
    @Override
    public void setActivatedTime(long activatedAt)
    {
        mActivatedTime = activatedAt;
    }
    
    @Override
    public long getUserId()
    {
        return this.mUserId;
    }
    
    @Override
    public void setUserId(long userId)
    {
        this.mUserId = userId;
    }
    
    @Override
    public int getRssi() {
        return mRssi;
    }
    
    @Override
    public void setRssi(int rssi) {
        mRssi = rssi;
    }
    
    @Override
    public void setInfo(String info) {
        mInfo = info;
    }
    
    @Override
    public String getInfo() {
        return mInfo;
    }
    
    @Override
    public void __setDeviceRefreshed()
    {
        this._isDeviceRefreshed = true;
    }
    
    @Override
    public void __clearDeviceRefreshed()
    {
        this._isDeviceRefreshed = false;
    }
    
    @Override
    public boolean __isDeviceRefreshed()
    {
        return this._isDeviceRefreshed;
    }
    
    @Override
    public void setDeviceType(DeviceType deviceType)
    {
        this.mDeviceType = deviceType;
    }
    
    @Override
    public DeviceType getDeviceType()
    {
        return this.mDeviceType;
    }
    
    @Override
    public void setDeviceState(IDeviceState deviceState)
    {
        this.mDeviceState = deviceState;
    }
    
    @Override
    public IDeviceState getDeviceState()
    {
        return this.mDeviceState;
    }
    
    @Override
    public void setInetAddress(InetAddress inetAddress)
    {
        this.mInetAddress = inetAddress;
    }
    
    @Override
    public InetAddress getInetAddress()
    {
        return this.mInetAddress;
    }
    
    @Override
    public void setIsMeshDevice(boolean isMeshDevice)
    {
        this.mIsMeshDevice = isMeshDevice;
    }
    
    @Override
    public boolean getIsMeshDevice()
    {
        return this.mIsMeshDevice;
    }
    
    @Override
    public void setParentDeviceBssid(String parentBssid)
    {
        this.mParentDeviceBssid = parentBssid;
    }
    
    @Override
    public String getParentDeviceBssid()
    {
        return this.mParentDeviceBssid;
    }
    

    @Override
    public boolean isSupportTimer()
    {
        switch (mDeviceType)
        {
            case PLUG:
            case PLUGS:
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public boolean isSupportTrigger() {
        return false;
    }
    
    @Override
    public void saveInDB()
    {
        DeviceDBManager deviceDBManager = DeviceDBManager.getInstance();
        int type = this.getDeviceType().getSerial();
        int state = this.getDeviceState().getStateValue();
        deviceDBManager.insertOrReplace(mDeviceId,
            mDeviceKey,
            mBssid,
            type,
            state,
            mIsOwner,
            mDeviceName,
            mRomVersion,
            mLatestRomVersion,
            mTimeStamp,
            mActivatedTime,
            mUserId);
    }
    
    @Override
    public void deleteInDB()
    {
        DeviceDBManager deviceDBManager = DeviceDBManager.getInstance();
        deviceDBManager.delete(mDeviceId);
    }
    
    @Override
    public void clear()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean isSimilar(IDevice device) {
        // check whether the device is null
        if (device == null) {
            return false;
        }
        // the same device is similar of course
        return this.equals(device) || this.getBssid().equals(device.getBssid());
    }
    
    @Override
    public boolean isStateEqual(IDevice device)
    {
        if (!this.equals(device))
        {
            return false;
        }
        return this.getDeviceState().getStateValue() == device.getDeviceState().getStateValue();
    }
    
    @Override
    public IDevice cloneDevice()
    {
        IDevice device = null;
        try
        {
            device = (Device)this.clone();
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return device;
    }
    
    @Override
    public void copyDeviceState(IDevice device)
    {
        this.mDeviceState.setStateValue(device.getDeviceState().getStateValue());
    }
    
    @Override
    public void copyDeviceRomVersion(IDevice deivce)
    {
        /*IEspDeviceUpgradeParser parser = EspDeviceUpgradeParser.getInstance();
//        IEspDeviceUpgradeInfo deviceUpgradeInfoCurrent = parser.parseUpgradeInfo(this.mRomVersion);
        IEspDeviceUpgradeInfo deviceUpgradeInfoNew = parser.parseUpgradeInfo(deivce.getRom_version());
        if (deviceUpgradeInfoNew == null)
        {
            // the rom version don't satisfy the new version format
            return;
        }
        // only the higher version could be copied to low version
//        if (deviceUpgradeInfoCurrent == null
//            || deviceUpgradeInfoNew.getVersionValue() > deviceUpgradeInfoCurrent.getVersionValue())
//        {
        this.mRomVersion = deivce.getRom_version();
//        }
        this.mLatestRomVersion = deivce.getLatest_rom_version();*/
    }
    

    @Override
    public void copyDeviceRssi(IDevice device)
    {
        int newRssi = device.getRssi();
        if (newRssi != IDevice.RSSI_NULL)
        {
            this.mRssi = newRssi;
        }
    }
    
    @Override
    public void copyDeviceName(IDevice device)
    {
        this.mDeviceName = device.getName();
    }
    
    @Override
    public void copyInetAddress(IDevice device)
    {
        this.mInetAddress = device.getInetAddress();
    }
    
    @Override
    public void copyIsMeshDevice(IDevice device)
    {
        this.mIsMeshDevice = device.getIsMeshDevice();
    }
    

    @Override
    public void copyParentDeviceBssid(IDevice device)
    {
        this.mParentDeviceBssid = device.getParentDeviceBssid();
    }
    
    @Override
    public void copyTimestamp(IDevice device)
    {
        this.mTimeStamp = device.getTimestamp();
    }
    
    @Override
    public void copyActivatedTime(IDevice device)
    {
        mActivatedTime = device.getActivatedTime();
    }
    
    @Override
    public void copyDeviceInfo(IDevice device)
    {
        String newInfo = device.getInfo();
        if (newInfo != null)
        {
            mInfo = device.getInfo();
        }
    }
    
    @Override
    public boolean equals(Object o)
    {
        // check the type
        if (o == null || !(o instanceof IDevice))
        {
            return false;
        }
        IDevice other = (IDevice)o;
        return other.getKey().equals(mDeviceKey);
    }
    
    @Override
    public int hashCode()
    {
        return mDeviceKey.hashCode();
    }
    
    @Override
    public Object clone()
        throws CloneNotSupportedException
    {
        Device device = (Device)super.clone();
        // deep copy
        DeviceState state = (DeviceState)device.getDeviceState();
        device.mDeviceState = (IDeviceState)state.clone();
        return device;
    }
    
    @Override
    public String toString()
    {
        return "Device: (mUserId=[" + mUserId + "]," + "mBssid=[" + mBssid + "],mParentDeviceBssid=["
            + mParentDeviceBssid + "]mDeviceId=[" + mDeviceId + "],mDeviceName=[" + mDeviceName + "],mDeviceState=["
            + mDeviceState + "],mIsMeshDevice=[" + mIsMeshDevice + "],mInetAddress=[" + mInetAddress + "])";
    }
    
   /* private List<IEspDeviceTreeElement> __getDeviceTreeElementListByBssid2(List<IDevice> allDeviceList)
    {
        if (!allDeviceList.contains(this))
        {
            throw new IllegalStateException("allDeviceList don't contain current device");
        }
        if (!this.getIsMeshDevice())
        {
            return Collections.emptyList();
        }
        
        // filter mesh devices
        List<IDevice> allMeshDeviceList = new ArrayList<IDevice>();
        for (IDevice device : allDeviceList)
        {
            if (device.getIsMeshDevice())
            {
                allMeshDeviceList.add(device);
            }
        }
        
        int allDeviceCount = allMeshDeviceList.size();
        int[] levelArray = new int[allDeviceCount];
        Arrays.fill(levelArray, -1);
        boolean[] hasChildArray = new boolean[allDeviceCount];
        Arrays.fill(hasChildArray, false);
        String[] parentKeyArray = new String[allDeviceCount];
        Arrays.fill(parentKeyArray, null);
        boolean[] hasProcessedArray = new boolean[allDeviceCount];
        Arrays.fill(hasProcessedArray, false);
        
        // set device tree element from top to bottom
        // set level 1
        for (int index = 0; index < allDeviceCount; ++index)
        {
            IDevice device = allMeshDeviceList.get(index);
            if (this.equals(device))
            {
                levelArray[index] = 1;
                break;
            }
        }
        int currentLevel = 1;
        boolean isContinue = false;
        do
        {
            isContinue = false;
            int parentIndex;
            do
            {
                parentIndex = -1;
                // get one device of specific level
                for (int i = 0; i < allDeviceCount; ++i)
                {
                    if (levelArray[i] == currentLevel && !hasProcessedArray[i])
                    {
                        hasProcessedArray[i] = true;
                        parentIndex = i;
                        isContinue = true;
                        break;
                    }
                }
                if (parentIndex != -1)
                {
                    IDevice parentDevice = allMeshDeviceList.get(parentIndex);
                    String parentBssid = parentDevice.getBssid();
                    String parentKey = parentDevice.getKey();
                    boolean parentHasChild = false;
                    // set the child device info
                    for (int childIndex = 0; childIndex < allDeviceCount; ++childIndex)
                    {
                        IDevice childDevice = allMeshDeviceList.get(childIndex);
                        String childParentBssid = childDevice.getParentDeviceBssid();
                        if (parentBssid.equals(childParentBssid))
                        {
                            parentHasChild = true;
                            levelArray[childIndex] = currentLevel + 1;
                            parentKeyArray[childIndex] = parentKey;
                        }
                    }
                    
                    // set the parent device info
                    hasChildArray[parentIndex] = parentHasChild;
                }
            } while (parentIndex != -1);
            ++currentLevel;
        } while (isContinue);
        // build deviceTreeElementList
        List<IEspDeviceTreeElement> deviceTreeElementList = new ArrayList<IEspDeviceTreeElement>();
        for (int i = 0; i < allDeviceCount; ++i)
        {
            int level = levelArray[i];
            if (level != -1)
            {
                IDevice device = allMeshDeviceList.get(i);
                String parentDeviceKey = parentKeyArray[i];
                boolean hasParent = level != 1;
                boolean hasChild = hasChildArray[i];
                IEspDeviceTreeElement deviceTreeElement =
                    new EspDeviceTreeElement(device, parentDeviceKey, hasParent, hasChild, level);
                deviceTreeElementList.add(deviceTreeElement);
            }
        }
        return deviceTreeElementList;
    }*/
/*
    @Override
    public List<IEspDeviceTreeElement> getDeviceTreeElementList(List<IDevice> allDeviceList)
    {
        return __getDeviceTreeElementListByBssid2(allDeviceList);
    }*/
    
    @Override
    public boolean isActivated()
    {
        return mDeviceId > 0 && !TextUtils.isEmpty(mDeviceKey);
    }

    
    @Override
    public void setRootDeviceBssid(String rootBssid)
    {
        this.mRootDeviceBssid = rootBssid;
    }

    @Override
    public String getRootDeviceBssid()
    {
        return mRootDeviceBssid;
    }

	@Override
	public List<String> getSubscribedTopics() {
		return mSubscribedTopics;
	}

	@Override
	public void addSubscribedTopics(String topic) {
		this.mSubscribedTopics.add(topic);
	}


	private void initSubscribedTopics(){
		if (this.getKey() != null){
			this.addSubscribedTopics(MqttConstant.getTopicDevice(this.getKey()));
			this.addSubscribedTopics(MqttConstant.getTopicDeviceWildcards(this.getKey()));
		}
	}

	private void initPreferencedTopics(){
		if (this.getKey() != null){
			this.addReferencedTopic(MqttConstant.getTopicDevice(this.getKey()));
			this.addReferencedTopic(MqttConstant.getDeviceSubTopic(this.getKey(), SubTopic.DEVICE_TYPE));
			this.addReferencedTopic(MqttConstant.getDeviceSubTopic(this.getKey(), SubTopic.WILL));
			this.addPowerSwitchTopic();
		}
	}

	protected  void addPowerSwitchTopic(){

	}

	public void resetSubscribedTopics(){
	    mSubscribedTopics.clear();
	    initSubscribedTopics();


	}

	private void resetReferenceTopics(){
		mPrefrenceTopics.clear();
		initPreferencedTopics();


	}

	public void addReferencedTopic(String topic){
		mPrefrenceTopics.add(topic);

	}

	@Override
	public List<String> getPrefrenceTopics() {
		return mPrefrenceTopics;
	}
}
