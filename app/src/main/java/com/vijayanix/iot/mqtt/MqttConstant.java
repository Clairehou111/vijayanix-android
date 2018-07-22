package com.vijayanix.iot.mqtt;

import android.text.TextUtils;

import org.eclipse.paho.client.mqttv3.MqttTopic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vijayanix.iot.mqtt.SubTopic.ALL;

/**
 * Created by hxhoua on 2018/7/7.
 */

public class MqttConstant {
	public static final int QOS0 = 0;
	public static final int QOS1 = 1;
	public static final int QOS2 = 2;

	public static final String TOPIC_APP = "app/device/";
	public static final String TOPIC_DEVICE = "device/device/";
	public static final String TOPIC_DEVICE_WILDCARDS = "device/device/#";
	public static final boolean retain = false;
	public static final String APP_CLIENT_TITLE_PRIFIX = "ANDROID";
	public static final String TOPIC_LEVEL_SEPARATOR = "/";

	/**
	 * Multi-level wildcard The number sign (#) is a wildcard character that
	 * matches any number of levels within a topic.
	 */
	public static final String MULTI_LEVEL_WILDCARD = "#";

	/**
	 * Single-level wildcard The plus sign (+) is a wildcard character that
	 * matches only one topic level.
	 */
	public static final String SINGLE_LEVEL_WILDCARD = "+";

	private static final String DEVICE_TOPIC_REGIX = "(device)(\\/)(device)(\\/)(\\d+)(\\/*)(\\d*)";

    public static String getTopicDevice(String deviceKey){
	    return new StringBuilder().append(TOPIC_DEVICE).append(deviceKey).toString();
    }

    public static String getTopicApp(String deviceKey){
	    return new StringBuilder().append(TOPIC_APP).append(deviceKey).toString();
    }

    public static String getTopicDeviceWildcards(String deviceKey){
	    return new StringBuilder().append(TOPIC_DEVICE).append(deviceKey).append(TOPIC_LEVEL_SEPARATOR).append(SINGLE_LEVEL_WILDCARD).toString();
    }

	public static String getTopicDeviceType(String deviceKey){
		return new StringBuilder().append(TOPIC_DEVICE).append(deviceKey).append(TOPIC_LEVEL_SEPARATOR).append(SubTopic.DEVICE_TYPE).toString();

	}
	public static String getAppSubTopic(String deviceKey,int subTopic){
		return new StringBuilder().append(TOPIC_APP).append(deviceKey).append(TOPIC_LEVEL_SEPARATOR).append(subTopic).toString();

	}
	public static String getDeviceSubTopic(String deviceKey,int subTopic){
		return new StringBuilder().append(TOPIC_DEVICE).append(deviceKey).append(TOPIC_LEVEL_SEPARATOR).append(subTopic).toString();

	}

    public static boolean isMatched(String topicFilter, String topicName){
	    return MqttTopic.isMatched(topicFilter, topicName);
    }


	public static boolean isWill(String topic){
		return SubTopic.WILL == getSubTopic(topic);
	}


	public static  String getDeviceKey(String topic){
		Pattern p = Pattern.compile(DEVICE_TOPIC_REGIX,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(topic);
		if (m.find())
		{
			return m.group(5);
		}

		return null;
	}

	public static  int getSubTopic(String topic){
		Pattern p = Pattern.compile(DEVICE_TOPIC_REGIX,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(topic);
		if (m.find())
		{
			return TextUtils.isEmpty(m.group(7))?ALL:Integer.valueOf(m.group(7));
		}

		return ALL;
	}



}



