package com.training.leos.secrettalk;

public interface RegistrationContract {
    interface View extends BaseView<Presenter>{
        void startMainActivity();
        void showProgressBar();
        void hideProgressBar();
        String getNameInput();
        String getEmailInput();
        String getPasswordInput();
        boolean isCheckBoxTermChecked();
    }
    interface Presenter extends BasePresenter{
        void onRegistering();
    }
}
