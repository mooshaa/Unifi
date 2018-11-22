package com.unifi.comp590.unifi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageActivity extends AppCompatActivity {

    private String mReceiverId;
    private String mReceiverName;

    private Toolbar toolbar;
    private TextView mUserName;
    private TextView mUserLastSeen;
    private CircleImageView mProfileImage;
    private DatabaseReference databaseReference;
    private DatabaseReference mReceiverChatsReference;
    private DatabaseReference mSenderChatsReference;

    private ImageButton mSendMessageButton;
    private EditText mMessageText;

    private FirebaseAuth mAuth;
    private String mSenderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        mAuth = FirebaseAuth.getInstance();
        mSenderId = mAuth.getCurrentUser().getUid();

        mSendMessageButton = (ImageButton) findViewById(R.id.send_text_button);
        mMessageText = (EditText) findViewById(R.id.chats_message_edit_text);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mReceiverId = getIntent().getExtras().get("user_id").toString();
        mReceiverName = getIntent().getExtras().get("user_name").toString();

        toolbar = (Toolbar) findViewById(R.id.chat_message_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mReceiverChatsReference = databaseReference.child("Users").child(mReceiverId).child("Chats");
        mSenderChatsReference = databaseReference.child("Users").child(mSenderId).child("Chats");
//        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = layoutInflater.inflate(R.layout.chat) TODO ??
        mUserName = (TextView) findViewById(R.id.chat_toolbar_username);
//        mUserLastSeen = (TextView) findViewById(R.id.chat_too)
        mProfileImage = (CircleImageView) findViewById(R.id.chat_toolbar_image);
        mUserName.setText(mReceiverName);
// TODO not working with thumbnails
        databaseReference.child("Users").child(mReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String userImage = dataSnapshot.child("user_image").getValue().toString();
                Picasso.get().load(userImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.pcdefault_small).into(mProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(userImage).placeholder(R.drawable.pcdefault_small).into(mProfileImage);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mMessageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    mSendMessageButton.setEnabled(false);
                    mSendMessageButton.setVisibility(View.INVISIBLE);
                } else {
                    mSendMessageButton.setEnabled(true);
                    mSendMessageButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        String message = mMessageText.getText().toString();
        if (TextUtils.isEmpty(message)) {
        } else {
            DatabaseReference message_key = mSenderChatsReference.push();
            String message_push_id = message_key.getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", message);
            messageTextBody.put("type", false);
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(mSenderChatsReference.toString() + message_push_id, messageTextBody);
            messageBodyDetails.put(mReceiverChatsReference.toString() + message_push_id, messageTextBody);
            databaseReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                }
            });

        }
    }
}
