package com.training.leos.secrettalk;

import com.training.leos.secrettalk.data.model.Credential;

/**
 * Created by Leo on 07/12/2017.
 */

public interface ManageAccountContract {
    interface View extends BaseView<Presenter>{
        void inflateInformation(Credential data);
        void finishActivity();
        void showProgressBar();
        void hideProgressBar();
        String getDisplayName();
        String getAbout();
    }
    interface Presenter extends BasePresenter{
        void onInitialize(String userId);
        void onSaveClicked();
    }
}
