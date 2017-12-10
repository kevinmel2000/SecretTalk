package com.training.leos.secrettalk.ui.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.training.leos.secrettalk.RegistrationContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.presenter.RegistrationPresenter;
import com.training.leos.secrettalk.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistrationActivity extends AppCompatActivity implements RegistrationContract.View, View.OnClickListener{
    @BindView(R.id.edt_regist_act_display_name) EditText edtName;
    @BindView(R.id.edt_regist_act_email) EditText edtEmail;
    @BindView(R.id.edt_regist_act_password) EditText edtPassword;
    @BindView(R.id.cb_regist_act_term_checkbox) CheckBox cbTerms;
    @BindView(R.id.btn_regist_act_create_account) Button btnCreateAccount;
    @BindView(R.id.toolbar_app) Toolbar toolbar;
    private ProgressDialog progressBar;

    private RegistrationContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_registration_title_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = new ProgressDialog(this);
        if (presenter == null){
            presenter = new RegistrationPresenter(this);
        }

        btnCreateAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_regist_act_create_account){
            presenter.onRegistering();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showProgressBar() {
        progressBar.setTitle("Creating User");
        progressBar.setMessage("Please wait a moment!");
        progressBar.setCanceledOnTouchOutside(true);
        progressBar.show();
    }

    @Override
    public void hideProgressBar() {
        progressBar.dismiss();
    }

    @Override
    public String getNameInput() {
        return edtName.getText().toString();
    }

    @Override
    public String getEmailInput() {
        return edtEmail.getText().toString();
    }

    @Override
    public String getPasswordInput() {
        return edtPassword.getText().toString();
    }

    @Override
    public boolean isCheckBoxTermChecked() {
        return cbTerms.isChecked();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(@StringRes int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
