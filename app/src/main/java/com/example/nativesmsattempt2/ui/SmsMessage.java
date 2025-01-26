package com.example.nativesmsattempt2.ui;

public class SmsMessage {
    private String sender;
    private String message;

    public SmsMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}