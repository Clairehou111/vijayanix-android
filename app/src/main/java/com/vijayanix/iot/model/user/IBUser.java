package com.vijayanix.iot.model.user;


import com.vijayanix.iot.base.object.ISingletonBuilder;

public interface IBUser extends ISingletonBuilder
{
    /**
     * load the IUser from local db
     * 
     * @return the user from db
     */
    IUser loadUser();
}
