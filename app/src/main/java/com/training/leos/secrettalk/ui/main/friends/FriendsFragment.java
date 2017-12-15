package com.training.leos.secrettalk.ui.main.friends;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.training.leos.secrettalk.FriendsContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.model.Credential;
import com.training.leos.secrettalk.presenter.FriendsPresenter;
import com.training.leos.secrettalk.ui.chat.ChatActivity;
import com.training.leos.secrettalk.ui.main.adapter.AccountsListView;
import com.training.leos.secrettalk.util.ItemClickContract;
import com.training.leos.secrettalk.util.ItemClickSupport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsFragment extends Fragment implements FriendsContract.View{
    @BindView(R.id.rv_my_friends_to_chat) RecyclerView rvMyFriends;

    private AccountsListView accountsListView;
    private FriendsContract.Presenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, v);

        accountsListView = new AccountsListView(getActivity());
        if (presenter == null){
            presenter = new FriendsPresenter(this);
        }

        rvMyFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMyFriends.setHasFixedSize(true);
        rvMyFriends.setAdapter(accountsListView);

        presenter.onInitShowFriends();

        return v;
    }

    @Override
    public void showFriendsList(final ArrayList<Credential> credentials) {
        accountsListView.setCredentials(credentials);
        ItemClickSupport.addTo(rvMyFriends).setOnItemClickListener(new ItemClickContract.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                   presenter.onAccountClicked(credentials.get(position).getId());
            }
        });
    }

    @Override
    public void startChatActivity(String uId) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("userId", uId);
        startActivity(intent);
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void showToast(@StringRes int message) {

    }
}
