package com.unifi.comp590.unifi;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private ImageButton mImageButton;

    private FirebaseAuth mAuth;
    private String mSenderId;
    private StorageReference imageStorageReference;
    private RecyclerView messageListView;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private final int GALLERY_CODE = 1;


    public static class downloadUrls {
        public static String dlURL;
        public   static String thumbdlURL;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        mAuth = FirebaseAuth.getInstance();
        mSenderId = mAuth.getCurrentUser().getUid();

        messageAdapter = new MessageAdapter(messageList);
        messageListView = (RecyclerView) findViewById(R.id.message_list);
        linearLayoutManager = new LinearLayoutManager(this);

        linearLayoutManager.setStackFromEnd(true);
//        messageListView.setHasFixedSize(true);
        messageListView.setLayoutManager(linearLayoutManager);
        messageListView.setAdapter(messageAdapter);

        mSendMessageButton = (ImageButton) findViewById(R.id.send_text_button);
        mMessageText = (EditText) findViewById(R.id.chats_message_edit_text);
        mImageButton = (ImageButton) findViewById(R.id.imageButton);

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
        imageStorageReference = FirebaseStorage.getInstance().getReference().child("Media");
//        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = layoutInflater.inflate(R.layout.chat) TODO ??
        mUserName = (TextView) findViewById(R.id.chat_toolbar_username);
//        mUserLastSeen = (TextView) findViewById(R.id.chat_too)
        mProfileImage = (CircleImageView) findViewById(R.id.chat_toolbar_image);
        mUserName.setText(mReceiverName);
        FetchMessages();
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
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/* video/*");
                startActivityForResult(intent,GALLERY_CODE);
            }
        });

    }

    private void FetchMessages() {
        databaseReference.child("Users").child(mSenderId).child("Chats").child(mReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.notifyDataSetChanged();
                messageListView.smoothScrollToPosition(messageAdapter.getItemCount()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message = mMessageText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
        } else {
            final DatabaseReference message_key = mSenderChatsReference.child(mReceiverId).push();
            final String message_push_id = message_key.getKey();

            String senderRef = "Users/" + mSenderId + "/Chats/" + mReceiverId;
            String receiverRef = "Users/" + mReceiverId + "/Chats/" + mSenderId;

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", message);
            messageTextBody.put("type", "text");
            messageTextBody.put("seen", false);
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from", mSenderId);
            final Map messageBodyDetails = new HashMap();
            //TODO
            messageBodyDetails.put(senderRef+"/"+message_push_id, messageTextBody);
            messageBodyDetails.put(receiverRef+"/"+message_push_id, messageTextBody);
            databaseReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError!=null) {
                        Log.d("log", "onComplete: " + databaseError.getMessage());
                        Toast.makeText(ChatMessageActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();

                    }
                    mMessageText.setText(null);
                }
            });
            messageListView.smoothScrollToPosition(messageAdapter.getItemCount()-1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            final String senderRef = "Users/" + mSenderId + "/Chats/" + mReceiverId;
            final String receiverRef = "Users/" + mReceiverId + "/Chats/" + mSenderId;
            final DatabaseReference message_key = mSenderChatsReference.child(mReceiverId).push();
            final String message_push_id = message_key.getKey();
            StorageReference path = imageStorageReference.child("Pictures").child(message_push_id + ".jpg");
            final Map messageTextBody = new HashMap();

            messageTextBody.put("type", "image");
            messageTextBody.put("seen", false);
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from", mSenderId);
            path.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrls.dlURL = uri.toString();
                                messageTextBody.put("message", downloadUrls.dlURL);
                                Log.d("Success", messageTextBody.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Log.e("onFailure", "onFailure:",e );
                            }
                        });

                        final Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(senderRef+"/"+message_push_id, messageTextBody);
                        messageBodyDetails.put(receiverRef+"/"+message_push_id, messageTextBody);
//                        Log.d("download", "onComplete: "+"download URL="+downloadUrls.dlURL);
                        databaseReference.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError!=null) {
                                    Log.d("Database Error", databaseError.getMessage());
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ChatMessageActivity.this, "Check your internet connection,", Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }

    }
}
