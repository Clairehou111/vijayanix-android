package com.vijayanix.iot.mqtt;


public interface IReceivedMessageListener {

    void onMessageReceived(ReceivedMessage message);
}