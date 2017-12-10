package com.training.leos.secrettalk.presenter;

import com.training.leos.secrettalk.RegistrationContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.DataConstants;
import com.training.leos.secrettalk.data.firebase.FirebaseAuthDataStore;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;


public class RegistrationPresenter implements RegistrationContract.Presenter {
    public static final String TAG = RegistrationPresenter.class.getSimpleName();
    private RegistrationContract.View view;
    private FirebaseAuthDataStore authentication;
    private CompositeDisposable compositeDisposable;

    public RegistrationPresenter(RegistrationContract.View view) {
        this.view = view;
        this.authentication = FirebaseAuthDataStore.getInstance();
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onRegistering() {
        String name = view.getNameInput();
        String email = view.getEmailInput();
        String password = view.getPasswordInput();
        boolean term = view.isCheckBoxTermChecked();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            view.showToast(R.string.error_field_required);
        }
        else if (!email.contains("@")){
            view.showToast(R.string.error_invalid_email);
        }
        else if (password.length() < 6){
            view.showToast(R.string.error_password_too_short);
        }
        else if (!term) {
            view.showToast(R.string.error_term_agreement);
        } else {
            view.showProgressBar();
            Credential data = new Credential(
                    null,
                    name,
                    email,
                    password,
                    DataConstants.DEFAULT_ABOUT,
                    DataConstants.DEFAULT_IMAGE_URL,
                    DataConstants.DEFAULT_THUMB_IMAGE_URL);
            attemptRegistration(data);
        }
    }

    private void attemptRegistration(final Credential data){
        compositeDisposable.add(authentication.registration(data)
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
                        view.showToast(e.getMessage());
                    }
                })
        );
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

}
