package com.vijayanix.iot.ui.main.section;

import android.content.Context;

import com.vijayanix.iot.R;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.IDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hxhoua on 2017/11/9.
 */

public class DeviceUtil {

	public static final String NEW_FOUND = "NEW";
	private static final String TAG = "DeviceUtil";

	//页面加载的设备列表
	public static List<DeviceSection> getDeviceSections(Context context, List<IDevice> deviceList, EnumMap<DeviceType, Map<String,Integer>> postions) {

		if (deviceList == null || deviceList.isEmpty()) {
			return Collections.emptyList();
		}

		EnumMap<DeviceType, List<IDevice>> map = new EnumMap(DeviceType.class);

		EnumMap<DeviceType, Map<String,Integer>> tempPos = groupByProductKey(deviceList, map);

		if (postions == null){
			throw new RuntimeException("postions should not be null");
		}

		postions.putAll(tempPos);

		Iterator<Map.Entry<DeviceType, List<IDevice>>> iterator = map.entrySet().iterator();
		List<DeviceSection> sectionList = new ArrayList<>();
		while (iterator.hasNext()) {
			Map.Entry<DeviceType, List<IDevice>> entry = iterator.next();
			DeviceType key = entry.getKey();
			List<IDevice> deviceList1 = entry.getValue();
			DeviceSection section = newDeviceSectionInstance(key, deviceList1, context);
			sectionList.add(section);
		}

/*
		sectionList.add()
*/
		return sectionList;
	}


	public static EnumMap<DeviceType, Map<String,Integer>> groupByProductKey(List<IDevice> list, EnumMap<DeviceType, List<IDevice>> map) {
		if (null == list || null == map) {
			return null;
		}

		// 按name开始分组
		DeviceType key;
		List<IDevice> listTmp;
		Map<String,Integer> posMap = null;
		int position = 0;

		EnumMap<DeviceType, Map<String,Integer>> postions = new EnumMap<DeviceType, Map<String,Integer>>(DeviceType.class);
		for (IDevice val : list) {
			key = val.getDeviceType() ;
			listTmp = map.get(key);
			if (null == listTmp) {
				listTmp = new ArrayList<IDevice>();
				map.put(key, listTmp);

				posMap = new TreeMap<String,Integer>();
				position = 0;
				postions.put(key,posMap);
			}

			listTmp.add(val);
			 posMap.put(val.getBssid(),Integer.valueOf(position++));

		}

		return postions;
	}

	public static DeviceSection newDeviceSectionInstance(DeviceType type, List<IDevice> deviceList1, Context context){
		String key = type.toString();
		switch (type){
			case  NEW:
				return new NewFoundDeviceSection(key,deviceList1,context);

			case PLUG:
				return new PlugSection(key,deviceList1,context);
			case WATCHMAN:
				return new WatchmanSection(key,deviceList1,context);
			default:
				return null;
		}


	}



	/**
	 * 使用 Map按key进行排序
	 *
	 * @param map
	 * @return
	 */
	public static Map<DeviceType, List<IDevice>> sortMapByKey(Map<DeviceType, List<IDevice>> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<DeviceType, List<IDevice>> sortMap = new TreeMap<DeviceType, List<IDevice>>(
				new Comparator<DeviceType>() {
					@Override
					public int compare(DeviceType o1, DeviceType o2) {

						return o1.compareTo(o2);
					}
				});
		sortMap.putAll(map);
		return sortMap;
	}


	public static int getDeviceImg(DeviceType deviceType){
		switch (deviceType){

			case PLUG:
				return R.drawable.app_aapay;

		}
		return R.drawable.app_aapay;
	}


	public static boolean isOnline(IDevice device){
		return device.getDeviceState().isStateInternet()||device.getDeviceState().isStateLocal();
	}

}
