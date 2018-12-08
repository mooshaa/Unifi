package com.unifi.comp590.unifi;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.unifi.comp590.unifi.ViewHolder;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<Message> messageList;
    private FirebaseAuth mAuth;
    private Context context;
    String senderUserId;
    String fromUserId;
    private DatabaseReference mUserRef;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_message_layout, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(i, viewGroup, false);
        ViewHolder holder = null;
        switch (i) {
            case R.layout.single_message_layout:
                holder = new SenderViewHolder(v);
                break;
            case R.layout.single_message_layout2:
                holder = new ReceiverViewHolder(v);
                break;
            case R.layout.sender_pic:
                holder = new SenderPicHolder(v);
            case R.layout.receiver_pic:
                holder = new ReceiverPicHolder(v);
        }

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder messageViewHolder, int i) {
        Message message = messageList.get(i);
        senderUserId = mAuth.getCurrentUser().getUid();
        fromUserId = message.getFrom();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("user_name").getValue().toString();
                String userImage = dataSnapshot.child("user_image").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        messageViewHolder.bind(message);


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        fromUserId = messageList.get(position).getFrom();
        if (senderUserId.equals(fromUserId)) {
            if (messageList.get(position).getType().equals("text"))
                return R.layout.single_message_layout2;
            else
                return R.layout.sender_pic;
        } else {
            if (messageList.get(position).getType().equals("text"))
                return R.layout.single_message_layout;
            else
                return R.layout.receiver_pic;
        }
    }

    public class SenderViewHolder extends ViewHolder {
        public TextView messageText;

        public SenderViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.single_message_text);

        }

        @Override
        public void bind(Message message) {

            messageText.setBackgroundResource(R.drawable.message_text_background_receiver);
            messageText.setTextColor(Color.WHITE);
            messageText.setText(message.getMessage());
        }


    }

    public class ReceiverViewHolder extends ViewHolder {
        public TextView messageText;

        public ReceiverViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.single_message_text2);

        }

        @Override
        public void bind(Message message) {

            messageText.setBackgroundResource(R.drawable.message_text_background_sender);
            messageText.setTextColor(Color.WHITE);
            messageText.setText(message.getMessage());


        }


    }

    public class ReceiverPicHolder extends ViewHolder {
        public ImageView mPic;

        public ReceiverPicHolder(View view) {
            super(view);

            mPic = (ImageView) view.findViewById(R.id.receiver_image);
            Log.d("tag", "ReceiverPicHolder: "+mPic);
        }

        @Override
        public void bind(Message message) {

            Picasso.get().load(message.getMessage()).into(mPic);

        }
    }
    public class SenderPicHolder extends ViewHolder {
        public ImageView mPic;

        public SenderPicHolder(View view) {
            super(view);

            mPic = (ImageView) view.findViewById(R.id.sender_image);

        }

        @Override
        public void bind(Message message) {
        Picasso.get().load(message.getMessage()).into(mPic);
        }
    }
}
