package com.vijayanix.iot.mqtt;

import android.util.Log;

import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.model.device.Device;
import com.vijayanix.iot.model.device.DeviceState;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.http.HeaderPair;
import com.vijayanix.iot.util.BaseApiUtil;
import com.vijayanix.iot.util.RandomUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

import static com.vijayanix.iot.mqtt.MqttConstant.APP_CLIENT_TITLE_PRIFIX;

public class Onenet{


	private static class OnenetUrl{
		public static final String SCHEME = "http";
		public static String sHost = "api.heclouds.com";
	}


	public static String getPassword(){
		return IOTApplication.getAppPreferences().getAuthInfo();
	}


    public static String  getAndroidClientId() {
	    String clientId =  IOTApplication.getAppPreferences().getClientId();
	    if (clientId != null){
		    Log.d("Onenet","clientId =" +clientId);
		    return clientId;
	    }
	    return createMqttClient();
    }


    private static String createMqttClient(){
	    JSONObject requestContent = new JSONObject();
	    try {
		    String title = APP_CLIENT_TITLE_PRIFIX +  RandomUtil.randomString(5);
		    requestContent.putOpt("title", title);
		    requestContent.putOpt("protocol", "MQTT");
		    requestContent.putOpt("private", true);

		    final String authInfo = title;
		    requestContent.putOpt("auth_info", title);

		    JSONObject resp = BaseApiUtil.Post(urlForAdding(), requestContent, getHearPair());

		    int errno = resp.getInt("errno");

		    Log.d("Onenet","errno =" +errno);
		    if (0 == errno) {
			    ///Toast.makeText(getApplicationContext(), R.string.added_successfully, Toast.LENGTH_SHORT).show();
			    JSONObject data = resp.getJSONObject("data");
			    final String deviceId = data.getString("device_id");

			    BaseApiUtil.submit(new Runnable() {
				    @Override
				    public void run() {
					    IOTApplication.getAppPreferences().saveMqttClient(deviceId,authInfo);
				    }
			    });

			    Log.d("Onenet","deviceId =" +deviceId);
			    return deviceId;
		    }


	    } catch (JSONException e) {
		    e.printStackTrace();
	    }

	    return null;
    }


	public static List<IDevice> getDevices() {
		int mCurrentPage = 1;
		Map<String, String> urlParams = new HashMap<>();
		urlParams.put("page", String.valueOf(mCurrentPage));
		urlParams.put("per_page", "20");

		String url = urlForfuzzyQuerying(urlParams);


		JSONObject resp = BaseApiUtil.Get(url,getHearPair());

		try {
			int errno = resp.getInt("errno");
			if (0 == errno) {
				return parseData(resp.getJSONObject("data"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return Collections.emptyList();

	}





	private static List<IDevice> parseData(JSONObject data) {
		if (null == data) {
			return Collections.emptyList();
		}

		try {
			JSONArray jsonArray = null;

			jsonArray = data.getJSONArray("devices");
			List<IDevice> devices = new ArrayList<IDevice>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject device = jsonArray.getJSONObject(i);
				String title = device.getString("title");

				//ignore the android phones
				if (title.startsWith(APP_CLIENT_TITLE_PRIFIX)) {
					continue;
				}

				IDevice device1 = new Device();
				device1.setDeviceState(new DeviceState());
				device1.setKey(device.getString("id"));
				if (device.getBoolean("online")){
					device1.getDeviceState().addStateInternet();
				}
				devices.add(device1);
			}


			return devices;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return Collections.emptyList();

	}


	private static  HeaderPair[] getHearPair(){
		HeaderPair pairs[] = new HeaderPair[1];
		HeaderPair headerPair = new HeaderPair("api-key","XV=1gacFAZcUO1cRFHRFUTziAx0=");
		pairs[0] = headerPair;
		return pairs;
	}

	private static String urlForfuzzyQuerying(Map<String, String> params) {
		HttpUrl.Builder builder = new HttpUrl.Builder()
				.scheme(OnenetUrl.SCHEME).host(OnenetUrl.sHost).addPathSegment("devices");
		if (params != null) {
			Iterator iterator = params.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				builder.addEncodedQueryParameter(key, value);
			}
		}
		return builder.toString();
	}


	private static String urlForAdding() {
		return new HttpUrl.Builder().scheme(OnenetUrl.SCHEME).host(OnenetUrl.sHost).addPathSegment("devices")
				.toString();
	}

}
