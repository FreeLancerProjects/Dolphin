package com.appzone.dolphin.activities.book_technical.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appzone.dolphin.R;
import com.appzone.dolphin.activities.book_technical.activity.BookTechnicalActivity;
import com.appzone.dolphin.tags.Tags;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Fragment_Date extends Fragment implements DatePickerDialog.OnDateSetListener{
    private static final String TAG="TITLE";
    private ImageView image_back;
    private TextView tv_date,tv_title;
    private LinearLayout ll_date;
    private Button btn_normal,btn_emergency,btn_next;
    private EditText edt_hours;
    private String order_type="";
    private String en_date="";
    private DatePickerDialog datePickerDialog;
    private BookTechnicalActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_date,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Date getInstance(String service_title)
    {
        Fragment_Date fragment_date = new Fragment_Date();
        Bundle bundle = new Bundle();
        bundle.putString(TAG,service_title);
        fragment_date.setArguments(bundle);
        return fragment_date;
    }
    private void initView(View view)
    {
        activity = (BookTechnicalActivity) getActivity();
        image_back = view.findViewById(R.id.image_back);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.back();
            }
        });
        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            image_back.setRotation(180f);
        }
        tv_title = view.findViewById(R.id.tv_title);

        tv_date = view.findViewById(R.id.tv_date);
        ll_date = view.findViewById(R.id.ll_date);
        btn_normal = view.findViewById(R.id.btn_normal);
        btn_emergency = view.findViewById(R.id.btn_emergency);
        btn_next = view.findViewById(R.id.btn_next);

        edt_hours = view.findViewById(R.id.edt_hours);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            String title = bundle.getString(TAG);
            tv_title.setText(title);

        }

        ll_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateDateDialog();
            }
        });

        btn_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order_type= Tags.ORDER_NORMAL;
                btn_normal.setBackgroundResource(R.drawable.btn_selected);
                btn_normal.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
                btn_emergency.setBackgroundResource(R.drawable.btn_unselected);
                btn_emergency.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));

            }
        });

        btn_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order_type= Tags.ORDER_EMERGENCY;
                btn_normal.setBackgroundResource(R.drawable.btn_unselected);
                btn_normal.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                btn_emergency.setBackgroundResource(R.drawable.btn_selected);
                btn_emergency.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));

            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });

    }

    private void CheckData() {

        String m_hour = edt_hours.getText().toString();
        if (!TextUtils.isEmpty(en_date)&&!TextUtils.isEmpty(order_type)&&!TextUtils.isEmpty(m_hour))
        {
            edt_hours.setError(null);
            activity.setDateData(order_type,m_hour,en_date);
        }else
            {
                if (TextUtils.isEmpty(en_date))
                {
                    Toast.makeText(activity, R.string.sel_date, Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(order_type))
                {
                    Toast.makeText(activity, R.string.sel_res_type, Toast.LENGTH_SHORT).show();

                }

                if (TextUtils.isEmpty(m_hour))
                {
                    edt_hours.setError(getString(R.string.exp_req));
                }else
                    {
                        edt_hours.setError(null);
                    }
            }
    }

    private void CreateDateDialog()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH,1);
        datePickerDialog = DatePickerDialog.newInstance(this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setLocale(Locale.ENGLISH);
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setCancelColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        datePickerDialog.setOkColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        datePickerDialog.setOkText(R.string.select);
        datePickerDialog.setCancelText(R.string.cancel);
        datePickerDialog.setAccentColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.show(activity.getFragmentManager(),"date");

    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        en_date = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;

        if (Locale.getDefault().getLanguage().equals("ar"))
        {
            Calendar calendar = Calendar.getInstance(new Locale("ar"));
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            Date date = new Date(calendar.getTimeInMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",new Locale("ar"));
            String dd = dateFormat.format(date);

            tv_date.setText(dd);

        }else
            {
                tv_date.setText(en_date);
            }
    }
}
