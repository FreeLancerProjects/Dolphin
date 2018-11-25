package com.appzone.dolphin.activities.home_activity.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appzone.dolphin.Models.ServiceModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.book_technical.activity.BookTechnicalActivity;
import com.appzone.dolphin.adapters.ServicesAdapter;
import com.appzone.dolphin.remote.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Home extends Fragment {
    private RecyclerView recView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private ProgressBar progBar;
    private List<ServiceModel> serviceModelList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Home getInstance()
    {
        return new Fragment_Home();
    }
    private void initView(View view) {
        serviceModelList = new ArrayList<>();
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new GridLayoutManager(getActivity(),3);
        recView.setLayoutManager(manager);
        adapter = new ServicesAdapter(getActivity(),this,serviceModelList);
        recView.setAdapter(adapter);

        getAllServices();

    }

    private void getAllServices() {
        Api.getService().getAllServices()
                .enqueue(new Callback<List<ServiceModel>>() {
                    @Override
                    public void onResponse(Call<List<ServiceModel>> call, Response<List<ServiceModel>> response) {
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);
                            serviceModelList.clear();
                            serviceModelList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ServiceModel>> call, Throwable t) {
                        progBar.setVisibility(View.GONE);

                        Log.e("Error",t.getMessage());
                        try {
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
    }

    public void setItem(ServiceModel serviceModel) {
        Intent intent = new Intent(getActivity(), BookTechnicalActivity.class);
        intent.putExtra("data",serviceModel);
        getActivity().startActivity(intent);
    }
}
