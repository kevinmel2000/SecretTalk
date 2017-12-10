package com.training.leos.secrettalk.ui.manageAccount;

import android.app.ProgressDialog;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.training.leos.secrettalk.ManageAccountContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.model.Credential;
import com.training.leos.secrettalk.presenter.ManageAccountPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageAccountActivity extends AppCompatActivity implements ManageAccountContract.View, View.OnClickListener{
    @BindView(R.id.btn_manage_save) Button btnSave;
    @BindView(R.id.edt_manage_display_name) EditText edtName;
    @BindView(R.id.edt_manage_about) EditText edtAbout;
    @BindView(R.id.toolbar_app) Toolbar toolbar;

    private ProgressDialog progressBar;
    private ManageAccountContract.Presenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_title_manage_account_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = new ProgressDialog(this);
        if (presenter == null){
            presenter = new ManageAccountPresenter(this);
        }

        btnSave.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onInitialize(getIntent().getStringExtra("userId"));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_manage_save) {
            presenter.onSaveClicked();
        }
    }

    @Override
    public void inflateInformation(Credential data) {
        edtName.setText(data.getName());
        edtAbout.setText(data.getAbout());
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public String getDisplayName() {
        return edtName.getText().toString();
    }

    @Override
    public String getAbout() {
        return edtAbout.getText().toString();
    }

    @Override
    public void showProgressBar() {
        progressBar.setTitle("Saving Information");
        progressBar.setMessage("Please wait a moment!");
        progressBar.setCanceledOnTouchOutside(true);
        progressBar.show();
    }

    @Override
    public void hideProgressBar() {
        progressBar.dismiss();
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
