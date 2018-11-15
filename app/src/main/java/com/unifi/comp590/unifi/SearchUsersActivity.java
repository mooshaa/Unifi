package com.unifi.comp590.unifi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsersActivity extends AppCompatActivity {

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
                viewHolder.setUser_image(model.getUser_image(), getApplicationContext());
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

        }public void setUser_image(String user_image, Context context) {
            CircleImageView image = (CircleImageView) view.findViewById(R.id.users_layout_profile_image);
            Picasso.get().load(user_image).placeholder(R.drawable.pcdefault_small).into(image);

        }
    }

}
