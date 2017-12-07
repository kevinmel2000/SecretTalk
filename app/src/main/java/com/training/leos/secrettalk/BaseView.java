package com.training.leos.secrettalk;

import android.support.annotation.StringRes;

/**
 * Created by Leo on 02/12/2017.
 */

public interface BaseView<T> {
    //void setPresenter(T presenter);
    void showToast(String message);
    void showToast(@StringRes int message);
}
