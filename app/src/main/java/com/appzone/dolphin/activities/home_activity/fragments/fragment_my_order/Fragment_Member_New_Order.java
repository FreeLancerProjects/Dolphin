package com.appzone.dolphin.activities.home_activity.fragments.fragment_my_order;

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

import com.appzone.dolphin.Models.ClientOrderModel;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.adapters.ClientNewOrderAdapter;
import com.appzone.dolphin.remote.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Member_New_Order extends Fragment {
    private static final String TAG="user";
    private UserModel userModel;
    private RecyclerView recView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager  manager;
    private ProgressBar progBar;
    private LinearLayout ll_no_order;
    private List<ClientOrderModel> clientOrderModelLis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technical_current_previous_order,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Member_New_Order getInstance(UserModel userModel)
    {
        Fragment_Member_New_Order fragment_current_order = new Fragment_Member_New_Order();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,userModel);
        fragment_current_order.setArguments(bundle);
        return fragment_current_order;
    }
    private void initView(View view) {
        clientOrderModelLis = new ArrayList<>();
        progBar = view.findViewById(R.id.progBar);
        ll_no_order = view.findViewById(R.id.ll_no_order);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        adapter = new ClientNewOrderAdapter(getActivity(),clientOrderModelLis);
        recView.setAdapter(adapter);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            userModel = (UserModel) bundle.getSerializable(TAG);
            UpdateUI(userModel);
        }

    }

    private void UpdateUI(UserModel userModel) {
        getNewOrder(userModel.getUser_id());
    }

    private void getNewOrder(String user_id) {
        Api.getService()
                .getClientNewOrder(user_id)
                .enqueue(new Callback<List<ClientOrderModel>>() {
                    @Override
                    public void onResponse(Call<List<ClientOrderModel>> call, Response<List<ClientOrderModel>> response) {
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);
                            clientOrderModelLis.clear();
                            clientOrderModelLis.addAll(response.body());
                            if (clientOrderModelLis.size()>0)
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
                    public void onFailure(Call<List<ClientOrderModel>> call, Throwable t) {
                        try {
                            progBar.setVisibility(View.GONE);
                            Log.e("Error",t.getMessage());
                            Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                        }catch (Exception e){}
                    }
                });
    }


}
