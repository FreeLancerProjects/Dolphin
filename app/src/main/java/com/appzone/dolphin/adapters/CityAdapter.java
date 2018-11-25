package com.appzone.dolphin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appzone.dolphin.Models.Country_City_Model;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.signup_activity.fragments.FragmentMemberSignUp;

import java.util.List;
import java.util.Locale;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyHolder> {

    private Context context;
    private List<Country_City_Model.CityModel> cityModelList;
    private FragmentMemberSignUp fragmentMemberSignUp;

    public CityAdapter(Context context, List<Country_City_Model.CityModel> cityModelList,FragmentMemberSignUp fragmentMemberSignUp) {
        this.context = context;
        this.cityModelList = cityModelList;

        this.fragmentMemberSignUp = fragmentMemberSignUp;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.country_city_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        Country_City_Model.CityModel cityModel = cityModelList.get(position);
        holder.BindData(cityModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Country_City_Model.CityModel cityModel = cityModelList.get(holder.getAdapterPosition());
                fragmentMemberSignUp.setCityItem(cityModel);

            }
        });
    }

    @Override
    public int getItemCount() {
        return cityModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        public MyHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
        }

        private void BindData(Country_City_Model.CityModel cityModel)
        {
            String lang = Locale.getDefault().getLanguage();
            if (lang.equals("ar"))
            {
                tv_title.setText(cityModel.getAr_city_title());
            }else if (lang.equals("en"))
            {
                tv_title.setText(cityModel.getEn_city_title());

            }

        }
    }


}
