package com.training.leos.secrettalk.data.firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.training.leos.secrettalk.data.model.Credential;

import io.reactivex.CompletableEmitter;

public class FirebaseAuthentication {
    private static FirebaseAuthentication instance;
    private FirebaseRealtimeDatabase firebaseRealtimeDatabase;
    private FirebaseAuth firebaseAuth;

    public static FirebaseAuthentication getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthentication();
        }
        return instance;
    }

    private FirebaseAuthentication() {
        if (firebaseAuth == null) {
            this.firebaseAuth = FirebaseAuth.getInstance();
        }
    }

    public FirebaseUser getCurrenUser() {
        return firebaseAuth.getCurrentUser();
    }

    //
    public String getCurrentUserId() {
        return getCurrenUser().getUid();
    }

    public boolean hasSignedInUser() {
        return getCurrenUser() != null;
    }

    public boolean signOut() {
        firebaseAuth.signOut();
        return true;
    }

    public void signIn(final CompletableEmitter e, Credential credential) {
        firebaseAuth.signInWithEmailAndPassword(
                credential.getEmail(),
                credential.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveTokenToDatabase();
                            e.onComplete();
                        } else {
                            e.onError(task.getException());
                        }
                    }
                });
    }

    public void registration(final CompletableEmitter e, final Credential credential) {
        firebaseAuth.createUserWithEmailAndPassword(
                credential.getEmail(),
                credential.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseRealtimeDatabase = FirebaseRealtimeDatabase.getInstance();
                            firebaseRealtimeDatabase.saveCreatedCredential(e, getCurrentUserId(), credential);
                            saveTokenToDatabase();
                        } else {
                            e.onError(task.getException());
                        }
                    }
                });
    }

    private void saveTokenToDatabase() {
        firebaseRealtimeDatabase = FirebaseRealtimeDatabase.getInstance();
        firebaseRealtimeDatabase.saveDeviceToken(getCurrentUserId());
    }
}
