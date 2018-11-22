package com.unifi.comp590.unifi;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        mChatsList = (RecyclerView) view.findViewById(R.id.chats_list);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mChatsReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Chats");
        mChatsReference.keepSynced(true);
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserReference.keepSynced(true);
        mChatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Chat, ChatsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat, ChatsViewHolder>(Chat.class,
                R.layout.chats_display_layout, ChatsViewHolder.class,mChatsReference) {
            @Override
            protected void populateViewHolder(final ChatsViewHolder viewHolder, Chat model, int position) {
//                viewHolder.setmUserName(model.getmUserName());
                String list_user_id = getRef(position).getKey();
                mChatsReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("user_name").getValue().toString();
                       //TODO
                        String image = dataSnapshot.child("user_thumbnail").getValue().toString();
                        String message = dataSnapshot.child("Chats").getValue().toString();
                        viewHolder.setmUserName(userName);
                        viewHolder.setmThumbnail(image);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
            final CircleImageView image = (CircleImageView) mView.findViewById(R.id.users_layout_profile_image);

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
