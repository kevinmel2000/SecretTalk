package com.training.leos.secrettalk.data.firebase.dump;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.training.leos.secrettalk.data.firebase.FirebaseContract;
import com.training.leos.secrettalk.data.model.Credential;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by Leo on 09/12/2017.
 */

public class FirebaseRealtimeDatabase implements FirebaseContract.RealtimeDatabase {
    private static FirebaseRealtimeDatabase database;

    public static FirebaseRealtimeDatabase getInstance() {
        if (database == null) {
            database = new FirebaseRealtimeDatabase();
            return database;
        }else {
            return database;
        }
    }

    @Override
    public Maybe<Credential> getUserCredential(final String uid) {
        return Maybe.create(new MaybeOnSubscribe<Credential>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<Credential> e) throws Exception {
                DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference().child("Users").child(uid);

                final Credential credential = new Credential();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        credential.setId(uid);
                        credential.setName((String) dataSnapshot.child("name").getValue());
                        credential.setEmail((String) dataSnapshot.child("email").getValue());
                        credential.setAbout((String) dataSnapshot.child("about").getValue());
                        credential.setImageUrl((String) dataSnapshot.child("imageUrl").getValue());
                        credential.setThumbImageUrl((String) dataSnapshot.child("thumbImageUrl").getValue());

                        Maybe.just(credential);
                        e.onSuccess(credential);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        e.onError(databaseError.toException());
                    }
                });
            }
        });
    }

    @Override
    public Completable saveUserCredential(final Credential credential, final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                DatabaseReference databaseReference = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("Users")
                        .child(uId);

                databaseReference.setValue(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    e.onComplete();
                                } else {
                                    e.onError(task.getException());
                                }
                            }
                        });
            }
        });
    }


    @Override
    public Completable saveEditedCredential(final Credential credential, final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter e) throws Exception {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                        .getReference().child("Users").child(uId);
                Map data = new HashMap();
                data.put("name", credential.getName());
                data.put("about", credential.getAbout());
                databaseReference.updateChildren(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    e.onComplete();
                                } else {
                                    e.onError(task.getException());
                                }
                            }
                        });
            }
        });
    }
}
