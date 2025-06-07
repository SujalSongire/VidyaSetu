package com.example.vidyasetu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;
    private Context context;
    private String currentUserId;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            // Hide sender name for sent messages
            holder.senderName.setVisibility(View.GONE);
            holder.messageText.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_message_sent));
        } else {
            // Show sender name for received messages
            holder.senderName.setVisibility(View.VISIBLE);
            holder.senderName.setText(message.getSenderName()); // ✅ Set sender's name
            holder.messageText.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_message_received));
        }

        holder.messageText.setText(message.getMessage());
        holder.timestamp.setText(formatTime(message.getTimestamp()));
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderId().equals(currentUserId) ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(timestamp);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, messageText, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.sender_name);
            messageText = itemView.findViewById(R.id.message_text);
            timestamp = itemView.findViewById(R.id.message_time);
        }
    }
}
