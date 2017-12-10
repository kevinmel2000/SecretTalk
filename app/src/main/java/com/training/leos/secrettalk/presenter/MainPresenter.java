package com.training.leos.secrettalk.presenter;

import android.util.Log;

import com.training.leos.secrettalk.MainContract;
import com.training.leos.secrettalk.data.firebase.FirebaseAuthDataStore;


public class MainPresenter implements MainContract.Presenter{

    private MainContract.View view;
    private FirebaseAuthDataStore authentication;

    public MainPresenter(MainContract.View view){
        this.view = view;
        this.authentication = FirebaseAuthDataStore.getInstance();
    }

    @Override
    public void onSignOut() {
        view.setConfirmationDialog("Are u sure want to logout?");
    }

    @Override
    public void onSignOutConfirmed() {
        if (authentication.signOut()){
            view.startLoginActivity();
        }
        else{
            view.showToast("Sign Out failed");
        }
    }

    @Override
    public void onCheckActiveUser() {
        Log.w("============", "onCheckActiveUser: " + authentication.isUserSignedIn());
        if (!authentication.isUserSignedIn()){
            view.startLoginActivity();
        }
    }
    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}

