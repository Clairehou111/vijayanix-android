package com.vijayanix.iot.command;

/**
 * ICommand is the foundation of on @see IAction. ICommand only has simple logic IUser and IDevice will
 * do IAction instead of ICommand
 * 

 * 
 */
public interface ICommand {
    String Authorization = "Authorization";
    
    String Token = "token";
    
    String Status = "status";
}
