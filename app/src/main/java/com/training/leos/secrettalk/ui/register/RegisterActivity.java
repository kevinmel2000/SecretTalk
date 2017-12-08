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

import com.training.leos.secrettalk.RegisterContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.presenter.RegisterPresenter;
import com.training.leos.secrettalk.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View{
    @BindView(R.id.edt_regist_act_display_name) EditText edtName;
    @BindView(R.id.edt_regist_act_email) EditText edtEmail;
    @BindView(R.id.edt_regist_act_password) EditText edtPassword;
    @BindView(R.id.cb_regist_act_term_checkbox) CheckBox cbTerms;
    @BindView(R.id.btn_regist_act_create_account) Button btnCreateAccount;
    @BindView(R.id.toolbar_account_creation) Toolbar toolbar;
    private ProgressDialog progressBar;

    private RegisterContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = new ProgressDialog(this);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAccountCreation();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter == null){
            presenter = new RegisterPresenter(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
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
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
