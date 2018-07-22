package com.vijayanix.iot.common.net.proxy;

import java.util.List;

/**
 * class for Mesh request
 * 

 * 
 */
public class MeshRequest
{
    private final int mProto;
    
    private final String mTargetBssid;
    
    private final byte[] mOriginRequestBytes;
    
    private final List<String> mTargetBssidList;
    
    private MeshRequest(int proto, String targetBssid, byte[] originRequestBytes)
    {
        mProto = proto;
        mTargetBssid = targetBssid;
        mOriginRequestBytes = originRequestBytes;
        mTargetBssidList = null;
    }
    
    private MeshRequest(int proto, List<String> targetBssidList, byte[] originRequestBytes)
    {
        mProto = proto;
        mTargetBssid = null;
        mOriginRequestBytes = originRequestBytes;
        mTargetBssidList = targetBssidList;
    }
    
    static MeshRequest createInstance(int proto, String targetBssid, byte[] originRequestBytes)
    {
        return new MeshRequest(proto, targetBssid, originRequestBytes);
    }
    
    static MeshRequest createInstance(int proto, List<String> targetBssidList, byte[] originRequestBytes)
    {
        return new MeshRequest(proto, targetBssidList, originRequestBytes);
    }
    
    public byte[] getRequestBytes()
    {
        if (mTargetBssidList == null)
        {
            return MeshPackageUtil.addMeshRequestPackageHeader(mProto, mTargetBssid, mOriginRequestBytes);
        }
        else
        {
            return MeshPackageUtil.addMeshGroupRequestPackageHeader(mProto, mTargetBssidList, mOriginRequestBytes);
        }
    }
    
}
