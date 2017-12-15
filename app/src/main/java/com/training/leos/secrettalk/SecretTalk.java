package com.training.leos.secrettalk;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class SecretTalk extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //firebase realtime database offline capabilities
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //picasso offline capabilities
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));

        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final DatabaseReference firebaseDatabase =
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("Users").child(firebaseAuth.getCurrentUser().getUid());

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    firebaseDatabase.child("online").onDisconnect().setValue(false);
                    firebaseDatabase.child("online").setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
