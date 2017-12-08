package com.training.leos.secrettalk.data.auth;

import android.net.Uri;

import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface AuthenticationContract {

    boolean isUserSignedIn();
    boolean signOut();
    Completable signIn(Credential credential);
    Completable accountCreation(Credential credential);
    Completable saveEditedCredential(Credential credential);
    Completable saveImageToStorage(Uri resultUri);
    Completable saveThumbImageToStorage(byte[] bytes);

    Maybe<Credential> getCurrentUserCredential();
    Maybe<Credential> getUserCredential(String id);
}
