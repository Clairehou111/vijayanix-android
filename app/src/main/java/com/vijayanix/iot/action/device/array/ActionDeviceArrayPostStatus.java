package com.vijayanix.iot.action.device.array;

import com.vijayanix.iot.action.device.common.ActionDevicePostStatusInternet;
import com.vijayanix.iot.action.device.common.ActionDevicePostStatusLocal;
import com.vijayanix.iot.action.device.common.IActionDevicePostStatusInternet;
import com.vijayanix.iot.action.device.common.IActionDevicePostStatusLocal;
import com.vijayanix.iot.command.device.IDeviceStatus;
import com.vijayanix.iot.command.device.plug.CommandPlugPostStatusInternet;
import com.vijayanix.iot.command.device.plug.CommandPlugPostStatusLocal;
import com.vijayanix.iot.command.device.plug.ICommandPlugPostStatusInternet;
import com.vijayanix.iot.command.device.plug.ICommandPlugPostStatusLocal;
import com.vijayanix.iot.model.device.DeviceState;
import com.vijayanix.iot.model.device.DeviceType;
import com.vijayanix.iot.model.device.IDevice;
import com.vijayanix.iot.model.device.IDeviceState;
import com.vijayanix.iot.model.device.array.IDeviceArray;
import com.vijayanix.iot.model.device.plug.IStatusPlug;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ActionDeviceArrayPostStatus implements IActionDeviceArrayPostStatus
{
    private class NetworkGroup
    {
        String rootDeviceKey;
        
        IDeviceState state;
        
        StringBuilder bssids;
        
        InetAddress inetAddress;
        
        List<String> postBssidList;
        
        public NetworkGroup()
        {
            state = new DeviceState();
            bssids = new StringBuilder();
            postBssidList = new ArrayList<String>();
        }
    }
    
    @Override
    public void doActionDeviceArrayPostStatus(IDeviceArray deviceArray, IDeviceStatus status)
    {
        List<IDevice> devices = deviceArray.getDeviceList();
        List<IDevice> nonMeshDevices = new ArrayList<IDevice>();
        List<IDevice> meshDevices = new ArrayList<IDevice>();
        for (IDevice deviceInArray : devices)
        {
            if (deviceInArray.getIsMeshDevice())
            {
                meshDevices.add(deviceInArray);
            }
            else
            {
                nonMeshDevices.add(deviceInArray);
            }
        }
        
        processNonMeshDevices(nonMeshDevices, status);
        
        processMeshDevices(meshDevices, status);
    }
    
    /**
     * Post status one by one
     * 
     * @param nonMeshDevices
     * @param status
     */
    private void processNonMeshDevices(List<IDevice> nonMeshDevices, IDeviceStatus status)
    {
        for (IDevice nonMeshDevice : nonMeshDevices)
        {
            if (nonMeshDevice.getDeviceState().isStateLocal())
            {
                IActionDevicePostStatusLocal actionLocal = new ActionDevicePostStatusLocal();
                actionLocal.doActionDevicePostStatusLocal(nonMeshDevice, status);
            }
            else
            {
                IActionDevicePostStatusInternet actionInternet = new ActionDevicePostStatusInternet();
                actionInternet.doActionDevicePostStatusInternet(nonMeshDevice, status);
            }
            
        }
    }
    
    /**
     * Post status by Multicast
     * 
     * @param meshDevices
     * @param status
     */
    private void processMeshDevices(List<IDevice> meshDevices, IDeviceStatus status)
    {
        if (meshDevices.size() > 0)
        {
            List<NetworkGroup> deviceNetworkGroups = new ArrayList<NetworkGroup>();
            groupingNetwork(deviceNetworkGroups);
            
            for (IDevice meshDevice : meshDevices)
            {
                for (NetworkGroup netGroup : deviceNetworkGroups)
                {
                    if (netGroup.bssids.toString().contains(meshDevice.getBssid()))
                    {
                        netGroup.postBssidList.add(meshDevice.getBssid());
                        break;
                    }
                }
            }
            for (NetworkGroup netGroup : deviceNetworkGroups)
            {
                if (netGroup.postBssidList.size() > 0)
                {
                    if (netGroup.state.isStateLocal())
                    {
                        postMulticastLocal(meshDevices.get(0).getDeviceType(), netGroup, status);
                    }
                    else
                    {
                        postMulticastInternet(meshDevices.get(0).getDeviceType(), netGroup, status);
                    }
                }
            }
        }
    }
    
    /**
     * Grouping user devices by network environment
     * 
     * @param networkGroups
     */
    private void groupingNetwork(List<NetworkGroup> networkGroups)
    {
        //List<IDevice> userDevices = BUser.getBuilder().getInstance().getAllDeviceList();

        List<IDevice> userDevices = new ArrayList<>();
        
        // Root devices
        for (int i = 0; i < userDevices.size(); i++)
        {
            IDevice device = userDevices.get(i);
            String rootBssid = device.getRootDeviceBssid();
            String bssid = device.getBssid();
            if (rootBssid.equals(bssid))
            {
                NetworkGroup networkGroup = new NetworkGroup();
                networkGroup.rootDeviceKey = device.getKey();
                networkGroup.bssids.append(bssid).append(',');
                addDeviceState(networkGroup, device);
                networkGroups.add(networkGroup);
                userDevices.remove(i--);
                continue;
            }
        }
        
        // Devices under Root device
        for (int i = 0; i < userDevices.size(); i++)
        {
            IDevice device = userDevices.get(i);
            String rootBssid = device.getRootDeviceBssid();
            String bssid = device.getBssid();
            if (!rootBssid.equals(bssid))
            {
                for (NetworkGroup ng : networkGroups)
                {
                    if (ng.bssids.toString().contains(rootBssid))
                    {
                        ng.bssids.append(bssid).append(',');
                        addDeviceState(ng, device);
                        break;
                    }
                }
            }
        }
        
        // Uncontrollable devices
        for (int i = 0; i < userDevices.size(); i++)
        {
            IDeviceState state = userDevices.get(i).getDeviceState();
            if (!state.isStateLocal() && !state.isStateInternet())
            {
                userDevices.remove(i--);
            }
        }
        
        // Devices under other user's root device
        for (int i = 0; i < userDevices.size(); i++)
        {
            IDevice device = userDevices.get(i);
            String bssid = device.getBssid();
            NetworkGroup networkGroup = new NetworkGroup();
            networkGroup.rootDeviceKey = device.getKey();
            networkGroup.bssids.append(bssid).append(',');
            addDeviceState(networkGroup, device);
            networkGroups.add(networkGroup);
        }
    }
    
    private void addDeviceState(NetworkGroup group, IDevice device)
    {
        if (device.getDeviceState().isStateLocal())
        {
            group.state.addStateLocal();
            group.inetAddress = device.getInetAddress();
        }
        if (device.getDeviceState().isStateInternet())
        {
            group.state.addStateInternet();
        }
    }
    
    private boolean postMulticastLocal(DeviceType deviceType, NetworkGroup netGroup, IDeviceStatus status)
    {
        switch (deviceType)
        {
            case PLUG:
                ICommandPlugPostStatusLocal plugCmd = new CommandPlugPostStatusLocal();
                return plugCmd.doCommandMulticastPostStatusLocal(netGroup.inetAddress,
                    (IStatusPlug)status,
                    netGroup.postBssidList);

            case PLUGS:
                break;

        }
        
        return false;
    }
    
    private boolean postMulticastInternet(DeviceType deviceType, NetworkGroup netGroup, IDeviceStatus status)
    {
        switch (deviceType)
        {
            case PLUG:
                ICommandPlugPostStatusInternet plugCmd = new CommandPlugPostStatusInternet();
                return plugCmd.doCommandMulticastPostStatusInternet(netGroup.rootDeviceKey,
                    (IStatusPlug)status,
                    netGroup.postBssidList);

            case PLUGS:
                break;

        }
        
        return false;
    }
}
