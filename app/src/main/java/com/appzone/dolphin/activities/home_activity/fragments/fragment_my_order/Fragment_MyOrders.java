package com.appzone.dolphin.activities.home_activity.fragments.fragment_my_order;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.activity.HomeActivity;
import com.appzone.dolphin.adapters.TabViewPagerAdapter;
import com.appzone.dolphin.tags.Tags;

public class Fragment_MyOrders extends Fragment {
    private UserModel userModel;
    private TabLayout tab;
    private ViewPager pager;
    private TabViewPagerAdapter adapter;
    private HomeActivity homeActivity;
    private Fragment_Member_New_Order fragment_member_new_order;
    private Fragment_Member_Current_Order fragment_member_current_order;
    private Fragment_Technical_New_Order fragment_technical_new_order;
    private Fragment_Technical_Current_Order fragment_technical_current_order;
    private Fragment_Technical_Previous_Order fragment_technical_previous_order;
    private double lat=0.0,lng=0.0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_order,container,false);
        initView(view);
        return view;
    }

    public static Fragment_MyOrders getInstance()
    {
        return new Fragment_MyOrders();
    }
    private void initView(View view) {
        homeActivity = (HomeActivity) getActivity();
        tab = view.findViewById(R.id.tab);
        pager = view.findViewById(R.id.pager);
        tab.setupWithViewPager(pager);
        adapter = new TabViewPagerAdapter(homeActivity.fragmentManager);

        if (userModel.getUser_type().equals(Tags.USER_MEMBER))
        {
            /*if (fragment_member_new_order==null)
            {
                fragment_member_new_order = Fragment_Member_New_Order.getInstance(userModel);
            }

            if (fragment_member_current_order==null)
            {
                fragment_member_current_order = Fragment_Member_Current_Order.getInstance(userModel);
            }*/
            adapter.AddFragment(Fragment_Member_New_Order.getInstance(userModel));
            adapter.AddFragment(Fragment_Member_Current_Order.getInstance(userModel));

            adapter.AddTitle(getString(R.string.new_order));
            adapter.AddTitle(getString(R.string.curr_order));
        }else if (userModel.getUser_type().equals(Tags.USER_TECHNICAL))
        {
            /*if (fragment_technical_new_order==null)
            {
                fragment_technical_new_order = Fragment_Technical_New_Order.getInstance(userModel);
            }
            if (fragment_technical_current_order==null)
            {
                fragment_technical_current_order = Fragment_Technical_Current_Order.getInstance(userModel);
            }

            if (fragment_technical_previous_order==null)
            {
                fragment_technical_previous_order = Fragment_Technical_Previous_Order.getInstance(userModel);
            }*/
            adapter.AddFragment(Fragment_Technical_New_Order.getInstance(userModel,lat,lng));
            adapter.AddFragment(Fragment_Technical_Current_Order.getInstance(userModel));
            adapter.AddFragment(Fragment_Technical_Previous_Order.getInstance(userModel));

            adapter.AddTitle(getString(R.string.new_order));
            adapter.AddTitle(getString(R.string.curr_order));
            adapter.AddTitle(getString(R.string.prev_order));
        }


        pager.setAdapter(adapter);




    }
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public void setLat_Lng(double lat,double lng)
    {
        this.lat=lat;
        this.lng = lng;
    }


}
