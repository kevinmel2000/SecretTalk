package com.training.leos.secrettalk;

import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

public interface FriendsContract {
    interface View extends BaseView<Presenter>{
        void showFriendsList(ArrayList<Credential> credentials);
        void startChatActivity(String uId);
        void showProgressBar();
        void hideProgressBar();
    }
    interface Presenter extends BasePresenter{
        void onInitShowFriends();
        void onAccountClicked(String uId);
    }
}
