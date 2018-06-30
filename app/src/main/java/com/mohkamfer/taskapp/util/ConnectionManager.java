package com.mohkamfer.taskapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.mohkamfer.taskapp.MainActivity;
import com.mohkamfer.taskapp.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class ConnectionManager {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleConnectivity();
        }
    };

    private Context context;
    private boolean connected = false;
    private Snackbar snackbar;
    private View parentView;
    private MqttAndroidClient client;

    private long last = System.currentTimeMillis();

    public ConnectionManager(Context context) {
        this.context = context;
        parentView = ((MainActivity) context).findViewById(R.id.main_content);

        context.registerReceiver(receiver,
                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        snackbar = Snackbar.make(parentView, "Connecting...", Snackbar.LENGTH_INDEFINITE);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, context.getString(R.string.cloudURI), clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                disconnected();
            }

            @SuppressWarnings("RedundantThrows")
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (System.currentTimeMillis() - last > 200) {
                    if (topic.equals("xiot/listen")) {
                        appendReceive(message.toString());
                    }
                }
                last = System.currentTimeMillis();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                ((MainActivity) ConnectionManager.this.context).updateSendStatus();
            }
        });

        handleConnectivity();
    }

    public boolean online() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void handleConnectivity() {
        if (online())
            connectClient();
        else
            disconnected();
    }

    private void connecting() {
        if (snackbar == null)
            return;

        connected = false;
        snackbar = Snackbar.make(parentView, "Connecting...", Snackbar.LENGTH_INDEFINITE);
        showSnackbar();
    }

    private void connected() {
        if (snackbar == null || connected)
            return;

        connected = true;
        snackbar = Snackbar.make(parentView, "Successfully connected!", Snackbar.LENGTH_LONG);
        showSnackbar();
    }

    private void disconnected() {
        if (snackbar == null)
            return;

        connected = false;
        snackbar = Snackbar.make(parentView, "Disconnected!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Try Again", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleConnectivity();
                    }
                });
        showSnackbar();
    }

    private void showSnackbar() {
        if (!snackbar.isShown())
            snackbar.show();
    }

    public void onDestroy() {
        context.unregisterReceiver(receiver);

        if (connected)
            disconnectClient();
    }

    public void connectClient() {
        if (context == null)
            return;

        connecting();

        try {
            MqttConnectOptions options = new MqttConnectOptions();

            options.setUserName(context.getString(R.string.username));
            options.setPassword(context.getString(R.string.password).toCharArray());
            options.setCleanSession(true);

            IMqttToken token = client.connect(options);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    connected();
                    subscribe("xiot/listen");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    disconnected();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectClient() {
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("MQTT Client Disconnected");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                }
            });

            if (connected) {
                client.unregisterResources();
                client.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String payload) {
        byte[] encodedPayload;
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendReceive(String message) {
        ((MainActivity) context).appendToBody(message);
    }

    public void subscribe(String topic) {
        int qos = 1;
        try {
            client.subscribe(topic, qos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
