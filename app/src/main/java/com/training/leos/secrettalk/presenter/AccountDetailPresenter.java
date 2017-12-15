package com.training.leos.secrettalk.presenter;

import android.net.Uri;

import com.training.leos.secrettalk.AccountDetailContract;
import com.training.leos.secrettalk.data.DataManager;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class AccountDetailPresenter implements AccountDetailContract.Presenter{
    private AccountDetailContract.View view;
    private CompositeDisposable compositeDisposable;
    private DataManager authentication;

    public AccountDetailPresenter(AccountDetailContract.View view) {
        this.view = view;
        this.compositeDisposable = new CompositeDisposable();
        this.authentication = DataManager.getInstance();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void onInitialize(String id) {
        compositeDisposable.add(authentication.getUserCredential(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<Credential>() {
                    @Override
                    public void onSuccess(@NonNull Credential credential) {
                        view.showAccountInformation(credential);
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
    public void onEditAccountClicked() {
        view.startEditAccountActivity();
    }

    @Override
    public void onThumbClicked() {
        view.openImageApp();
    }

    @Override
    public void onSaveImage(Uri resultUri) {
        compositeDisposable.add(authentication.saveImageToStorage(resultUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        //a method to get the data
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showToast(e.getMessage());
                    }
                })
        );
    }

    @Override
    public void onSaveThumbImage(byte[] thumb_byte) {
        view.showProgressBar();
        compositeDisposable.add(authentication.saveThumbImageToStorage(thumb_byte)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        view.hideProgressBar();
                        view.reloadInformation();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.hideProgressBar();
                        view.showToast(e.getMessage());
                    }
                })
        );
    }
}
