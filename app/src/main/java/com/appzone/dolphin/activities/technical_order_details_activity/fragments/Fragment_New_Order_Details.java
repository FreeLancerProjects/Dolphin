package com.appzone.dolphin.activities.technical_order_details_activity.fragments;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.appzone.dolphin.Models.TechnicalOrderModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.tags.Tags;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class Fragment_New_Order_Details extends Fragment implements OnMapReadyCallback{

    private static final String TAG="DATA";
    private TechnicalOrderModel technicalOrderModel;
    private TextView tv_not_date,tv_client_name,tv_address,tv_date,tv_work_details;
    private FrameLayout fl_image,fl_video,fl_sound;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private Marker marker;
    private final float zoom = 16.5f;
    private MediaPlayer mp=null;
    private  SeekBar seekBar;
    private ImageView image_play;
    private TextView tv_total_time,tv_progress_time;
    private Handler handler;
    private Runnable runnable;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_order_details,container,false);
        initView(view);
        return view;
    }

    public static Fragment_New_Order_Details getInstance(TechnicalOrderModel technicalOrderModel)
    {
        Fragment_New_Order_Details fragment_new_order_details = new Fragment_New_Order_Details();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,technicalOrderModel);
        fragment_new_order_details.setArguments(bundle);
        return fragment_new_order_details;
    }
    private void initView(View view) {

        tv_not_date = view.findViewById(R.id.tv_not_date);
        tv_client_name = view.findViewById(R.id.tv_client_name);
        tv_address = view.findViewById(R.id.tv_address);
        tv_date = view.findViewById(R.id.tv_date);
        tv_work_details = view.findViewById(R.id.tv_work_details);
        fl_image = view.findViewById(R.id.fl_image);
        fl_video = view.findViewById(R.id.fl_video);
        fl_sound = view.findViewById(R.id.fl_sound);


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
                    Toast.makeText(getActivity(), "No Image", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "No Audio", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "No Video", Toast.LENGTH_SHORT).show();
                }else
                {
                    String path = Tags.VIDEO_PATH+technicalOrderModel.getOrder_video();
                    CreateAlertDialogFor_Image_Video_Sound(path,"video");
                }
            }
        });
    }

    private void updateUI(TechnicalOrderModel technicalOrderModel) {
        initMap();
        tv_not_date.setText(technicalOrderModel.getDate_notification());
        tv_client_name.setText(technicalOrderModel.getUser_full_name());
        tv_address.setText(technicalOrderModel.getOrder_address());
        tv_date.setText(technicalOrderModel.getOrder_date()+" - "+technicalOrderModel.getTechnical_arrival());
        tv_work_details.setText(technicalOrderModel.getOrder_details());
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
    private void initMap()
    {
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            supportMapFragment.getMapAsync(this);

        }

        getChildFragmentManager().beginTransaction().replace(R.id.map, supportMapFragment).commit();
    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        if (googleMap != null) {
            mMap = googleMap;
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setTrafficEnabled(false);
            mMap.setIndoorEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            AddMarker(Double.parseDouble(technicalOrderModel.getOrder_address_lat()),Double.parseDouble(technicalOrderModel.getOrder_address_long()));
        }
    }

    private void AddMarker(double lat ,double lng)
    {
        if (marker==null)
        {
            marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(getBitmap())).position(new LatLng(lat,lng)));

        }else
            {
                marker.setPosition(new LatLng(lat,lng));
            }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),zoom));
    }

    private Bitmap getBitmap()
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_user);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int req_width,req_height;
        req_width = 90;
        req_height = 90;


        float scale_width = (float) req_width / width;
        float scale_height = (float) req_height / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scale_width, scale_height);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

    }
}
