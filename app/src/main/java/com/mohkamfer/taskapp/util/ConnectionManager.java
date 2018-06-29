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
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class ConnectionManager {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleConnectivity(intent);
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

        context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        snackbar = Snackbar.make(parentView, "Connecting...", Snackbar.LENGTH_INDEFINITE);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, context.getString(R.string.cloudURI), clientId);

        connectClient();
    }

    private void handleConnectivity(Intent intent) {
        if (intent != null) {
            ConnectivityManager manager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                NetworkInfo info = manager.getActiveNetworkInfo();
                if (info != null) {
                    System.out.println("Received something!");
                    if (info.isConnected())
                        connected();
                    else
                        disconnected();
                }
            } else
                System.out.println("Freaking null...");
        }
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
                        System.out.println("Clicked!");
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
                    System.out.println("Disconnected");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    System.out.println("Disconnection failed xDDD");
                }
            });

            client.unregisterResources();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
