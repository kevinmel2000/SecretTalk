package com.training.leos.secrettalk.ui.allUsers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.model.Credential;
import com.training.leos.secrettalk.ui.main.adapter.UserListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllUsersActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    @BindView(R.id.rv_all_users)
    RecyclerView
            rvAllUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        ButterKnife.bind(this);
        final UserListView userListView = new UserListView(this);

        rvAllUsers.setLayoutManager(new LinearLayoutManager(this));
        rvAllUsers.setHasFixedSize(false);
        rvAllUsers.setAdapter(userListView);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Credential> credentials = new ArrayList<>();
                Credential credential;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    credential = new Credential();
                    credential.setName((String) data.child("name").getValue());
                    credential.setImageUrl((String) data.child("imageUrl").getValue());
                    credentials.add(credential);
                }
                userListView.setCredentials(credentials);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
