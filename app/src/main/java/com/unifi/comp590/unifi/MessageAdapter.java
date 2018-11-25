package com.unifi.comp590.unifi;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Message> messageList;
    private FirebaseAuth mAuth;
    private Context context;
    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_message_layout, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        Message message = messageList.get(i);
        String senderUserId = mAuth.getCurrentUser().getUid();
        String fromUserId = message.getFrom();
        if (fromUserId.equals(senderUserId)) {
            messageViewHolder.messageText.setBackgroundResource(R.drawable.message_text_background_sender);
            messageViewHolder.messageText.setTextColor(Color.WHITE);
            messageViewHolder.messageText.setGravity(Gravity.RIGHT);
        } else {
            messageViewHolder.messageText.setBackgroundResource(R.drawable.message_text_background_receiver);
            messageViewHolder.messageText.setTextColor(Color.WHITE);
            messageViewHolder.messageText.setGravity(Gravity.LEFT);
        }
        messageViewHolder.messageText.setText(message.getMessage());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public CircleImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);
            messageText = (TextView) view.findViewById(R.id.single_message_text);
//            messageImage = (CircleImageView) view.findViewById(R.id.single_message_image);

        }



    }

}
