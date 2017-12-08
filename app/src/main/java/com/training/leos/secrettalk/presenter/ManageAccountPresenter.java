package com.training.leos.secrettalk.presenter;

import com.training.leos.secrettalk.ManageAccountContract;
import com.training.leos.secrettalk.RegisterContract;
import com.training.leos.secrettalk.data.auth.FirebaseAuthentication;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class ManageAccountPresenter implements ManageAccountContract.Presenter {
    public static final String TAG = RegisterPresenter.class.getSimpleName();
    private ManageAccountContract.View view;
    private FirebaseAuthentication authentication;
    private CompositeDisposable compositeDisposable;

    public ManageAccountPresenter(ManageAccountContract.View view) {
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
        compositeDisposable.add(authentication.getCurrentUserCredential()
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
