package com.training.leos.secrettalk;

import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

public interface AccountContract {
    interface View extends BaseView<Presenter>{
        void showMyAccount(Credential credential);

        void showReceivedFriendsRequest(ArrayList<Credential> credentials);

        void showFriendsAccount(ArrayList<Credential> credentials);
        void startAccountDetailFragment(String uid);

        void startAllAccountDetailFragment(String uid);

        void showProgressBar();
        void hideProgressBar();
    }
    interface Presenter extends BasePresenter{
        void onInitMyAccount();

        void onInitReceivedFriendRequest();

        void onInitFriendsAccount();

        void onAccountClicked(String id);

        void onUserAccountClicked(String id);
    }
}
