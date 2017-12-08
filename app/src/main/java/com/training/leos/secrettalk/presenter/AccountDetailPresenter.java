package com.training.leos.secrettalk.presenter;

import android.net.Uri;
import android.util.Log;

import com.training.leos.secrettalk.AccountDetailContract;
import com.training.leos.secrettalk.data.auth.FirebaseAuthentication;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class AccountDetailPresenter implements AccountDetailContract.Presenter{
    private AccountDetailContract.View view;
    private FirebaseAuthentication authentication;
    private CompositeDisposable compositeDisposable;

    public AccountDetailPresenter(AccountDetailContract.View view) {
        this.view = view;
        this.authentication = FirebaseAuthentication.getInstance();
        this.compositeDisposable = new CompositeDisposable();
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
                        view.showToast("Save Succeeds");
                        //a method to get the data
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
