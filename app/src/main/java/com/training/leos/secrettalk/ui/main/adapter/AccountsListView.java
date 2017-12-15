package com.training.leos.secrettalk.ui.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountsListView extends RecyclerView.Adapter<AccountsListView.ViewHolder>{

    private ArrayList<Credential> credentials = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;

    public ArrayList<Credential> getCredentials() {
        return credentials;
    }

    public void setCredentials(ArrayList<Credential> credentials) {
        this.credentials = credentials;
        notifyDataSetChanged();
    }

    public AccountsListView(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_account, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Credential data = credentials.get(position);

        if (data.getThumbImageUrl() != "default"){
            Picasso.with(context)
                    .load(data.getThumbImageUrl())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(holder.cimgPhoto, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        if (data.getOnlineStatus()){
            holder.imgOnlineStatus.setImageResource(android.R.drawable.presence_online);
        }else {
            holder.imgOnlineStatus.setImageResource(android.R.drawable.presence_offline);
        }

        holder.tvName.setText(data.getName());
        holder.tvAbout.setText(data.getAbout());
    }

    @Override
    public int getItemCount() {
        return getCredentials().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cimg_item_account_photo) CircleImageView cimgPhoto;
        @BindView(R.id.tv_item_account_name) TextView tvName;
        @BindView(R.id.tv_item_account_about) TextView tvAbout;
        @BindView(R.id.imgv_item_account_online_status) ImageView imgOnlineStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
