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

    public ConnectionManager(Context context) {
        this.context = context;
        parentView = ((MainActivity) context).findViewById(R.id.main_content);

        context.registerReceiver(receiver,
                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        snackbar = Snackbar.make(parentView, "Connecting...", Snackbar.LENGTH_INDEFINITE);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, context.getString(R.string.cloudURI), clientId);

        handleConnectivity();
    }

    synchronized public boolean online() {
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
            IMqttDeliveryToken deliveryToken = client.publish(topic, message);
            deliveryToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    ((MainActivity) context).updateSendStatus();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
