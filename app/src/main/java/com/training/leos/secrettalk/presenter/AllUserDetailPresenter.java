package com.training.leos.secrettalk.presenter;

import com.training.leos.secrettalk.AllUserDetailContract;
import com.training.leos.secrettalk.data.firebase.FirebaseAuthDataStore;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class AllUserDetailPresenter implements AllUserDetailContract.Presenter {
    private AllUserDetailContract.View view;
    private CompositeDisposable compositeDisposable;
    private FirebaseAuthDataStore authentication;

    public AllUserDetailPresenter(AllUserDetailContract.View view){
        this.view = view;
        this.authentication = FirebaseAuthDataStore.getInstance();
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

    @Override
    public void onCheckUserFriendState(String uId){
        compositeDisposable.add(authentication.getUserFriendRequestState(uId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<String>() {
                    @Override
                    public void onSuccess(@NonNull String s) {
                        if (s.equals("friended")){
                            view.inflateUserStateView(
                                    "This person added as your friend :D",
                                    "Remove Friend",
                                    "friended");
                        }
                        else if (s.equals("sent")){
                            view.inflateUserStateView(
                                    "Request friend has sent to this person",
                                    "Cancel Friend Request",
                                    "sent");
                        }
                        else if (s.equals("notFriend")){
                            view.inflateUserStateView(
                                    "You are not friend with this person yet",
                                    "Send Friend Request",
                                    "notFriend");
                        }
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
    public void onRequestClicked(final String uId, final String tag) {
        view.disableUserStateView();
        Completable completable = null;

        if (tag.equals("notFriend")){
            completable = authentication.sendFriendRequest(uId);
        }else if (tag.equals("sent")){
            completable = authentication.cancelFriendRequest(uId);
        }else if (tag.equals("friended")){
            completable = authentication.deleteFriend(uId);
        }

        compositeDisposable.add(completable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        view.enableUserStateView();
                        onCheckUserFriendState(uId);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.enableUserStateView();
                    }
                }));
    }
}
