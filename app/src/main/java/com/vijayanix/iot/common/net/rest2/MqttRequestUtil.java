package com.vijayanix.iot.common.net.rest2;

import com.vijayanix.iot.mqtt.Mqtt;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hxhoua on 2018/7/7.
 */

public class MqttRequestUtil {

	private final static Logger log = Logger.getLogger(HttpUtil.class);
/*
	private static final String get  = "{\"method\":\"GET\"}";

	String post = "{\"body\": {\"response\": {\"status\": 0}},\"method\":\"POST\"}";*/

	private static final String METHOD = "method";
	private static final String GET = "GET";
	private static final String POST = "POST";
	static final String BODY = "body";


	public static boolean Get(String topic)
	{
//		String logTag =
//				Thread.currentThread().toString() + "##" + HttpLogUtil.convertToCurl(true, null, url, headers);
//		log.debug(logTag);

		JSONObject request = new JSONObject();
		try {
			request.put(METHOD,GET);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return publish(topic,request);
	}

	public static boolean Post(String topic, JSONObject jsonObject)
	{
//		String logTag =
//				Thread.currentThread().toString() + "##" + HttpLogUtil.convertToCurl(true, null, url, headers);
//		log.debug(logTag);

		JSONObject request = new JSONObject();
		JSONObject response = jsonObject;
		try {
			request.put(BODY,response);
			request.put(METHOD,POST);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return publish(topic,request);
	}


	private static boolean publish(String topic,JSONObject jsonObject){

		log.debug("the json will publish is \n" + jsonObject.toString());
		return Mqtt.getInstance().publish(topic,jsonObject.toString());
	}







}
