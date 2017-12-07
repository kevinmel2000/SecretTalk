package com.training.leos.secrettalk.ui.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.training.leos.secrettalk.R;
import com.training.leos.secrettalk.data.model.Credential;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserListView extends RecyclerView.Adapter<UserListView.ViewHolder>{

    private ArrayList<Credential> credentials = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public ArrayList<Credential> getCredentials() {
        return credentials;
    }

    public void setCredentials(ArrayList<Credential> credentials) {
        this.credentials = credentials;
        notifyDataSetChanged();
    }

    public UserListView(Context context) {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_account, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //use Glide/Picasso using url;
        holder.cimgPhoto.setImageResource(R.drawable.ic_account_box_black);
        holder.tvName.setText(credentials.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return getCredentials().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cimg_item_account_photo)
        CircleImageView cimgPhoto;
        @BindView(R.id.tv_item_account_name)
        TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
