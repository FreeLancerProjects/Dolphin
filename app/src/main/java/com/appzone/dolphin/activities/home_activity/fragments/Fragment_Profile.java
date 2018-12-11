package com.appzone.dolphin.activities.home_activity.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
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
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.share.Common;
import com.appzone.dolphin.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Profile extends Fragment {
    private UserModel userModel;
    private TextView tv_name,tv_email,tv_phone,tv_job,tv_address,tv_password,tv_update;
    private SimpleRatingBar rate_bar;
    private LinearLayout ll_address,ll_job,ll_rate;
    private CircleImageView image;
    private HomeActivity homeActivity;
    private AlertDialog updatedialog;
    private ImageView img_update_name,img_update_phone,img_update_email,img_update_specialization,img_update_address,img_update_password;
    private final String read_per = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final int read_req = 102;
    private final  int img1_req_profile=12;
    private AlertDialog serviceDialog;
    private List<ServiceModel> serviceModelList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Profile getInstance()
    {
        return new Fragment_Profile();
    }
    private void initView(View view) {
        serviceModelList = new ArrayList<>();
        homeActivity = (HomeActivity) getActivity();
        image = view.findViewById(R.id.image);
        tv_name = view.findViewById(R.id.tv_name);
        tv_phone = view.findViewById(R.id.tv_phone);
        tv_email = view.findViewById(R.id.tv_email);
        tv_job = view.findViewById(R.id.tv_job);
        tv_address = view.findViewById(R.id.tv_address);
        tv_password = view.findViewById(R.id.tv_password);
        tv_update = view.findViewById(R.id.tv_update);

        img_update_name = view.findViewById(R.id.img_update_name);
        img_update_phone = view.findViewById(R.id.img_update_phone);
        img_update_email = view.findViewById(R.id.img_update_email);
        img_update_specialization = view.findViewById(R.id.img_update_specialization);
        img_update_address = view.findViewById(R.id.img_update_address);
        img_update_password = view.findViewById(R.id.img_update_password);




        rate_bar = view.findViewById(R.id.rate_bar);
        ll_address = view.findViewById(R.id.ll_address);
        ll_job = view.findViewById(R.id.ll_job);
        ll_rate = view.findViewById(R.id.ll_rate);


        UpdateUi(this.userModel);


        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
            }
        });

        img_update_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_full_name);
            }
        });

        img_update_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_phone);
            }
        });

        img_update_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_email);
            }
        });

        img_update_specialization.setOnClickListener(new View.OnClickListener() {
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

        img_update_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_address);
            }
        });

        img_update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog_UpdateProfile(Tags.update_password);
            }
        });


    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    private void UpdateUi(UserModel userModel) {

        if (userModel!=null)
        {
            tv_name.setText(userModel.getUser_full_name());
            tv_email.setText(userModel.getUser_email());
            tv_phone.setText(userModel.getUser_phone());

            if (userModel.getUser_type().equals(Tags.USER_MEMBER))
            {
                ll_address.setVisibility(View.VISIBLE);
                ll_rate.setVisibility(View.GONE);
                ll_job.setVisibility(View.GONE);
                tv_address.setText(userModel.getUser_address());
                image.setImageResource(R.drawable.logo);
                tv_update.setVisibility(View.GONE);
            }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
            {
                ll_address.setVisibility(View.GONE);
                ll_rate.setVisibility(View.VISIBLE);
                ll_job.setVisibility(View.VISIBLE);
                tv_update.setVisibility(View.VISIBLE);

                Picasso.with(getActivity()).load(Uri.parse(Tags.IMAGE_PATH+userModel.getUser_photo())).into(image);

                SimpleRatingBar.AnimationBuilder builder = rate_bar.getAnimationBuilder();
                builder.setRatingTarget(Float.parseFloat(String.valueOf(userModel.getRate())))
                        .setRepeatMode(ValueAnimator.REVERSE)
                        .setInterpolator(new AccelerateInterpolator())
                        .setRepeatCount(0)
                        .start();
                if (Locale.getDefault().getLanguage().equals("ar"))
                {
                    tv_job.setText(userModel.getAr_user_specialization());
                }else
                    {
                        tv_job.setText(userModel.getEn_user_specialization());

                    }

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==img1_req_profile&&resultCode== Activity.RESULT_OK&&data!=null)
        {
            Uri uri = data.getData();
            UpdateImage(uri,"user_photo");

        }
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==read_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]!= PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getActivity(),R.string.per_den, Toast.LENGTH_SHORT).show();
                }else
                {
                    SelectImage();


                }
            }
        }
    }

    private void CheckPermission()
    {

        if (ContextCompat.checkSelfPermission(getActivity(),read_per)!= PackageManager.PERMISSION_GRANTED)
        {
            String [] perm = {read_per};
            ActivityCompat.requestPermissions(getActivity(),perm,read_req);
        }else
        {

                SelectImage();


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
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        intent.setType("image/*");
        getActivity().startActivityForResult(intent.createChooser(intent,"Select Image"),img1_req_profile);

    }

    private void CreateAlertDialog_UpdateProfile(final String type)
    {
        updatedialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_profile,null);
        final TextView tv_title = view.findViewById(R.id.tv_title);
        final EditText edt_update = view.findViewById(R.id.edt_update);
        final EditText edt_newPassword = view.findViewById(R.id.edt_newPassword);
        Button btn_update = view.findViewById(R.id.btn_update);
        Button btn_close = view.findViewById(R.id.btn_close);

        if (type.equals(Tags.update_full_name))
        {
            tv_title.setText(R.string.upd_name);
            edt_update.setInputType(InputType.TYPE_CLASS_TEXT);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.name);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_full_name());


            }
        }else if (type.equals(Tags.update_phone))
        {
            tv_title.setText(R.string.upd_phone);
            edt_update.setInputType(InputType.TYPE_CLASS_PHONE);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.phone);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_phone());


            }
        }else if (type.equals(Tags.update_email))
        {
            tv_title.setText(R.string.upd_email);
            edt_update.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.email_address);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_email());


            }
        }else if (type.equals(Tags.update_address))
        {
            tv_title.setText(R.string.upd_address);
            edt_update.setInputType(InputType.TYPE_CLASS_TEXT);
            edt_newPassword.setVisibility(View.GONE);
            edt_update.setHint(R.string.address);
            if (userModel!=null)
            {
                edt_update.setText(userModel.getUser_address());


            }
        }
        else if (type.equals(Tags.update_password))
        {

            tv_title.setText(R.string.upd_password);
            edt_update.setTransformationMethod(PasswordTransformationMethod.getInstance());
            edt_newPassword.setVisibility(View.VISIBLE);
            edt_update.setHint(R.string.old_pass);

        }

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedialog.dismiss();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals(Tags.update_full_name))
                {
                    String m_name = edt_update.getText().toString();
                    if (!TextUtils.isEmpty(m_name))
                    {
                        updatedialog.dismiss();
                        edt_update.setError(null);

                        Common.CloseKeyBoard(getActivity(),edt_update);

                        update_name(m_name);

                    }else
                    {
                        edt_update.setError(getString(R.string.name_req));
                    }

                }
                if (type.equals(Tags.update_address))
                {
                    String m_address = edt_update.getText().toString();
                    if (!TextUtils.isEmpty(m_address))
                    {
                        updatedialog.dismiss();

                        edt_update.setError(null);

                        Common.CloseKeyBoard(getActivity(),edt_update);

                        update_address(m_address);

                    }else
                    {
                        edt_update.setError(getString(R.string.address_req));
                    }

                }
                else if (type.equals(Tags.update_phone))
                {

                    updatedialog.dismiss();

                    String m_phone = edt_update.getText().toString();

                    if (TextUtils.isEmpty(m_phone))
                    {
                        edt_update.setError(getString(R.string.phone_req));


                    }else if (!Patterns.PHONE.matcher(m_phone).matches()||m_phone.length()<6||m_phone.length()>13)
                    {
                        edt_update.setError(getString(R.string.inv_phone));
                    }
                    else
                    {
                        updatedialog.dismiss();

                        Common.CloseKeyBoard(getActivity(),edt_update);
                        edt_update.setError(null);
                        update_phone(m_phone);

                    }
                }else if (type.equals(Tags.update_email))
                {

                    String m_email = edt_update.getText().toString();

                    if (TextUtils.isEmpty(m_email))
                    {
                        edt_update.setError(getString(R.string.email_req));

                    }else if (!Patterns.EMAIL_ADDRESS.matcher(m_email).matches())
                    {
                        edt_update.setError(getString(R.string.inv_email));

                    }else
                    {
                        updatedialog.dismiss();

                        Common.CloseKeyBoard(getActivity(),edt_update);

                        edt_update.setError(null);

                        update_email(m_email);

                    }

                }else if (type.equals(Tags.update_password))
                {

                    Log.e("upd","password");
                    String m_oldPassword = edt_update.getText().toString();
                    String m_newPassword = edt_newPassword.getText().toString();

                    if (!TextUtils.isEmpty(m_oldPassword)&&!TextUtils.isEmpty(m_newPassword))
                    {
                        updatedialog.dismiss();

                        Common.CloseKeyBoard(getActivity(),edt_update);
                        edt_update.setError(null);
                        edt_newPassword.setError(null);

                        update_Password(m_oldPassword,m_newPassword);

                    }else
                    {
                        if (TextUtils.isEmpty(m_oldPassword))
                        {
                            edt_update.setError(getString(R.string.password_req));

                        }else
                        {
                            edt_update.setError(null);

                        }

                        if (TextUtils.isEmpty(m_newPassword))
                        {
                            edt_newPassword.setError(getString(R.string.newpass_req));

                        }
                        else
                        {
                            edt_newPassword.setError(null);

                        }

                    }





                }
            }
        });
        updatedialog.getWindow().getAttributes().windowAnimations=R.style.custom_dialog;
        updatedialog.setView(view);
        updatedialog.show();




    }

    public void setCountryItem(ServiceModel serviceModel) {
        update_specialization(serviceModel.getId_services());
        serviceDialog.dismiss();
    }

    private void UpdateImage(Uri uri,String part_name) {

        final ProgressDialog updateImageDialog = Common.createProgressDialog(getActivity(),getString(R.string.updtng));
        updateImageDialog.show();

        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody country_part=null,address_part=null,city_part=null,specialization_part=null;

        if (userModel.getUser_type().equals(Tags.USER_MEMBER))
        {
            country_part = Common.getRequestBodyText(userModel.getId_country());
            address_part = Common.getRequestBodyText(userModel.getUser_address());

            city_part = Common.getRequestBodyText(userModel.getUser_city());
            specialization_part = Common.getRequestBodyText("");

        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
        {
            country_part = Common.getRequestBodyText("");
            address_part = Common.getRequestBodyText("");
            city_part = Common.getRequestBodyText("");
            specialization_part = Common.getRequestBodyText(userModel.getUser_specialization_id_fk());

        }
        MultipartBody.Part image_part =Common.getMultiPart(getActivity(),uri,part_name);

        Api.getService()
                .UpdateProfileImage(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part,specialization_part,image_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        if (response.isSuccessful())
                        {
                            updateImageDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.ph_em, Toast.LENGTH_SHORT).show();

                            }                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        updateImageDialog.dismiss();
                        try {
                            Log.e("Error",t.getMessage());
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}

                    }
                });



    }

    private void update_name(String newName)
    {
        Log.e("name",newName);
        RequestBody name_part = Common.getRequestBodyText(newName);
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody country_part=null,address_part=null,city_part=null,specialization_part=null;

        if (userModel.getUser_type().equals(Tags.USER_MEMBER))
        {
            country_part = Common.getRequestBodyText(userModel.getId_country());
            address_part = Common.getRequestBodyText(userModel.getUser_address());

            city_part = Common.getRequestBodyText(userModel.getUser_city());
            specialization_part = Common.getRequestBodyText("");

        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
        {
            country_part = Common.getRequestBodyText("");
            address_part = Common.getRequestBodyText("");
            city_part = Common.getRequestBodyText("");
            specialization_part = Common.getRequestBodyText(userModel.getUser_specialization_id_fk());

        }

        final ProgressDialog progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updtng));
        progressDialog.show();

        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part,specialization_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.ph_em, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        try {
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}

                    }
                });
    }

    private void update_phone(String newPhone)
    {
        Log.e("phone",newPhone);
        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(newPhone);
        RequestBody country_part=null,address_part=null,city_part=null,specialization_part=null;

        if (userModel.getUser_type().equals(Tags.USER_MEMBER))
        {
            country_part = Common.getRequestBodyText(userModel.getId_country());
            address_part = Common.getRequestBodyText(userModel.getUser_address());

            city_part = Common.getRequestBodyText(userModel.getUser_city());
            specialization_part = Common.getRequestBodyText("");

        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
        {
            country_part = Common.getRequestBodyText("");
            address_part = Common.getRequestBodyText("");
            city_part = Common.getRequestBodyText("");
            specialization_part = Common.getRequestBodyText(userModel.getUser_specialization_id_fk());

        }
        final ProgressDialog progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updtng));
        progressDialog.show();

        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part,specialization_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.ph_em, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        try {
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}
                    }
                });
    }
    private void update_email(String newEmail)
    {
        Log.e("email",newEmail);

        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(newEmail);
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody country_part=null,address_part=null,city_part=null,specialization_part=null;

        if (userModel.getUser_type().equals(Tags.USER_MEMBER))
        {
            country_part = Common.getRequestBodyText(userModel.getId_country());
            address_part = Common.getRequestBodyText(userModel.getUser_address());

            city_part = Common.getRequestBodyText(userModel.getUser_city());
            specialization_part = Common.getRequestBodyText("");

        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
        {
            country_part = Common.getRequestBodyText("");
            address_part = Common.getRequestBodyText("");
            city_part = Common.getRequestBodyText("");
            specialization_part = Common.getRequestBodyText(userModel.getUser_specialization_id_fk());

        }
        final ProgressDialog progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updtng));
        progressDialog.show();

        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part,specialization_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {

                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.ph_em, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        try {
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}
                    }
                });
    }

    private void update_Password(String oldPass,String newPass)
    {
        final ProgressDialog progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updtng));
        progressDialog.show();

        Api.getService().UpdatePassword(userModel.getUser_id(),oldPass,newPass)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();

                            Log.e("dddd",response.body().getSuccess_update_pass()+"");
                            if (response.body().getSuccess_update_pass()==0)
                            {

                                Toast.makeText(getActivity(), R.string.wrong_oldpass, Toast.LENGTH_SHORT).show();
                            }else if (response.body().getSuccess_update_pass()==1)
                            {


                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.succ, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        try {
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}
                    }
                });
    }

    private void update_specialization(String service_id)
    {
        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());

        RequestBody country_part=null,address_part=null,city_part=null,specialization_part=null;

        if (userModel.getUser_type().equals(Tags.USER_MEMBER))
        {
            country_part = Common.getRequestBodyText(userModel.getId_country());
            address_part = Common.getRequestBodyText(userModel.getUser_address());

            city_part = Common.getRequestBodyText(userModel.getUser_city());
            specialization_part = Common.getRequestBodyText("");

        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
        {
            country_part = Common.getRequestBodyText("");
            address_part = Common.getRequestBodyText("");
            city_part = Common.getRequestBodyText("");
            specialization_part = Common.getRequestBodyText(service_id);

        }

        final ProgressDialog progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updtng));
        progressDialog.show();

        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part,specialization_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {

                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.ph_em, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        try {
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}
                    }
                });

    }
    private void update_address(String address)
    {

        RequestBody name_part = Common.getRequestBodyText(userModel.getUser_full_name());
        RequestBody email_part = Common.getRequestBodyText(userModel.getUser_email());
        RequestBody phone_part = Common.getRequestBodyText(userModel.getUser_phone());
        RequestBody country_part=null,address_part=null,city_part=null,specialization_part=null;

        if (userModel.getUser_type().equals(Tags.USER_MEMBER))
        {
            country_part = Common.getRequestBodyText(userModel.getId_country());
            address_part = Common.getRequestBodyText(address);

            city_part = Common.getRequestBodyText(userModel.getUser_city());
            specialization_part = Common.getRequestBodyText("");

        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
        {
            country_part = Common.getRequestBodyText("");
            address_part = Common.getRequestBodyText("");
            city_part = Common.getRequestBodyText("");
            specialization_part = Common.getRequestBodyText(userModel.getUser_specialization_id_fk());

        }
        final ProgressDialog progressDialog = Common.createProgressDialog(getActivity(),getString(R.string.updtng));
        progressDialog.show();
        Api.getService().UpdateProfileData(userModel.getUser_id(),phone_part,country_part,email_part,name_part,city_part,address_part,specialization_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                        if (response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            if (response.body().getSuccess_update()==1)
                            {
                                UpdateUserData(response.body());
                                Toast.makeText(getActivity(), R.string.succ, Toast.LENGTH_SHORT).show();
                            }
                            else if (response.body().getSuccess_update()==2)
                            {
                                Toast.makeText(getActivity(), R.string.ph_em, Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e("Error",t.getMessage());
                        try {
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}
                    }
                });
    }
    private void  UpdateUserData(UserModel userModel)
    {
        this.userModel = userModel;
        UpdateUi(userModel);
        homeActivity.UpdateUserData(userModel);

    }


}
