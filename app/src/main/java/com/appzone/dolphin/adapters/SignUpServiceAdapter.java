package com.appzone.dolphin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appzone.dolphin.Models.ServiceModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.fragments.Fragment_Profile;
import com.appzone.dolphin.activities.signup_activity.fragments.FragmentTechnicalSignUp;

import java.util.List;
import java.util.Locale;

public class SignUpServiceAdapter extends RecyclerView.Adapter<SignUpServiceAdapter.MyHolder> {

    private Context context;
    private List<ServiceModel> serviceModelList;
    private Fragment fragment;

    public SignUpServiceAdapter(Context context, List<ServiceModel> serviceModelList, Fragment fragment) {
        this.context = context;
        this.serviceModelList = serviceModelList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.country_city_row,parent,false);
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

                if (fragment instanceof FragmentTechnicalSignUp)
                {
                    FragmentTechnicalSignUp fragmentTechnicalSignUp = (FragmentTechnicalSignUp) fragment;
                    fragmentTechnicalSignUp.setCountryItem(serviceModel);

                }else if (fragment instanceof Fragment_Profile)
                {
                    Fragment_Profile fragment_profile = (Fragment_Profile) fragment;
                    fragment_profile.setCountryItem(serviceModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        public MyHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
        }

        private void BindData(ServiceModel serviceModel)
        {
            String lang = Locale.getDefault().getLanguage();
            if (lang.equals("ar"))
            {
                tv_title.setText(serviceModel.getAr_services_title());
            }else if (lang.equals("en"))
            {
                tv_title.setText(serviceModel.getEn_services_title());

            }

        }
    }


}
