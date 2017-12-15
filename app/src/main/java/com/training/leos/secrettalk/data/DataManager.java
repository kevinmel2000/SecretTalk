package com.training.leos.secrettalk.data;

import android.net.Uri;
import android.util.Log;

import com.training.leos.secrettalk.data.firebase.FirebaseAuthentication;
import com.training.leos.secrettalk.data.firebase.FirebaseDataStorage;
import com.training.leos.secrettalk.data.firebase.FirebaseRealtimeDatabase;
import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.annotations.NonNull;

public class DataManager implements DataContract {
    public static final String TAG = DataManager.class.getSimpleName();
    private static DataManager dataManager;

    public static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    private FirebaseAuthentication authentication;
    private FirebaseRealtimeDatabase realtimeDatabase;
    private FirebaseDataStorage storage;

    private DataManager() {
        this.authentication = FirebaseAuthentication.getInstance();
        this.realtimeDatabase = FirebaseRealtimeDatabase.getInstance();
        this.storage = FirebaseDataStorage.getInstance();
    }

    //Firebase Authentication
    @Override
    public String getCurrentUserId() {
        return authentication.getCurrentUserId();
    }

    @Override
    public boolean hasSignedInUser() {
        return authentication.hasSignedInUser();
    }

    @Override
    public boolean signOut() {
        authentication.signOut();
        return true;
    }

    @Override
    public Completable signIn(final Credential credential) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                authentication.signIn(e, credential);
            }
        });
    }

    @Override
    public Completable registration(final Credential credential) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter e) throws Exception {
                authentication.registration(e, credential);
            }
        });
    }


    //Firebase Realtime Database
    @Override
    public Maybe<Credential> getUserCredential(final String uId) {
        return Maybe.create(new MaybeOnSubscribe<Credential>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<Credential> e) throws Exception {
                realtimeDatabase.getUserCredential(e, uId);
            }
        });
    }

    @Override
    public Completable saveEditedCredential(final Credential credential) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter e) throws Exception {
                realtimeDatabase.saveEditedCredential(e, credential, authentication.getCurrentUserId());
            }
        });
    }

    @Override
    public Maybe<ArrayList<Credential>> getAllUsers() {
        return Maybe.create(new MaybeOnSubscribe<ArrayList<Credential>>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<ArrayList<Credential>> e) throws Exception {
                realtimeDatabase.getAllUsers(e);
            }
        });
    }

    @Override
    public Maybe<String> getUserFriendRequestState(final String uId) {
        return Maybe.create(new MaybeOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<String> e) throws Exception {
                realtimeDatabase.getUserFriendRequestState(e, getCurrentUserId(), uId);
            }
        });
    }

    @Override
    public Completable sendFriendRequest(final String uId) {
        Log.w(TAG, "sendFriendRequest: " + getCurrentUserId() );
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                Log.w(TAG, "subscribe: " + getCurrentUserId());
                realtimeDatabase.sendFriendRequest(e, getCurrentUserId() , uId);
            }
        });
    }

    @Override
    public Completable cancelFriendRequest(final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                realtimeDatabase.cancelFriendRequest(e, getCurrentUserId(), uId);
            }
        });
    }

    @Override
    public Completable acceptedFriendRequest(final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                realtimeDatabase.acceptedFriendRequest(e, getCurrentUserId(), uId);
            }
        });
    }

    @Override
    public Completable deleteFriend(final String uId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                realtimeDatabase.deleteFriend(e, getCurrentUserId(), uId);
            }
        });
    }

    //onProgress
    public Maybe<ArrayList<Credential>> getReceivedFriendRequest() {
        return Maybe.create(new MaybeOnSubscribe<ArrayList<Credential>>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<ArrayList<Credential>> e) throws Exception {
                realtimeDatabase.getReceivedFriendRequest(e, getCurrentUserId());
            }
        });
    }

    public Maybe<ArrayList<Credential>> getMyFriends() {
        return Maybe.create(new MaybeOnSubscribe<ArrayList<Credential>>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<ArrayList<Credential>> e) throws Exception {
                realtimeDatabase.getMyFriends(e, getCurrentUserId());
            }
        });
    }


    //Firebase Storage
    @Override
    public Completable saveImageToStorage(final Uri resultUri) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                storage.saveImageToStorage(e, getCurrentUserId(), resultUri);
            }
        });
    }

    @Override
    public Completable saveThumbImageToStorage(final byte[] thumbBytes) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                storage.saveThumbImageToStorage(thumbBytes, getCurrentUserId());
            }
        });
    }
}
