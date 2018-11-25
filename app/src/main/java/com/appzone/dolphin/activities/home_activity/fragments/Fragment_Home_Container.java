package com.appzone.dolphin.activities.home_activity.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.home_activity.activity.HomeActivity;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;

public class Fragment_Home_Container extends Fragment {
    private AHBottomNavigation ah_bottomNav;
    private HomeActivity homeActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_container,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Home_Container getInstance()
    {
        return new Fragment_Home_Container();
    }

    private void initView(View view)
    {
        homeActivity = (HomeActivity) getActivity();

        ah_bottomNav = view.findViewById(R.id.ah_bottomNav);
        ah_bottomNav.setInactiveColor(ContextCompat.getColor(getActivity(),R.color.bottom_bav_inactive_color));
        ah_bottomNav.setAccentColor(ContextCompat.getColor(getActivity(),R.color.white));
        ah_bottomNav.setForceTint(true);
        ah_bottomNav.setTitleTextSizeInSp(13f,10);
        ah_bottomNav.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        ah_bottomNav.setDefaultBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.main),R.drawable.nav_home_icon,R.color.bottom_bav_inactive_color);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.me),R.drawable.nav_user_icon,R.color.bottom_bav_inactive_color);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.my_orders),R.drawable.nav_cart_icon,R.color.bottom_bav_inactive_color);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.notf),R.drawable.notification,R.color.bottom_bav_inactive_color);

        ah_bottomNav.addItem(item1);
        ah_bottomNav.addItem(item2);
        ah_bottomNav.addItem(item3);
        ah_bottomNav.addItem(item4);


        ah_bottomNav.setCurrentItem(0,false);
        homeActivity.Display_FragmentHome();

        ah_bottomNav.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                setSelectedItem(position);

                    if (position==0)
                    {
                        homeActivity.Display_FragmentHome();
                    }else if (position==1)
                    {
                        homeActivity.Display_FragmentProfile();
                    }else if (position==2)
                    {
                        homeActivity.Display_FragmentMyOrder();
                    }else if (position==3)
                    {
                        homeActivity.Display_FragmentNotifications();
                        ReadNotification();
                    }

                return false;
            }
        });
    }



    public void setSelectedItem(int pos)
    {
        ah_bottomNav.setCurrentItem(pos,false);
    }

    public void UpdateNotificationCount(int count)
    {
        if (count>0)
        {
            AHNotification ahNotification = new AHNotification.Builder()
                    .setTextColor(ContextCompat.getColor(getActivity(),R.color.white))
                    .setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.sel_color))
                    .setText(String.valueOf(count))
                    .build();
            ah_bottomNav.setNotification(ahNotification,3);


        }else
            {
                AHNotification ahNotification = new AHNotification.Builder()
                        .setTextColor(ContextCompat.getColor(getActivity(),R.color.white))
                        .setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.sel_color))
                        .setText("")
                        .build();
                ah_bottomNav.setNotification(ahNotification,3);
            }
    }
    private void ReadNotification() {
        homeActivity.ReadNotification();
    }


}
