package com.vijayanix.iot.mqtt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.vijayanix.iot.R;
import com.vijayanix.iot.util.Notify;

import org.apache.log4j.Logger;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import static com.vijayanix.iot.mqtt.MqttConstant.QOS0;
import static com.vijayanix.iot.mqtt.MqttConstant.TOPIC_DEVICE_WILDCARDS;

/**
 * This Class handles receiving information from the
 * {@link MqttAndroidClient} and updating the {@link Connection} associated with
 * the action
 */
public class ActionListener implements IMqttActionListener {

	private static final Logger log = Logger.getLogger(ActionListener.class);

	/**
     * Actions that can be performed Asynchronously <strong>and</strong> associated with a
     * {@link ActionListener} object
     */
    enum Action {
        /**
         * Connect Action
         **/
        CONNECT,
        /**
         * Disconnect Action
         **/
        DISCONNECT,
        /**
         * Subscribe Action
         **/
        SUBSCRIBE,
        /**
         * Publish Action
         **/
        PUBLISH
    }

    /**
     * The {@link Action} that is associated with this instance of
     * <code>ActionListener</code>
     **/
    private final Action action;
    /**
     * The arguments passed to be used for formatting strings
     **/
    private final String[] additionalArgs;

    private final Connection connection;

    /**
     * {@link Context} for performing various operations
     **/
    private final Context context;

    /**
     * Creates a generic action listener for actions performed form any activity
     *
     * @param context        The application context
     * @param action         The action that is being performed
     * @param connection     The connection
     * @param additionalArgs Used for as arguments for string formating
     */
    public ActionListener(Context context, Action action,
                          Connection connection, String... additionalArgs) {
        this.context = context;
        this.action = action;
        this.connection = connection;
        this.additionalArgs = additionalArgs;
    }

    /**
     * The action associated with this listener has been successful.
     *
     * @param asyncActionToken This argument is not used
     */
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        switch (action) {
            case CONNECT:
                connect();
                break;
            case DISCONNECT:
                disconnect();
                break;
            case SUBSCRIBE:
                subscribe();
                break;
            case PUBLISH:
                publish();
                break;
        }

    }

    /**
     * A publish action has been successfully completed, update connection
     * object associated with the client this action belongs to, then notify the
     * user of success
     */
    private void publish() {

        Connection c = Connection.getInstance();
        @SuppressLint("StringFormatMatches") String actionTaken = context.getString(R.string.toast_pub_success,
              (Object[]) additionalArgs);
        c.addAction(actionTaken);
        Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);
	    log.debug(actionTaken);

    }

    /**
     * A addNewSubscription action has been successfully completed, update the connection
     * object associated with the client this action belongs to and then notify
     * the user of success
     */
    private void subscribe() {
        Connection c = connection;
        String actionTaken = context.getString(R.string.toast_sub_success,
                (Object[]) additionalArgs);
        c.addAction(actionTaken);
        Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);
	    log.debug(actionTaken);


    }

    /**
     * A disconnection action has been successfully completed, update the
     * connection object associated with the client this action belongs to and
     * then notify the user of success.
     */
    private void disconnect() {
	    log.debug("ActionListerner disconnect onSuccess");
//        Connection c = Connection.getInstance();
//        c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
        String actionTaken = context.getString(R.string.toast_disconnected);
    //    c.addAction(actionTaken);
        Notify.toast(context, actionTaken, Toast.LENGTH_SHORT);
	    log.debug(actionTaken);

    }

    /**
     * A connection action has been successfully completed, update the
     * connection object associated with the client this action belongs to and
     * then notify the user of success.
     */
    private void connect() {


	    log.debug("ActionListerner connect onSuccess");

/*	    for(String topic:  c.getTopics()){
		    Mqtt.getInstance().subscribe(TOPIC_DEVICE_WILDCARDS,QOS1);
		    log.debug("ActionListerner auto subscribe topic:" + topic);
	    }*/
	    Mqtt.getInstance().subscribe(TOPIC_DEVICE_WILDCARDS,QOS0);
	    log.debug("ActionListerner auto subscribe topic:" + TOPIC_DEVICE_WILDCARDS);

    }

    /**
     * The action associated with the object was a failure
     *
     * @param token     This argument is not used
     * @param exception The exception which indicates why the action failed
     */
    @Override
    public void onFailure(IMqttToken token, Throwable exception) {
        switch (action) {
            case CONNECT:
                connect(exception);
                break;
            case DISCONNECT:
                disconnect(exception);
                break;
            case SUBSCRIBE:
                subscribe(exception);
                break;
            case PUBLISH:
                publish(exception);
                break;
        }

    }

    /**
     * A publish action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void publish(Throwable exception) {
        Connection c = connection;
        @SuppressLint("StringFormatMatches") String action = context.getString(R.string.toast_pub_failed,
                (Object[]) additionalArgs);
        c.addAction(action);
        Notify.toast(context, action, Toast.LENGTH_SHORT);
        System.out.print("Publish failed");

    }

    /**
     * A addNewSubscription action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void subscribe(Throwable exception) {
        Connection c = connection;
        String action = context.getString(R.string.toast_sub_failed,
                (Object[]) additionalArgs);
        c.addAction(action);
        Notify.toast(context, action, Toast.LENGTH_SHORT);
        System.out.print(action);

    }

    /**
     * A disconnect action was unsuccessful, notify user and update client history
     *
     * @param exception This argument is not used
     */
    private void disconnect(Throwable exception) {

	    log.debug("ActionListerner disconnect onFailure");

//	    Connection c = Connection.getInstance();
//        c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
//        c.addAction("Disconnect Failed - an error occured");
		log.error("Disconnect Failed - an error occured");
    }

    /**
     * A connect action was unsuccessful, notify the user and update client history
     *
     * @param exception This argument is not used
     */
    private void connect(Throwable exception) {

	    log.debug("ActionListerner connect onFailure");


	 /*   Connection c = Connection.getInstance();
        c.changeConnectionStatus(Connection.ConnectionStatus.ERROR);
        c.addAction("Client failed to connect");*/
        System.out.println("Client failed to connect" );
	    log.error(exception.getMessage());

    }

}