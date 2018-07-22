package com.vijayanix.iot;


import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by hxhoua on 2018/7/10.
 */

@RunWith(AndroidJUnit4.class)
public class JsonTest {

	@Test
	public void parse(){
		String payload = "{\n" +
				"                                                                        \t\"response\":\t{\n" +
				"                                                                        \t\t\"bssid\":\t\"5c:cf:7f:f2:29:77\",\n" +
				"                                                                        \t\t\"deviceType\":\t23701,\n" +
				"                                                                        \t\t\"clientId\":\t\"31589734\"\n" +
				"                                                                        \t}\n" +
				"                                                                        }";


		JSONObject responseJson = null;
		String bssid = null;
		int deviceType = -1;
		String deviceKey = null;

		try {
			JSONObject jo = new JSONObject(payload);
			if (!jo.isNull("response")) {
				responseJson = jo.getJSONObject("response");
			} else {
				responseJson = jo.getJSONObject("response");
			}

			bssid = responseJson.getString("bssid");
			deviceType  = responseJson.getInt("deviceType");
			deviceKey  = responseJson.getString("clientId");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.out.println(bssid);

		/*"{\"response\":{\"bssid\":\"5c:cf:7f:f2:27:94\",\"deviceType\":23703,\"clientId\":\"35838489\",\"lightA\":0,\"lightB\":0}}"*/
	
	}



}
