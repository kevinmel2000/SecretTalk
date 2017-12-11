package com.training.leos.secrettalk.ui.main.friends;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.ui.main.adapter.AccountsListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsFragment extends Fragment {
    @BindView(R.id.rv_my_friends_to_chat) RecyclerView rvMyFriends;

    private AccountsListView accountsListView;

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
        rvMyFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMyFriends.setHasFixedSize(true);
        rvMyFriends.setAdapter(accountsListView);

        return v;
    }

}
