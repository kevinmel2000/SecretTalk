package com.training.leos.secrettalk.data.firebase;

import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.annotations.NonNull;

public class FirebaseDataStorage {
    private static FirebaseDataStorage instance;

    public static FirebaseDataStorage getInstance() {
        if (instance == null){
            instance = new FirebaseDataStorage();
        }
        return instance;
    }

    private FirebaseDataStorage(){

    }

    private StorageReference getProfilesImagesReference() {
        return FirebaseStorage.getInstance().getReference()
                .child("profile_images");
    }

    private StorageReference getProfilThumbImagesReference() {
        return FirebaseStorage.getInstance()
                .getReference()
                .child("profile_images")
                .child("thumb_images");
    }

    public void saveImageToStorage(final CompletableEmitter e, final String currentUserId, final Uri resultUri) {
        getProfilesImagesReference()
                .child(currentUserId + ".jpg")
                .putFile(resultUri)
                .addOnCompleteListener(
                        new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    String image_url = String.valueOf(task.getResult().getDownloadUrl());

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("Users")
                                            .child(currentUserId);
                                    databaseReference
                                            .child("imageUrl")
                                            .setValue(image_url)
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
                                } else {
                                    e.onError(task.getException());
                                }
                            }
                        }
                );

    }

    public Completable saveThumbImageToStorage(final byte[] thumbBytes, final String currentUserId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                StorageReference thumbsReference = FirebaseStorage.getInstance()
                        .getReference()
                        .child("profile_images")
                        .child("thumb_images")
                        .child(currentUserId + ".jpg");

                UploadTask uploadTask = thumbsReference.putBytes(thumbBytes);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String thumbUrl = String.valueOf(task.getResult().getDownloadUrl());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("Users")
                                    .child(currentUserId);
                            databaseReference.child("thumbImageUrl")
                                    .setValue(thumbUrl)
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

                        } else {
                            e.onError(task.getException());
                        }
                    }
                });
            }
        });
    }
}
