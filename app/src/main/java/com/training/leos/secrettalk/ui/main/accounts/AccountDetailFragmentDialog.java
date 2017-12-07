package com.training.leos.secrettalk.ui.main.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.training.leos.secrettalk.AccountDetailContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.model.Credential;
import com.training.leos.secrettalk.presenter.AccountDetailPresenter;
import com.training.leos.secrettalk.ui.manageAccount.ManageAccountActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountDetailFragmentDialog extends DialogFragment implements AccountDetailContract.View {
    @BindView(R.id.cimg_account_detail_thumb)
    CircleImageView cimgThumbImage;
    @BindView(R.id.tv_account_detail_name)
    TextView tvName;
    @BindView(R.id.tv_account_detail_email)
    TextView tvEmail;
    @BindView(R.id.tv_account_detail_about_me)
    TextView tvAbout;
    @BindView(R.id.btn_account_detail_edit)
    ImageButton btnEdit;

    private AccountDetailContract.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.fragment_dialog_account_detail,
                container,
                false);
        ButterKnife.bind(this, v);

        if (presenter == null) {
            presenter = new AccountDetailPresenter(this);
        }

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.w("Akun Detail", "onActivityCreated: " + getArguments().getString("userId"));
        presenter.onInitialize(getArguments().getString("userId"));
    }

    @Override
    public void showAccountInformation(Credential data) {
       /* Glide.with(this)
                .asBitmap()
                .load(data.getThumbImageUrl())
                .into(cimgThumbImage);*/

        cimgThumbImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_box_black, null));
        tvName.setText(data.getName());
        tvEmail.setText(data.getEmail());
        tvAbout.setText(data.getEmail());
    }

    @Override
    public void startEditAccountActivity() {
        Intent intent = new Intent(getActivity(), ManageAccountActivity.class);
        getActivity().startActivity(intent);
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
