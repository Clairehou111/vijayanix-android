package com.vijayanix.iot.action.device.builder;

import com.vijayanix.iot.action.IAction;


public class BAction implements IBAction
{
    /*
     * Singleton lazy initialization start
     */
    private BAction()
    {
    }
    
    private static class InstanceHolder
    {
        static BAction instance = new BAction();
    }
    
    public static BAction getInstance()
    {
        return InstanceHolder.instance;
    }
    
    /*
     * Singleton lazy initialization end
     */
    
    @Override
    public IAction alloc(Class<?> clazz)
    {

        return null;
    }
    
}
