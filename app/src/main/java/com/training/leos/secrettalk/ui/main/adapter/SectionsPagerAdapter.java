package com.training.leos.secrettalk.ui.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.training.leos.secrettalk.ui.main.accounts.AccountsFragment;
import com.training.leos.secrettalk.ui.main.friends.FriendsFragment;
import com.training.leos.secrettalk.ui.main.posts.PostFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public static final int SECTION_COUNT = 3;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AccountsFragment();
            case 1:
                return new FriendsFragment();
            case 2:
                return new PostFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return SECTION_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "ACCOUNTS";
            case 1:
                return "FRIENDS";
            case 2:
                return "POSTS";
        }
        return null;
    }
}
