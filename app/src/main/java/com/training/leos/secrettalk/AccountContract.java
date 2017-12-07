package com.training.leos.secrettalk;

import com.training.leos.secrettalk.data.model.Account;
import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

public interface AccountContract {
    interface View extends BaseView<Presenter>{
        void showMyAccount(Credential credential);
        void showFriendsAccount(ArrayList<Credential> credentials);
        void startDetailAccountFragment(String uid);
        void showProgressBar();
        void hideProgressBar();
    }
    interface Presenter extends BasePresenter{
        void onInitialize();
        void onAccountClicked(String id);
    }
}
