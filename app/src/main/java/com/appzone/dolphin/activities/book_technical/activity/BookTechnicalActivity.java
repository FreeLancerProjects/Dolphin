package com.appzone.dolphin.activities.book_technical.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appzone.dolphin.Models.ResponseModel;
import com.appzone.dolphin.Models.ServiceModel;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.book_technical.fragments.Fragment_Address;
import com.appzone.dolphin.activities.book_technical.fragments.Fragment_Book_Terms_Condition;
import com.appzone.dolphin.activities.book_technical.fragments.Fragment_Date;
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.share.Common;
import com.appzone.dolphin.singletone.UserSingleTone;
import com.appzone.dolphin.tags.Tags;

import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookTechnicalActivity extends AppCompatActivity {
    private Toolbar toolBar;
    private ImageView image_back;
    private TextView tv_title;
    private ServiceModel serviceModel;
    public  FragmentManager fragmentManager;
    private Fragment_Address fragment_address;
    private Fragment_Book_Terms_Condition fragment_book_terms_condition;
    private String order_type="",order_image_path="",order_voice_path="",order_video_path="",order_hours="",order_details="",order_date="",order_address="";
    private double order_address_lat=0.0,order_address_long=0.0;
    private Fragment_Date fragment_date;
    private UserModel userModel;
    private UserSingleTone userSingleTone;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_technical);
        initView();
        getDataFromIntent();
    }
    private void initView()
    {
        fragmentManager = getSupportFragmentManager();
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        toolBar = findViewById(R.id.toolBar);
        image_back = findViewById(R.id.image_back);
        tv_title = findViewById(R.id.tv_title);

        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            image_back.setRotation(180f);
        }

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent!=null)
        {
            serviceModel = (ServiceModel) intent.getSerializableExtra("data");
            UpdateUI(serviceModel);
        }
    }
    private void UpdateUI(ServiceModel serviceModel)
    {
        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            tv_title.setText(serviceModel.getAr_services_title());
        }else
            {
                tv_title.setText(serviceModel.getEn_services_title());

            }

        DisplayFragmentAddress();



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

    }

    public void setAddressData(double order_address_lat,double order_address_long,String order_address,String order_image_path,String order_video_path,String order_voice_path ,String order_details)
    {

        this.order_address_lat = order_address_lat;
        this.order_address_long = order_address_long;
        this.order_address = order_address;
        this.order_image_path = order_image_path;
        this.order_video_path = order_video_path;
        this.order_voice_path = order_voice_path;
        this.order_details = order_details;
        DisplayFragmentDate();

    }

    public void setDateData(String order_type,String order_hours,String order_date)
    {
        this.order_type = order_type;
        this.order_hours = order_hours;
        this.order_date = order_date;

        Display_Terms_Condition();
    }

    private void DisplayFragmentAddress()
    {
        toolBar.setVisibility(View.VISIBLE);
        if (fragment_address==null)
        {
            fragment_address = Fragment_Address.getInstance();
        }


        if (!fragment_address.isAdded())
        {
            fragmentManager.beginTransaction().add(R.id.book_fragment_container,fragment_address,"fragment_address").addToBackStack("fragment_address").commit();
        }else
        {
            fragmentManager.beginTransaction().show(fragment_address).commit();
        }
    }
    private void DisplayFragmentDate()
    {
        if (fragment_address!=null&&fragment_address.isVisible())
        {
            fragmentManager.beginTransaction().hide(fragment_address).commit();
        }
        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            if (fragment_date==null)
            {
                fragment_date = Fragment_Date.getInstance(serviceModel.getAr_services_title());

            }

        }else
        {
            if (fragment_date==null)
            {
                fragment_date = Fragment_Date.getInstance(serviceModel.getEn_services_title());

            }

        }
        toolBar.setVisibility(View.GONE);


        if (!fragment_date.isAdded())
        {
            fragmentManager.beginTransaction().add(R.id.book_fragment_container,fragment_date,"fragment_date").addToBackStack("fragment_date").commit();

        }else
        {
            fragmentManager.beginTransaction().show(fragment_date).commit();
        }


    }
    private void Display_Terms_Condition()
    {

        if (fragment_book_terms_condition==null)
        {
            fragment_book_terms_condition = Fragment_Book_Terms_Condition.getInstance();

        }

        if (fragment_date!=null)
        {
            fragmentManager.beginTransaction().hide(fragment_date).commit();
        }

        if (!fragment_book_terms_condition.isAdded())
        {
            fragmentManager.beginTransaction().add(R.id.book_fragment_container,fragment_book_terms_condition,"fragment_book_terms_condition").addToBackStack("fragment_book_terms_condition").commit();
        }else
            {
                fragmentManager.beginTransaction().show(fragment_book_terms_condition).commit();
            }
    }

    public void Book()
    {
        Log.e("order_type",order_type);
        Log.e("order_image_path",order_image_path+"_");
        Log.e("order_voice_path",order_voice_path+"_");
        Log.e("order_video_path",order_video_path+"_");
        Log.e("order_hours",order_hours);
        Log.e("order_details",order_details);
        Log.e("order_date",order_date);
        Log.e("order_address",order_address);
        Log.e("order_address_lat",order_address_lat+"_");
        Log.e("order_address_long",order_address_long+"_");

        if (userModel==null)
        {
            Common.CreateUserNotSignInAlertDialog(this,getString(R.string.si_su));
        }else
            {
                Log.e("user_type",userModel.getUser_type());
            if(userModel.getUser_type().equals(Tags.USER_TECHNICAL))
            {
                Common.CreateUserNotSignInAlertDialog(this,getString(R.string.serv_aval_client));
            }
            else
                {
                    ////////////////////////////////////////////////////////////////////////////////////////////

                    if (!TextUtils.isEmpty(order_image_path)&&!TextUtils.isEmpty(order_voice_path)&&!TextUtils.isEmpty(order_video_path))
                    {
                        Book_Image_Video_Sound(order_type,order_image_path,order_voice_path,order_video_path,order_hours,order_details,order_date,order_address,order_address_lat,order_address_long);
                    }else if (TextUtils.isEmpty(order_image_path)&&TextUtils.isEmpty(order_voice_path)&&TextUtils.isEmpty(order_video_path))
                    {
                        BookWithoutMedia(order_type,order_hours,order_details,order_date,order_address,order_address_lat,order_address_long);

                    }else if (!TextUtils.isEmpty(order_image_path)&&TextUtils.isEmpty(order_voice_path)&&TextUtils.isEmpty(order_video_path))
                    {
                        Book_With_Image_Only(order_type,order_image_path,order_hours,order_details,order_date,order_address,order_address_lat,order_address_long);


                    }else if (TextUtils.isEmpty(order_image_path)&&!TextUtils.isEmpty(order_voice_path)&&TextUtils.isEmpty(order_video_path))
                    {
                        Book_With_Voice_Only(order_type,order_voice_path,order_hours,order_details,order_date,order_address,order_address_lat,order_address_long);

                    }
                    else if (TextUtils.isEmpty(order_image_path)&&TextUtils.isEmpty(order_voice_path)&&!TextUtils.isEmpty(order_video_path))
                    {
                        Book_With_Video_Only(order_type,order_video_path,order_hours,order_details,order_date,order_address,order_address_lat,order_address_long);

                    }
                    else if (!TextUtils.isEmpty(order_image_path)&&!TextUtils.isEmpty(order_voice_path)&&TextUtils.isEmpty(order_video_path))
                    {
                        Book_Image_Sound(order_type,order_image_path,order_voice_path,order_hours,order_details,order_date,order_address,order_address_lat,order_address_long);

                    }
                    else if (!TextUtils.isEmpty(order_image_path)&&TextUtils.isEmpty(order_voice_path)&&!TextUtils.isEmpty(order_video_path))
                    {
                        Book_Image_Video(order_type,order_image_path,order_video_path,order_hours,order_details,order_date,order_address,order_address_lat,order_address_long);

                    }
                    else if (TextUtils.isEmpty(order_image_path)&&!TextUtils.isEmpty(order_voice_path)&&!TextUtils.isEmpty(order_video_path))
                    {
                        Book_Video_Sound(order_type,order_voice_path,order_video_path,order_hours,order_details,order_date,order_address,order_address_lat,order_address_long);

                    }


                }
        }

    }

    private void Book_Video_Sound(String order_type, String order_voice_path, String order_video_path, String order_hours, String order_details, String order_date, String order_address, double order_address_lat, double order_address_long) {

        RequestBody order_type_part = Common.getRequestBodyText(order_type);
        MultipartBody.Part voice_part = Common.getMultiPartVoice(order_voice_path,"order_voice");
        MultipartBody.Part video_part = Common.getMultiPartVideoFromPath(order_video_path,"order_video");
        RequestBody hour_part = Common.getRequestBodyText(order_hours);
        RequestBody details_part = Common.getRequestBodyText(order_details);
        RequestBody date_part = Common.getRequestBodyText(order_date);
        RequestBody address_part = Common.getRequestBodyText(order_address);
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(order_address_lat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(order_address_long));


        final ProgressDialog dialog  = Common.createProgressDialog(this,getString(R.string.bokng));
        dialog.show();

        Api.getService()
                .book_video_sound(userModel.getUser_id(),serviceModel.getId_services(),order_type_part,hour_part,details_part,date_part,address_part,lat_part,lng_part,voice_part,video_part)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess_resevation()==1)
                            {
                                dialog.dismiss();
                                fragmentManager.popBackStack("fragment_address",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                Toast.makeText(BookTechnicalActivity.this, R.string.bok_succ, Toast.LENGTH_SHORT).show();
                                finish();
                            }else
                            {
                                Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e) {}
                    }
                });
    }

    private void Book_Image_Video(String order_type, String order_image_path, String order_video_path, String order_hours, String order_details, String order_date, String order_address, double order_address_lat, double order_address_long) {

        RequestBody order_type_part = Common.getRequestBodyText(order_type);
        MultipartBody.Part image_part = Common.getMultiPartImageFromPath(order_image_path,"order_image");
        MultipartBody.Part video_part = Common.getMultiPartVideoFromPath(order_video_path,"order_video");
        RequestBody hour_part = Common.getRequestBodyText(order_hours);
        RequestBody details_part = Common.getRequestBodyText(order_details);
        RequestBody date_part = Common.getRequestBodyText(order_date);
        RequestBody address_part = Common.getRequestBodyText(order_address);
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(order_address_lat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(order_address_long));


        final ProgressDialog dialog  = Common.createProgressDialog(this,getString(R.string.bokng));
        dialog.show();

        Api.getService()
                .book_image_video(userModel.getUser_id(),serviceModel.getId_services(),order_type_part,hour_part,details_part,date_part,address_part,lat_part,lng_part,image_part,video_part)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess_resevation()==1)
                            {
                                dialog.dismiss();
                                fragmentManager.popBackStack("fragment_address",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                Toast.makeText(BookTechnicalActivity.this, R.string.bok_succ, Toast.LENGTH_SHORT).show();
                                finish();
                            }else
                            {
                                Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e) {}
                    }
                });
    }

    private void Book_Image_Sound(String order_type, String order_image_path, String order_voice_path, String order_hours, String order_details, String order_date, String order_address, double order_address_lat, double order_address_long) {
        RequestBody order_type_part = Common.getRequestBodyText(order_type);
        MultipartBody.Part image_part = Common.getMultiPartImageFromPath(order_image_path,"order_image");
        MultipartBody.Part voice_part = Common.getMultiPartVoice(order_voice_path,"order_voice");
        RequestBody hour_part = Common.getRequestBodyText(order_hours);
        RequestBody details_part = Common.getRequestBodyText(order_details);
        RequestBody date_part = Common.getRequestBodyText(order_date);
        RequestBody address_part = Common.getRequestBodyText(order_address);
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(order_address_lat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(order_address_long));


        final ProgressDialog dialog  = Common.createProgressDialog(this,getString(R.string.bokng));
        dialog.show();

        Api.getService()
                .book_image_sound(userModel.getUser_id(),serviceModel.getId_services(),order_type_part,hour_part,details_part,date_part,address_part,lat_part,lng_part,image_part,voice_part)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess_resevation()==1)
                            {
                                dialog.dismiss();
                                fragmentManager.popBackStack("fragment_address",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                Toast.makeText(BookTechnicalActivity.this, R.string.bok_succ, Toast.LENGTH_SHORT).show();
                                finish();
                            }else
                            {
                                Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e) {}
                    }
                });
    }

    private void Book_With_Video_Only(String order_type, String order_video_path, String order_hours, String order_details, String order_date, String order_address, double order_address_lat, double order_address_long) {
        RequestBody order_type_part = Common.getRequestBodyText(order_type);
        MultipartBody.Part video_part = Common.getMultiPartVideoFromPath(order_video_path,"order_video");
        RequestBody hour_part = Common.getRequestBodyText(order_hours);
        RequestBody details_part = Common.getRequestBodyText(order_details);
        RequestBody date_part = Common.getRequestBodyText(order_date);
        RequestBody address_part = Common.getRequestBodyText(order_address);
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(order_address_lat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(order_address_long));


        final ProgressDialog dialog  = Common.createProgressDialog(this,getString(R.string.bokng));
        dialog.show();

        Api.getService()
                .book_video(userModel.getUser_id(),serviceModel.getId_services(),order_type_part,hour_part,details_part,date_part,address_part,lat_part,lng_part,video_part)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess_resevation()==1)
                            {
                                dialog.dismiss();
                                fragmentManager.popBackStack("fragment_address",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                Toast.makeText(BookTechnicalActivity.this, R.string.bok_succ, Toast.LENGTH_SHORT).show();
                                finish();
                            }else
                            {
                                Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e) {}
                    }
                });

    }

    private void Book_With_Voice_Only(String order_type, String order_voice_path, String order_hours, String order_details, String order_date, String order_address, double order_address_lat, double order_address_long) {
        RequestBody order_type_part = Common.getRequestBodyText(order_type);
        MultipartBody.Part voice_part = Common.getMultiPartVoice(order_voice_path,"order_voice");
        RequestBody hour_part = Common.getRequestBodyText(order_hours);
        RequestBody details_part = Common.getRequestBodyText(order_details);
        RequestBody date_part = Common.getRequestBodyText(order_date);
        RequestBody address_part = Common.getRequestBodyText(order_address);
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(order_address_lat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(order_address_long));


        final ProgressDialog dialog  = Common.createProgressDialog(this,getString(R.string.bokng));
        dialog.show();

        Api.getService()
                .book_sound(userModel.getUser_id(),serviceModel.getId_services(),order_type_part,hour_part,details_part,date_part,address_part,lat_part,lng_part,voice_part)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess_resevation()==1)
                            {
                                dialog.dismiss();
                                fragmentManager.popBackStack("fragment_address",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                Toast.makeText(BookTechnicalActivity.this, R.string.bok_succ, Toast.LENGTH_SHORT).show();
                                finish();
                            }else
                            {
                                Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e) {}
                    }
                });

    }

    private void Book_With_Image_Only(String order_type, String order_image_path, String order_hours, String order_details, String order_date, String order_address, double order_address_lat, double order_address_long) {

        RequestBody order_type_part = Common.getRequestBodyText(order_type);
        MultipartBody.Part image_part = Common.getMultiPartImageFromPath(order_image_path,"order_image");
        RequestBody hour_part = Common.getRequestBodyText(order_hours);
        RequestBody details_part = Common.getRequestBodyText(order_details);
        RequestBody date_part = Common.getRequestBodyText(order_date);
        RequestBody address_part = Common.getRequestBodyText(order_address);
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(order_address_lat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(order_address_long));


        final ProgressDialog dialog  = Common.createProgressDialog(this,getString(R.string.bokng));
        dialog.show();

        Api.getService()
                .book_image(userModel.getUser_id(),serviceModel.getId_services(),order_type_part,hour_part,details_part,date_part,address_part,lat_part,lng_part,image_part)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess_resevation()==1)
                            {
                                dialog.dismiss();
                                fragmentManager.popBackStack("fragment_address",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                Toast.makeText(BookTechnicalActivity.this, R.string.bok_succ, Toast.LENGTH_SHORT).show();
                                finish();
                            }else
                            {
                                Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e) {}
                    }
                });

    }

    private void BookWithoutMedia(String order_type, String order_hours, String order_details, String order_date, String order_address, double order_address_lat, double order_address_long) {

        RequestBody order_type_part = Common.getRequestBodyText(order_type);
        RequestBody hour_part = Common.getRequestBodyText(order_hours);
        RequestBody details_part = Common.getRequestBodyText(order_details);
        RequestBody date_part = Common.getRequestBodyText(order_date);
        RequestBody address_part = Common.getRequestBodyText(order_address);
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(order_address_lat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(order_address_long));


        final ProgressDialog dialog  = Common.createProgressDialog(this,getString(R.string.bokng));
        dialog.show();

        Api.getService()
                .book(userModel.getUser_id(),serviceModel.getId_services(),order_type_part,hour_part,details_part,date_part,address_part,lat_part,lng_part)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess_resevation()==1)
                            {
                                dialog.dismiss();
                                fragmentManager.popBackStack("fragment_address",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                Toast.makeText(BookTechnicalActivity.this, R.string.bok_succ, Toast.LENGTH_SHORT).show();
                                finish();
                            }else
                            {
                                Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e) {}
                    }
                });




    }

    private void Book_Image_Video_Sound(String order_type, String order_image_path, String order_voice_path, String order_video_path, String order_hours, String order_details, String order_date, String order_address, double order_address_lat, double order_address_long) {

        RequestBody order_type_part = Common.getRequestBodyText(order_type);
        MultipartBody.Part image_part = Common.getMultiPartImageFromPath(order_image_path,"order_image");
        MultipartBody.Part voice_part = Common.getMultiPartVoice(order_voice_path,"order_voice");
        MultipartBody.Part video_part = Common.getMultiPartVideoFromPath(order_video_path,"order_video");
        RequestBody hour_part = Common.getRequestBodyText(order_hours);
        RequestBody details_part = Common.getRequestBodyText(order_details);
        RequestBody date_part = Common.getRequestBodyText(order_date);
        RequestBody address_part = Common.getRequestBodyText(order_address);
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(order_address_lat));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(order_address_long));


        final ProgressDialog dialog  = Common.createProgressDialog(this,getString(R.string.bokng));
        dialog.show();

        Api.getService()
                .book_image_sound_video(userModel.getUser_id(),serviceModel.getId_services(),order_type_part,hour_part,details_part,date_part,address_part,lat_part,lng_part,image_part,voice_part,video_part)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess_resevation()==1)
                            {
                                dialog.dismiss();
                                fragmentManager.popBackStack("fragment_address",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                Toast.makeText(BookTechnicalActivity.this, R.string.bok_succ, Toast.LENGTH_SHORT).show();
                                finish();
                            }else
                            {
                                Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(BookTechnicalActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e) {}
                    }
                });





    }


    @Override
    public void onBackPressed()
    {
        if (fragment_address!=null&&fragment_address.isVisible())
        {
            fragmentManager.popBackStack();
            finish();
        }else
            {
                back();
            }
    }

    public void back() {
        if (fragment_date!=null&&fragment_date.isVisible())
        {
            fragmentManager.popBackStack();
            DisplayFragmentAddress();

        }else if(fragment_book_terms_condition!=null&&fragment_book_terms_condition.isVisible())
        {
            Log.e("fragment_book_terms","fragment_book_terms_condition");
            fragmentManager.popBackStack();
            DisplayFragmentDate();
        }
    }
}
