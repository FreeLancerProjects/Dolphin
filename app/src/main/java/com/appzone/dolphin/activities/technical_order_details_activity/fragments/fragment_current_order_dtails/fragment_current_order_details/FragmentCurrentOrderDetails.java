package com.appzone.dolphin.activities.technical_order_details_activity.fragments.fragment_current_order_dtails.fragment_current_order_details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appzone.dolphin.Models.TechnicalOrderModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.technical_order_details_activity.fragments.fragment_current_order_dtails.Fragment_Bill;
import com.appzone.dolphin.activities.technical_order_details_activity.fragments.fragment_current_order_dtails.Fragment_Details;
import com.appzone.dolphin.adapters.TabViewPagerAdapter;

public class FragmentCurrentOrderDetails extends Fragment{
    private static final String TAG="DATA";
    private TechnicalOrderModel technicalOrderModel;
    private TabLayout tab;
    private ViewPager pager;
    private TabViewPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_order_details,container,false);
        initView(view);
        return view;
    }

    public static FragmentCurrentOrderDetails getInstance(TechnicalOrderModel technicalOrderModel)
    {
        FragmentCurrentOrderDetails fragmentCurrentOrderDetails = new FragmentCurrentOrderDetails();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,technicalOrderModel);
        fragmentCurrentOrderDetails.setArguments(bundle);
        return fragmentCurrentOrderDetails;
    }
    private void initView(View view) {
        tab = view.findViewById(R.id.tab);
        pager = view.findViewById(R.id.pager);
        tab.setupWithViewPager(pager);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            technicalOrderModel = (TechnicalOrderModel) bundle.getSerializable(TAG);
            updateUI(technicalOrderModel);
        }



    }

    private void updateUI(TechnicalOrderModel technicalOrderModel) {
        adapter = new TabViewPagerAdapter(getChildFragmentManager());
        adapter.AddFragment(Fragment_Details.getInstance(technicalOrderModel));
        adapter.AddFragment(Fragment_Bill.getInstance(technicalOrderModel));
        adapter.AddTitle(getString(R.string.details));
        adapter.AddTitle(getString(R.string.bill));

        pager.setAdapter(adapter);

    }

    public void UpdateFrgmentBill()
    {
        Fragment_Bill fragment_bill = (Fragment_Bill) adapter.getItem(1);
        fragment_bill.getOrder_State();
    }
}
