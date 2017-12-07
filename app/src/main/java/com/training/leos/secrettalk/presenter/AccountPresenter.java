package com.training.leos.secrettalk.presenter;

import android.util.Log;

import com.training.leos.secrettalk.AccountContract;
import com.training.leos.secrettalk.data.auth.FirebaseAuthentication;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class AccountPresenter implements AccountContract.Presenter {
    private AccountContract.View view;
    private FirebaseAuthentication authentication;
    private CompositeDisposable compositeDisposable;

    public AccountPresenter(AccountContract.View view){
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
    public void onInitialize() {
        view.showProgressBar();
        compositeDisposable.add(authentication.getCurrentUserCredential()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<Credential>() {
                    @Override
                    public void onSuccess(@NonNull Credential credential) {
                        view.hideProgressBar();
                        view.showMyAccount(credential);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.hideProgressBar();
                        view.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.w("AccountPresenter", "onComplete: ");
                    }
                })
        );
    }
    @Override
    public void onAccountClicked(String id) {
        view.startDetailAccountFragment(id);
    }
}
