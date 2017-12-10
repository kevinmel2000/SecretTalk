package com.training.leos.secrettalk.data.firebase;

import android.net.Uri;

import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface FirebaseContract {
    String getCurrentUserId();

    boolean isUserSignedIn();
    boolean signOut();
    Completable signIn(Credential credential);
    Completable registration(Credential credential);

    Maybe<Credential> getUserCredential(String uId);
    Completable saveEditedCredential(Credential credential);

    Maybe<ArrayList<Credential>> getAllUsers();

    Completable saveImageToStorage(Uri resultUri);
    Completable saveThumbImageToStorage(byte[] bytes);




    interface Authentication {
        boolean isUserSignedIn();
        boolean signOut();
        String getCurrentUserId();
        Completable signIn(Credential credential);
        Completable registration(Credential credential);
    }
    interface RealtimeDatabase {
        Maybe<Credential> getUserCredential(String id);
        Completable saveUserCredential(Credential credential, String id);
        Completable saveEditedCredential(Credential credential, String uId);
    }
    interface Storage{
        Completable saveImageToStorage(Uri resultUri, String uId);
        Completable saveThumbImageToStorage(byte[] bytes, String uId);
    }
}
