package com.example.iot_app;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTClient {
//    private MqttAndroidClient mqttAndroidClient;
//
//    public MQTTClient(Context context, String serverUri, String clientId) {
//        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
//    }
//
//    public void connect(String username, String password, Runnable onSuccess, Runnable onFailure) {
//        MqttConnectOptions options = new MqttConnectOptions();
//        options.setUserName(username);
//        options.setPassword(password.toCharArray());
//        options.setAutomaticReconnect(true);
//        options.setCleanSession(true);
//
//        mqttAndroidClient.connect(options, null, new IMqttActionListener() {
//            @Override
//            public void onSuccess(IMqttToken asyncActionToken) {
//                onSuccess.run();
//            }
//
//            @Override
//            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                onFailure.run();
//            }
//        });
//    }
//
//    public void subscribe(String topic, int qos, MessageCallback callback) {
//        mqttAndroidClient.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//                System.out.println("Connection lost: " + cause.getMessage());
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) {
//                callback.onMessageReceived(topic, message.toString());
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//                // Not needed for subscribing
//            }
//        });
//
//        mqttAndroidClient.subscribe(topic, qos, null, new IMqttActionListener() {
//            @Override
//            public void onSuccess(IMqttToken asyncActionToken) {
//                System.out.println("Subscribed to topic: " + topic);
//            }
//
//            @Override
//            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                System.out.println("Failed to subscribe: " + exception.getMessage());
//            }
//        });
//    }
//
//    public void publish(String topic, String payload, int qos, boolean retained) {
//        MqttMessage message = new MqttMessage();
//        message.setPayload(payload.getBytes());
//        message.setQos(qos);
//        message.setRetained(retained);
//
//        try {
//            mqttAndroidClient.publish(topic, message);
//            System.out.println("Message published to topic: " + topic);
//        } catch (Exception e) {
//            System.out.println("Failed to publish: " + e.getMessage());
//        }
//    }
//
//    public void disconnect(Runnable onComplete) {
//        try {
//            mqttAndroidClient.disconnect(null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    onComplete.run();
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    System.out.println("Failed to disconnect: " + exception.getMessage());
//                }
//            });
//        } catch (Exception e) {
//            System.out.println("Error while disconnecting: " + e.getMessage());
//        }
//    }
//
//    public interface MessageCallback {
//        void onMessageReceived(String topic, String message);
//    }
}
