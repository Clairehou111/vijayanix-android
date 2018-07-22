package com.vijayanix.iot.command;

/**
 * ICommandInternet indicate that the action is related to internet
 * 

 * 
 */
public interface ICommandInternet extends ICommand
{
    String Time_Zone = "Time-Zone";
    
    String Epoch = "Epoch";
    
    String Bssid = "bssid";
    
    String Rom_Version = "rom_version";
    
    String Latest_Rom_Version = "latest_rom_version";
    
    String Email = "email";
    
    String Password = "password";
    
    String IsOwnerKey = "is_owner_key";
    
    String Ptype = "ptype";
    
    String Id = "id";
    
    String Key = "key";
    
    String Keys = "keys";
    
    // static final String Metadata = "metadata";
    String Device = "device";
    
    String Datapoint = "datapoint";
    
    String Datapoints = "datapoints";
    
    String X = "x";
    
    String Y = "y";
    
    String Z = "z";
    
    String K = "k";
    
    String L = "l";
    
    String Payload = "payload";
    
    String Info = "info";
    
    String Rssi = "rssi";
    
    String Name = "name";
    
    String Scope = "scope";
    
    String Remember = "remember";
    
    String Parent_Mdev_Mac = "parent_mdev_mac";
    
    String METHOD_PUT = "method=PUT";
    
    String METHOD_DELETE = "method=DELETE";


}
