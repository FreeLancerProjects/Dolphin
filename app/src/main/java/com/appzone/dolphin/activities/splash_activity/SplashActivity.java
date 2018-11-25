package com.appzone.dolphin.activities.splash_activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.activity.HomeActivity;
import com.appzone.dolphin.activities.signin_activity.SignInActivity;
import com.appzone.dolphin.preferences.Preferences;
import com.appzone.dolphin.singletone.UserSingleTone;
import com.appzone.dolphin.tags.Tags;

public class SplashActivity extends AppCompatActivity {

    private Animation animation;
    private FrameLayout fr;
    private Preferences preferences;
    private String session="";
    private ProgressBar progBar;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = Preferences.getInstance();
        session = preferences.getSession(this);

        progBar = findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.sel_color), PorterDuff.Mode.SRC_IN);
        
        fr = findViewById(R.id.fr);
        animation = AnimationUtils.loadAnimation(this,R.anim.fade);
        fr.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (session.equals(Tags.LOGIN_STATE))
                {
                    userSingleTone = UserSingleTone.getInstance();
                    userModel = preferences.getUserModel(SplashActivity.this);
                    userSingleTone.setUserModel(userModel);
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                }else
                    {
                        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
