package com.training.leos.secrettalk.data.firebase.dump;

import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.training.leos.secrettalk.data.firebase.FirebaseContract;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.annotations.NonNull;

public class FirebaseDataStorage implements FirebaseContract.Storage {
    public static FirebaseDataStorage storage;
    public static FirebaseDataStorage getInstance() {
        if (storage == null) {
            storage = new FirebaseDataStorage();
            return storage;
        }else {
            return storage;
        }
    }

    public String getImageUrl(String uId){
        StorageReference imagesReference = FirebaseStorage.getInstance().getReference()
                .child("profile_images").child(uId + ".jpg");
        String url = imagesReference.getDownloadUrl().getResult().toString();
        return url;
    }

    @Override
    public Completable saveImageToStorage(final Uri resultUri, final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {

                StorageReference imagesReference = FirebaseStorage.getInstance().getReference()
                        .child("profile_images").child(uId + ".jpg");
                imagesReference.putFile(resultUri).addOnCompleteListener(
                        new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    String image_url = String.valueOf(task.getResult().getDownloadUrl());
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                            .getReference().child("Users").child(uId);
                                    databaseReference.child("imageUrl").setValue(image_url)
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
        });
    }

    @Override
    public Completable saveThumbImageToStorage(final byte[] thumbBytes, final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                StorageReference thumbsReference = FirebaseStorage.getInstance()
                        .getReference()
                        .child("profile_images")
                        .child("thumb_images")
                        .child(uId + ".jpg");

                UploadTask uploadTask = thumbsReference.putBytes(thumbBytes);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String thumbUrl = task.getResult().getDownloadUrl().toString();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(uId);
                            databaseReference.child("thumbImageUrl").setValue(thumbUrl)
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
