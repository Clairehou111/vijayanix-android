package com.vijayanix.iot.mqtt;


import android.content.Context;
import android.util.Log;

import com.vijayanix.iot.IOTApplication;
import com.vijayanix.iot.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * Represents a {@link MqttAndroidClient} and the actions it has performed
 *
 */
public class Connection {
    /**
     * Basic information about the client
     */


    /** The clientId of the client associated with this <code>Connection</code> object **/
    //private static String clientId = "35198981";


    /** The host that the {@link MqttAndroidClient} represented by this <code>Connection</code> is represented by **/
    private static String host = "183.230.40.39";

    /** The port on the server that this client is connecting to **/
    private static int port = 6002;

	private List<String> topics = new ArrayList<String>();


    /** The {@link MqttAndroidClient} instance this class represents **/
    private MqttAndroidClient client = null;

    /** The {@link MqttConnectOptions} that were used to connect this client **/
    private MqttConnectOptions mqttConnectOptions;

    /** True if this connection is secured using TLS **/
    private boolean tlsConnection = false;

	private static String clientId;
	private static final String USER_NAME = "142577";
	private static final boolean CLEAN_SESSION = false;
	private static final boolean AUTOMATIC_RECONNECT = true;
	public static final int KEEP_ALIVE_INTERVAL= 120;
	public static final int CONNECTION_TIMEOUT = 120;




	private final ArrayList<IReceivedMessageListener> receivedMessageListeners = new ArrayList<IReceivedMessageListener>();
	private Context context;





	public static Connection getInstance(){

		return ConnectionInstanceHolder.INSTANCE;
	}

	private static class ConnectionInstanceHolder{

		private static  Connection INSTANCE = new Connection();

	}


    /**
     * Creates a connection from persisted information in the database store, attempting
     * to create a {@link MqttAndroidClient} and the client handle.
     * @return a new instance of <code>Connection</code>
     */
    private Connection(){

	    this.context = IOTApplication.getContext();
	    clientId = Onenet.getAndroidClientId();

	    String password = Onenet.getPassword();

	    Log.d("Connection","clientId =" +clientId);

	    String uri;
        if(tlsConnection) {
            uri = "ssl://" + host + ":" + port;
        } else {
            uri = "tcp://" + host + ":" + port;
        }
	    this.client = new MqttAndroidClient(context, uri, clientId);

	    MqttConnectOptions connectOptions= new  MqttConnectOptions();
	    connectOptions.setUserName(USER_NAME);
	    connectOptions.setPassword(password.toCharArray());
	    connectOptions.setCleanSession(CLEAN_SESSION);
	    connectOptions.setAutomaticReconnect(AUTOMATIC_RECONNECT);
	    connectOptions.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);
	    connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
	    this.mqttConnectOptions = connectOptions;
	    this.client.setCallback(new MqttCallbackHandler(context));

    }


    /**
     * Add an action to the history of the client
     * @param action the history item to add
     */
    public void addAction(String action) {

        Object[] args = new String[1];
        DateFormat dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        args[0] = dateTimeFormatter.format(new Date());

        String timestamp = context.getString(R.string.timestamp, args);
//        history.add(action + timestamp);
//
//        notifyListeners(new PropertyChangeEvent(this, ActivityConstants.historyProperty, null, null));
    }


	public void addReceivedMessageListner(IReceivedMessageListener listener){
		receivedMessageListeners.add(listener);
	}

	public void messageArrived(String topic, MqttMessage message){
		ReceivedMessage msg = new ReceivedMessage(topic, message);
		for(IReceivedMessageListener listener : receivedMessageListeners){
			listener.onMessageReceived(msg);
		}


	}




    /**
     * Creates a connection object with the server information and the client
     * hand which is the reference used to pass the client around activities
     * @param clientId The Id of the client
     * @param host The server which the client is connecting to
     * @param port The port on the server which the client will attempt to connect to
     * @param context The application context
     * @param client The MqttAndroidClient which communicates with the service for this connection
     * @param tlsConnection true if the connection is secured by SSL
     */
    private Connection(String clientId, String host,
                       int port, Context context, MqttAndroidClient client, boolean tlsConnection,MqttConnectOptions connectOptions) {

        Connection.clientId = clientId;
        Connection.host = host;
	    Connection.port = port;
        this.context = context;
        this.client = client;
        this.tlsConnection = tlsConnection;
	    this.mqttConnectOptions = connectOptions;
        String sb = "Client: " +
                clientId +
                " created";
        //addAction(sb);
    }


    /**
     * Determines if the client is connected
     * @return is the client connected
     */
    public boolean isConnected() {
	    return getInstance().getClient().isConnected();
    }

    /**
     * A string representing the state of the client this connection
     * object represents
     *
     *
     * @return A string representing the state of the client
     */
  /*  @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(clientId);
        sb.append("\n ");

        switch (status) {

            case CONNECTED :
                sb.append(context.getString(R.string.connection_connected_to));
                break;
            case DISCONNECTED :
                sb.append(context.getString(R.string.connection_disconnected_from));
                break;
            case NONE :
                sb.append(context.getString(R.string.connection_unknown_status));
                break;
            case CONNECTING :
                sb.append(context.getString(R.string.connection_connecting_to));
                break;
            case DISCONNECTING :
                sb.append(context.getString(R.string.connection_disconnecting_from));
                break;
            case ERROR :
                sb.append(context.getString(R.string.connection_error_connecting_to));
        }
        sb.append(" ");
        sb.append(host);

        return sb.toString();
    }*/


    /**
     * Get the client Id for the client this object represents
     * @return the client id for the client this object represents
     */
    public static String getId() {
        return clientId;
    }

    /**
     * Get the host name of the server that this connection object is associated with
     * @return the host name of the server this connection object is associated with
     */
    public static String getHostName() {

        return host;
    }

    /**
     * Gets the client which communicates with the org.eclipse.paho.android.service service.
     * @return the client which communicates with the org.eclipse.paho.android.service service
     */
    public MqttAndroidClient getClient() {
        return client;
    }

    /**
     * Add the connectOptions used to connect the client to the server
     * @param connectOptions the connectOptions used to connect to the server
     */
    public void addConnectionOptions(MqttConnectOptions connectOptions) {
        mqttConnectOptions = connectOptions;

    }

    /**
     * Get the connectOptions used to connect this client to the server
     * @return The connectOptions used to connect the client to the server
     */
    public MqttConnectOptions getConnectionOptions()
    {
        return mqttConnectOptions;
    }


    /**
     * Gets the port that this connection connects to.
     * @return port that this connection connects to
     */
    public int getPort() {
        return port;
    }

    /**
     * Determines if the connection is secured using SSL, returning a C style integer value
     * @return 1 if SSL secured 0 if plain text
     */
    public int isSSL() {
        return tlsConnection ? 1 : 0;
    }


    public  void addTopic(String topic){
	    topics.add(topic);
    }

	public List<String> getTopics() {
		return topics;
	}
}
