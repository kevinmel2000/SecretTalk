package com.training.leos.secrettalk;


import com.training.leos.secrettalk.data.model.Credential;

public interface AllUserDetailContract {
    interface View extends BaseView<Presenter>{
        void showAccountInformation(Credential data);
        void inflateUserStateView(String desc, String state, String tag);
        void disableUserStateView();
        void enableUserStateView();
        void showProgressBar();
        void hideProgressBar();
        void showDeclineRequestButton();

        void hideDeclineRequestButton();
    }
    interface Presenter extends BasePresenter{
        void onInitialize(String id);
        void onCheckUserFriendState(String uId);
        void onRequestClicked(String uId, String tag);
        void onDeclineClicked(String userId);
    }
}
