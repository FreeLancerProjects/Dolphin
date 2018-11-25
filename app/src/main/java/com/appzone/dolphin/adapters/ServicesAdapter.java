package com.appzone.dolphin.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appzone.dolphin.Models.ServiceModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.fragments.Fragment_Home;
import com.appzone.dolphin.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.MyHolder>{
    private Context context;
    private Fragment_Home fragment_home;
    private List<ServiceModel> serviceModelList;

    public ServicesAdapter(Context context, Fragment_Home fragment_home, List<ServiceModel> serviceModelList) {
        this.context = context;
        this.fragment_home = fragment_home;
        this.serviceModelList = serviceModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        ServiceModel serviceModel = serviceModelList.get(position);
        holder.BindData(serviceModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceModel serviceModel = serviceModelList.get(holder.getAdapterPosition());
                fragment_home.setItem(serviceModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView tv_name;
        public MyHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tv_name = itemView.findViewById(R.id.tv_name);
        }

        public void BindData(ServiceModel serviceModel)
        {
            String lang = Locale.getDefault().getLanguage();
            if (lang.equals("ar"))
            {
                tv_name.setText(serviceModel.getAr_services_title());
            }else
                {
                    tv_name.setText(serviceModel.getEn_services_title());
                }

            Picasso.with(context).load(Uri.parse(Tags.IMAGE_PATH+serviceModel.getServices_logo())).into(image);
        }
    }
}
