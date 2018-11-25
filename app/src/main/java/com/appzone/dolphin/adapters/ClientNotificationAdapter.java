package com.appzone.dolphin.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appzone.dolphin.Models.ClientNotificationModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.fragments.Fragment_Notifications;
import com.appzone.dolphin.tags.Tags;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ClientNotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_BEFORE_CONFIRM = 1;
    private final int ITEM_AFTER_CONFIRM = 2;
    private final int ITEM_EVALUATE_TECHNICAL = 3;
    private final int ITEM_EVALUATE_QUALITY_OFFICER = 4;

    private Context context;
    private List<ClientNotificationModel>clientNotificationModelList;
    private Fragment_Notifications fragment_notifications;

    public ClientNotificationAdapter(Context context,Fragment_Notifications fragment_notifications, List<ClientNotificationModel> clientNotificationModelList) {
        this.context = context;
        this.clientNotificationModelList = clientNotificationModelList;
        this.fragment_notifications = fragment_notifications;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_BEFORE_CONFIRM)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.client_not_before_confirm,parent,false);
            return new HolderBeforeConfirm(view);
        }else if (viewType==ITEM_AFTER_CONFIRM)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.client_not_after_confirm,parent,false);
            return new HolderAfterConfirm(view);
        }else if (viewType==ITEM_EVALUATE_TECHNICAL)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.client_not_evaluate_technical,parent,false);
            return new HolderEvaluateTechnical(view);
        }else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.client_not_evaluate_quality_officer,parent,false);
            return new HolderEvaluateQualityOfficer(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ClientNotificationModel clientNotificationModel = clientNotificationModelList.get(position);
        if (holder instanceof HolderBeforeConfirm)
        {
            final HolderBeforeConfirm holderBeforeConfirm = (HolderBeforeConfirm) holder;
            holderBeforeConfirm.BindData(clientNotificationModel);

            holderBeforeConfirm.btn_confirm
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClientNotificationModel clientNotificationModel = clientNotificationModelList.get(holderBeforeConfirm.getAdapterPosition());
                            fragment_notifications.setItemForConfirm(clientNotificationModel,holderBeforeConfirm.getAdapterPosition(),Tags.CONFIRM_ORDER);
                        }
                    });
            holderBeforeConfirm.btn_reprieve
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClientNotificationModel clientNotificationModel = clientNotificationModelList.get(holderBeforeConfirm.getAdapterPosition());
                            fragment_notifications.setItemForConfirm(clientNotificationModel,holderBeforeConfirm.getAdapterPosition(),Tags.REPRIEVE_ORDER);

                        }
                    });
            holderBeforeConfirm.btn_cancel
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClientNotificationModel clientNotificationModel = clientNotificationModelList.get(holderBeforeConfirm.getAdapterPosition());
                            fragment_notifications.setItemForConfirm(clientNotificationModel,holderBeforeConfirm.getAdapterPosition(),Tags.CANCEL_ORDER);

                        }
                    });

        }else if (holder instanceof HolderAfterConfirm)
        {
            HolderAfterConfirm holderAfterConfirm = (HolderAfterConfirm) holder;
            holderAfterConfirm.BindData(clientNotificationModel);
        }else if (holder instanceof HolderEvaluateTechnical)
        {
            final HolderEvaluateTechnical holderEvaluateTechnical = (HolderEvaluateTechnical) holder;
            holderEvaluateTechnical.BindData(clientNotificationModel);
            holderEvaluateTechnical.btn_rate
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClientNotificationModel clientNotificationModel = clientNotificationModelList.get(holderEvaluateTechnical.getAdapterPosition());
                            fragment_notifications.setItemTechnicalEvaluate(clientNotificationModel,holderEvaluateTechnical.getAdapterPosition());
                        }
                    });
        }else if (holder instanceof HolderEvaluateQualityOfficer)
        {
            final HolderEvaluateQualityOfficer holderEvaluateQualityOfficer = (HolderEvaluateQualityOfficer) holder;
            holderEvaluateQualityOfficer.BindData(clientNotificationModel);

            holderEvaluateQualityOfficer.btn_rate
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClientNotificationModel clientNotificationModel = clientNotificationModelList.get(holderEvaluateQualityOfficer.getAdapterPosition());
                            fragment_notifications.setItemQualityOfficerEvaluate(clientNotificationModel,holderEvaluateQualityOfficer.getAdapterPosition());
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return clientNotificationModelList.size();
    }

    public class HolderBeforeConfirm extends RecyclerView.ViewHolder {
        private RoundedImageView image;
        private TextView tv_not_date,tv_service;
        private Button btn_confirm,btn_reprieve,btn_cancel;
        public HolderBeforeConfirm(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tv_not_date = itemView.findViewById(R.id.tv_not_date);
            tv_service = itemView.findViewById(R.id.tv_service);
            btn_confirm = itemView.findViewById(R.id.btn_confirm);
            btn_reprieve = itemView.findViewById(R.id.btn_reprieve);
            btn_cancel = itemView.findViewById(R.id.btn_cancel);

        }

        public void BindData(ClientNotificationModel clientNotificationModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_PATH+clientNotificationModel.getServices_logo())).into(image);
            tv_not_date.setText(clientNotificationModel.getDate_notification());
            if (Locale.getDefault().getLanguage().equals("ar"))
            {
                tv_service.setText(clientNotificationModel.getAr_user_specialization());

            }else
                {
                    tv_service.setText(clientNotificationModel.getEn_user_specialization());

                }
        }
    }

    public class HolderAfterConfirm extends RecyclerView.ViewHolder {
        private RoundedImageView image;
        private TextView tv_not_date,tv_service,tv_technical_name,tv_date_time;
        public HolderAfterConfirm(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tv_not_date = itemView.findViewById(R.id.tv_not_date);
            tv_service = itemView.findViewById(R.id.tv_service);
            tv_technical_name = itemView.findViewById(R.id.tv_technical_name);
            tv_date_time = itemView.findViewById(R.id.tv_date_time);

        }
        public void BindData(ClientNotificationModel clientNotificationModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_PATH+clientNotificationModel.getUser_photo())).into(image);
            tv_not_date.setText(clientNotificationModel.getDate_notification());
            tv_technical_name.setText(clientNotificationModel.getUser_full_name());
            tv_date_time.setText(clientNotificationModel.getOrder_date()+" - "+clientNotificationModel.getTechnical_arrival());
            if (Locale.getDefault().getLanguage().equals("ar"))
            {
                tv_service.setText(clientNotificationModel.getAr_user_specialization());

            }else
            {
                tv_service.setText(clientNotificationModel.getEn_user_specialization());

            }
        }
    }

    public class HolderEvaluateTechnical extends RecyclerView.ViewHolder {
        private RoundedImageView image;
        private TextView tv_not_date,tv_service,tv_technical_name;
        private Button btn_rate;
        public HolderEvaluateTechnical(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tv_not_date = itemView.findViewById(R.id.tv_not_date);
            tv_service = itemView.findViewById(R.id.tv_service);
            tv_technical_name = itemView.findViewById(R.id.tv_technical_name);
            btn_rate = itemView.findViewById(R.id.btn_rate);

        }
        public void BindData(ClientNotificationModel clientNotificationModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_PATH+clientNotificationModel.getUser_photo())).into(image);
            tv_not_date.setText(clientNotificationModel.getDate_notification());
            tv_technical_name.setText(clientNotificationModel.getUser_full_name());
            if (Locale.getDefault().getLanguage().equals("ar"))
            {
                tv_service.setText(clientNotificationModel.getAr_user_specialization());

            }else
            {
                tv_service.setText(clientNotificationModel.getEn_user_specialization());

            }
        }
    }

    public class HolderEvaluateQualityOfficer extends RecyclerView.ViewHolder {
        private RoundedImageView image;
        private TextView tv_not_date;
        private Button btn_rate;
        public HolderEvaluateQualityOfficer(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tv_not_date = itemView.findViewById(R.id.tv_not_date);
            btn_rate = itemView.findViewById(R.id.btn_rate);
        }

        public void BindData(ClientNotificationModel clientNotificationModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_PATH+clientNotificationModel.getServices_logo())).into(image);
            tv_not_date.setText(clientNotificationModel.getDate_notification());

        }
    }

    @Override
    public int getItemViewType(int position)
    {
        ClientNotificationModel model = clientNotificationModelList.get(position);
        if (model.getOrder_status().equals(Tags.CLIENT_ORDER_BEFORE_CONFIRM))
        {
            return ITEM_BEFORE_CONFIRM;
        }else if (model.getOrder_status().equals(Tags.CLIENT_ORDER_AFTER_CONFIRM))
        {
            return ITEM_AFTER_CONFIRM;
        }else if (model.getOrder_status().equals(Tags.CLIENT_ORDER_EVALUATE_TECHNICAL))
        {
            return ITEM_EVALUATE_TECHNICAL;
        }else
            {
                return ITEM_EVALUATE_QUALITY_OFFICER;
            }
    }
}
