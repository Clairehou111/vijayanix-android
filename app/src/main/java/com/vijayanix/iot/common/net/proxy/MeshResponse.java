package com.vijayanix.iot.common.net.proxy;

public class MeshResponse
{
    private int mPackageLength;
    
    private int mOptionLength;
    
    private String mTargetBssid;
    
    private int mProto;
    
    private boolean mIsDeviceAvailable;
    
    private MeshOption mMeshOption;
    
    private byte[] mResponseBytes;
    
    private MeshResponse(byte[] first4bytes)
    {
        mPackageLength = MeshPackageUtil.getResponsePackageLength(first4bytes);
    }
    
    static MeshResponse createInstance(byte[] first4bytes)
    {
        return new MeshResponse(first4bytes);
    }
    
    public boolean fillInAll(byte[] responseBytes)
    {
        try
        {
            mResponseBytes = responseBytes;
            mIsDeviceAvailable = MeshPackageUtil.isDeviceAvailable(mResponseBytes);
            mOptionLength = MeshPackageUtil.getResponseOptionLength(responseBytes);
            if (mOptionLength > 0)
            {
                mMeshOption = MeshOption.createInstance(responseBytes, mPackageLength, mOptionLength);
            }
            else
            {
                mMeshOption = null;
            }
            mProto = MeshPackageUtil.getResponseProto(responseBytes);
            mTargetBssid = MeshPackageUtil.getDeviceBssid(responseBytes);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getPackageLength()
    {
        return mPackageLength;
    }
    
    public int getOptionLength()
    {
        return mOptionLength;
    }
    
    public int getProto()
    {
        return mProto;
    }
    
    public String getTargetBssid()
    {
        return mTargetBssid;
    }
    
    public boolean hasMeshOption()
    {
        return mMeshOption != null;
    }
    
    public MeshOption getMeshOption()
    {
        return mMeshOption;
    }
    
    public boolean isBodyEmpty()
    {
        return mPackageLength - mOptionLength == MeshPackageUtil.M_HEADER_LEN;
    }
    
    public boolean isDeviceAvailable()
    {
        return mIsDeviceAvailable;
    }
    
    public byte[] getPureResponseBytes()
    {
        int pureResponseOffset = MeshPackageUtil.M_HEADER_LEN + mOptionLength;
        int pureResponseCount = mPackageLength - mOptionLength - MeshPackageUtil.M_HEADER_LEN;
        byte[] pureResponseBytes = new byte[pureResponseCount];
        for (int i = 0; i < pureResponseCount; ++i)
        {
            pureResponseBytes[i] = mResponseBytes[pureResponseOffset + i];
        }
        return pureResponseBytes;
    }
    
    @Override
    public String toString()
    {
        return "[MeshResponse mPackageLength = " + mPackageLength + " | " + "mOptionLength = " + mOptionLength
            + " | " + "mOptionLength = " + mOptionLength + " | " + "mTargetBssid = " + mTargetBssid + " | "
            + "mProto = " + mProto + " | " + "hasMeshOption = " + hasMeshOption() + " | " + "isBodyEmpty = "
            + isBodyEmpty() + " | " + "mIsDeviceAvailable = " + mIsDeviceAvailable + "]";
    }
}
