package com.training.leos.secrettalk.ui.allUsers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.training.leos.secrettalk.AllUserContract;
import com.training.leos.secrettalk.AllUserDetailContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.model.Credential;
import com.training.leos.secrettalk.presenter.AllUserPresenter;
import com.training.leos.secrettalk.ui.main.accounts.AccountDetailFragmentDialog;
import com.training.leos.secrettalk.ui.main.adapter.UserListView;
import com.training.leos.secrettalk.util.ItemClickContract;
import com.training.leos.secrettalk.util.ItemClickSupport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllUsersActivity extends AppCompatActivity implements AllUserContract.View{
    @BindView(R.id.rv_all_users) RecyclerView rvAllUsers;
    @BindView(R.id.toolbar_app) Toolbar toolbar;

    private ProgressDialog progressBar;

    private AllUserContract.Presenter presenter;
    private UserListView userListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = new ProgressDialog(this);
        userListView = new UserListView(this);
        if (presenter == null){
            presenter = new AllUserPresenter(this);
        }

        rvAllUsers.setLayoutManager(new LinearLayoutManager(this));
        rvAllUsers.setHasFixedSize(true);
        rvAllUsers.setAdapter(userListView);

        presenter.onInitialize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showAllUsers(final ArrayList<Credential> credentials) {
        userListView.setCredentials(credentials);
        ItemClickSupport.addTo(rvAllUsers).setOnItemClickListener(
                new ItemClickContract.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        presenter.onItemClicked(credentials.get(position).getId());
                    }
                }
        );
    }

    @Override
    public void startUserDetail(String uId) {
        Bundle args = new Bundle();
        args.putCharSequence("userId", uId);

        AllUserDetailFragment detailFragment;
        detailFragment = new AllUserDetailFragment();
        detailFragment.setArguments(args);
        detailFragment.show(
                getSupportFragmentManager(),
                AllUserDetailFragment.class.getSimpleName());
    }

    @Override
    public void showProgressBar() {
        progressBar.setMessage("Loading");
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
