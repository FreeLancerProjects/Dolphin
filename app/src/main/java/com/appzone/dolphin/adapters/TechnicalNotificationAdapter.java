package com.appzone.dolphin.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appzone.dolphin.Models.TechnicalNotificationModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.tags.Tags;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class TechnicalNotificationAdapter extends RecyclerView.Adapter<TechnicalNotificationAdapter.MyHolder> {

    private Context context;
    private List<TechnicalNotificationModel> technicalNotificationModelList;

    public TechnicalNotificationAdapter(Context context, List<TechnicalNotificationModel> technicalNotificationModelList) {
        this.context = context;
        this.technicalNotificationModelList = technicalNotificationModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.technical_notification_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        TechnicalNotificationModel technicalNotificationModel = technicalNotificationModelList.get(position);
        holder.BindData(technicalNotificationModel);

    }

    @Override
    public int getItemCount() {
        return technicalNotificationModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private RoundedImageView image;
        private TextView tv_not_date,tv_service,tv_date;

        public MyHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tv_not_date = itemView.findViewById(R.id.tv_not_date);
            tv_service = itemView.findViewById(R.id.tv_service);
            tv_date = itemView.findViewById(R.id.tv_date);

        }

        private void BindData(TechnicalNotificationModel technicalNotificationModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_PATH+technicalNotificationModel.getServices_logo())).into(image);
            String lang = Locale.getDefault().getLanguage();
            if (lang.equals("ar"))
            {
                tv_service.setText(technicalNotificationModel.getAr_user_specialization());

            }else
            {
                tv_service.setText(technicalNotificationModel.getEn_user_specialization());

            }
            tv_not_date.setText(technicalNotificationModel.getDate_notification());
            tv_date.setText(technicalNotificationModel.getOrder_date());

        }
    }


}
