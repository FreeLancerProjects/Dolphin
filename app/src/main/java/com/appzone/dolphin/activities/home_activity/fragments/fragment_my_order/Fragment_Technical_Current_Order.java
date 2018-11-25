package com.appzone.dolphin.activities.home_activity.fragments.fragment_my_order;

import android.app.Activity;
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

import com.appzone.dolphin.Models.TechnicalOrderModel;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.technical_order_details_activity.activity.TechnicalOrderDetailsActivity;
import com.appzone.dolphin.adapters.TechnicalCurrentOrderAdapter;
import com.appzone.dolphin.remote.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Technical_Current_Order extends Fragment {
    private static final String TAG="user";
    private UserModel userModel;
    private RecyclerView recView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager  manager;
    private ProgressBar progBar;
    private LinearLayout ll_no_order;
    private List<TechnicalOrderModel> technicalOrderModelList;
    private final int req_code = 558;
    private int last_pos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technical_current_previous_order,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Technical_Current_Order getInstance(UserModel userModel)
    {
        Fragment_Technical_Current_Order fragment_current_order = new Fragment_Technical_Current_Order();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,userModel);
        fragment_current_order.setArguments(bundle);
        return fragment_current_order;
    }
    private void initView(View view) {
        technicalOrderModelList = new ArrayList<>();
        ll_no_order = view.findViewById(R.id.ll_no_order);
        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        adapter = new TechnicalCurrentOrderAdapter(getActivity(),technicalOrderModelList,this);
        recView.setAdapter(adapter);
        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            userModel = (UserModel) bundle.getSerializable(TAG);
            UpdateUI(userModel);
        }

    }

    private void UpdateUI(UserModel userModel) {

        getCurrentOrder(userModel.getUser_id());

    }

    private void getCurrentOrder(String user_id) {
        Api.getService()
                .getTechnicalCurrentOrder(user_id)
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
                            }else
                                {
                                    ll_no_order.setVisibility(View.VISIBLE);

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

    public void setItem(TechnicalOrderModel technicalOrderModel, int pos)
    {
        this.last_pos = pos;
        Intent intent = new Intent(getActivity(), TechnicalOrderDetailsActivity.class);
        intent.putExtra("data",technicalOrderModel);
        intent.putExtra("type","current");
        getActivity().startActivityForResult(intent,req_code);

        Log.e("iddddddddddddddd",technicalOrderModel.getId_order());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==req_code&&resultCode== Activity.RESULT_OK&&data!=null)
        {
            technicalOrderModelList.remove(this.last_pos);
            adapter.notifyItemRemoved(last_pos);
            if (technicalOrderModelList.size()>0)
            {
                ll_no_order.setVisibility(View.GONE);
            }else
                {
                    ll_no_order.setVisibility(View.VISIBLE);

                }
        }
    }
}
