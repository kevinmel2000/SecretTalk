package com.training.leos.secrettalk.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.training.leos.secrettalk.MainContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.presenter.MainPresenter;
import com.training.leos.secrettalk.ui.allAccount.AllAccountActivity;
import com.training.leos.secrettalk.ui.main.adapter.SectionsPagerAdapter;
import com.training.leos.secrettalk.ui.signIn.SignInActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainContract.View{
    public static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.vp_main_container) ViewPager viewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.toolbar_app) Toolbar toolbar;

    private MainContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_main_title_text);

        SectionsPagerAdapter sectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());
        if (presenter == null){
            presenter = new MainPresenter(this);
        }

        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        presenter.onCheckActiveUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings :
                return true;
            case R.id.action_all_users :
                startActivity(new Intent(this, AllAccountActivity.class));
                return true;
            case R.id.action_logout :
                presenter.onSignOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe();
    }

    @Override
    public void startLoginActivity() {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    @Override
    public void setConfirmationDialog(String message) {
        Snackbar.make(viewPager,
                message,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.sign_out_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSignOutConfirmed();
            }
        }).show();
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
