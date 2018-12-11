package com.appzone.dolphin.activities.home_activity.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appzone.dolphin.Models.ResponseModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.share.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_ContactUs extends Fragment {
    private EditText edt_name,edt_phone,edt_message;
    private Button btn_send;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us,container,false);
        initView(view);
        return view;
    }

    public static Fragment_ContactUs getInstance()
    {
        return new Fragment_ContactUs();
    }
    private void initView(View view) {

        edt_name = view.findViewById(R.id.edt_name);
        edt_phone = view.findViewById(R.id.edt_phone);
        edt_message = view.findViewById(R.id.edt_message);
        btn_send = view.findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });


    }

    private void CheckData() {
        String m_name = edt_name.getText().toString();
        String m_phone = edt_phone.getText().toString();
        String m_msg = edt_message.getText().toString();

        if (!TextUtils.isEmpty(m_name)&&
                !TextUtils.isEmpty(m_phone)&&
                m_phone.length()>=7&&m_phone.length()<13&&
                !TextUtils.isEmpty(m_msg)
                )
        {
            edt_name.setError(null);
            edt_phone.setError(null);
            edt_message.setError(null);

            SendData(m_name,m_phone,m_msg);

        }else
            {
                if (TextUtils.isEmpty(m_name))
                {
                    edt_name.setError(getString(R.string.name_req));
                }else
                    {
                        edt_name.setError(null);

                    }

                if (TextUtils.isEmpty(m_phone))
                {
                    edt_phone.setError(getString(R.string.phone_req));
                }else if (m_phone.length()<=6||m_phone.length()>=13)
                {
                    edt_phone.setError(getString(R.string.inv_phone));
                }
                else
                {
                    edt_phone.setError(null);

                }

                if (TextUtils.isEmpty(m_msg))
                {
                    edt_message.setError(getString(R.string.msg_req));
                }else
                {
                    edt_message.setError(null);

                }
            }
    }

    private void SendData(String m_name, String m_phone, String m_msg) {
        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.sendng));
        dialog.show();
        Api.getService()
                .sendContactUs(m_name,m_phone,m_msg)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();
                            if (response.body().getSuccess_contact()==1)
                            {
                                dialog.dismiss();
                                edt_message.setText(null);
                                edt_name.setText(null);
                                edt_phone.setText(null);
                                Toast.makeText(getActivity(),R.string.succ, Toast.LENGTH_SHORT).show();

                            }else
                                {
                                    Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {

                        dialog.dismiss();
                        try {
                            Log.e("Error",t.getMessage());
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();

                        }catch (Exception e){}
                    }
                });
    }
}
