package com.training.leos.secrettalk.data.auth;

import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface AuthenticationContract {

    boolean isUserSignedIn();
    boolean signOut();
    Completable signIn(Credential credential);
    Completable accountCreation(Credential credential);

    Maybe<Credential> getCurrentUserCredential();
    Maybe<Credential> getUserCredential(String id);
}
