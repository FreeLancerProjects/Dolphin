package com.appzone.dolphin.activities.technical_order_details_activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.appzone.dolphin.Models.TechnicalOrderModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.technical_order_details_activity.fragments.Fragment_New_Order_Details;
import com.appzone.dolphin.activities.technical_order_details_activity.fragments.fragment_current_order_dtails.fragment_current_order_details.FragmentCurrentOrderDetails;

import java.util.List;
import java.util.Locale;

public class TechnicalOrderDetailsActivity extends AppCompatActivity {

    private ImageView image_back;
    private TechnicalOrderModel technicalOrderModel;
    private String type="";
    private FragmentCurrentOrderDetails fragmentCurrentOrderDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technical_order_details);
        initView();
        getDataFromIntent();
    }



    private void initView()
    {
        image_back = findViewById(R.id.image_back);
        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            image_back.setRotation(180f);
        }

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent!=null)
        {
            type = intent.getStringExtra("type");
            technicalOrderModel = (TechnicalOrderModel) intent.getSerializableExtra("data");
            updateUI(type,technicalOrderModel);
        }
    }

    private void updateUI(String type, TechnicalOrderModel technicalOrderModel) {
        if (type.equals("new"))
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.technical_order_details_fragment_container, Fragment_New_Order_Details.getInstance(technicalOrderModel)).commit();
        }else if (type.equals("current"))
        {
            fragmentCurrentOrderDetails = FragmentCurrentOrderDetails.getInstance(technicalOrderModel);
            getSupportFragmentManager().beginTransaction().replace(R.id.technical_order_details_fragment_container, fragmentCurrentOrderDetails).commit();

        }
    }

    public void Finish()
    {

        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment:fragmentList)
        {
            fragment.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment:fragmentList)
        {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    public void UpdateFragmentBill()
    {
        if (fragmentCurrentOrderDetails!=null)
        {
            fragmentCurrentOrderDetails.UpdateFrgmentBill();
        }
    }
}
