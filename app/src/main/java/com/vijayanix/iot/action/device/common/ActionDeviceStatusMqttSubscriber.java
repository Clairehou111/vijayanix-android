package com.vijayanix.iot.action.device.common;


import com.google.gson.Gson;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.builder.BDevice;
import com.vijayanix.iot.model.device.plug.IDevicePlug;
import com.vijayanix.iot.model.device.plug.IStatusPlug;
import com.vijayanix.iot.model.device.plug.StatusPlug;
import com.vijayanix.iot.model.device.watchman.DeviceWatchmanStatus;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchman;
import com.vijayanix.iot.model.device.watchman.IDeviceWatchmanStatus;
import com.vijayanix.iot.util.DeviceDatastreamUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.vijayanix.iot.command.ICommand.Status;
import static com.vijayanix.iot.command.device.ICommandPlug.Response;

public class ActionDeviceStatusMqttSubscriber implements IActionDeviceStatusMqttSubscriber {

	@Override
	public void doActionDeviceGetStatus(IDevice device, String topic, String payload) {
		JSONObject responseJson = null;

		try {
			JSONObject jo = new JSONObject(payload);
			if (!jo.isNull(Response)) {
				responseJson = jo.getJSONObject(Response);
			} else {
				return;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		DeviceType deviceType = device.getDeviceType();
        if (!device.getDeviceState().isStateInternet()){
	        device.getDeviceState().clearStateOffline();
	        device.getDeviceState().addStateInternet();
        }


		switch (deviceType) {
			case PLUG:
				 executeGetPlugStatus(device, responseJson);
				break;

			case WATCHMAN:
				executeGetWatchmanStatus(topic,device,responseJson);
				break;

			default:
				throw new IllegalArgumentException();

		}
	}

	@Override
	public void doActionDeviceWill(IDevice device) {
		device.getDeviceState().clearStateInternet();
//		device.getDeviceState().clearStateInternet();
	}



   @Override
	public IDevice doActionDeviceType(IDevice device, String topic, String payload){
		return this.doActionGetDeviceTopic1(device,topic,payload);
	}


	private IDevice doActionGetDeviceTopic1(IDevice device, String topic, String payload) {
//		log.debug(payload);
		JSONObject responseJson = null;

		String bssid = null;
		int deviceType = -1;
		String deviceKey = null;

		try {
			JSONObject jo = new JSONObject(payload);

			if (!jo.isNull(Response)) {
				responseJson = jo.getJSONObject(Response);
			} else {
				return null;
			}

			bssid = responseJson.getString("bssid");
			deviceType = responseJson.getInt("deviceType");
			deviceKey = responseJson.getString("clientId");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		DeviceType type = DeviceType.getEspTypeEnumBySerial(deviceType);

		if (device == null) {
			device = BDevice.getInstance().createClearDevice(type, deviceKey, bssid);
		}
	/*	switch (type) {
			case PLUG:
				executeGetPlugStatus(device, responseJson);
				break;

			case WATCHMAN:
				executeGetWatchmanStatus(topic, device, responseJson);
				break;

			default:
				throw new IllegalArgumentException();

		}*/

		return device;

	}




	private void executeGetPlugStatus(IDevice device, JSONObject responseJson) {
		if (responseJson == null){
			return;
		}

		int on = 0;
		try {
			on = responseJson.getInt(Status);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		IStatusPlug statusPlug = new StatusPlug();
		statusPlug.setIsOn(on == 1);
		if (statusPlug != null) {
			((IDevicePlug) device).setStatusPlug(statusPlug);
		}

	}




	private void executeGetWatchmanStatus(String topic, IDevice device, JSONObject responseJson) {
		if (responseJson == null){
			return;
		}

		Gson gson = new Gson();
		IDeviceWatchmanStatus status = gson.fromJson(responseJson.toString(),DeviceWatchmanStatus.class);
		if (status != null){
			int datastreamType = DeviceDatastreamUtil.topic2status(topic);

			DeviceDatastreamUtil.copyDeviceStatus(datastreamType,status,((IDeviceWatchman)device).getWatchmanStatus());
		}

	}


}
