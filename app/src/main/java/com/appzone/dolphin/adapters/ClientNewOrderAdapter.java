package com.appzone.dolphin.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appzone.dolphin.Models.ClientOrderModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.tags.Tags;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ClientNewOrderAdapter extends RecyclerView.Adapter<ClientNewOrderAdapter.MyHolder> {

    private Context context;
    private List<ClientOrderModel> clientOrderModelList;

    public ClientNewOrderAdapter(Context context, List<ClientOrderModel> clientOrderModelList) {
        this.context = context;
        this.clientOrderModelList = clientOrderModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.client_new_order_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        ClientOrderModel clientOrderModel = clientOrderModelList.get(position);
        holder.BindData(clientOrderModel);

    }

    @Override
    public int getItemCount() {
        return clientOrderModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private RoundedImageView image;
        private TextView tv_not_date,tv_service;

        public MyHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tv_not_date = itemView.findViewById(R.id.tv_not_date);
            tv_service = itemView.findViewById(R.id.tv_service);

        }

        private void BindData(ClientOrderModel clientOrderModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_PATH+clientOrderModel.getServices_logo())).into(image);
            String lang = Locale.getDefault().getLanguage();
            if (lang.equals("ar"))
            {
                tv_service.setText(clientOrderModel.getAr_user_specialization());

            }else
            {
                tv_service.setText(clientOrderModel.getEn_user_specialization());

            }
            tv_not_date.setText(clientOrderModel.getDate_notification());

        }
    }


}
