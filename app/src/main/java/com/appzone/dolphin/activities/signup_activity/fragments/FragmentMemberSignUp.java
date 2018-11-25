package com.appzone.dolphin.activities.signup_activity.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appzone.dolphin.Models.Country_City_Model;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.activity.HomeActivity;
import com.appzone.dolphin.adapters.CityAdapter;
import com.appzone.dolphin.adapters.CountryAdapter;
import com.appzone.dolphin.preferences.Preferences;
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.share.Common;
import com.appzone.dolphin.singletone.UserSingleTone;
import com.appzone.dolphin.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMemberSignUp extends Fragment{
    private EditText edt_name,edt_email,edt_address,edt_phone,edt_password;
    private TextView tv_country,tv_city;
    private Button btn_signup;
    private LinearLayout ll_country,ll_city;
    private String country="",city="";
    private double myLat = 0.0,myLng = 0.0;
    private AlertDialog country_dialog,city_dialog;
    private List<Country_City_Model> country_city_modelList;
    private Preferences preferences;
    private UserSingleTone userSingleTone;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_signup,container,false);
        initView(view);
        return view;
    }

    public static FragmentMemberSignUp getInstance()
    {
        return new FragmentMemberSignUp();
    }
    private void initView(View view) {
        country_city_modelList = new ArrayList<>();
        edt_name = view.findViewById(R.id.edt_name);
        edt_email = view.findViewById(R.id.edt_email);
        edt_address = view.findViewById(R.id.edt_address);
        edt_phone = view.findViewById(R.id.edt_phone);
        edt_password = view.findViewById(R.id.edt_password);
        tv_country = view.findViewById(R.id.tv_country);
        tv_city = view.findViewById(R.id.tv_city);
        ll_country = view.findViewById(R.id.ll_country);
        ll_city = view.findViewById(R.id.ll_city);
        btn_signup = view.findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();

            }
        });

        ll_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (country_city_modelList.size()>0)
                {
                    country_dialog.show();

                }else
                {
                    getCountry_CityData();

                    if (country_city_modelList.size()>0)
                    {
                        country_dialog.show();

                    }
                }
            }
        });

        ll_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(country)||country_city_modelList.size()==0)
                {
                    Toast.makeText(getActivity(),R.string.ch_country, Toast.LENGTH_SHORT).show();
                }else
                {
                    city_dialog.show();
                }
            }
        });

        getCountry_CityData();

    }

    private void CheckData() {
        String m_name = edt_name.getText().toString();
        String m_email = edt_email.getText().toString();
        String m_address = edt_address.getText().toString();
        String m_phone = edt_phone.getText().toString();
        String m_password = edt_password.getText().toString();

        if(!TextUtils.isEmpty(m_name)&&
                !TextUtils.isEmpty(m_email)&&
                Patterns.EMAIL_ADDRESS.matcher(m_email).matches()&&
                !TextUtils.isEmpty(m_address)&&
                !TextUtils.isEmpty(m_phone)&&
                m_phone.length()>=7&&m_phone.length()<13&&
                !TextUtils.isEmpty(m_password)&&
                !TextUtils.isEmpty(country)&&
                !TextUtils.isEmpty(city)

                )
        {
            SignUp(m_name,m_email,m_address,m_phone,m_password,country,city);
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

                if (TextUtils.isEmpty(m_address))
                {
                    edt_address.setError(getString(R.string.address_req));
                }else
                {
                    edt_address.setError(null);
                }

                if (TextUtils.isEmpty(country))
                {
                    tv_country.setError(getString(R.string.country_req));
                }else
                    {
                        tv_country.setError(null);

                    }
                if (TextUtils.isEmpty(city))
                {
                    tv_city.setError(getString(R.string.city_req));
                }else
                    {
                        tv_city.setError(null);

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

    public void Location(double lat, double lng)
    {
        myLat = lat;
        myLng = lng;

        Log.e("lat",lat+"_");
        Log.e("lng",lng+"_");

    }

    private void getCountry_CityData() {
        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.lod_country));
        dialog.show();
        Api.getService()
                .getCountry_City()
                .enqueue(new Callback<List<Country_City_Model>>() {
                    @Override
                    public void onResponse(Call<List<Country_City_Model>> call, Response<List<Country_City_Model>> response) {
                        if (response.isSuccessful())
                        {
                            country_city_modelList.clear();
                            country_city_modelList.addAll(response.body());
                            CreateCountryDialog(response.body());
                            dialog.dismiss();

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Country_City_Model>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        dialog.dismiss();
                        try {
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (NullPointerException e){}
                    }
                });
    }
    private void CreateCountryDialog(List<Country_City_Model> country_city_modelList)
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.country_city_dialog,null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.ch_country);
        RecyclerView recView = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        RecyclerView.Adapter adapter = new CountryAdapter(getActivity(),country_city_modelList,this);
        recView.setAdapter(adapter);
        country_dialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();

        country_dialog.getWindow().getAttributes().windowAnimations = R.style.custom_dialog;
        country_dialog.setCanceledOnTouchOutside(false);
        country_dialog.setView(view);

    }
    private void CreateCityDialog(List<Country_City_Model.CityModel> cityModelList)
    {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.country_city_dialog,null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.ch_city);
        RecyclerView recView = view.findViewById(R.id.recView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        RecyclerView.Adapter adapter = new CityAdapter(getActivity(),cityModelList,this);
        recView.setAdapter(adapter);

        city_dialog = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .create();

        city_dialog.getWindow().getAttributes().windowAnimations = R.style.custom_dialog;
        city_dialog.setCanceledOnTouchOutside(false);
        city_dialog.setView(view);
        city_dialog.show();

    }
    public void setCountryItem(Country_City_Model countryItem)
    {
        country_dialog.dismiss();
        country = countryItem.getId_country();
        tv_city.setHint(getString(R.string.ch_city));
        city="";


        String lang = Locale.getDefault().getLanguage();
        if (lang.equals("ar"))
        {
            tv_country.setText(countryItem.getAr_name());
        }else if (lang.equals("en"))
        {
            tv_country.setText(countryItem.getEn_name());

        }

        CreateCityDialog(countryItem.getSub_city());
    }
    public void setCityItem(Country_City_Model.CityModel cityItem)
    {
        city_dialog.dismiss();
        city = cityItem.getId_city();
        String lang = Locale.getDefault().getLanguage();

        if (lang.equals("ar"))
        {
            tv_city.setText(cityItem.getAr_city_title());
        }else if (lang.equals("en"))
        {
            tv_city.setText(cityItem.getEn_city_title());

        }

    }

    private void SignUp(String m_name, String m_email, String m_address, String m_phone, String m_password, String country, String city) {
        Common.CloseKeyBoard(getActivity(),edt_name);
        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.signing_up));
        dialog.show();
        Api.getService()
                .memberSignUp(m_password,m_phone,country,m_email,m_name,"",myLat,myLng,city,m_address, Tags.USER_MEMBER)
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
}
