package com.training.leos.secrettalk.ui.main.accounts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.training.leos.secrettalk.AccountDetailContract;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.model.Credential;
import com.training.leos.secrettalk.presenter.AccountDetailPresenter;
import com.training.leos.secrettalk.ui.manageAccount.ManageAccountActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class AccountDetailFragmentDialog extends DialogFragment implements AccountDetailContract.View {
    public static final int IMAGE_PICK = 11;
    public static final String TAG = AccountDetailFragmentDialog.class.getSimpleName();

    @BindView(R.id.cimg_account_detail_thumb) CircleImageView cimgThumbImage;
    @BindView(R.id.tv_account_detail_name) TextView tvName;
    @BindView(R.id.tv_account_detail_email) TextView tvEmail;
    @BindView(R.id.tv_account_detail_about_me) TextView tvAbout;
    @BindView(R.id.btn_account_detail_edit) ImageButton btnEdit;

    private ProgressDialog progressBar;
    private AccountDetailContract.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.fragment_dialog_account_detail,
                container,
                false);
        ButterKnife.bind(this, v);
        progressBar = new ProgressDialog(getActivity());
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEditAccountClicked();
            }
        });
        cimgThumbImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onThumbClicked();
            }
        });
        if (presenter == null) {
            presenter = new AccountDetailPresenter(this);
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.w("Akun Detail", "onActivityCreated: " + getArguments().getString("userId"));
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onInitialize(getArguments().getString("userId"));
    }

    @Override
    public void showAccountInformation(Credential data) {
        String thumbImageUrl = data.getThumbImageUrl();
        if (thumbImageUrl != "default"){
            Glide.with(this)
                    .asBitmap()
                    .load(data.getImageUrl())
                    .into(cimgThumbImage);
        }

        tvName.setText(data.getName());
        tvEmail.setText(data.getEmail());
        tvAbout.setText(data.getAbout());
    }

    @Override
    public void startEditAccountActivity() {
        Intent intent = new Intent(getActivity(), ManageAccountActivity.class);
        startActivity(intent);
    }

    @Override
    public void openImageApp() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_PICK);
    }


    //i think this part really breaking my MVP architecture, o.o
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK && resultCode == RESULT_OK) {
            Uri resultUri = data.getData();
            CropImage.activity(resultUri)
                    .start(getContext(), this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                File thumb_path = new File(resultUri.getPath());

                try {
                    Bitmap thumb_bitmap = new Compressor(getContext())
                            .setMaxHeight(150)
                            .setMaxWidth(150)
                            .setQuality(50)
                            .compressToBitmap(thumb_path);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] thumb_byte = baos.toByteArray();

                    presenter.onSaveThumbImage(thumb_byte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                presenter.onSaveImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showToast(result.getError().toString());
            }
        }
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
