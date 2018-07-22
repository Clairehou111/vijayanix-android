package com.vijayanix.iot.action;


import com.vijayanix.iot.model.user.IUser;

public interface IActionUserLoginDB extends IActionUser, IActionDB
{
    /**
     * auto login according to local db
     * 
     * @return @see IUser
     */
    IUser doActionUserLoginDB();
}
