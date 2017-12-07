package com.training.leos.secrettalk;

import com.training.leos.secrettalk.data.model.Credential;

public interface AccountDetailContract {
    interface View extends BaseView<Presenter>{
        void showAccountInformation(Credential data);
        void startEditAccountActivity();
    }
    interface Presenter extends BasePresenter{
        void onInitialize(String id);
        void onEditAccountClicked(String id);
    }
}
