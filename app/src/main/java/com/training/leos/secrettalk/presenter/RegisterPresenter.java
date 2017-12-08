package com.training.leos.secrettalk.presenter;

import android.util.Log;

import com.training.leos.secrettalk.RegisterContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.auth.FirebaseAuthentication;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;


public class RegisterPresenter implements RegisterContract.Presenter {
    public static final String TAG = RegisterPresenter.class.getSimpleName();
    private RegisterContract.View view;
    private FirebaseAuthentication authentication;
    private CompositeDisposable compositeDisposable;

    public RegisterPresenter(RegisterContract.View view) {
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
    public void onAccountCreation() {
        String name = view.getNameInput();
        String email = view.getEmailInput();
        String password = view.getPasswordInput();
        boolean term = view.isCheckBoxTermChecked();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            view.showToast(R.string.error_field_required);
        } else if (!term) {
            view.showToast(R.string.error_term_agreement);
        } else {
            view.showProgressBar();
            Credential credential = new Credential();
            credential.setName(name);
            credential.setEmail(email);
            credential.setPassword(password);

            attemptAccountCreation(credential);
        }
    }

    public void attemptAccountCreation(final Credential credential){
        compositeDisposable.add((Disposable) authentication.accountCreation(credential)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        view.hideProgressBar();
                        view.startMainActivity();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.hideProgressBar();
                        Log.w(TAG, "onError: " + e.getMessage());
                        view.showToast("User Creation Failed");
                    }
                }));
    }
}
