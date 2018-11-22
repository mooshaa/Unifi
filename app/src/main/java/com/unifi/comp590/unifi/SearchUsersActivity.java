package com.unifi.comp590.unifi;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsersActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    private Toolbar mToolbar;
    private RecyclerView SearchUsersList;
    private DatabaseReference mDataBaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        mToolbar = (Toolbar) findViewById(R.id.search_users_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Search User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SearchUsersList = (RecyclerView) findViewById(R.id.search_users_list);
        SearchUsersList.setHasFixedSize(true);
        SearchUsersList.setLayoutManager(new LinearLayoutManager(this));
        mDataBaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mDataBaseReference.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Users, UsersViewHolder>
                (Users.class, R.layout.users_display_layout, UsersViewHolder.class, mDataBaseReference) {

            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_image(model.getUser_image());
//                viewHolder.setser_thumbnail(model.get);
                final String list_user_id = getRef(position).getKey();
                final String[] userName = new String[1];
                mDataBaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           userName[0] = dataSnapshot.child("user_name").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchUsersActivity.this, ChatMessageActivity.class);
                        intent.putExtra("user_id", list_user_id);
                        intent.putExtra("user_name", userName[0]);
                        Log.d(TAG, "onClick: id="+ list_user_id+"  username="+ userName[0]);
                        startActivity(intent);
                    }
                });
            }
        };

        SearchUsersList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View view;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }



        public void setUser_name(String user_name) {
            TextView name = (TextView) view.findViewById(R.id.users_layout_user_name);
            name.setText(user_name);

        }

        public void setUser_status(String user_status) {
            TextView status = (TextView) view.findViewById(R.id.users_layout_status);
            status.setText(user_status);

        }
        public void setUser_image(final String user_image) {
            final CircleImageView image = (CircleImageView) view.findViewById(R.id.users_layout_profile_image);

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
//    TODO ^^^^^^^^
//    public void setser_thumbnail(String user_thumbnail){
//            CircleImageView imageView = (CircleImageView) view.findViewById(R.id.users_layout_profile_image);
//            Picasso.get().load(user_thumbnail).placeholder(R.drawable.pcdefault_small).into(imageView);
//        }
    }

}
