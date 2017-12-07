package com.training.leos.secrettalk.presenter;

import com.training.leos.secrettalk.MainContract;
import com.training.leos.secrettalk.data.auth.AuthenticationContract;
import com.training.leos.secrettalk.data.auth.FirebaseAuthentication;


public class MainPresenter implements MainContract.Presenter{

    private MainContract.View view;
    private AuthenticationContract authentication;

    public MainPresenter(MainContract.View view){
        this.view = view;
        this.authentication = FirebaseAuthentication.getInstance();
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
        if (authentication.isUserSignedIn()){

        }else {
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

