package com.example.nativesmsattempt2.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView; // Import RecyclerView

import com.example.nativesmsattempt2.R;
import com.example.nativesmsattempt2.ui.SmsAdapter;
import com.example.nativesmsattempt2.ui.SmsConstants;
import com.example.nativesmsattempt2.ui.SmsMessage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private RecyclerView smsRecyclerView;
    private SmsAdapter smsAdapter;
    private List<SmsMessage> smsMessages = new ArrayList<>();
    private BroadcastReceiver smsReceiver;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Toast.makeText(this, "Notifications permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsRecyclerView = findViewById(R.id.smsRecyclerView);
        smsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        smsAdapter = new SmsAdapter(smsMessages);
        smsRecyclerView.setAdapter(smsAdapter);

        // Check and request SMS permissions
        if (!hasSmsPermissions()) {
            requestSmsPermissions();
        } else {
            Toast.makeText(this, "Permissions granted. Ready to receive SMS.", Toast.LENGTH_SHORT).show();
        }


        // Register the receiver to listen for SMS messages
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sender = intent.getStringExtra(SmsConstants.SMS_SENDER);
                String message = intent.getStringExtra(SmsConstants.SMS_MESSAGE);
                addSmsMessage(sender, message);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(smsReceiver, new IntentFilter(SmsConstants.SMS_RECEIVED_ACTION));

        // Check and request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver when the activity is destroyed
        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
    }

    private void addSmsMessage(String sender, String message) {
        SmsMessage smsMessage = new SmsMessage(sender, message);
        smsAdapter.addMessage(smsMessage);
    }

    private boolean hasSmsPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted. Ready to receive SMS.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions denied. SMS features won't work.", Toast.LENGTH_LONG).show();
            }
        }
    }
}