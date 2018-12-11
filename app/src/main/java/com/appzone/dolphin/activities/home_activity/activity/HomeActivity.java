package com.appzone.dolphin.activities.home_activity.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appzone.dolphin.Models.LocationModel;
import com.appzone.dolphin.Models.NotificationCountModel;
import com.appzone.dolphin.Models.ResponseModel;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.fragments.Fragment_ContactUs;
import com.appzone.dolphin.activities.home_activity.fragments.Fragment_Home;
import com.appzone.dolphin.activities.home_activity.fragments.Fragment_Home_Container;
import com.appzone.dolphin.activities.home_activity.fragments.Fragment_Notifications;
import com.appzone.dolphin.activities.home_activity.fragments.Fragment_Profile;
import com.appzone.dolphin.activities.home_activity.fragments.Fragment_Terms_Condition;
import com.appzone.dolphin.activities.home_activity.fragments.fragment_my_order.Fragment_MyOrders;
import com.appzone.dolphin.activities.signin_activity.SignInActivity;
import com.appzone.dolphin.preferences.Preferences;
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.service.LocationUpdateService;
import com.appzone.dolphin.share.Common;
import com.appzone.dolphin.singletone.UserSingleTone;
import com.appzone.dolphin.tags.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private TextView tv_title;
    public FragmentManager fragmentManager;
    private Fragment_Home_Container fragment_home_container;
    private Fragment_Home fragment_home;
    private Fragment_Profile fragment_profile;
    private Fragment_MyOrders fragment_myOrders;
    private Fragment_Notifications fragment_notifications;
    private Fragment_ContactUs fragment_contactUs;
    private Fragment_Terms_Condition fragment_terms_condition;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private Preferences preferences;
    private CircleImageView technical_image;
    private ImageView member_image;
    private TextView tv_name;
    private int lastSelectNavItem;
    private Intent intentService;
    private final int gps_req = 102,loc_req=103;
    private String fineLoc = Manifest.permission.ACCESS_FINE_LOCATION;
    private boolean canRead=true;
    private double lat=0.0,lng=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

    }

    private void initView()
    {
        preferences = Preferences.getInstance();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer =  findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        technical_image = view.findViewById(R.id.technical_image);
        member_image = view.findViewById(R.id.member_image);

        tv_name = view.findViewById(R.id.tv_name);

        ///////////////////////////////////////////////////////
        fragmentManager = getSupportFragmentManager();
        tv_title = findViewById(R.id.tv_title);
        Display_FragmentHome_Container();

        updateUi(userModel);
    }

    public void updateUi(UserModel userModel) {

        if (userModel==null)
        {
            member_image.setVisibility(View.VISIBLE);
            member_image.setImageResource(R.drawable.logo);
        }else
            {

                if (!EventBus.getDefault().isRegistered(this))
                {
                    EventBus.getDefault().register(this);

                }
                CheckPermission();
                UpdateToken();
                getUnReadNotificationCount(userModel.getUser_id());
                if (userModel.getUser_type().equals(Tags.USER_MEMBER))
                {
                    member_image.setVisibility(View.VISIBLE);
                    member_image.setImageResource(R.drawable.logo);


                }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
                {
                    technical_image.setVisibility(View.VISIBLE);
                    Picasso.with(this).load(Uri.parse(Tags.IMAGE_PATH+userModel.getUser_photo())).into(technical_image);
                    tv_name.setText(userModel.getUser_full_name());
                }
            }
    }

    public void UpdateUserData(UserModel userModel)
    {
        this.userModel = userModel;
        userSingleTone.setUserModel(userModel);
        preferences.Create_Update_UserModel(this,userModel);
        if (userModel.getUser_type().equals(Tags.USER_MEMBER))
        {
            member_image.setVisibility(View.VISIBLE);
            member_image.setImageResource(R.drawable.logo);


        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
        {
            technical_image.setVisibility(View.VISIBLE);
            Picasso.with(this).load(Uri.parse(Tags.IMAGE_PATH+userModel.getUser_photo())).into(technical_image);
            tv_name.setText(userModel.getUser_full_name());
        }

    }

    private void getUnReadNotificationCount(String user_id)
    {
        if (userModel.getUser_type().equals(Tags.USER_MEMBER)){
            Api.getService()
                    .getClientUnReadNotificationCount(user_id)
                    .enqueue(new Callback<NotificationCountModel>() {
                        @Override
                        public void onResponse(Call<NotificationCountModel> call, Response<NotificationCountModel> response) {
                            if (response.isSuccessful())
                            {
                                canRead=true;
                                if (fragment_home_container!=null)
                                {
                                    fragment_home_container.UpdateNotificationCount(response.body().getAlert_count());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<NotificationCountModel> call, Throwable t) {
                            try {
                                Log.e("Error",t.getMessage());
                            }catch (Exception e){}
                        }
                    });
        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
        {
            Api.getService()
                    .getTechnicalUnReadNotificationCount(user_id)
                    .enqueue(new Callback<NotificationCountModel>() {
                        @Override
                        public void onResponse(Call<NotificationCountModel> call, Response<NotificationCountModel> response) {
                            if (response.isSuccessful())
                            {
                                canRead=true;
                                if (fragment_home_container!=null)
                                {
                                    fragment_home_container.UpdateNotificationCount(response.body().getAlert_count());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<NotificationCountModel> call, Throwable t) {
                            try {
                                Log.e("Error",t.getMessage());
                            }catch (Exception e){}
                        }
                    });
        }

    }
    public void ReadNotification()
    {
        if (canRead)
        {
            if (userModel.getUser_type().equals(Tags.USER_MEMBER))
            {
                Api.getService()
                        .readClientNotification(userModel.getUser_id(),"1")
                        .enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                if (response.isSuccessful())
                                {
                                    canRead=false;
                                    if (response.body().getSuccess_read()==1)
                                    {
                                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        manager.cancelAll();
                                        if (fragment_home_container!=null)
                                        {
                                            fragment_home_container.UpdateNotificationCount(0);
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseModel> call, Throwable t) {
                                try {
                                    Log.e("Error",t.getMessage());
                                }catch (Exception e) {}
                            }
                        });
            }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
            {
                Api.getService()
                        .readTechnicalNotification(userModel.getUser_id(),"1")
                        .enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                if (response.isSuccessful())
                                {
                                    canRead=false;
                                    if (response.body().getSuccess_read()==1)
                                    {
                                        if (fragment_home_container!=null)
                                        {
                                            fragment_home_container.UpdateNotificationCount(0);
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseModel> call, Throwable t) {
                                try {
                                    Log.e("Error",t.getMessage());
                                }catch (Exception e) {}
                            }
                        });
            }

        }

    }
    private void StartLocationUpdate()
    {

        Log.e("ssssssssssssssssss","ssssssssssssssssssss");
        intentService = new Intent(this, LocationUpdateService.class);
        startService(intentService);
    }
    private void StopLocationUpdate()
    {
        if (intentService!=null)
        {
            stopService(intentService);
        }
    }
    public void UpdateTitle(String title)
    {
        tv_title.setText(title);
    }
    private void CheckPermission()
    {
        if (ContextCompat.checkSelfPermission(this,fineLoc)!= PackageManager.PERMISSION_GRANTED)
        {
            String [] perm = {fineLoc};
            ActivityCompat.requestPermissions(this,perm,loc_req);
        }else
        {
            if (isGpsOpen())
            {
                StartLocationUpdate();
            }else
            {
                CreateGpsDialog();
            }
        }
    }
    private boolean isGpsOpen()
    {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (manager!=null&&manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            return true;
        }

        return false;
    }
    private void OpenGps()
    {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent,gps_req);
    }
    private void CreateGpsDialog()
    {
        final AlertDialog gps_dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog,null);
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

        gps_dialog.getWindow().getAttributes().windowAnimations=R.style.custom_dialog;
        gps_dialog.setView(view);
        gps_dialog.setCanceledOnTouchOutside(false);
        gps_dialog.show();

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateLocationListning(LocationModel locationModel)
    {
        Log.e("ssssss","laaaaaaaaaaaaaaaaaaaaaaaat");
        lat = locationModel.getLat();
        lng = locationModel.getLng();
        UpdateLocation(locationModel);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ListenToNotification(NotificationCountModel notificationCountModel)
    {
        if (fragment_notifications!=null)
        {
            if (fragment_notifications.isAdded())
            {
                if (fragment_notifications.isVisible())
                {
                    Log.e("23","11111");
                    ReadNotification();
                    if (userModel.getUser_type().equals(Tags.USER_MEMBER))
                    {
                        fragment_notifications.getClientNotification(userModel.getUser_id());
                    }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
                    {
                        fragment_notifications.getTechnicalNotification(userModel.getUser_id());
                    }
                }else
                    {
                        canRead=true;
                        getUnReadNotificationCount(userModel.getUser_id());
                        if (userModel.getUser_type().equals(Tags.USER_MEMBER))
                        {
                            fragment_notifications.getClientNotification(userModel.getUser_id());
                        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
                        {
                            fragment_notifications.getTechnicalNotification(userModel.getUser_id());
                        }
                    }
            }else
                {
                    canRead=true;
                    getUnReadNotificationCount(userModel.getUser_id());
                }
        }else
            {
                canRead=true;
                getUnReadNotificationCount(userModel.getUser_id());
            }
    }
    private void UpdateLocation(LocationModel locationModel)
    {
        Log.e("home_lat",locationModel.getLat()+"_");
        Api.getService()
                .updateLocation(userModel.getUser_id(),locationModel.getLat(),locationModel.getLng())
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess_location()==1)
                            {
                                Log.e("Location_update","Location updated successfully");
                            }else
                                {
                                    Log.e("Location_update","Location updated failed");

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }
    private void UpdateToken()
    {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                if (task.isSuccessful())
                {
                    String token = task.getResult().getToken();

                    Api.getService()
                            .updateToken(userModel.getUser_id(),token)
                            .enqueue(new Callback<ResponseModel>() {
                                @Override
                                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                    if (response.isSuccessful())
                                    {
                                        if (response.body().getSuccess_token_id()==1)
                                        {
                                            Log.e("token updated","Token updated successfully");
                                        }else
                                            {
                                                Log.e("token updated","Token updated failed");

                                            }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseModel> call, Throwable t) {
                                   try {
                                       Log.e("Error",t.getMessage());

                                   }catch (Exception e){}
                                }
                            });
                }
            }
        });
    }
    public void Display_FragmentHome_Container()
    {
        if (fragment_home_container==null)
        {
            fragment_home_container = Fragment_Home_Container.getInstance();
        }
        if (fragment_home_container.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_home_container).commit();

        }else
            {
                fragmentManager.beginTransaction().add(R.id.fragment_activity_home_container,fragment_home_container,"fragment_home_container").addToBackStack("fragment_home_container").commit();

            }

            if (fragment_contactUs!=null&&fragment_contactUs.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_contactUs).commit();
            }

            if (fragment_terms_condition!=null&&fragment_terms_condition.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_terms_condition).commit();

            }


    }
    public void Display_FragmentHome()
    {
        Display_FragmentHome_Container();
        if (fragment_home==null)
        {
            fragment_home = Fragment_Home.getInstance();
        }

        if (!fragment_home.isAdded())
        {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_home,"fragment_home").addToBackStack("fragment_home").commit();

        }else
            {
                fragment_home = (Fragment_Home) fragmentManager.findFragmentByTag("fragment_home");
                fragmentManager.beginTransaction().show(fragment_home).commit();
            }


        lastSelectNavItem =0;
        fragment_home_container.setSelectedItem(0);
        UpdateTitle(getString(R.string.home));
        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigationView.getMenu().getItem(0).setChecked(true);
                    }
                },500);


        if (fragment_profile!=null&&fragment_profile.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_profile).commit();
        }

        if (fragment_myOrders!=null&&fragment_myOrders.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_myOrders).commit();
        }
        if (fragment_notifications!=null&&fragment_notifications.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_notifications).commit();
        }



    }
    public void Display_FragmentProfile()
    {
        if (userModel==null) {

            Common.CreateUserNotSignInAlertDialog(this,getString(R.string.si_su));
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigationView.getMenu().getItem(0).setChecked(true);
                        }
                    },500);
            if (fragment_home_container!=null)
            {
                fragment_home_container.setSelectedItem(0);
            }


        }else
        {
            Display_FragmentHome_Container();
            lastSelectNavItem =1;
            fragment_home_container.setSelectedItem(1);

            UpdateTitle(getString(R.string.profile));
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigationView.getMenu().getItem(1).setChecked(true);
                        }
                    },500);


            if (fragment_home!=null&&fragment_home.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }

            if (fragment_myOrders!=null&&fragment_myOrders.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_myOrders).commit();
            }

            if (fragment_notifications!=null&&fragment_notifications.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_notifications).commit();
            }

            if (fragment_profile==null)
            {
                fragment_profile = Fragment_Profile.getInstance();
                fragment_profile.setUserModel(userModel);
            }else
            {
                fragment_profile.setUserModel(userModel);

            }
            if (fragment_profile.isAdded())
            {
                fragment_profile = (Fragment_Profile) fragmentManager.findFragmentByTag("fragment_profile");
                fragmentManager.beginTransaction().show(fragment_profile).commit();
            }else
            {

                fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_profile,"fragment_profile").addToBackStack("fragment_profile").commit();

            }
        }

    }
    public void Display_FragmentNotifications()
    {
        if (userModel==null) {

            Common.CreateUserNotSignInAlertDialog(this,getString(R.string.si_su));
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigationView.getMenu().getItem(0).setChecked(true);
                        }
                    },500);
            if (fragment_home_container!=null)
            {
                fragment_home_container.setSelectedItem(0);
            }


        }else
        {
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigationView.getMenu().getItem(0).setChecked(true);
                        }
                    },500);
            Display_FragmentHome_Container();
            lastSelectNavItem =3;
            fragment_home_container.setSelectedItem(3);

            UpdateTitle(getString(R.string.notification));

            if (fragment_home!=null&&fragment_home.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }

            if (fragment_myOrders!=null&&fragment_myOrders.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_myOrders).commit();
            }

            if (fragment_profile!=null&&fragment_profile.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }


            if (fragment_notifications==null)
            {
                fragment_notifications = Fragment_Notifications.getInstance(userModel);
            }
            if (fragment_notifications.isAdded())
            {
                fragment_notifications = (Fragment_Notifications) fragmentManager.findFragmentByTag("fragment_notifications");
                fragmentManager.beginTransaction().show(fragment_notifications).commit();
            }else
            {

                fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_notifications,"fragment_notifications").addToBackStack("fragment_notifications").commit();

            }

        }
    }
    public void Display_FragmentMyOrder()
    {
        if (userModel==null)
        {
            Common.CreateUserNotSignInAlertDialog(this,getString(R.string.si_su));
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigationView.getMenu().getItem(0).setChecked(true);
                        }
                    },500);
            if (fragment_home_container!=null)
            {
                fragment_home_container.setSelectedItem(0);
            }
        }else
        {
            Display_FragmentHome_Container();
            lastSelectNavItem =2;
            fragment_home_container.setSelectedItem(2);
            UpdateTitle(getString(R.string.my_orders));
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigationView.getMenu().getItem(2).setChecked(true);
                        }
                    },500);

            if (fragment_home!=null&&fragment_home.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_home).commit();
            }

            if (fragment_profile!=null&&fragment_profile.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_profile).commit();
            }

            if (fragment_notifications!=null&&fragment_notifications.isAdded())
            {
                fragmentManager.beginTransaction().hide(fragment_notifications).commit();
            }
            if (fragment_myOrders==null)
            {
                fragment_myOrders = Fragment_MyOrders.getInstance();
                fragment_myOrders.setUserModel(userModel);
                fragment_myOrders.setLat_Lng(lat,lng);
            }else
            {
                fragment_myOrders.setUserModel(userModel);
                fragment_myOrders.setLat_Lng(lat,lng);

            }
            if (fragment_myOrders.isAdded())
            {
                fragment_myOrders = (Fragment_MyOrders) fragmentManager.findFragmentByTag("fragment_my_order");
                fragmentManager.beginTransaction().show(fragment_myOrders).commit();
            }else
            {

                fragmentManager.beginTransaction().add(R.id.fragment_home_container,fragment_myOrders,"fragment_my_order").addToBackStack("fragment_my_order").commit();

            }
        }

    }
    public void Display_FragmentContactUs()
    {
        if (fragment_contactUs==null)
        {
            fragment_contactUs = Fragment_ContactUs.getInstance();
        }

        if (fragment_contactUs.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_contactUs).commit();
        }else
            {
                fragmentManager.beginTransaction().add(R.id.fragment_activity_home_container,fragment_contactUs,"fragment_contactUs").addToBackStack("fragment_contactUs").commit();
            }
        UpdateTitle(getString(R.string.contact_us));
        if (fragment_home_container!=null&&fragment_home_container.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_home_container).commit();
        }

        if (fragment_terms_condition!=null&&fragment_terms_condition.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_terms_condition).commit();

        }
    }

    public void UpdateTechnicalCurrentNotification()
    {
        if (fragment_myOrders!=null&&fragment_myOrders.isAdded())
        {
            fragment_myOrders.UpdateTechnicalCurrentOrder();
        }


    }
    public void UpdateTechnicalPreviousNotification()
    {
        if (fragment_myOrders!=null&&fragment_myOrders.isAdded())
        {
            fragment_myOrders.UpdateTechnicalPreviousOrder();
        }
    }
    public void Display_FragmentTermsCondition()
    {
        if (fragment_terms_condition==null)
        {
            fragment_terms_condition = Fragment_Terms_Condition.getInstance();
        }

        if (fragment_terms_condition.isAdded())
        {
            fragmentManager.beginTransaction().show(fragment_terms_condition).commit();
        }else
        {
            fragmentManager.beginTransaction().add(R.id.fragment_activity_home_container,fragment_terms_condition,"fragment_terms_condition").addToBackStack("fragment_terms_condition").commit();
        }
        UpdateTitle(getString(R.string.terms_and_condition));
        if (fragment_home_container!=null&&fragment_home_container.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_home_container).commit();
        }

        if (fragment_contactUs!=null&&fragment_contactUs.isAdded())
        {
            fragmentManager.beginTransaction().hide(fragment_contactUs).commit();

        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.home) {
            Display_FragmentHome_Container();
        } else if (id == R.id.profile) {
            if (fragment_home_container!=null)
            {
                Display_FragmentProfile();
            }

        } else if (id == R.id.my_order) {
            if (fragment_home_container!=null)
            {
                Display_FragmentMyOrder();
            }
        } else if (id == R.id.contact_us) {
            Display_FragmentContactUs();
        } else if (id == R.id.rule) {
            Display_FragmentTermsCondition();
        } else if (id == R.id.logout) {

            logout();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void logout()
    {
        if (userModel==null)
        {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }else
            {
                final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.loggng_out));
                dialog.show();
                Api.getService()
                        .logout(userModel.getUser_id())
                        .enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                if (response.isSuccessful())
                                {
                                    dialog.dismiss();
                                    if (response.body().getSuccess_logout()==1)
                                    {
                                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        manager.cancelAll();
                                        userModel = null;
                                        userSingleTone.ClearUserModel();
                                        preferences.ClearData(HomeActivity.this);
                                        fragmentManager.popBackStack("fragment_home_container",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                        Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }else
                                        {
                                            Toast.makeText(HomeActivity.this,R.string.something, Toast.LENGTH_SHORT).show();

                                        }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseModel> call, Throwable t) {
                                try {
                                    dialog.dismiss();

                                    Log.e("Error",t.getMessage());
                                    Toast.makeText(HomeActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                                }catch (Exception e){}
                            }
                        });
            }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode==gps_req)
        {
            if (isGpsOpen())
            {
                StartLocationUpdate();
            }else
            {
                CreateGpsDialog();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList)
        {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (requestCode==loc_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (isGpsOpen())
                    {
                        StartLocationUpdate();
                    }else
                    {
                        CreateGpsDialog();
                    }
                }else
                {
                    Toast.makeText(this, R.string.acc_loc_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public void onBackPressed()
    {
        Log.e("back_stack",fragmentManager.getBackStackEntryCount()+"count");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (fragment_home_container!=null&&fragment_home_container.isVisible())
        {
            fragmentManager.popBackStack("fragment_home_container",FragmentManager.POP_BACK_STACK_INCLUSIVE);
            finish();
        }else if (fragment_home_container!=null&&!fragment_home_container.isVisible())
        {
            Display_FragmentHome_Container();
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navigationView.getMenu().getItem(lastSelectNavItem).setChecked(true);
                        }
                        },500);
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }

        StopLocationUpdate();
    }


}
