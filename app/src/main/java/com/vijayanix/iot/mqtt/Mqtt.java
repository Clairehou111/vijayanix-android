package com.vijayanix.iot.mqtt;


import android.content.Context;

import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.util.BaseApiUtil;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import static com.vijayanix.iot.mqtt.MqttConstant.QOS0;
import static com.vijayanix.iot.mqtt.MqttConstant.retain;


public class Mqtt {

	private static final Logger log = Logger.getLogger(Mqtt.class);


	private  Connection connection;
	private  Context context;

	private static Mqtt Instance =   new Mqtt();

	public static Mqtt getInstance(){
		return Instance ;
	}


/*	private static class MqttInstanceHolder{

		private static final Mqtt INSTANCE = new Mqtt();

	}*/

	private Mqtt(){
		this.connection = Connection.getInstance();
		this.context = IOTApplication.getContext();
	}


	public  void connect() {

		if(!BaseApiUtil.isNetworkAvailable()){
			log.error("The network is not reachable. Will not do connect");
			return;
		}

        String[] actionArgs = new String[1];
        actionArgs[0] = Connection.getId();
        final ActionListener callback = new ActionListener(context,
                ActionListener.Action.CONNECT, connection, actionArgs);
       // this.connection.getClient().setCallback(new MqttCallbackHandler(context));

		try {
			this. connection.getClient().connect(connection.getConnectionOptions(), null, callback);
		}
		catch (MqttException e) {
			log.error("Exception occurred during connect: " + e.getMessage());
		}


    }

    public void disconnect(){

	    if(!connection.isConnected()){
		    log.error("The network is not reachable. Will not do disconnect");
		    return;
	    }

	    try {
		    connection.getClient().disconnect();
	    } catch( MqttException ex){
		    log.error("Exception occurred during disconnect: " + ex.getMessage());
	    }

    }



    public boolean publish(String topic, String message){

	    if(!connection.isConnected()){
		    log.error("The network is not reachable. Will not do publish");
		    return false;
	    }

        try {
            String[] actionArgs = new String[2];
            actionArgs[0] = message;
            actionArgs[1] = topic;
            final ActionListener callback = new ActionListener(context,
                    ActionListener.Action.PUBLISH, connection, actionArgs);
            connection.getClient().publish(topic, message.getBytes(), QOS0, retain, null, callback);

	        return true;

        } catch( MqttException ex){
            log.error("Exception occurred during publish: " + ex.getMessage());
	        return false;
        }
    }

    public void subscribe(String topic,  int qos){

	    if(!connection.isConnected()){
		    log.error("The network is not reachable. Will not do subscribe");
		    return;
	    }

	    try{
		    String[] actionArgs = new String[1];
		    actionArgs[0] = topic;
		    final ActionListener callback = new ActionListener(context,
				    ActionListener.Action.SUBSCRIBE, connection, actionArgs);
		    connection.getClient().subscribe(topic, qos, null, callback);
	    } catch (MqttException e) {
		    log.error("Exception occurred during subscribe: " + e.getMessage());
	    }


    }



}
