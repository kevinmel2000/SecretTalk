package com.training.leos.secrettalk.presenter;

import com.training.leos.secrettalk.ManageAccountContract;
import com.training.leos.secrettalk.data.DataManager;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class ManageAccountPresenter implements ManageAccountContract.Presenter {
    private ManageAccountContract.View view;
    private CompositeDisposable compositeDisposable;
    private DataManager authentication;

    public ManageAccountPresenter(ManageAccountContract.View view) {
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
                        view.inflateInformation(credential);
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
    public void onSaveClicked() {
        view.showProgressBar();
        Credential credential = new Credential();
        credential.setName(view.getDisplayName());
        credential.setAbout(view.getAbout());

        compositeDisposable.add(authentication.saveEditedCredential(credential)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        view.hideProgressBar();
                        view.finishActivity();
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
