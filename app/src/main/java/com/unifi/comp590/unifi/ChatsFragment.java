package com.unifi.comp590.unifi;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private RecyclerView mChatsList;
    private DatabaseReference mChatsReference;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;

    private String user_id;
    private View view;

    public ChatsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view= inflater.inflate(R.layout.fragment_chats, container, false);

        mChatsList = (RecyclerView) view.findViewById(R.id.chats_fragment_list);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mChatsReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Chats");
        mChatsReference.keepSynced(true);
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserReference.keepSynced(true);
        mChatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Chat,ChatsFragment.ChatsViewHolder> firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Chat, ChatsViewHolder>
                (Chat.class, R.layout.chats_display_layout, ChatsFragment.ChatsViewHolder.class, mChatsReference) {

            @Override
            protected void populateViewHolder(final ChatsFragment.ChatsViewHolder viewHolder,  Chat model, int position) {

//                viewHolder.setser_thumbnail(model.get);
                final String list_user_id = getRef(position).getKey();
                final String[] userName = new String[1];
                mUserReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userName[0] = dataSnapshot.child("user_name").getValue().toString();
                        String image = dataSnapshot.child("user_image").getValue().toString();
                        Query last = mChatsReference.child(list_user_id).orderByKey().limitToLast(1);
                        last.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child: dataSnapshot.getChildren()) {
                                    if (!child.child("type").getValue().toString().equals("image")) {

                                    String message = child.child("message").getValue().toString();
                                    viewHolder.setmLastMessage(message);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        viewHolder.setmUserName(userName[0]);
                        viewHolder.setmThumbnail(image);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ChatMessageActivity.class);
                        intent.putExtra("user_id", list_user_id);
                        intent.putExtra("user_name", userName[0]);
                        Log.d("", "onClick: id="+ list_user_id+"  username="+ userName[0]);
                        startActivity(intent);
                    }
                });
            }
        };

        mChatsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
         View mView;
        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public  void setmUserName(String userName) {
            TextView name = (TextView) mView.findViewById(R.id.chats_display_layout_user_name);
            name.setText(userName);
        }

        public  void setmLastMessage(String message) {
            TextView messageView = (TextView) mView.findViewById(R.id.chats_display_layout_last_message);
            messageView.setText(message);

        }

        public  void setmThumbnail(final String user_image ){
            final CircleImageView image = (CircleImageView) mView.findViewById(R.id.chats_display_layout_profile_image);

            Picasso.get().load(user_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.pcdefault_small).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(user_image).placeholder(R.drawable.pcdefault_small).into(image);
                }
            });

        }
    }
}
