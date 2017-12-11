package com.training.leos.secrettalk.ui.main.accounts;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.training.leos.secrettalk.AccountContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.model.Credential;
import com.training.leos.secrettalk.presenter.AccountsPresenter;
import com.training.leos.secrettalk.ui.allAccount.AllAccountDetailFragmentDialog;
import com.training.leos.secrettalk.ui.main.adapter.AccountsListView;
import com.training.leos.secrettalk.util.ItemClickContract;
import com.training.leos.secrettalk.util.ItemClickSupport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AccountsFragment extends Fragment implements AccountContract.View {
    public static final String TAG = AccountsFragment.class.getSimpleName();
    @BindView(R.id.rv_my_account) RecyclerView rvMyAccount;
    @BindView(R.id.rv_received_friend_request_account) RecyclerView rvReceivedRequest;
    @BindView(R.id.rv_my_friends_account) RecyclerView rvMyFriends;

    private AccountContract.Presenter presenter;
    private AccountsListView accountsListView;
    private ProgressDialog progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        ButterKnife.bind(this, v);
        progressBar = new ProgressDialog(getActivity());

        rvMyAccount.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMyAccount.setHasFixedSize(true);

        rvReceivedRequest.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvReceivedRequest.setHasFixedSize(true);

        rvMyFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMyFriends.setHasFixedSize(true);

        if (presenter == null) {
            presenter = new AccountsPresenter(this);
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onInitMyAccount();
        presenter.onInitReceivedFriendRequest();
        presenter.onInitFriendsAccount();
    }

    @Override
    public void showMyAccount(final Credential credential) {
        accountsListView = new AccountsListView(getActivity());
        rvMyAccount.setAdapter(accountsListView);

        final ArrayList<Credential> credentials = new ArrayList<>();
        credentials.add(credential);

        accountsListView.setCredentials(credentials);
        ItemClickSupport.addTo(rvMyAccount)
                .setOnItemClickListener(new ItemClickContract.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        presenter.onAccountClicked(credentials.get(position).getId());
                    }
                });
    }

    @Override
    public void startAccountDetailFragment(String uid) {
        Bundle args = new Bundle();
        args.putCharSequence("userId", uid);

        AccountDetailFragmentDialog detailFragment;
        detailFragment = new AccountDetailFragmentDialog();
        detailFragment.setArguments(args);
        detailFragment.show(
                getChildFragmentManager(),
                AccountDetailFragmentDialog.class.getSimpleName());
    }



    @Override
    public void showReceivedFriendsRequest(final ArrayList<Credential> credentials){
        accountsListView = new AccountsListView(getActivity());
        rvReceivedRequest.setAdapter(accountsListView);

        accountsListView.setCredentials(credentials);
        ItemClickSupport.addTo(rvReceivedRequest)
                .setOnItemClickListener(new ItemClickContract.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        presenter.onUserAccountClicked(credentials.get(position).getId());
                    }
                });
    }

    @Override
    public void showFriendsAccount(final ArrayList<Credential> credentials) {
        accountsListView = new AccountsListView(getActivity());
        rvMyFriends.setAdapter(accountsListView);

        accountsListView.setCredentials(credentials);
        ItemClickSupport.addTo(rvMyFriends)
                .setOnItemClickListener(new ItemClickContract.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        presenter.onUserAccountClicked(credentials.get(position).getId());
                    }
                });
    }

    @Override
    public void startAllAccountDetailFragment(String uid) {
        Bundle args = new Bundle();
        args.putCharSequence("userId", uid);

        AllAccountDetailFragmentDialog detailFragment;
        detailFragment = new AllAccountDetailFragmentDialog();
        detailFragment.setArguments(args);
        detailFragment.show(
                getChildFragmentManager(),
                AllAccountDetailFragmentDialog.class.getSimpleName());
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(@StringRes int message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe();
    }
}
