package com.appzone.dolphin.activities.home_activity.fragments;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.appzone.dolphin.Models.ClientNotificationModel;
import com.appzone.dolphin.Models.ResponseModel;
import com.appzone.dolphin.Models.TechnicalNotificationModel;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.activity.HomeActivity;
import com.appzone.dolphin.adapters.ClientNotificationAdapter;
import com.appzone.dolphin.adapters.TechnicalNotificationAdapter;
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.share.Common;
import com.appzone.dolphin.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Notifications extends Fragment implements DatePickerDialog.OnDateSetListener{
    private static final String TAG="DATA";
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;
    private ProgressBar progBar;
    private LinearLayout ll_no_not;
    private UserModel userModel;
    private List<ClientNotificationModel> clientNotificationModelList;
    private List<TechnicalNotificationModel> technicalNotificationModelList;
    private AlertDialog rateDialog;
    private String rate="",note="",newDate="";
    private TextView tv_date;
    private HomeActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications,container,false);
        initView(view);
        return view;
    }


    public static Fragment_Notifications getInstance(UserModel userModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,userModel);
        Fragment_Notifications fragment_notifications = new Fragment_Notifications();
        fragment_notifications.setArguments(bundle);
        return fragment_notifications;
    }
    private void initView(View view)
    {
        activity = (HomeActivity) getActivity();
        technicalNotificationModelList = new ArrayList<>();
        clientNotificationModelList = new ArrayList<>();
        recView =view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        ll_no_not = view.findViewById(R.id.ll_no_not);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            userModel = (UserModel) bundle.getSerializable(TAG);
            UpdateUI(userModel);
        }

    }
    private void UpdateUI(UserModel userModel)
    {

        if (userModel.getUser_type().equals(Tags.USER_MEMBER))
        {
            getClientNotification(userModel.getUser_id());
        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
        {
            getTechnicalNotification(userModel.getUser_id());
        }

    }
    private void getTechnicalNotification(String user_id)
    {
        Api.getService()
                .getTechnicalNotification(user_id)
                .enqueue(new Callback<List<TechnicalNotificationModel>>() {
                    @Override
                    public void onResponse(Call<List<TechnicalNotificationModel>> call, Response<List<TechnicalNotificationModel>> response) {
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);

                            if (response.body().size()>0)
                            {
                                technicalNotificationModelList.clear();
                                technicalNotificationModelList.addAll(response.body());
                                adapter = new TechnicalNotificationAdapter(getActivity(),technicalNotificationModelList);
                                recView.setAdapter(adapter);
                                ll_no_not.setVisibility(View.GONE);

                            }else
                            {
                                ll_no_not.setVisibility(View.VISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<List<TechnicalNotificationModel>> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Log.e("Error",t.getMessage());
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
    }
    private void getClientNotification(String user_id)
    {
        Api.getService()
                .getClientNotifications(user_id)
                .enqueue(new Callback<List<ClientNotificationModel>>() {
                    @Override
                    public void onResponse(Call<List<ClientNotificationModel>> call, Response<List<ClientNotificationModel>> response) {

                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);

                            if (response.body().size()>0)
                            {
                                clientNotificationModelList.clear();
                                clientNotificationModelList.addAll(response.body());
                                adapter = new ClientNotificationAdapter(getActivity(),Fragment_Notifications.this,clientNotificationModelList);
                                recView.setAdapter(adapter);
                                ll_no_not.setVisibility(View.GONE);

                            }else
                            {
                                ll_no_not.setVisibility(View.VISIBLE);
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<List<ClientNotificationModel>> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Log.e("Error",t.getMessage());
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
    }

    public void setItemTechnicalEvaluate(ClientNotificationModel clientNotificationModel, int pos)
    {
        CreateRateDialog(clientNotificationModel,pos,"technical");
    }

    public void setItemQualityOfficerEvaluate(ClientNotificationModel clientNotificationModel, int pos)
    {
        CreateRateDialog(clientNotificationModel,pos,"officer");

    }

    private void addTechnicalEvaluate(final ClientNotificationModel clientNotificationModel, final int pos, String rate)
    {
        final ProgressDialog progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.evaltng));
        progressDialog.show();
        Api.getService()
                .evaluateTechnical(userModel.getUser_id(),clientNotificationModel.getId_order(),rate,note)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();

                            if (response.body().getSuccess_evaluation()==1)
                            {
                                clientNotificationModelList.remove(pos);
                                adapter.notifyItemRemoved(pos);
                                if (clientNotificationModelList.size()>0)
                                {
                                    ll_no_not.setVisibility(View.GONE);
                                }else
                                    {
                                        ll_no_not.setVisibility(View.VISIBLE);

                                    }
                            }else
                                {
                                    Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        progressDialog.dismiss();

                        try {
                            Log.e("Error",t.getMessage());
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e)
                        {

                        }
                    }
                });
    }

    private void addQualityOfficerEvaluate(ClientNotificationModel clientNotificationModel, final int pos, String rate)
    {

        final ProgressDialog progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.evaltng));
        progressDialog.show();
        Api.getService()
                .evaluateQualityOfficer(userModel.getUser_id(),clientNotificationModel.getId_order(),rate)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();

                            if (response.body().getSuccess_evaluation()==1)
                            {
                                clientNotificationModelList.remove(pos);
                                adapter.notifyItemRemoved(pos);
                                if (clientNotificationModelList.size()>0)
                                {
                                    ll_no_not.setVisibility(View.GONE);
                                }else
                                {
                                    ll_no_not.setVisibility(View.VISIBLE);

                                }
                            }else
                            {
                                Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        progressDialog.dismiss();

                        try {
                            Log.e("Error",t.getMessage());
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e)
                        {

                        }
                    }
                });
    }

    private void CreateRateDialog(final ClientNotificationModel clientNotificationModel, final int pos, final String type)
    {
        rateDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_rate,null);
        ImageView image_logo = view.findViewById(R.id.image_logo);
        CircleImageView image = view.findViewById(R.id.image);
        final SimpleRatingBar rateBar = view.findViewById(R.id.rate_bar);
        final EditText edt_note = view.findViewById(R.id.edt_note);
        RadioButton rb_bad = view.findViewById(R.id.rb_bad);
        RadioButton rb_moderate = view.findViewById(R.id.rb_moderate);
        RadioButton rb_perfect = view.findViewById(R.id.rb_perfect);
        final Button btn_rate = view.findViewById(R.id.btn_rate);

        if (type.equals("technical"))
        {
            edt_note.setVisibility(View.VISIBLE);
            image_logo.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(Uri.parse(Tags.IMAGE_PATH+clientNotificationModel.getUser_photo())).into(image);
        }else if (type.equals("officer"))
        {
            image_logo.setVisibility(View.VISIBLE);
            edt_note.setVisibility(View.GONE);
            image.setVisibility(View.GONE);


        }

        rb_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = Tags.RATE_BAD;
                btn_rate.setVisibility(View.VISIBLE);
                SimpleRatingBar.AnimationBuilder builder = rateBar.getAnimationBuilder()
                        .setDuration(1500)
                        .setInterpolator(new AccelerateInterpolator())
                        .setRepeatCount(0)
                        .setRepeatMode(ValueAnimator.REVERSE)
                        .setRatingTarget(1.5f);
                builder.start();

            }
        });

        rb_moderate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = Tags.RATE_MODERATE;
                btn_rate.setVisibility(View.VISIBLE);

                SimpleRatingBar.AnimationBuilder builder = rateBar.getAnimationBuilder()
                        .setDuration(1500)
                        .setInterpolator(new AccelerateInterpolator())
                        .setRepeatCount(0)
                        .setRepeatMode(ValueAnimator.REVERSE)
                        .setRatingTarget(3.0f);
                builder.start();
            }
        });

        rb_perfect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = Tags.RATE_PERFECT;
                btn_rate.setVisibility(View.VISIBLE);
                SimpleRatingBar.AnimationBuilder builder = rateBar.getAnimationBuilder()
                        .setDuration(1500)
                        .setInterpolator(new AccelerateInterpolator())
                        .setRepeatCount(0)
                        .setRepeatMode(ValueAnimator.REVERSE)
                        .setRatingTarget(5.0f);
                builder.start();
            }
        });

        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                rateDialog.dismiss();
                Common.CloseKeyBoard(getActivity(),edt_note);

                if (type.equals("technical"))
                {
                    note = edt_note.getText().toString();
                    addTechnicalEvaluate(clientNotificationModel,pos,rate);
                }else if (type.equals("officer"))
                {

                    addQualityOfficerEvaluate(clientNotificationModel,pos,rate);

                }

            }
        });

        rateDialog.setCanceledOnTouchOutside(false);
        rateDialog.setView(view);
        rateDialog.getWindow().getAttributes().windowAnimations = R.style.custom_dialog;
        rateDialog.show();
    }

    public void setItemForConfirm(ClientNotificationModel clientNotificationModel , int pos,String type)
    {
        if (type.equals(Tags.REPRIEVE_ORDER))
        {

            CreateUpdateDateDialog(clientNotificationModel,pos,"2");
        }else if (type.equals(Tags.CONFIRM_ORDER))

            {
                confirmOrder(clientNotificationModel,pos,"","1");
            }
            else if (type.equals(Tags.CANCEL_ORDER))
            {
                confirmOrder(clientNotificationModel,pos,"","3");

            }
    }

    private void confirmOrder(final ClientNotificationModel clientNotificationModel, final int pos, String date,String confirm_type) {
        final ProgressDialog progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        progressDialog.show();
        Api.getService()
                .clientConfirmOrder(userModel.getUser_id(),clientNotificationModel.getId_order(),date,confirm_type)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();

                            if (response.body().getSuccess_confirm()==1)
                            {
                                Toast.makeText(getActivity(), R.string.succ, Toast.LENGTH_SHORT).show();
                                clientNotificationModelList.remove(pos);
                                adapter.notifyItemRemoved(pos);
                                if (clientNotificationModelList.size()>0)
                                {
                                    ll_no_not.setVisibility(View.GONE);
                                }else
                                    {
                                        ll_no_not.setVisibility(View.VISIBLE);

                                    }
                            }else
                                {
                                    Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
    }


    private void CreateDateDialog()
    {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        DatePickerDialog dialog = DatePickerDialog.newInstance(this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setLocale(Locale.ENGLISH);
        dialog.setVersion(DatePickerDialog.Version.VERSION_2);
        dialog.setCancelColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        dialog.setOkColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        dialog.setOkText(R.string.select);
        dialog.setCancelText(R.string.cancel);
        dialog.setAccentColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        dialog.setMinDate(calendar);
        dialog.show(activity.getFragmentManager(),"date");

    }

    private void CreateUpdateDateDialog(final ClientNotificationModel clientNotificationModel, final int pos, final String confirm_type)
    {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_date,null);
        tv_date = view.findViewById(R.id.tv_date);

        final Button btn_update = view.findViewById(R.id.btn_update);
        final Button btn_cancel = view.findViewById(R.id.btn_cancel);


        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateDateDialog();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(newDate))
                {
                    tv_date.setError(getString(R.string.choose_date));
                }else
                    {
                        tv_date.setError(null);
                        dialog.dismiss();
                        confirmOrder(clientNotificationModel,pos,newDate,confirm_type);
                    }


            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.getWindow().getAttributes().windowAnimations = R.style.custom_dialog;
        dialog.show();
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        newDate = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            Calendar calendar = Calendar.getInstance(new Locale("ar"));
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            Date date = new Date(calendar.getTimeInMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",new Locale("ar"));
            String dd = dateFormat.format(date);
            tv_date.setText(dd);

        }else
            {
                tv_date.setText(newDate);

            }
    }
}
