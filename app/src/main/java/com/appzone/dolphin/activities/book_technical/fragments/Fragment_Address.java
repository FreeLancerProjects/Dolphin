package com.appzone.dolphin.activities.book_technical.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.book_technical.activity.BookTechnicalActivity;
import com.appzone.dolphin.share.Common;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;

public class Fragment_Address extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private EditText edt_address, edt_description;
    private LinearLayout ll_image, ll_video, ll_sound;
    private Button btn_next;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private Marker marker;
    private final float zoom = 16.5f;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final int gps_req = 102, loc_req = 103;
    private String fineLoc = Manifest.permission.ACCESS_FINE_LOCATION;
    private AlertDialog alertDialog;
    private String image_path="",video_path="",sound_path ="";
    private final int img_req = 11,video_req=12,sound_req=13,read_req_image=22,read_req_sound=23,read_req_video=24;
    private final String read_perm = Manifest.permission.READ_EXTERNAL_STORAGE;
    private MediaPlayer mp=null;
    private  SeekBar seekBar;
    private ImageView image_play;
    private TextView tv_total_time,tv_progress_time;
    private Handler handler;
    private Runnable runnable;
    private BookTechnicalActivity activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_address, container, false);
        initView(view);
        return view;
    }

    public static Fragment_Address getInstance() {
        return new Fragment_Address();
    }

    private void initView(View view)
    {
        activity = (BookTechnicalActivity) getActivity();
        edt_address = view.findViewById(R.id.edt_address);
        edt_description = view.findViewById(R.id.edt_description);
        ll_image = view.findViewById(R.id.ll_image);
        ll_video = view.findViewById(R.id.ll_video);
        ll_sound = view.findViewById(R.id.ll_sound);
        btn_next = view.findViewById(R.id.btn_next);
        initMap();
        ll_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckReadPerm("image",read_req_image);
            }
        });

        ll_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckReadPerm("video",read_req_video);
            }
        });

        ll_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckReadPerm("sound",read_req_sound);
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });


    }

    private void CheckData() {
        String m_address = edt_address.getText().toString();
        String m_description =edt_description.getText().toString();

        if (!TextUtils.isEmpty(m_address)&&!TextUtils.isEmpty(m_description))
        {
            Common.CloseKeyBoard(getActivity(),edt_address);
            edt_address.setError(null);
            edt_description.setError(null);
            activity.setAddressData(marker.getPosition().latitude,marker.getPosition().longitude,m_address,image_path,video_path,sound_path,m_description);
        }else
            {
                if (TextUtils.isEmpty(m_address))
                {
                    edt_address.setError(getString(R.string.address_req));
                }else
                    {
                        edt_address.setError(null);

                    }

                if (TextUtils.isEmpty(m_description))
                {
                    edt_description.setError(getString(R.string.desc_req));
                }else
                {
                    edt_description.setError(null);

                }
            }
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
            CheckPermission();

        }
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
    private void AddMarker(double lat, double lng)
    {
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromBitmap(getBitmap())));
        } else {
            marker.setPosition(new LatLng(lat, lng));

        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                AddMarker(latLng.latitude,latLng.longitude);
            }
        });
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
    private void CheckReadPerm(String type,int req)
    {
        String perm [] = {read_perm};

        if (ContextCompat.checkSelfPermission(getActivity(),read_perm)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),perm,req);
        }else
        {
            if (type.equals("image"))
            {
                SelectImage();

            }else if (type.equals("video"))
            {
                SelectVideo();
            }else if (type.equals("sound"))
            {
                SelectSound();
            }
        }
    }

    private void SelectImage()
    {

        Intent intent;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }else
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(intent,img_req);


    }
    private void SelectVideo()
    {
        Intent intent;

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else
            {
                intent = new Intent(Intent.ACTION_GET_CONTENT);

            }

        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,video_req);

    }
    private void SelectSound()
    {
        Intent intent;

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {
            intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else
        {
            intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);

        }

        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,sound_req);
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
        }else if (requestCode==img_req && resultCode== Activity.RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            String path = Common.getImagePath(getActivity(),uri);
            CreateAlertDialogFor_Image_Video_Sound(path,"image");
        }
        else if (requestCode==video_req && resultCode== Activity.RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            String path = Common.getImagePath(getActivity(),uri);
            CreateAlertDialogFor_Image_Video_Sound(path,"video");
        }
        else if (requestCode==sound_req && resultCode== Activity.RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            String path = Common.getImagePath(getActivity(),uri);
            Log.e("path",path);
            CreateAlertDialogFor_Image_Video_Sound(path,"sound");
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
        }else if (requestCode==read_req_image)
        {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage();
                }
            }
        }
        else if (requestCode==read_req_sound)
        {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectSound();
                }
            }
        }
        else if (requestCode==read_req_video)
        {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectVideo();
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

        Log.e("FragAddress lat",location.getLatitude()+"_");
        AddMarker(location.getLatitude(),location.getLongitude());
        StopLocationUpdate();
    }

    private void CreateAlertDialogFor_Image_Video_Sound(final String path, String type)
    {
        alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .create();
        View view = null;
        if (type.equals("image"))
        {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image,null);
            ImageView imageView = view.findViewById(R.id.image);
            Button btn_ok = view.findViewById(R.id.btn_ok);
            Button btn_cancel = view.findViewById(R.id.btn_cancel);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bitmap);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image_path = path;
                    alertDialog.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    image_path="";
                }
            });



        }else if (type.equals("video"))
        {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_video,null);
            final VideoView videoView = view.findViewById(R.id.videoView);
            Button btn_ok = view.findViewById(R.id.btn_ok);
            Button btn_cancel = view.findViewById(R.id.btn_cancel);
            final ImageView image_Play = view.findViewById(R.id.image_play);


            image_Play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image_Play.setVisibility(View.GONE);
                    videoView.setVideoPath(path);
                    final MediaController mc = new MediaController(getActivity());
                    videoView.setMediaController(mc);
                    mc.setAnchorView(videoView);
                    videoView.requestFocus();
                    videoView.start();
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.reset();
                            image_Play.setVisibility(View.VISIBLE);

                        }
                    });

                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    video_path = path;
                    videoView.stopPlayback();
                    alertDialog.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.stopPlayback();

                    alertDialog.dismiss();

                    video_path="";
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
            mp = MediaPlayer.create(getActivity(),Uri.parse(path));
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
                    sound_path = path;
                    alertDialog.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    sound_path="";
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


    @Override
    public void onDestroy()
    {
        StopLocationUpdate();
        super.onDestroy();
    }
}
