package com.vijayanix.iot.action.device.common;


import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.command.device.common.CommandDeviceDiscoverLocal;
import com.vijayanix.iot.command.device.common.CommandDeviceSynchronizeInternet;
import com.vijayanix.iot.command.device.common.ICommandDeviceDiscoverLocal;
import com.vijayanix.iot.command.device.common.ICommandDeviceSynchronizeInternet;
import com.vijayanix.iot.common.net.udp.LightUdpClient;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.IDeviceState;
import com.vijayanix.iot.model.device.IOTAddress;
import com.vijayanix.iot.model.device.builder.BDevice;
import com.vijayanix.iot.model.device.cache.IDeviceCache;
import com.vijayanix.iot.mqtt.MqttConstant;
import com.vijayanix.iot.mqtt.ReceivedMessage;
import com.vijayanix.iot.mqtt.SubTopic;
import com.vijayanix.iot.util.BSSIDUtil;
import com.vijayanix.iot.util.BaseApiUtil;
import com.vijayanix.iot.util.NotifyUIUtil;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ActionDeviceSynchronizeInterentDiscoverLocal implements
		IActionDeviceSynchronizeInterentDiscoverLocal {
	private final static Logger log = Logger.getLogger(ActionDeviceSynchronizeInterentDiscoverLocal.class);

	private static Object lock = new Object();

	private List<IOTAddress> doCommandDeviceDiscoverLocal() {
		ICommandDeviceDiscoverLocal command = new CommandDeviceDiscoverLocal();
		return command.doCommandDeviceDiscoverLocal();
	}

	private List<IDevice> doCommandGroupSynchronizeInternet(String userKey) {
	    ICommandDeviceSynchronizeInternet action = new CommandDeviceSynchronizeInternet();
        return action.doCommandDeviceSynchronizeInternet(userKey);
	}

	private void  doCommandGetDeviceType(String deviceKey){
		ICommandDeviceSynchronizeInternet action = new CommandDeviceSynchronizeInternet();
		action.getDeviceType(deviceKey);
	}


	private ConcurrentHashMap<String, IDevice> deviceHashMap = new ConcurrentHashMap<String, IDevice>();


	Set<String> stationSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	Set<String> serverSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
//	Set<String> localSet = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());


	@Override
	public IDevice getFoundedDeviceByssid(String bssid) {
		return deviceHashMap.get(bssid);
	}

	@Override
	public List<IDevice> getFoundDevices() {
		return new ArrayList(deviceHashMap.values());
	}

	private void doActionDeviceSynchronizeInterentDiscoverLocal(final String userKey, boolean serverRequired,
	                                                            boolean stationRequired) {

		stationSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
		serverSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

		/*//clear states, in case some devices state chagned or counld found locally next time
		Iterator<Map.Entry<String, IDevice>> iterator = deviceHashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, IDevice> entry = iterator.next();
			entry.getValue().getDeviceState().addStateOffline();
		}
*/
		// internet variables
		Callable taskInternet = null;
		Future futureInternet = null;
		// local variables
		List<IOTAddress> iotAddressList = new ArrayList<IOTAddress>();
		Callable taskLocal = null;
		Future futureLocal = null;

		List<Future> futures = new ArrayList<>();


		// task Internet
		if (serverRequired) {
			taskInternet = new Callable() {
				@Override
				public Object call()
						throws Exception {
					log.debug(Thread.currentThread().toString()
							+ "##__doActionDeviceSynchronizeInterentDiscoverLocal(userKey=[" + userKey
							+ "]): doCommandDeviceSynchronizeInternet()");
					List<IDevice>  espDevices = doCommandGroupSynchronizeInternet(userKey);
					log.debug("got  "+ espDevices.size() +" devices on internet");

					for (IDevice espDevice : espDevices) {
						boolean isFound = updateExistingDevice(espDevice);

						//request the device if we hanvent stored its type and stuff before
						if (!isFound){
							doCommandGetDeviceType(espDevice.getKey());
						}
					}

					return null;
				}

			};


			futureInternet = BaseApiUtil.submit(taskInternet);
			futures.add(futureInternet);

		}

		// task Local
		if (stationRequired) {
			taskLocal = new Callable() {
				@Override
				public Object call()
						throws Exception {
					log.debug(Thread.currentThread().toString()
							+ "##__doActionDeviceSynchronizeInterentDiscoverLocal(userKey=[" + userKey
							+ "]): doCommandDeviceDiscoverLocal()");
					List<IOTAddress> addressList = doCommandDeviceDiscoverLocal();

					log.debug("doCommandDeviceDiscoverLocal get IOTAddress size is :" + (addressList == null || addressList.isEmpty() ? 0 : addressList.size()));

					boolean changed = false;

					for (IOTAddress iotAddress : addressList) {
						boolean temp = putOrUpdateStationDevice(iotAddress);
						if (temp) {
							changed = true;
						}
					}
/*					if (changed) {
						NotifyUIUtil.notifyIUser(IDeviceCache.NotifyType.PULL_REFRESH, null);

					}
					log.debug("changed = " + changed);*/
// else{
//	                    NotifyUIUtil.notifyIUser(IDeviceCache.NotifyType.PULL_REFRESH,null);
//                    }
					return null;
				}

			};

			for (int executeTime = 0; executeTime < UDP_EXECUTE_MAX_TIMES; executeTime++) {
				futureLocal = BaseApiUtil.submit(taskLocal);
				futures.add(futureLocal);
				try {
					Thread.sleep(UDP_EXECUTE_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}


		//waiting for all asyn task is done
		for (Future future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		}


		compare();


	}


	private void compare(){

		//clear states, in case some devices cant be found next time
		Iterator<Map.Entry<String, IDevice>> iterator = deviceHashMap.entrySet().iterator();
		boolean changed = false;
		//boolean onlineChanged = false;
		while (iterator.hasNext()) {
			Map.Entry<String, IDevice> entry = iterator.next();
			String bssid = entry.getKey();
			IDeviceState deviceState= entry.getValue().getDeviceState();
			//boolean previousOffline = deviceState.isStateOffline();
			if (deviceState.isStateLocal()&&!stationSet.contains(bssid)) {
				//entry.getValue().getDeviceState().clearStateLocal();
				deviceState.clearStateLocal();
				changed = true;
			}
			if (deviceState.isStateInternet()&&!serverSet.contains(bssid)) {
				//entry.getValue().getDeviceState().clearStateInternet();
				deviceState.clearStateInternet();
				changed = true;
			}
			if (!deviceState.isStateOffline()&&!stationSet.contains(bssid) && !serverSet.contains(bssid)) {
				//entry.getValue().getDeviceState().addStateOffline();
				deviceState.addStateOffline();
				changed = true;
			}
/*
			if (deviceState.isStateOffline() != previousOffline){
				onlineChanged = true;
			}*/
		}

		triggerChange(changed,true);

	}


	private void triggerChange(boolean changed,boolean stateChanged){

		if (changed){
			saveDeviceAsyn();
		}

		if (stateChanged){
			NotifyUIUtil.notifyIUser(IDeviceCache.NotifyType.PULL_REFRESH,null);
		}

	}

	private void triggerChangeSingleItem(IDevice espDevice, boolean stateChanged){
		if (stateChanged){
			NotifyUIUtil.notifyIUser(IDeviceCache.NotifyType.STATE_MACHINE_UI,espDevice.getBssid());
		}

	}


	private void saveDeviceAsyn() {
		BaseApiUtil.submit(new Runnable() {

			@Override
			public void run() {
				for (IDevice device : getFoundDevices()) {
					device.saveInDB();
				}


			}

		});
	}


	@Override
	public boolean putIntoDevice(IDevice espDevice) {
		//if (espDevice.getDeviceState().isStateDeleted())
		//localSet.add(espDevice.getBssid());
		deviceHashMap.putIfAbsent(espDevice.getBssid(), espDevice);

		return true;
	}


	private boolean updateExistingDevice(IDevice espDevice){
		String deviceKey = espDevice.getKey();
		boolean isOnline = espDevice.getDeviceState().isStateInternet();
		boolean found = false;

		synchronized (lock) {
			Iterator<IDevice> deviceIterator = deviceHashMap.values().iterator();
			while (deviceIterator.hasNext()){
				IDevice espDevice1 = deviceIterator.next();
				if (deviceKey.equals(espDevice1.getKey())){
					if (isOnline){
						espDevice1.getDeviceState().clearStateOffline();
						espDevice1.getDeviceState().addStateInternet();
					}else{
						espDevice1.getDeviceState().clearStateInternet();
					}
					serverSet.add(espDevice1.getBssid());
					found = true;
					break;
				}
			}
		}


		return found;

	}


	/**
	 * add or upagte the device got from mqtt message
	 * @param espDevice
	 * @return
	 */
	private boolean putOrUpdateInternetDevice(IDevice espDevice){
		String bssid = espDevice.getBssid();
		IDevice tempStaDevice = null;
		boolean changed = false;

		synchronized (lock) {
			if (deviceHashMap.containsKey(bssid)) {
				tempStaDevice = deviceHashMap.get(bssid);

				tempStaDevice.setDeviceType(espDevice.getDeviceType());
				if (!espDevice.getKey().equals(tempStaDevice.getKey())){
					tempStaDevice.setKey(espDevice.getKey());
					changed = true;
				}
				if (!espDevice.getDeviceType().equals(tempStaDevice.getDeviceType())){
					tempStaDevice.setDeviceType(espDevice.getDeviceType());
					changed = true;
				}

			} else {
				tempStaDevice = espDevice;
				deviceHashMap.put(bssid, tempStaDevice);
				changed = true;
			}

			serverSet.add(bssid);
			boolean stateInternet = tempStaDevice.getDeviceState().isStateInternet();

			if (!stateInternet) {
				tempStaDevice.getDeviceState().addStateInternet();
				tempStaDevice.getDeviceState().clearStateOffline();
				changed = true;
			}		}

		return changed;

	}

	/*@Override
	public boolean putOrUpdate(IOTAddress address, DiscoverSource source) {

		String bssid = address.getBSSID();
		IDevice tempStaDevice = null;

		boolean changed = false;
		synchronized (lock) {

			boolean stateInternet = false;
			boolean stateLocal = false;


			if (deviceHashMap.containsKey(bssid)) {
				tempStaDevice = deviceHashMap.get(bssid);
				tempStaDevice.setInetAddress(address.getInetAddress());

			} else {
				String ssid = BSSIDUtil.genDeviceNameByBSSID(bssid);
				address.setSSID(ssid);
				tempStaDevice = BDevice.getInstance().createClearDevice(address);

				deviceHashMap.put(bssid, tempStaDevice);
			}

			switch (source) {
				case SERVER:
					serverSet.add(bssid);
					stateInternet = tempStaDevice.getDeviceState().isStateInternet();
					if (!stateInternet) {
						tempStaDevice.getDeviceState().addStateInternet();
						changed = true;
					}

					break;
				case STATION:
					stationSet.add(bssid);
					stateLocal = tempStaDevice.getDeviceState().isStateLocal();
					if (!stateLocal) {
						tempStaDevice.getDeviceState().addStateLocal();
						tempStaDevice.getDeviceState().clearStateOffline();
						changed = true;
					}

					break;
				case LOCAL:
					break;

				default:
					break;
			}

		}

		if (changed) {
			// NotifyUIUtil.notifyIUser(IDeviceCache.NotifyType.STATE_MACHINE_UI,tempStaDevice.getBssid());
		}

		return changed;
	}*/
	public boolean putOrUpdateStationDevice(IOTAddress address) {

		String bssid = address.getBSSID();
		IDevice tempStaDevice = null;

		boolean changed = false;
		boolean stateLocal = false;

		synchronized (lock) {

			if (deviceHashMap.containsKey(bssid)) {
				tempStaDevice = deviceHashMap.get(bssid);
				tempStaDevice.setInetAddress(address.getInetAddress());

			} else {
				String ssid = BSSIDUtil.genDeviceNameByBSSID(bssid);
				address.setSSID(ssid);
				tempStaDevice = BDevice.getInstance().createClearDevice(address);
				deviceHashMap.put(bssid, tempStaDevice);
			}

			stationSet.add(bssid);

			stateLocal = tempStaDevice.getDeviceState().isStateLocal();
			if (!stateLocal) {
				tempStaDevice.getDeviceState().addStateLocal();
				tempStaDevice.getDeviceState().clearStateOffline();
				changed = true;
			}
		}


		return changed;
	}


	private IDevice getEspDevice(String deviceKey){
		Iterator<Map.Entry<String, IDevice>> iterator = deviceHashMap.entrySet().iterator();
		while (iterator.hasNext()){
			IDevice espDevice = iterator.next().getValue();
			if (deviceKey.equals(espDevice.getKey())){
				return  espDevice;
			}
		}
		return null;
	}


	private boolean shouldConcern(IDevice espDevice, String topicName){
		if (espDevice == null){
			return false;
		}
		 for(String topicFilter: espDevice.getPrefrenceTopics()){
			 if (MqttConstant.isMatched(topicFilter, topicName)) {
				 return true;
			 }
		 }
		return false;
	}

	@Override
	public void processMqttMessage(ReceivedMessage receivedMessage) {
		String topic = receivedMessage.getTopic();
		int subTopic = MqttConstant.getSubTopic(topic);

		String deviceKey = MqttConstant.getDeviceKey(topic);
		IDevice device = getEspDevice(deviceKey);

		if (subTopic == SubTopic.DEVICE_TYPE){
			IActionDeviceStatusMqttSubscriber subscriber = new ActionDeviceStatusMqttSubscriber();
			IDevice espDevice = subscriber.doActionDeviceType(null,topic,new String(receivedMessage.getMessage().getPayload()));
			putOrUpdateInternetDevice(espDevice);
			//new device
			if (device == null && espDevice!=null){
				triggerChange(true,true);
			}else {
				//update device
				triggerChangeSingleItem(device,true);
			}

			return;

		}


		if (!shouldConcern(device,topic)){
			return;
		}

		 if (subTopic == SubTopic.WILL){
			IActionDeviceStatusMqttSubscriber subscriber = new ActionDeviceStatusMqttSubscriber();
			subscriber.doActionDeviceWill(device);
			 triggerChangeSingleItem(device,true);

		}else {
			 IActionDeviceStatusMqttSubscriber subscriber = new ActionDeviceStatusMqttSubscriber();
			subscriber.doActionDeviceGetStatus(device,topic,new String(receivedMessage.getMessage().getPayload()));
			 triggerChangeSingleItem(device,true);
		}


	}


	@Override
	public void doActionDeviceSynchronizeInterentDiscoverLocal(final String userKey) {
		// do it in background thread
		BaseApiUtil.submit(new Runnable() {

			@Override
			public void run() {
				broadcastPhoneAddress();
				doActionDeviceSynchronizeInterentDiscoverLocal(userKey, true, true);
			}

		});
	}

	@Override
	public void doActionDeviceSynchronizeDiscoverLocal(boolean isSyn) {
		if (isSyn) {
			doActionDeviceSynchronizeInterentDiscoverLocal(null, false, true);
		} else {
			BaseApiUtil.submit(new Runnable() {

				@Override
				public void run() {
					broadcastPhoneAddress();
					doActionDeviceSynchronizeInterentDiscoverLocal(null, false, true);
				}

			});
		}
	}

	private void broadcastPhoneAddress() {
		LightUdpClient client = new LightUdpClient(IOTApplication.getContext());
		for (int i = 0; i < 3; i++) {
			client.notifyAddress();
		}
		client.close();
	}

	@Override
	public void doActionDeviceSynchronizeInternet(final String userKey) {
		BaseApiUtil.submit(new Runnable() {

			@Override
			public void run() {
				doActionDeviceSynchronizeInterentDiscoverLocal(userKey, true, false);
			}

		});
	}

}
