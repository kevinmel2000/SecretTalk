package com.training.leos.secrettalk.data.firebase;

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
import com.training.leos.secrettalk.util.DateProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.annotations.NonNull;

public class FirebaseAuthDataStore implements FirebaseContract {
    public static final String TAG = FirebaseAuthDataStore.class.getSimpleName();
    private static FirebaseAuthDataStore firebaseAuthDataStore;
    private FirebaseAuth firebaseAuth;

    public static FirebaseAuthDataStore getInstance() {
        if (firebaseAuthDataStore == null) {
            firebaseAuthDataStore = new FirebaseAuthDataStore();
        }
        return firebaseAuthDataStore;
    }

    //Firebase Authentication
    private void getFirebaseAuthInstance() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
    }

    private FirebaseUser getCurrenUser() {
        getFirebaseAuthInstance();
        return firebaseAuth.getCurrentUser();
    }

    @Override
    public String getCurrentUserId() {
        getFirebaseAuthInstance();
        return getCurrenUser().getUid();
    }

    @Override
    public boolean isUserSignedIn() {
        return getCurrenUser() != null;
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
    public Completable registration(final Credential credential) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter e) throws Exception {
                getFirebaseAuthInstance();
                firebaseAuth.createUserWithEmailAndPassword(
                        credential.getEmail(),
                        credential.getPassword())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference databaseReference = FirebaseDatabase
                                            .getInstance()
                                            .getReference()
                                            .child("Users")
                                            .child(getCurrentUserId());
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
                                } else {
                                    e.onError(task.getException());
                                }
                            }
                        });
            }
        });
    }


    //Firebase Realtime Database
    @Override
    public Maybe<Credential> getUserCredential(final String uId) {
        return Maybe.create(new MaybeOnSubscribe<Credential>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<Credential> e) throws Exception {
                DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference().child("Users").child(uId);

                final Credential credential = new Credential();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        credential.setId(dataSnapshot.getKey());
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
                        //e.onError(databaseError.toException());
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
                DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                        .getReference().child("Users").child(getCurrentUserId());
                Map<String, Object> data = new HashMap<>();
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

    @Override
    public Maybe<ArrayList<Credential>> getAllUsers() {
        return Maybe.create(new MaybeOnSubscribe<ArrayList<Credential>>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<ArrayList<Credential>> e) throws Exception {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Credential> credentials = new ArrayList<>();
                        Credential credential;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            credential = new Credential();
                            Log.w("========", "onDataChange: " + data.getKey());
                            credential.setId(data.getKey());
                            credential.setName((String) data.child("name").getValue());
                            credential.setAbout((String) data.child("about").getValue());
                            credential.setImageUrl((String) data.child("imageUrl").getValue());
                            credential.setThumbImageUrl((String) data.child("thumbImageUrl").getValue());
                            credentials.add(credential);

                            Maybe.just(credentials);
                            e.onSuccess(credentials);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public Maybe<String> getUserFriendRequestState(final String uId) {
        return Maybe.create(new MaybeOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<String> e) throws Exception {
                final String currentUserId = getCurrentUserId();
                final DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference();
                DatabaseReference friendsDataReference = reference
                        .child("FriendsData")
                        .child(currentUserId)
                        .child(uId);
                friendsDataReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Maybe.just("friended");
                            e.onSuccess("friended");
                        } else {
                            DatabaseReference friendRequestReference = reference
                                    .child("FriendRequest")
                                    .child(currentUserId)
                                    .child(uId)
                                    .child("requestType");
                            friendRequestReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.w(TAG, "onDataChange: " + dataSnapshot.toString());
                                    Log.w(TAG, "onDataChange: " + dataSnapshot.getKey() );
                                    Log.w(TAG, "onDataChange: " + dataSnapshot.getValue());
                                    Log.w(TAG, "onDataChange: " + dataSnapshot.exists() );

                                    if (dataSnapshot.exists()) {
                                        Maybe.just(dataSnapshot.getValue());
                                        e.onSuccess((String) dataSnapshot.getValue());
                                    } else {
                                        Maybe.just("notFriend");
                                        e.onSuccess("notFriend");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    e.onError(databaseError.toException());
                                }
                            });
                        }
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
    public Completable sendFriendRequest(final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                final String currentUserId = getCurrentUserId();
                DatabaseReference currentUserReference = FirebaseDatabase.getInstance().getReference()
                        .child("FriendRequest")
                        .child(currentUserId)
                        .child(uId)
                        .child("requestType");
                final DatabaseReference requestedUserReference = FirebaseDatabase.getInstance().getReference()
                        .child("FriendRequest")
                        .child(uId)
                        .child(currentUserId)
                        .child("requestType");
                currentUserReference
                        .setValue("sent")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    requestedUserReference
                                            .setValue("received")
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
    public Completable cancelFriendRequest(final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                final String currentUserId = getCurrentUserId();
                DatabaseReference currentUserReference = FirebaseDatabase.getInstance().getReference()
                        .child("FriendRequest")
                        .child(currentUserId);
                final DatabaseReference requestedUserReference = FirebaseDatabase.getInstance().getReference()
                        .child("FriendRequest")
                        .child(uId);
                currentUserReference
                        .child(uId)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    requestedUserReference
                                            .child(currentUserId)
                                            .removeValue()
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
    public Completable acceptedFriendRequest(final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                final String currentUserId = getCurrentUserId();
                DatabaseReference currentFriendsDataReference = FirebaseDatabase.getInstance().getReference()
                        .child("FriendsData")
                        .child(currentUserId)
                        .child(uId);
                final DatabaseReference userFriendsDataReference = FirebaseDatabase.getInstance().getReference()
                        .child("FriendsData")
                        .child(uId)
                        .child(currentUserId);

                final String date = DateProvider.getCurrentDate();
                currentFriendsDataReference.setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userFriendsDataReference.setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        e.onComplete();

                                        //then call cancel friend request in presenter
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

    @Override
    public Completable deleteFriend(final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                final String currentUserId = getCurrentUserId();
                DatabaseReference currentFriendsDataReference = FirebaseDatabase.getInstance().getReference()
                        .child("FriendsData")
                        .child(currentUserId)
                        .child(uId);
                final DatabaseReference userFriendsDataReference = FirebaseDatabase.getInstance().getReference()
                        .child("FriendsData")
                        .child(uId)
                        .child(currentUserId);

                currentFriendsDataReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userFriendsDataReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    //onProgress
    public Maybe<ArrayList<Credential>> getReceivedFriendRequest() {
        return Maybe.create(new MaybeOnSubscribe<ArrayList<Credential>>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<ArrayList<Credential>> e) throws Exception {
                String currentUserId = getCurrentUserId();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("FriendRequest")
                        .child(currentUserId);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<Credential> credentials = new ArrayList<>();

                        if (dataSnapshot.exists()) {
                            for (final DataSnapshot data : dataSnapshot.getChildren()) {
                                String requestValue = (String) data.child("requestType").getValue();
                                if (requestValue.equals("received")) {
                                    DatabaseReference userReference = FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("Users");
                                    userReference
                                            .child(data.getKey())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Credential credential = new Credential();
                                                    credential.setId(dataSnapshot.getKey());
                                                    credential.setName((String) dataSnapshot.child("name").getValue());
                                                    credential.setAbout((String) dataSnapshot.child("about").getValue());
                                                    credential.setImageUrl((String) dataSnapshot.child("imageUrl").getValue());
                                                    credential.setThumbImageUrl((String) dataSnapshot.child("thumbImageUrl").getValue());
                                                    credentials.add(credential);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    e.onError(databaseError.toException());
                                                }
                                            });
                                    Maybe.just(credentials);
                                    e.onSuccess(credentials);
                                }
                                else {
                                    Maybe.just(credentials);
                                    e.onSuccess(credentials);
                                }
                            }
                        } else {
                            Maybe.just(credentials);
                            e.onSuccess(credentials);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        e.onError(databaseError.toException());
                    }
                });
            }
        });
    }

    public Maybe<ArrayList<Credential>> getMyFriends() {
        return Maybe.create(new MaybeOnSubscribe<ArrayList<Credential>>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<ArrayList<Credential>> e) throws Exception {
                String currentUserId = getCurrentUserId();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("FriendsData")
                        .child(currentUserId);
                final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                        .child("Users");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final ArrayList<Credential> credentials = new ArrayList<>();
                            for (final DataSnapshot data : dataSnapshot.getChildren()) {
                                userReference.child(data.getKey()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Credential credential = new Credential();
                                        credential.setId(dataSnapshot.getKey());
                                        credential.setName((String) dataSnapshot.child("name").getValue());
                                        credential.setAbout((String) dataSnapshot.child("about").getValue());
                                        credential.setImageUrl((String) dataSnapshot.child("imageUrl").getValue());
                                        credential.setThumbImageUrl((String) dataSnapshot.child("thumbImageUrl").getValue());
                                        credentials.add(credential);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        e.onError(databaseError.toException());
                                    }
                                });
                            }
                            Maybe.just(credentials);
                            e.onSuccess(credentials);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        e.onError(databaseError.toException());
                    }
                });
            }
        });
    }


    //Firebase Storage
    @Override
    public Completable saveImageToStorage(final Uri resultUri) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                final String uId = getCurrentUserId();
                StorageReference imagesReference = FirebaseStorage.getInstance().getReference()
                        .child("profile_images").child(uId + ".jpg");
                imagesReference
                        .putFile(resultUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
    public Completable saveThumbImageToStorage(final byte[] thumbBytes) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                final String uId = getCurrentUserId();
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
                            String thumbUrl = String.valueOf(task.getResult().getDownloadUrl());
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
