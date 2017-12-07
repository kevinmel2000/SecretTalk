package com.training.leos.secrettalk;

public interface MainContract {
    interface View extends BaseView<Presenter>{
        void setConfirmationDialog(String message);
        void startLoginActivity();
    }
    interface Presenter extends BasePresenter{
        void onSignOut();
        void onSignOutConfirmed();
        void onCheckActiveUser();
    }
}
