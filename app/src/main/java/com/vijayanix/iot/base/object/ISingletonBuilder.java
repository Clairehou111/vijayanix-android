package com.vijayanix.iot.base.object;

/**
 * all objects in com.vijayanix.iot used to build Singleton Object implement it
 * 

 * 
 */
public interface ISingletonBuilder
{
    /**
     * 
     * @return the Singleton instance
     */
    ISingletonObject getInstance();
}
