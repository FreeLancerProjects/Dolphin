package com.appzone.dolphin.activities.signup_activity.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appzone.dolphin.Models.ServiceModel;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.activity.HomeActivity;
import com.appzone.dolphin.adapters.SignUpServiceAdapter;
import com.appzone.dolphin.preferences.Preferences;
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.share.Common;
import com.appzone.dolphin.singletone.UserSingleTone;
import com.appzone.dolphin.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentTechnicalSignUp extends Fragment{
    private double myLat = 0.0,myLng = 0.0;
    private EditText edt_name,edt_email,edt_phone,edt_password;
    private TextView tv_job;
    private LinearLayout ll_add_photo,ll_job;
    private ImageView image;
    private Button btn_signup;
    private String job="";
    private final String readPerm = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final int read_req = 11,img_req=12;
    private Uri uri = null;
    private AlertDialog serviceDialog;
    private List<ServiceModel> serviceModelList;
    private Preferences preferences;
    private UserSingleTone userSingleTone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technical_signup,container,false);
        initView(view);
        return view;
    }

    public static FragmentTechnicalSignUp getInstance()
    {
        return new FragmentTechnicalSignUp();
    }
    private void initView(View view) {
        serviceModelList = new ArrayList<>();
        edt_name = view.findViewById(R.id.edt_name);
        edt_email = view.findViewById(R.id.edt_email);
        edt_phone = view.findViewById(R.id.edt_phone);
        edt_password = view.findViewById(R.id.edt_password);
        tv_job = view.findViewById(R.id.tv_job);
        ll_job = view.findViewById(R.id.ll_job);
        ll_add_photo = view.findViewById(R.id.ll_add_photo);

        image = view.findViewById(R.id.image);
        btn_signup = view.findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });

        ll_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkReadPermission();
            }
        });

        ll_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceModelList.size()>0)
                {
                    CreateServiceDialog(serviceModelList);
                }else
                    {
                        getServices();
                    }
            }
        });


    }

    private void getServices() {

       final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.lodng_spec));
       dialog.show();
       Api.getService()
                .getAllServices()
                .enqueue(new Callback<List<ServiceModel>>() {
                    @Override
                    public void onResponse(Call<List<ServiceModel>> call, Response<List<ServiceModel>> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();
                            serviceModelList.clear();
                            if (response.body().size()>0)
                            {
                                serviceModelList.addAll(response.body());
                                CreateServiceDialog(response.body());

                            }else
                                {
                                    Toast.makeText(getActivity(), R.string.no_spec, Toast.LENGTH_SHORT).show();
                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ServiceModel>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        dialog.dismiss();

                        try {
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
    }

    private void SelectImage() {

        Intent intent;

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {

            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        }else
            {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
            }

        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,img_req);


    }

    private void CheckData() {

        String m_name = edt_name.getText().toString();
        String m_email = edt_email.getText().toString();
        String m_phone = edt_phone.getText().toString();
        String m_password = edt_password.getText().toString();

        if(!TextUtils.isEmpty(m_name)&&
                !TextUtils.isEmpty(m_email)&&
                Patterns.EMAIL_ADDRESS.matcher(m_email).matches()&&
                !TextUtils.isEmpty(m_phone)&&
                m_phone.length()>=7&&m_phone.length()<13&&
                !TextUtils.isEmpty(m_password)&&
                !TextUtils.isEmpty(job)&&
                uri!=null

                )
        {
            SignUp(m_name,m_email,m_phone,m_password,job,uri);
        }else
        {
            if (TextUtils.isEmpty(m_name))
            {
                edt_name.setError(getString(R.string.name_req));
            }else
            {
                edt_name.setError(null);
            }

            if (TextUtils.isEmpty(m_email))
            {
                edt_email.setError(getString(R.string.email_req));
            }else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
            {
                edt_email.setError(getString(R.string.inv_email));
            }else
            {
                edt_email.setError(null);
            }

            if (TextUtils.isEmpty(m_phone))
            {
                edt_phone.setError(getString(R.string.phone_req));
            }else if (m_phone.length()<=6||m_phone.length()>=13)
            {
                edt_phone.setError(getString(R.string.inv_phone));
            }else
            {
                edt_phone.setError(null);
            }



            if (TextUtils.isEmpty(job))
            {
                tv_job.setError(getString(R.string.country_req));
            }else
                {
                    tv_job.setError(null);

                }



            if (TextUtils.isEmpty(m_password))
            {
                edt_password.setError(getString(R.string.password_req));
            }else
            {
                edt_password.setError(null);
            }

            if (uri==null)
            {
                Toast.makeText(getActivity(), R.string.ch_photo, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkReadPermission()
    {
        if (ContextCompat.checkSelfPermission(getActivity(),readPerm)!= PackageManager.PERMISSION_GRANTED)
        {
            String [] perm = {readPerm};
            ActivityCompat.requestPermissions(getActivity(),perm,read_req);
        }else {
            SelectImage();
        }
    }
    private void SignUp(String m_name, String m_email, String m_phone, String m_password, String job, Uri uri) {
        Common.CloseKeyBoard(getActivity(),edt_name);

        RequestBody name_part = Common.getRequestBodyText(m_name);
        RequestBody email_part = Common.getRequestBodyText(m_email);
        RequestBody phone_part = Common.getRequestBodyText(m_phone);
        RequestBody password_part = Common.getRequestBodyText(m_password);
        RequestBody country_part = Common.getRequestBodyText("");
        RequestBody city_part = Common.getRequestBodyText("");

        RequestBody job_part = Common.getRequestBodyText(job);
        RequestBody address_part = Common.getRequestBodyText("");
        RequestBody token_part = Common.getRequestBodyText("");
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(myLat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(myLng));
        RequestBody type_part = Common.getRequestBodyText(Tags.USER_TECHNICAL);


        MultipartBody.Part image_part = Common.getMultiPart(getActivity(),uri,"user_photo");

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.signing_up));
        dialog.show();

        Api.getService()
                .technicalSignUp(password_part,phone_part,country_part,email_part,name_part,token_part,lat_part,lng_part,city_part,address_part,job_part,type_part,image_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();
                            if (response.body().getSuccess_signup()==1)
                            {
                                preferences = Preferences.getInstance();
                                userSingleTone = UserSingleTone.getInstance();
                                UserModel userModel = response.body();
                                preferences.Create_Update_UserModel(getActivity(),userModel);
                                userSingleTone.setUserModel(userModel);
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }else if (response.body().getSuccess_signup()==2)
                            {
                                Toast.makeText(getActivity(), R.string.ph_em, Toast.LENGTH_SHORT).show();
                            }else
                            {
                                Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        dialog.dismiss();
                        try {
                            Log.e("Error",t.getMessage());
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });

    }

    private void CreateServiceDialog(List<ServiceModel> serviceModelList)
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.country_city_dialog,null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.ch_country);
        RecyclerView recView = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        RecyclerView.Adapter adapter = new SignUpServiceAdapter(getActivity(),serviceModelList,this);
        recView.setAdapter(adapter);
        serviceDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();

        serviceDialog.getWindow().getAttributes().windowAnimations = R.style.custom_dialog;
        serviceDialog.setCanceledOnTouchOutside(false);
        serviceDialog.setView(view);
        serviceDialog.show();

    }

    public void Location(double lat, double lng)
    {
        myLat = lat;
        myLng = lng;
        Log.e("lat",lat+"_");
        Log.e("lng",lng+"_");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==img_req&&resultCode== Activity.RESULT_OK&&data!=null)
        {
            uri = data.getData();
            Bitmap bitmap = BitmapFactory.decodeFile(Common.getImagePath(getActivity(),uri));
            image.setImageBitmap(bitmap);
            image.setVisibility(View.VISIBLE);
            
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==read_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage();
                }else 
                    {
                        Toast.makeText(getActivity(), R.string.acc_photo_denied, Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    public void setCountryItem(ServiceModel serviceModel) {
        job = serviceModel.getId_services();
        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            tv_job.setText(serviceModel.getAr_services_title());
        }else
            {
                tv_job.setText(serviceModel.getEn_services_title());
            }
            serviceDialog.dismiss();
    }
}
