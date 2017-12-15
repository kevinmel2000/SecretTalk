package com.training.leos.secrettalk.presenter;

import com.training.leos.secrettalk.AccountContract;
import com.training.leos.secrettalk.data.DataManager;
import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class AccountsPresenter implements AccountContract.Presenter {
    private AccountContract.View view;
    private CompositeDisposable compositeDisposable;
    private DataManager authentication;

    public AccountsPresenter(AccountContract.View view){
        this.view = view;
        this.authentication = DataManager.getInstance();
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onInitMyAccount() {
        String uId = authentication.getCurrentUserId();
        compositeDisposable.add(authentication.getUserCredential(uId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<Credential>() {
                    @Override
                    public void onSuccess(@NonNull Credential credential) {
                        view.showMyAccount(credential);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    @Override
    public void onInitReceivedFriendRequest(){
        compositeDisposable.add(authentication.getReceivedFriendRequest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<ArrayList<Credential>>() {
                    @Override
                    public void onSuccess(@NonNull ArrayList<Credential> credentials) {
                        view.showReceivedFriendsRequest(credentials);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    @Override
    public void onInitFriendsAccount(){
        compositeDisposable.add(authentication.getMyFriends()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<ArrayList<Credential>>() {
                    @Override
                    public void onSuccess(@NonNull ArrayList<Credential> credentials) {
                        view.showFriendsAccount(credentials);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    @Override
    public void onAccountClicked(String uId) {
        view.startAccountDetailFragment(uId);
    }

    @Override
    public void onUserAccountClicked(String uId) {
        view.startAllAccountDetailFragment(uId);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

}
