/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package com.vijayanix.iot.mqtt;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.vijayanix.iot.R;
import com.vijayanix.iot.util.Notify;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

//import org.eclipse.paho.android.sample.Connection.ConnectionStatus;

/**
 * Handles call backs from the MQTT Client
 *
 */
public class MqttCallbackHandler implements MqttCallbackExtended,MqttCallback {
	private static final Logger log = Logger.getLogger(MqttCallbackHandler.class);


	/** {@link Context} for the application used to format and import external strings**/
  private final Context context;

  private static final String TAG = "MqttCallbackHandler";

  /**
   * Creates an <code>MqttCallbackHandler</code> object
   * @param context The application's context
   */
  public MqttCallbackHandler(Context context)
  {
    this.context = context;
  }



	@Override
	public void connectComplete(boolean reconnect, String serverURI) {

		Log.d(TAG, "connectComplete: reconnect = " + reconnect);
	}


  /**
   * @see MqttCallback#connectionLost(java.lang.Throwable)
   */
  @Override
  public void connectionLost(Throwable cause) {
    if (cause != null) {
      Log.d(TAG, "Connection Lost: " + cause.getMessage());
      Connection c = Connection.getInstance();
      c.addAction("Connection Lost");
	    if (!c.isConnected()){
		    String message = context.getString(R.string.connection_lost, Connection.getId(), Connection.getHostName());
		    Notify.toast(context, message, Toast.LENGTH_LONG);
	    }

    }
  }




  /**
   * @see MqttCallback#messageArrived(java.lang.String, MqttMessage)
   */
  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {

	  EventBus.getDefault().post(new ReceivedMessage(topic,message));
    //get the string from strings.xml and format
    String messageString = context.getString(R.string.messageRecieved, new String(message.getPayload()), topic+";qos:"+message.getQos()+";retained:"+message.isRetained());

    Log.i(TAG, Thread.currentThread()+ messageString);

   // Notify.toast(context, messageString, Toast.LENGTH_SHORT);


  }

  /**
   * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
   */
  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
    // Do nothing
    Notify.toast(context, "has been delivered", Toast.LENGTH_SHORT);
  }


}
