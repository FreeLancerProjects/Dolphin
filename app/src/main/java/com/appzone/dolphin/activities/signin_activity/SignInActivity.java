package com.appzone.dolphin.activities.signin_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.forget_password_activity.ForgetPasswordActivity;
import com.appzone.dolphin.activities.home_activity.activity.HomeActivity;
import com.appzone.dolphin.activities.signup_activity.activity.SignUpActivity;
import com.appzone.dolphin.preferences.Preferences;
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.share.Common;
import com.appzone.dolphin.singletone.UserSingleTone;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private Button btn_member_signup,btn_tec_signup,btn_signin;
    private ImageView image_back;
    private TextView tv_password,tv_skip;
    private EditText edt_phone,edt_password;
    private Preferences preferences;
    private UserSingleTone userSingleTone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
    }

    private void initView() {
        image_back  = findViewById(R.id.image_back);

        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            image_back.setRotation(180f);
        }

        edt_phone  = findViewById(R.id.edt_phone);
        edt_password  = findViewById(R.id.edt_password);
        tv_skip  = findViewById(R.id.tv_skip);

        btn_member_signup  = findViewById(R.id.btn_member_signup);
        btn_tec_signup  = findViewById(R.id.btn_tec_signup);
        tv_password  = findViewById(R.id.tv_password);
        btn_signin  = findViewById(R.id.btn_signin);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_member_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignUpActivity("member");
            }
        });
        btn_tec_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignUpActivity("technical");
            }
        });
        tv_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });

        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });



    }

    private void CheckData() {
        String m_phone = edt_phone.getText().toString();
        String m_password = edt_password.getText().toString();

        if (!TextUtils.isEmpty(m_phone)&&
                m_phone.length()>=7&&
                m_phone.length()<13&&
                !TextUtils.isEmpty(m_password))
        {
            edt_phone.setError(null);
            edt_password.setError(null);
            SignIn(m_phone,m_password);
        }else
            {
                if (TextUtils.isEmpty(m_phone))
                {
                    edt_phone.setError(getString(R.string.phone_req));
                }else if (m_phone.length()<7||m_phone.length()>=13)
                {
                    edt_phone.setError(getString(R.string.inv_phone));
                }else
                    {
                        edt_phone.setError(null);

                    }

                    if (TextUtils.isEmpty(m_password))
                    {
                        edt_password.setError(getString(R.string.password_req));

                    }else
                        {
                            edt_password.setError(null);

                        }
            }
    }

    private void SignIn(String m_phone, String m_password) {
        Common.CloseKeyBoard(this,edt_phone);
        final ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.signng_in));
        dialog.show();
        Api.getService()
                .signIn(m_phone,m_password)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();
                            if (response.body().getSuccess_login()==1)
                            {
                                preferences = Preferences.getInstance();
                                userSingleTone = UserSingleTone.getInstance();
                                UserModel userModel = response.body();
                                preferences.Create_Update_UserModel(SignInActivity.this,userModel);
                                userSingleTone.setUserModel(userModel);
                                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else
                                {
                                    Toast.makeText(SignInActivity.this,R.string.something, Toast.LENGTH_SHORT).show();

                                }


                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        dialog.dismiss();
                        try {
                            Log.e("Error",t.getMessage());
                            Toast.makeText(SignInActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });

    }

    private void navigateToSignUpActivity(String type)
    {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);
        finish();
    }
}
