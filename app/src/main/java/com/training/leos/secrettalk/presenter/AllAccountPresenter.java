package com.training.leos.secrettalk.presenter;

import com.training.leos.secrettalk.AllUserContract;
import com.training.leos.secrettalk.data.firebase.FirebaseAuthDataStore;
import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Leo on 09/12/2017.
 */

public class AllAccountPresenter implements AllUserContract.Presenter{
    private AllUserContract.View view;
    private CompositeDisposable compositeDisposable;
    private FirebaseAuthDataStore authentication;

    public AllAccountPresenter(AllUserContract.View view) {
        this.view = view;
        this.compositeDisposable = new CompositeDisposable();
        this.authentication = FirebaseAuthDataStore.getInstance();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void onInitialize() {
        view.showProgressBar();
        compositeDisposable.add(authentication.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<ArrayList<Credential>>() {
                    @Override
                    public void onSuccess(@NonNull ArrayList<Credential> credentials) {
                        view.hideProgressBar();
                        view.showAllUsers(credentials);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.hideProgressBar();
                        view.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    @Override
    public void onItemClicked(String uid) {
        view.startUserDetail(uid);
    }
}
