package com.appzone.dolphin.activities.technical_order_details_activity.fragments.fragment_current_order_dtails;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.appzone.dolphin.Models.OrderStateModel;
import com.appzone.dolphin.Models.ResponseModel;
import com.appzone.dolphin.Models.TechnicalOrderModel;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.technical_order_details_activity.activity.TechnicalOrderDetailsActivity;
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.share.Common;
import com.appzone.dolphin.singletone.UserSingleTone;
import com.appzone.dolphin.tags.Tags;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

public class Fragment_Details extends Fragment implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,LocationListener{

    private static final String TAG="DATA";
    private TechnicalOrderModel technicalOrderModel;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private TextView tv_not_date,tv_client_name,tv_address,tv_date,tv_work_details;
    private FrameLayout fl_image,fl_video,fl_sound;
    private MediaPlayer mp=null;
    private  SeekBar seekBar;
    private ImageView image_play;
    private TextView tv_total_time,tv_progress_time;
    private Handler handler;
    private Runnable runnable;
    private Button btn_arrive,btn_start,btn_cancel,btn_stop,btn_continue,btn_finish;
    private LinearLayout ll_btn_container;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final int gps_req = 102, loc_req = 103;
    private String fineLoc = Manifest.permission.ACCESS_FINE_LOCATION;
    private double lat=0.0,lng=0.0;
    private TechnicalOrderDetailsActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details,container,false);
        initView(view);
        CheckPermission();
        return view;
    }

    public static Fragment_Details getInstance(TechnicalOrderModel technicalOrderModel)
    {
        Fragment_Details fragment_details = new Fragment_Details();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,technicalOrderModel);
        fragment_details.setArguments(bundle);
        return fragment_details;
    }
    private void initView(View view)
    {

        activity= (TechnicalOrderDetailsActivity) getActivity();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();

        tv_not_date = view.findViewById(R.id.tv_not_date);
        tv_client_name = view.findViewById(R.id.tv_client_name);
        tv_address = view.findViewById(R.id.tv_address);
        tv_date = view.findViewById(R.id.tv_date);
        tv_work_details = view.findViewById(R.id.tv_work_details);
        fl_image = view.findViewById(R.id.fl_image);
        fl_video = view.findViewById(R.id.fl_video);
        fl_sound = view.findViewById(R.id.fl_sound);

        btn_arrive = view.findViewById(R.id.btn_arrive);
        btn_start = view.findViewById(R.id.btn_start);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_stop = view.findViewById(R.id.btn_stop);
        btn_continue = view.findViewById(R.id.btn_continue);
        btn_finish = view.findViewById(R.id.btn_finish);
        ll_btn_container = view.findViewById(R.id.ll_btn_container);


        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            technicalOrderModel = (TechnicalOrderModel) bundle.getSerializable(TAG);
            updateUI(technicalOrderModel);
        }

        fl_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (technicalOrderModel.getOrder_image().equals("0"))
                {
                    Toast.makeText(getActivity(), R.string.no_image, Toast.LENGTH_SHORT).show();
                }else
                    {
                        String path = Tags.IMAGE_PATH+technicalOrderModel.getOrder_image();
                        CreateAlertDialogFor_Image_Video_Sound(path,"image");
                    }
            }
        });

        fl_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (technicalOrderModel.getOrder_voice().equals("0"))
                {
                    Toast.makeText(getActivity(), R.string.no_sound, Toast.LENGTH_SHORT).show();
                }else
                {
                    String path = Tags.VOICE_PATH+technicalOrderModel.getOrder_voice();
                    CreateAlertDialogFor_Image_Video_Sound(path,"sound");
                }
            }
        });

        fl_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (technicalOrderModel.getOrder_video().equals("0"))
                {
                    Toast.makeText(getActivity(), R.string.no_video, Toast.LENGTH_SHORT).show();
                }else
                {
                    String path = Tags.VIDEO_PATH+technicalOrderModel.getOrder_video();
                    CreateAlertDialogFor_Image_Video_Sound(path,"video");
                }
            }
        });

        btn_arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrive(userModel.getUser_id(),technicalOrderModel.getId_order(),technicalOrderModel.getClient_id_fk(),lat,lng);
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canStart();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stop(userModel.getUser_id(),technicalOrderModel.getId_order(),technicalOrderModel.getClient_id_fk(),lat,lng);
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Restart(userModel.getUser_id(),technicalOrderModel.getId_order(),technicalOrderModel.getClient_id_fk(),lat,lng);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //activity.Finish();
                Log.e("canccc","ddddddddddd");
                Cancel(userModel.getUser_id(),technicalOrderModel.getId_order(),technicalOrderModel.getClient_id_fk(),lat,lng);
            }
        });


        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Finish(userModel.getUser_id(),technicalOrderModel.getId_order(),technicalOrderModel.getClient_id_fk(),lat,lng);
            }
        });



    }

    private void updateUI(TechnicalOrderModel technicalOrderModel)
    {
        tv_not_date.setText(technicalOrderModel.getDate_notification());
        tv_client_name.setText(technicalOrderModel.getUser_full_name());
        tv_address.setText(technicalOrderModel.getOrder_address());
        tv_date.setText(technicalOrderModel.getOrder_date()+" - "+technicalOrderModel.getTechnical_arrival());
        tv_work_details.setText(technicalOrderModel.getOrder_details());

        ll_btn_container.setVisibility(View.VISIBLE);
        getOrder_State(technicalOrderModel.getId_order());

    }

    private void arrive(String user_id, String id_order, String client_id_fk, double lat, double lng)
    {

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();

        Api.getService()
                .arrival(user_id,Tags.TECHNICAL_ARRIVE,String.valueOf(lat),String.valueOf(lng),id_order,client_id_fk)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getSuccess_movement()==1)
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.VISIBLE);
                                btn_start.setVisibility(View.VISIBLE);
                                btn_cancel.setVisibility(View.VISIBLE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.GONE);
                                btn_finish.setVisibility(View.GONE);

                            }else
                                {
                                    Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {

                        try {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void Start(String user_id, String id_order, String client_id_fk, double lat, double lng)
    {

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();

        Api.getService()
                .arrival(user_id,Tags.TECHNICAL_START,String.valueOf(lat),String.valueOf(lng),id_order,client_id_fk)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getSuccess_movement()==1)
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.VISIBLE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.VISIBLE);
                                btn_continue.setVisibility(View.GONE);
                                btn_finish.setVisibility(View.VISIBLE);


                            }else
                            {
                                Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {

                        try {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void Stop(String user_id, String id_order, String client_id_fk, double lat, double lng) {

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();

        Api.getService()
                .arrival(user_id,Tags.TECHNICAL_STOP,String.valueOf(lat),String.valueOf(lng),id_order,client_id_fk)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getSuccess_movement()==1)
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.VISIBLE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.VISIBLE);
                                btn_stop.setVisibility(View.GONE);
                                btn_finish.setVisibility(View.GONE);

                            }else
                            {
                                Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {

                        try {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    private void Restart(String user_id, String id_order, String client_id_fk, double lat, double lng) {

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();

        Api.getService()
                .arrival(user_id,Tags.TECHNICAL_RESTART,String.valueOf(lat),String.valueOf(lng),id_order,client_id_fk)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getSuccess_movement()==1)
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.VISIBLE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.VISIBLE);
                                btn_finish.setVisibility(View.VISIBLE);

                            }else
                            {
                                Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {

                        try {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    private void Cancel(String user_id, String id_order, String client_id_fk, double lat, double lng) {

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();

        Log.e("cancel",Tags.TECHNICAL_CANCEL);
        Api.getService()
                .arrival(user_id,Tags.TECHNICAL_CANCEL,String.valueOf(lat),String.valueOf(lng),id_order,client_id_fk)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            Log.e("setp",response.body().getOrder_step());
                            if (response.body().getSuccess_movement()==1)
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.VISIBLE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.VISIBLE);
                                btn_finish.setVisibility(View.GONE);

                                activity.Finish();
                                //finish

                            }else
                            {
                                Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {

                        try {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });

    }

    private void Finish(String user_id, String id_order, String client_id_fk, double lat, double lng) {

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();

        Api.getService()
                .arrival(user_id,Tags.TECHNICAL_FINISH,String.valueOf(lat),String.valueOf(lng),id_order,client_id_fk)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getSuccess_movement()==1)
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.VISIBLE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.VISIBLE);
                                btn_finish.setVisibility(View.GONE);

                                activity.Finish();
                                //finish

                            }else
                            {
                                Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {

                        try {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    private void getOrder_State(final String id_order)
    {
        Log.e("userid",userModel.getUser_id()+"_");
        Log.e("order_id",id_order+"_");

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();

        Api.getService()
                .getOrderState(userModel.getUser_id(),id_order)
                .enqueue(new Callback<OrderStateModel>() {
                    @Override
                    public void onResponse(Call<OrderStateModel> call, Response<OrderStateModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            Log.e("data",response.body().getOrder_step()+"_");

                            if (response.body().getOrder_step().equals("-1"))
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.GONE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.GONE);
                                btn_finish.setVisibility(View.GONE);
                            }
                            else if (response.body().getOrder_step().equals("1"))
                            {
                                btn_arrive.setVisibility(View.VISIBLE);
                                ll_btn_container.setVisibility(View.GONE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.GONE);
                                btn_finish.setVisibility(View.GONE);
                            }else if (response.body().getOrder_step().equals("2"))
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.VISIBLE);
                                btn_start.setVisibility(View.VISIBLE);
                                btn_cancel.setVisibility(View.VISIBLE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.GONE);
                                btn_finish.setVisibility(View.GONE);
                            } else if (response.body().getOrder_step().equals("3"))
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.VISIBLE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.VISIBLE);
                                btn_finish.setVisibility(View.VISIBLE);
                            }

                            else if (response.body().getOrder_step().equals("4"))
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.VISIBLE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.VISIBLE);
                                btn_stop.setVisibility(View.GONE);
                                btn_finish.setVisibility(View.GONE);
                            }

                            else if (response.body().getOrder_step().equals("5"))
                            {
                                btn_arrive.setVisibility(View.GONE);
                                ll_btn_container.setVisibility(View.VISIBLE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.VISIBLE);
                                btn_finish.setVisibility(View.VISIBLE);

                            }
                            else if (response.body().getOrder_step().equals("6"))
                            {
                                btn_arrive.setVisibility(View.VISIBLE);
                                ll_btn_container.setVisibility(View.GONE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.GONE);
                                btn_finish.setVisibility(View.GONE);
                            }
                            else if (response.body().getOrder_step().equals("7"))
                            {
                                btn_arrive.setVisibility(View.VISIBLE);
                                ll_btn_container.setVisibility(View.GONE);
                                btn_start.setVisibility(View.GONE);
                                btn_cancel.setVisibility(View.GONE);
                                btn_continue.setVisibility(View.GONE);
                                btn_stop.setVisibility(View.GONE);
                                btn_finish.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderStateModel> call, Throwable t) {
                        try {
                            dialog.dismiss();

                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });

    }

    private void canStart()
    {
        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();

        Api.getService()
                .getOrderState(userModel.getUser_id(),technicalOrderModel.getId_order())
                .enqueue(new Callback<OrderStateModel>() {
                    @Override
                    public void onResponse(Call<OrderStateModel> call, Response<OrderStateModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getOrder_payment()==1)
                            {
                                Start(userModel.getUser_id(),technicalOrderModel.getId_order(),technicalOrderModel.getClient_id_fk(),lat,lng);
                            }else if (response.body().getOrder_payment()==0)
                            {
                                Toast.makeText(getActivity(), R.string.cnt_start, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderStateModel> call, Throwable t) {
                        try {
                            dialog.dismiss();

                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }


    private void CreateAlertDialogFor_Image_Video_Sound(final String path, String type)
    {
       final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .create();
        View view = null;
        if (type.equals("image"))
        {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image,null);
            ImageView imageView = view.findViewById(R.id.image);
            Button btn_ok = view.findViewById(R.id.btn_ok);
            Button btn_cancel = view.findViewById(R.id.btn_cancel);
            Picasso.with(getActivity()).load(Uri.parse(path)).into(imageView);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });



        }else if (type.equals("video"))
        {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_video,null);
            final VideoView videoView = view.findViewById(R.id.videoView);
            final ProgressBar progBar = view.findViewById(R.id.progBar);
            progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            Button btn_ok = view.findViewById(R.id.btn_ok);
            Button btn_cancel = view.findViewById(R.id.btn_cancel);
            final ImageView image_Play = view.findViewById(R.id.image_play);


            image_Play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.setVideoPath(path);
                    final MediaController mc = new MediaController(getActivity());
                    videoView.setMediaController(mc);
                    mc.setAnchorView(videoView);
                    videoView.requestFocus();
                    image_Play.setVisibility(View.GONE);
                    videoView.start();

                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.stopPlayback();
                    alertDialog.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.stopPlayback();

                    alertDialog.dismiss();

                }
            });



            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.reset();
                    image_Play.setVisibility(View.VISIBLE);
                }
            });
        }else if (type.equals("sound"))
        {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sound,null);
            image_play = view.findViewById(R.id.image_play);
            seekBar = view.findViewById(R.id.seekBar);
            LayerDrawable layerDrawable = (LayerDrawable) seekBar.getProgressDrawable();
            layerDrawable.getDrawable(0).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            layerDrawable.getDrawable(1).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            layerDrawable.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            tv_total_time    = view.findViewById(R.id.tv_total_time);
            tv_progress_time = view.findViewById(R.id.tv_progress_time);

            handler = new Handler();
            mp = MediaPlayer.create(getActivity(), Uri.parse(path));
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            seekBar.setMax(mp.getDuration());
            tv_total_time.setText(updateTime(mp.getDuration()));

            UpdateProgress();
            image_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mp!=null && mp.isPlaying())
                    {

                        mp.stop();
                        seekBar.setProgress(0);
                        seekBar.setSecondaryProgress(0);
                        tv_progress_time.setText("00:00");
                        image_play.setImageResource(R.drawable.play);
                        mp = MediaPlayer.create(getActivity(),Uri.parse(path));
                        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        seekBar.setMax(mp.getDuration());
                        tv_total_time.setText(updateTime(mp.getDuration()));

                    }else
                    {
                        tv_progress_time.setText("00:00");
                        mp.start();
                        UpdateProgress();



                    }

                }
            });


            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b)
                    {

                        mp.seekTo(i);
                        UpdateProgress();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


            Button btn_ok = view.findViewById(R.id.btn_ok);
            Button btn_cancel = view.findViewById(R.id.btn_cancel);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setView(view);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.custom_dialog;
        alertDialog.show();
    }

    private void UpdateProgress()
    {
        seekBar.setProgress(mp.getCurrentPosition());

        if (mp.isPlaying())
        {
            image_play.setImageResource(R.drawable.stop_button);
            runnable = new Runnable() {
                @Override
                public void run() {
                    UpdateProgress();
                }
            };
            handler.postDelayed(runnable,1000);
            tv_progress_time.setText(updateTime(mp.getCurrentPosition()));


        }else
        {
            image_play.setImageResource(R.drawable.play);
        }

    }
    private String updateTime(long time)
    {
        int minutes = (int) ((time % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((time % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        String duration = String.format("%02d:%02d",minutes,seconds);

        return duration;
    }




    private void initGoogleApiClient()
    {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
        googleApiClient.connect();
    }

    private void CheckPermission()
    {
        if (ContextCompat.checkSelfPermission(getActivity(), fineLoc) != PackageManager.PERMISSION_GRANTED) {
            String[] perm = {fineLoc};
            ActivityCompat.requestPermissions(getActivity(), perm, loc_req);
        } else {
            if (isGpsOpen()) {
                initGoogleApiClient();
            } else {
                CreateGpsDialog();
            }
        }
    }
    private boolean isGpsOpen()
    {
        LocationManager manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }

        return false;
    }
    private void OpenGps()
    {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, gps_req);
    }
    private void CreateGpsDialog()
    {
        final AlertDialog gps_dialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .create();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_dialog, null);
        TextView tv_msg = view.findViewById(R.id.tv_msg);
        tv_msg.setText(R.string.app_open_gps);
        Button doneBtn = view.findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps_dialog.dismiss();
                OpenGps();
            }
        });

        gps_dialog.getWindow().getAttributes().windowAnimations = R.style.custom_dialog;
        gps_dialog.setView(view);
        gps_dialog.setCanceledOnTouchOutside(false);
        gps_dialog.show();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gps_req) {
            if (isGpsOpen()) {
                initGoogleApiClient();
            } else {
                CreateGpsDialog();
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == loc_req) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isGpsOpen()) {
                        initGoogleApiClient();
                    } else {
                        CreateGpsDialog();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.acc_loc_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        StartLocationUpdate();
    }

    private void StartLocationUpdate()
    {
        initLocationRequest();

        if (locationCallback == null) {
            locationCallback = new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    onLocationChanged(locationResult.getLastLocation());
                }
            };
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        LocationServices.getFusedLocationProviderClient(getActivity())
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
    private void StopLocationUpdate()
    {
        if (googleApiClient!=null&&googleApiClient.isConnected()&&locationCallback!=null)
        {
            LocationServices.getFusedLocationProviderClient(getActivity())
                    .removeLocationUpdates(locationCallback);
            googleApiClient.disconnect();
        }
    }
    private void initLocationRequest()
    {
        locationRequest = new LocationRequest();
        locationRequest.setFastestInterval(60000);
        locationRequest.setInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    public void onConnectionSuspended(int i)
    {

        if (googleApiClient!=null)
        {
            googleApiClient.connect();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onLocationChanged(Location location)
    {
        lat =location.getLatitude();
        lng = location.getLongitude();
        Log.e("details",lat+"_");
        StopLocationUpdate();
    }
}
