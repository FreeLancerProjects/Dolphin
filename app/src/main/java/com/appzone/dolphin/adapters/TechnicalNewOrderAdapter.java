package com.appzone.dolphin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appzone.dolphin.Models.TechnicalOrderModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.fragments.fragment_my_order.Fragment_Technical_New_Order;

import java.util.List;
import java.util.Locale;

public class TechnicalNewOrderAdapter extends RecyclerView.Adapter<TechnicalNewOrderAdapter.MyHolder> {

    private Context context;
    private List<TechnicalOrderModel> technicalOrderModelList;
    private Fragment_Technical_New_Order fragment_technical_new_order;
    public TechnicalNewOrderAdapter(Context context, List<TechnicalOrderModel> technicalOrderModelList,Fragment_Technical_New_Order fragment_technical_new_order) {
        this.context = context;
        this.technicalOrderModelList = technicalOrderModelList;
        this.fragment_technical_new_order = fragment_technical_new_order;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.technical_new_current_previous_order_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        TechnicalOrderModel technicalOrderModel = technicalOrderModelList.get(position);
        holder.BindData(technicalOrderModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TechnicalOrderModel technicalOrderModel = technicalOrderModelList.get(holder.getAdapterPosition());
                fragment_technical_new_order.setItem(technicalOrderModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return technicalOrderModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_not_date,tv_client_name,tv_specialization,tv_date,tv_address;

        public MyHolder(View itemView) {
            super(itemView);
            tv_not_date = itemView.findViewById(R.id.tv_not_date);
            tv_client_name = itemView.findViewById(R.id.tv_client_name);
            tv_specialization = itemView.findViewById(R.id.tv_specialization);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_date = itemView.findViewById(R.id.tv_date);


        }

        private void BindData(TechnicalOrderModel technicalOrderModel)
        {
            String lang = Locale.getDefault().getLanguage();
            if (lang.equals("ar"))
            {
                tv_specialization.setText(technicalOrderModel.getAr_user_specialization());

            }else
            {
                tv_specialization.setText(technicalOrderModel.getEn_user_specialization());

            }
            tv_not_date.setText(technicalOrderModel.getDate_notification());
            tv_date.setText(technicalOrderModel.getTechnical_arrival());
            tv_client_name.setText(technicalOrderModel.getUser_full_name());
            tv_address.setText(technicalOrderModel.getOrder_address());

        }
    }


}
