package com.vijayanix.iot.model.http;


public class HeaderPair
{
    private String mName;
    
    private String mValue;
    
    public HeaderPair(String name, String value)
    {
        mName = name;
        mValue = value;
    }
    
    public String getName()
    {
        return mName;
    }
    
    public String getValue()
    {
        return mValue;
    }
    
    @Override
    public String toString()
    {
        return "[name=" + mName + ", value=" + mValue + "]";
    }
    
}
