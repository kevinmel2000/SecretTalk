package com.training.leos.secrettalk;

import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

public interface AllUserContract {
    interface View extends BaseView<Presenter>{
        void showAllUsers(ArrayList<Credential> credential);
        void startUserDetail(String uId);
        void showProgressBar();
        void hideProgressBar();
    }

    interface Presenter extends BasePresenter{
        void onInitialize();
        void onItemClicked(String uid);
    }
}
