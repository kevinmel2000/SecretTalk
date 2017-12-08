package com.training.leos.secrettalk;

import android.net.Uri;

import com.training.leos.secrettalk.data.model.Credential;

public interface AccountDetailContract {
    interface View extends BaseView<Presenter>{
        void showAccountInformation(Credential data);
        void startEditAccountActivity();
        void openImageApp();
        void showProgressBar();
        void hideProgressBar();
    }
    interface Presenter extends BasePresenter{
        void onInitialize(String id);
        void onEditAccountClicked();
        void onThumbClicked();
        void onSaveImage(Uri resultUri);
        void onSaveThumbImage(byte[] thumb_byte);
    }
}
