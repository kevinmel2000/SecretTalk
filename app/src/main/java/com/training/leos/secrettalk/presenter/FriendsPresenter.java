package com.training.leos.secrettalk.presenter;

import com.training.leos.secrettalk.FriendsContract;
import com.training.leos.secrettalk.data.DataManager;
import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Leo on 11/12/2017.
 */

public class FriendsPresenter implements FriendsContract.Presenter {
    private FriendsContract.View view;
    private DataManager authDataStore;
    private CompositeDisposable compositeDisposable;

    public FriendsPresenter(FriendsContract.View view) {
        this.view = view;
        this.authDataStore = DataManager.getInstance();
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onInitShowFriends() {
        compositeDisposable.add(authDataStore.getMyFriends()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableMaybeObserver<ArrayList<Credential>>() {
            @Override
            public void onSuccess(@NonNull ArrayList<Credential> credentials) {
                view.showFriendsList(credentials);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                view.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    @Override
    public void onAccountClicked(String uId) {
        view.startChatActivity(uId);
    }


    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }
}
