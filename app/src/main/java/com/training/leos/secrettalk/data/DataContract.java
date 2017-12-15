package com.training.leos.secrettalk.data;

import android.net.Uri;

import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface DataContract {
    String getCurrentUserId();

    boolean hasSignedInUser();
    boolean signOut();
    Completable signIn(Credential credential);
    Completable registration(Credential credential);
    Maybe<Credential> getUserCredential(String uId);
    Completable saveEditedCredential(Credential credential);
    Maybe<ArrayList<Credential>> getAllUsers();
    //onProgress
    Maybe<String> getUserFriendRequestState(String uId);
    Completable sendFriendRequest(String uId);
    Completable cancelFriendRequest(String uId);
    Completable acceptedFriendRequest(String uId);
    Completable deleteFriend(String uId);
    Completable saveImageToStorage(Uri resultUri);
    Completable saveThumbImageToStorage(byte[] bytes);


    interface Authentication {

    }

    interface RealtimeDatabase {

    }
    interface Storage{

    }
}
