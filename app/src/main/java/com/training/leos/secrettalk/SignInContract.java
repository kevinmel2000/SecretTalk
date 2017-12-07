package com.training.leos.secrettalk;

import android.support.annotation.StringRes;

public interface SignInContract {
    interface View extends BaseView<Presenter>{
        void startAccountCreationAcitivty();
        void startMainActivity();
        void showProgressBar();
        void hideProgressBar();
        String getEmail();
        String getPassword();

    }

    interface Presenter extends BasePresenter{
        void onSignIn();
        void onAccountCreationButtonClicked();
    }
}
