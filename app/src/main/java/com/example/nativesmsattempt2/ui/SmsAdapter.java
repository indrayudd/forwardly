package com.example.nativesmsattempt2.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nativesmsattempt2.R;

import java.util.List;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SmsViewHolder> {

    private List<SmsMessage> smsMessages;

    public SmsAdapter(List<SmsMessage> smsMessages) {
        this.smsMessages = smsMessages;
    }

    @NonNull
    @Override
    public SmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_card, parent, false);
        return new SmsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SmsViewHolder holder, int position) {
        SmsMessage smsMessage = smsMessages.get(position);
        holder.senderTextView.setText(smsMessage.getSender());
        holder.messageTextView.setText(smsMessage.getMessage());
    }

    @Override
    public int getItemCount() {
        return smsMessages.size();
    }

    public void addMessage(SmsMessage smsMessage) {
        smsMessages.add(0, smsMessage); // Add to the top
        notifyItemInserted(0);
    }

    public static class SmsViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextView;
        TextView messageTextView;

        public SmsViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}