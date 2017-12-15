package com.training.leos.secrettalk.presenter;

import android.util.Log;

import com.training.leos.secrettalk.AllUserDetailContract;
import com.training.leos.secrettalk.data.DataManager;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class AllAccountDetailPresenter implements AllUserDetailContract.Presenter {
    private AllUserDetailContract.View view;
    private CompositeDisposable compositeDisposable;
    private DataManager authentication;

    public AllAccountDetailPresenter(AllUserDetailContract.View view){
        this.view = view;
        this.authentication = DataManager.getInstance();
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
    public void onInitialize(String uId) {
        compositeDisposable.add(authentication.getUserCredential(uId)
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

    private static final String TAG = AllAccountDetailPresenter.class.getSimpleName();
    @Override
    public void onCheckUserFriendState(String uId){
        compositeDisposable.add(authentication.getUserFriendRequestState(uId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<String>() {
                    @Override
                    public void onSuccess(@NonNull String s) {
                        Log.w(TAG, "onSuccess: " + s);
                        switch (s) {
                            case "friended":
                                view.inflateUserStateView(
                                        "This person added as your friend :D",
                                        "Unfriend this person",
                                        "friended");
                                break;
                            case "sent":
                                view.inflateUserStateView(
                                        "Request friend has sent to this person",
                                        "Cancel Friend Request",
                                        "sent");
                                break;
                            case "received":
                                view.inflateUserStateView(
                                        "You have friend request from this person",
                                        "Accept Friend Request",
                                        "received");
                                view.showDeclineRequestButton();
                                break;
                            case "notFriend":
                                view.inflateUserStateView(
                                        "You are not friend with this person yet",
                                        "Send Friend Request",
                                        "notFriend");
                                break;
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.w(TAG, "onComplete: ");
                    }
                }));
    }

    @Override
    public void onRequestClicked(final String uId, String tag) {
        view.disableUserStateView();
        Completable completable = null;

        switch (tag) {
            case "notFriend":
                completable = authentication.sendFriendRequest(uId);
                break;
            case "sent":
                completable = authentication.cancelFriendRequest(uId);
                break;
            case "received" :
                completable = authentication.acceptedFriendRequest(uId);
                break;
            case "friended":
                completable = authentication.deleteFriend(uId);
                break;
            default :
                break;
        }

        if (completable != null) {
            compositeDisposable.add(completable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            view.enableUserStateView();
                            view.hideDeclineRequestButton();

                            onCheckUserFriendState(uId);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            view.enableUserStateView();
                        }
                    })
            );
        }else {
            view.showToast("Something wrong");
        }
    }

    @Override
    public void onDeclineClicked(final String uId) {
        view.disableUserStateView();
        compositeDisposable.add(authentication.cancelFriendRequest(uId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        view.enableUserStateView();
                        view.hideDeclineRequestButton();
                        onCheckUserFriendState(uId);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.enableUserStateView();
                    }
                })
        );
    }
}
