package com.vijayanix.iot.model.device;

import java.util.List;

/**
 * Created by hxhoua on 2018/7/12.
 */

public interface IDeviceTopic {

	 List<String> getSubscribedTopics();

	void addSubscribedTopics(String topic);
}
