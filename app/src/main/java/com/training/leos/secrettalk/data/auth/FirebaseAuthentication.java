package com.training.leos.secrettalk.data.auth;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.annotations.NonNull;

public class FirebaseAuthentication implements AuthenticationContract {

    public static final String TAG = FirebaseAuthentication.class.getSimpleName();

    public static FirebaseAuthentication firebaseAuthentication;
    private FirebaseAuth firebaseAuth;

    public static FirebaseAuthentication getInstance() {
        if (firebaseAuthentication == null) {
            firebaseAuthentication = new FirebaseAuthentication();
        }
        return firebaseAuthentication;
    }

    private void getFirebaseAuthInstance() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
    }

    @Override
    public boolean isUserSignedIn() {
        if (getCurrenUser() != null) {
            return true;
        } else {
            return false;
        }
    }

    private FirebaseUser getCurrenUser(){
        getFirebaseAuthInstance();
        return firebaseAuth.getCurrentUser();
    }

    @Override
    public boolean signOut() {
        getFirebaseAuthInstance();
        firebaseAuth.signOut();
        return true;
    }

    @Override
    public Completable signIn(final Credential credential) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                getFirebaseAuthInstance();
                firebaseAuth.signInWithEmailAndPassword(credential.getEmail(), credential.getPassword())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
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
    public Completable accountCreation(final Credential credential) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter e) throws Exception {
                getFirebaseAuthInstance();
                firebaseAuth.createUserWithEmailAndPassword(credential.getEmail(), credential.getPassword())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    getFirebaseAuthInstance();
                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    DatabaseReference databaseReference =FirebaseDatabase.getInstance()
                                            .getReference().child("Users").child(currentUser.getUid());

                                    credential.setAbout("Hi, let's have some secret talk");
                                    credential.setImageUrl("default");
                                    credential.setThumbImageUrl("default");

                                    databaseReference.setValue(credential)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            e.onComplete();
                                        }
                                    });

                                } else {
                                    e.onError(task.getException());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public Maybe<Credential> getUserCredential(final String uid) {
        return Maybe.create(new MaybeOnSubscribe<Credential>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<Credential> e) throws Exception {
                getFirebaseAuthInstance();
                DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference().child("Users").child(uid);

                final Credential credential = new Credential();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        credential.setId(uid.toString());
                        credential.setName(dataSnapshot.child("name").getValue().toString());
                        credential.setEmail(dataSnapshot.child("email").getValue().toString());
                        credential.setAbout(dataSnapshot.child("about").getValue().toString());
                        credential.setImageUrl(dataSnapshot.child("imageUrl").getValue().toString());
                        credential.setThumbImageUrl(dataSnapshot.child("thumbImageUrl").getValue().toString());

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
    public Maybe<Credential> getCurrentUserCredential(){
        return Maybe.create(new MaybeOnSubscribe<Credential>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<Credential> e) throws Exception {
                getFirebaseAuthInstance();
                final String uid = getCurrenUser().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference().child("Users").child(uid);

                final Credential credential = new Credential();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        credential.setId(uid.toString());
                        credential.setName(dataSnapshot.child("name").getValue().toString());
                        credential.setEmail(dataSnapshot.child("email").getValue().toString());
                        credential.setAbout(dataSnapshot.child("about").getValue().toString());
                        credential.setImageUrl(dataSnapshot.child("imageUrl").getValue().toString());
                        credential.setThumbImageUrl(dataSnapshot.child("thumbImageUrl").getValue().toString());

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
}
