package com.training.leos.secrettalk.data.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.training.leos.secrettalk.data.model.Credential;
import com.training.leos.secrettalk.util.DateProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.CompletableEmitter;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;

public class FirebaseRealtimeDatabase {
    public static final String TAG = FirebaseRealtimeDatabase.class.getSimpleName();
    private static FirebaseRealtimeDatabase instance;

    public static FirebaseRealtimeDatabase getInstance() {
        if (instance == null) {
            instance = new FirebaseRealtimeDatabase();
        }
        return instance;
    }

    private DatabaseReference getRootReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    private DatabaseReference getUsersReference() {
        return FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private DatabaseReference getFriendsDataReference() {
        return FirebaseDatabase.getInstance().getReference().child("FriendsData");
    }

    //==================
    public void getUserCredential(final MaybeEmitter<Credential> e, final String uId) {
        DatabaseReference userReference = getUsersReference().child(uId);
        userReference.keepSynced(true);

        final Credential credential = new Credential();
        userReference.addValueEventListener(new ValueEventListener() {
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
                e.onError(databaseError.toException());
            }
        });
    }

    public void getAllUsers(final MaybeEmitter<ArrayList<Credential>> e) {
        DatabaseReference usersReference = getUsersReference();
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Credential> credentials = new ArrayList<>();
                Credential credential;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    credential = new Credential();
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
                e.onError(databaseError.toException());
            }
        });
    }

    public void getUserFriendRequestState(final MaybeEmitter<String> e, final String currentUserId, final String uId) {
        DatabaseReference friendDataReference = getFriendsDataReference().child(currentUserId);
        final DatabaseReference friendRequestReference = getRootReference()
                .child("FriendRequest")
                .child(currentUserId)
                .child(uId)
                .child("requestType");

        friendDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uId)) {
                    Maybe.just("friended");
                    e.onSuccess("friended");
                } else {
                    friendRequestReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.w(TAG, "onDataChange: " + dataSnapshot.getValue());
                            if (dataSnapshot.getValue() != null) {
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

    public void getReceivedFriendRequest(final MaybeEmitter<ArrayList<Credential>> e, String currentUserId) {
        final ArrayList<Credential> credentials = new ArrayList<>();

        DatabaseReference requestReference = getRootReference().child("FriendRequest").child(currentUserId);
        requestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        String requestValue = (String) data.child("requestType").getValue();

                        if (requestValue.equals("received")) {
                            getUsersReference().child(data.getKey()).addValueEventListener(new ValueEventListener() {
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
                        } else {
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

    public void getMyFriends(final MaybeEmitter<ArrayList<Credential>> e, String currentUserId) {
        getFriendsDataReference().child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    final ArrayList<Credential> credentials = new ArrayList<>();
                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        getUsersReference().child(data.getKey()).addValueEventListener(new ValueEventListener() {
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

    public void saveCreatedCredential(final CompletableEmitter e, final String currentUserId, Credential credential) {
        getUsersReference()
                .child(currentUserId)
                .setValue(credential)
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

    public void saveEditedCredential(final CompletableEmitter e, Credential credential, String uId) {
        DatabaseReference currentUserReference = getUsersReference().child(uId);
        Map<String, Object> data = new HashMap<>();
        data.put("name", credential.getName());
        data.put("about", credential.getAbout());
        currentUserReference.updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void sendFriendRequest(final CompletableEmitter e, final String currentUserId, final String uId) {
        Map requestData = new HashMap();
        requestData.put("FriendRequest/" + currentUserId + "/" + uId + "requestType", "sent");
        requestData.put("FriendRequest/" + uId + "/" + currentUserId + "requestType", "received");

        getRootReference().updateChildren(requestData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    e.onError(databaseError.toException());
                } else {
                    e.onComplete();
                    sendNotification(currentUserId, uId, "request");
                }
            }
        });
    }

    public void cancelFriendRequest(final CompletableEmitter e, final String currentUserId, final String uId) {
        Map requestData = new HashMap();
        requestData.put("FriendRequest/" + currentUserId + "/" + uId, null);
        requestData.put("FriendRequest/" + uId + "/" + currentUserId, null);

        getRootReference().updateChildren(requestData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    e.onError(databaseError.toException());
                } else {
                    e.onComplete();
                }
            }
        });
    }

    public void acceptedFriendRequest(final CompletableEmitter e, final String currentUserId, final String uId) {
        final String date = DateProvider.getCurrentDate();

        final Map friendsData = new HashMap();
        friendsData.put("FriendsData/" + currentUserId + "/" + uId + "/date", date);
        friendsData.put("FriendsData/" + uId + "/" + currentUserId + "/date", date);

        friendsData.put("FriendRequest/" + currentUserId + "/" + uId, null);
        friendsData.put("FriendRequest/" + uId + "/" + currentUserId, null);

        getRootReference().updateChildren(friendsData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    e.onError(databaseError.toException());
                } else {
                    e.onComplete();
                }
            }
        });
    }

    public void deleteFriend(final CompletableEmitter e, final String currentUserId, final String uId) {
        getFriendsDataReference()
                .child(currentUserId)
                .child(uId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            getFriendsDataReference()
                                    .child(uId)
                                    .child(currentUserId)
                                    .removeValue()
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

    protected void saveDeviceToken(String currentUserId) {
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference userReference = getRootReference().child("Users").child(currentUserId).child("deviceToken");
        userReference.setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void sendNotification(String currentUserId, String uId, String requestType) {
        HashMap<String, String> info = new HashMap<>();
        info.put("from", currentUserId);
        info.put("type", requestType);

        DatabaseReference notificationsReference = getRootReference().child("Notifications").child(uId);
        notificationsReference.push().setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.w(TAG, "onComplete: sendNotif" + task.isSuccessful());
            }
        });
    }

}
