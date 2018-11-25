package com.appzone.dolphin.activities.signup_activity.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appzone.dolphin.Models.LocationModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.signin_activity.SignInActivity;
import com.appzone.dolphin.activities.signup_activity.fragments.FragmentMemberSignUp;
import com.appzone.dolphin.activities.signup_activity.fragments.FragmentTechnicalSignUp;
import com.appzone.dolphin.service.LocationUpdateService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private String type="";
    private ImageView image_back;
    private Intent intentService=null;
    private FragmentMemberSignUp fragmentMemberSignUp;
    private FragmentTechnicalSignUp fragmentTechnicalSignUp;
    private final int gps_req = 102,loc_req=103;
    private String fineLoc = Manifest.permission.ACCESS_FINE_LOCATION;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EventBus.getDefault().register(this);
        initView();
        getDataFromIntent();
        CheckPermission();
    }
    private void initView()
    {

        image_back  = findViewById(R.id.image_back);

        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            image_back.setRotation(180f);
        }
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent!=null)
        {
            type = intent.getStringExtra("type");
            updateUI(type);
        }
    }

    private void updateUI(String type)
    {
        if (type.equals("member"))
        {
            fragmentMemberSignUp = FragmentMemberSignUp.getInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_signup_container, fragmentMemberSignUp).commit();
        }else if (type.equals("technical"))
        {
            fragmentTechnicalSignUp = FragmentTechnicalSignUp.getInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_signup_container, fragmentTechnicalSignUp).commit();

        }
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
    private void StartLocationUpdate()
    {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateLocation(LocationModel locationModel)
    {
        if (fragmentMemberSignUp!=null)
        {
            fragmentMemberSignUp.Location(locationModel.getLat(),locationModel.getLng());
        }else if (fragmentTechnicalSignUp!=null)
        {
            fragmentTechnicalSignUp.Location(locationModel.getLat(),locationModel.getLng());
        }
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment:fragmentList)
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment:fragmentList)
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
    protected void onDestroy()
    {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
        if (intentService!=null)
        {
            StopLocationUpdate();
        }
    }
}
