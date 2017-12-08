package com.training.leos.secrettalk.data.auth;

import android.net.Uri;
import android.util.Log;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.training.leos.secrettalk.data.model.Credential;
import com.training.leos.secrettalk.util.StringRandomGenerator;

import java.util.HashMap;
import java.util.Map;

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
    public Completable saveEditedCredential(final Credential credential) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter e) throws Exception {
                getFirebaseAuthInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                DatabaseReference databaseReference =FirebaseDatabase.getInstance()
                        .getReference().child("Users").child(currentUser.getUid());

                databaseReference.child("name").setValue(credential.getName());
                databaseReference.child("about").setValue(credential.getAbout())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                 if (task.isSuccessful()){
                                     e.onComplete();
                                 }else {
                                     e.onError(task.getException());
                                 }
                            }
                        });
            }
        });
    }

    @Override
    public Completable saveImageToStorage(final Uri resultUri) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                final String currentUserId = getCurrenUser().getUid();
                StorageReference imagesReference = FirebaseStorage.getInstance().getReference()
                        .child("profile_images").child( currentUserId + ".jpg");
                imagesReference.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            String image_url = task.getResult().getDownloadUrl().toString();

                            DatabaseReference databaseReference =FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(currentUserId);
                            databaseReference.child("imageUrl").setValue(image_url)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                e.onComplete();
                                            }else {
                                                e.onError(task.getException());
                                            }
                                        }
                                    });
                        }else {
                            e.onError(task.getException());
                        }
                        }
                    }
                );
            }
        });
    }

    @Override
    public Completable saveThumbImageToStorage(final byte[] thumbBytes){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                final String uid = getCurrenUser().getUid();
                StorageReference thumbsReference = FirebaseStorage.getInstance()
                        .getReference()
                        .child("profile_images")
                        .child("thumb_images")
                        .child(uid + ".jpg");

                UploadTask uploadTask = thumbsReference.putBytes(thumbBytes);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            String thumbUrl = task.getResult().getDownloadUrl().toString();
                            DatabaseReference databaseReference =FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(uid);
                            databaseReference.child("thumbImageUrl").setValue(thumbUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                e.onComplete();
                                            }else {
                                                e.onError(task.getException());
                                            }
                                        }
                                    });

                        }else {
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
