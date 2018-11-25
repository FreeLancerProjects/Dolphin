package com.appzone.dolphin.activities.home_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appzone.dolphin.Models.TermsModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.remote.Api;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Terms_Condition extends Fragment {
    private TextView tv_content;
    private SmoothProgressBar smoothProgress;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terms_condition,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Terms_Condition getInstance()
    {
        return new Fragment_Terms_Condition();
    }
    private void initView(View view) {
        tv_content = view.findViewById(R.id.tv_content);
        smoothProgress = view.findViewById(R.id.smoothProgress);

        getTerms();

    }

    private void getTerms() {
        Api.getService()
                .getTerms_Condition()
                .enqueue(new Callback<TermsModel>() {
                    @Override
                    public void onResponse(Call<TermsModel> call, Response<TermsModel> response) {
                        if (response.isSuccessful())
                        {
                            smoothProgress.setVisibility(View.GONE);
                            tv_content.setText(response.body().getContent());
                        }
                    }

                    @Override
                    public void onFailure(Call<TermsModel> call, Throwable t) {
                        smoothProgress.setVisibility(View.GONE);
                        try {
                            Log.e("Error",t.getMessage());
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
    }
}
