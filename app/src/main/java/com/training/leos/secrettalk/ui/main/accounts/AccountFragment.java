package com.training.leos.secrettalk.ui.main.accounts;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.training.leos.secrettalk.AccountContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.model.Account;
import com.training.leos.secrettalk.data.model.Credential;
import com.training.leos.secrettalk.presenter.AccountPresenter;
import com.training.leos.secrettalk.ui.main.adapter.UserListView;
import com.training.leos.secrettalk.util.ItemClickContract;
import com.training.leos.secrettalk.util.ItemClickSupport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AccountFragment extends Fragment implements AccountContract.View {
    @BindView(R.id.rv_my_account) RecyclerView rvMyAccount;

    private AccountContract.Presenter presenter;
    private UserListView userListView;
    private ProgressDialog progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, v);

        progressBar = new ProgressDialog(getActivity());

        userListView = new UserListView(getActivity());
        rvMyAccount.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMyAccount.setHasFixedSize(true);
        rvMyAccount.setAdapter(userListView);

        if (presenter == null){
            presenter = new AccountPresenter(this);
        }
        presenter.onInitialize();
        return v;
    }

    public static final String TAG = AccountFragment.class.getSimpleName();
    @Override
    public void showMyAccount(final Credential credential) {
        final ArrayList<Credential> credentials = new ArrayList<>();
        credentials.add(credential);
        Log.w(TAG, "showMyAccount: " + credential.getName());

        userListView.setCredentials(credentials);
        ItemClickSupport.addTo(rvMyAccount).setOnItemClickListener(new ItemClickContract.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                presenter.onAccountClicked(credentials.get(position).getId());
            }
        });
    }

    @Override
    public void showFriendsAccount(ArrayList<Credential> credentials) {

    }

    @Override
    public void startDetailAccountFragment(String uid) {
        Bundle args = new Bundle();
        args.putCharSequence("userId", uid);

        AccountDetailFragmentDialog detailFragment;
        detailFragment = new AccountDetailFragmentDialog();
        detailFragment.setArguments(args);
        detailFragment.show(getChildFragmentManager(),
                AccountDetailFragmentDialog.class.getSimpleName());
    }

    @Override
    public void showProgressBar() {
        progressBar.setCanceledOnTouchOutside(true);
        progressBar.show();
    }
    @Override
    public void hideProgressBar() {
        progressBar.dismiss();
    }


    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(@StringRes int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
