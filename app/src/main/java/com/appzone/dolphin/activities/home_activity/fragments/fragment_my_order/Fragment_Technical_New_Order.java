package com.appzone.dolphin.activities.home_activity.fragments.fragment_my_order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appzone.dolphin.Models.ResponseModel;
import com.appzone.dolphin.Models.TechnicalOrderModel;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.technical_order_details_activity.activity.TechnicalOrderDetailsActivity;
import com.appzone.dolphin.adapters.TechnicalNewOrderAdapter;
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.share.Common;
import com.appzone.dolphin.tags.Tags;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Technical_New_Order extends Fragment {
    private static final String TAG="user";
    private static final String LAT="lat";
    private static final String LNG="lng";

    private double technical_lat=0.0,technical_lng=0.0;
    private UserModel userModel;
    private RecyclerView recView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager  manager;
    private ProgressBar progBar;
    private LinearLayout ll_no_order,ll_start;
    private Shimmer shimmer;
    private ShimmerTextView shimmer_tv;
    private List<TechnicalOrderModel> technicalOrderModelList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technical_new_order,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Technical_New_Order getInstance(UserModel userModel,double lat,double lng)
    {
        Fragment_Technical_New_Order fragment_current_order = new Fragment_Technical_New_Order();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,userModel);
        bundle.putDouble(LAT,lat);
        bundle.putDouble(LNG,lng);
        fragment_current_order.setArguments(bundle);
        return fragment_current_order;
    }
    private void initView(View view) {
        technicalOrderModelList = new ArrayList<>();
        ll_no_order = view.findViewById(R.id.ll_no_order);
        ll_start = view.findViewById(R.id.ll_start);
        shimmer_tv = view.findViewById(R.id.shimmer_tv);

        shimmer = new Shimmer()
                .setDirection(Shimmer.ANIMATION_DIRECTION_RTL)
                .setDuration(2000)
                .setStartDelay(1000);
        shimmer.start(shimmer_tv);

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        adapter = new TechnicalNewOrderAdapter(getActivity(),technicalOrderModelList,this);
        recView.setAdapter(adapter);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            userModel = (UserModel) bundle.getSerializable(TAG);
            technical_lat = bundle.getDouble(LAT);
            technical_lng = bundle.getDouble(LNG);
            Log.e("tech_lat",technical_lat+"_");

            UpdateUI(userModel);
        }

        ll_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startWork(userModel.getUser_id(),technical_lat,technical_lng);
            }
        });

    }



    private void UpdateUI(UserModel userModel) {

        getNewOrder(userModel.getUser_id());

    }

    private void getNewOrder(String user_id) {
        Api.getService()
                .getTechnicalNewOrder(user_id)
                .enqueue(new Callback<List<TechnicalOrderModel>>() {
                    @Override
                    public void onResponse(Call<List<TechnicalOrderModel>> call, Response<List<TechnicalOrderModel>> response) {
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);
                            technicalOrderModelList.clear();
                            technicalOrderModelList.addAll(response.body());

                            if (technicalOrderModelList.size()>0)
                            {
                                adapter.notifyDataSetChanged();
                                ll_no_order.setVisibility(View.GONE);

                                if (technicalOrderModelList.get(0).getUser_state().equals("0")||technicalOrderModelList.get(0).getUser_state().equals("8"))
                                {
                                    ll_start.setVisibility(View.VISIBLE);

                                }else
                                    {
                                        ll_start.setVisibility(View.GONE);

                                    }

                            }else
                            {
                                ll_no_order.setVisibility(View.VISIBLE);
                                ll_start.setVisibility(View.GONE);

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TechnicalOrderModel>> call, Throwable t) {
                        try {
                            Log.e("Error",t.getMessage());
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
    }


    public void setItem(TechnicalOrderModel technicalOrderModel)
    {
        Intent intent = new Intent(getActivity(), TechnicalOrderDetailsActivity.class);
        intent.putExtra("data",technicalOrderModel);
        intent.putExtra("type","new");
        getActivity().startActivity(intent);

    }

    private void startWork(String user_id, double technical_lat, double technical_lng) {
        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();
        Api.getService()
                .startWork(user_id, Tags.TECHNICAL_START_WORK,technical_lat,technical_lng)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getSuccess_movement()==1)
                            {
                                ll_start.setVisibility(View.GONE);
                                technicalOrderModelList.clear();
                                adapter.notifyDataSetChanged();
                                ll_no_order.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(),R.string.succ, Toast.LENGTH_SHORT).show();

                            }else
                                {
                                    Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error",t.getMessage());
                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
    }


}
