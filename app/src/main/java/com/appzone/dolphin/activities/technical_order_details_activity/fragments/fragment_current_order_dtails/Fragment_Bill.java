package com.appzone.dolphin.activities.technical_order_details_activity.fragments.fragment_current_order_dtails;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appzone.dolphin.Models.BillModel;
import com.appzone.dolphin.Models.OrderStateModel;
import com.appzone.dolphin.Models.ResponseModel;
import com.appzone.dolphin.Models.TechnicalOrderModel;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.remote.Api;
import com.appzone.dolphin.share.Common;
import com.appzone.dolphin.singletone.UserSingleTone;
import com.appzone.dolphin.tags.Tags;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Bill extends Fragment {
    private static final String TAG="DATA";

    private TechnicalOrderModel technicalOrderModel;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private EditText edt_hour,edt_spare_part,edt_delivery,edt_discount;
    private TextView tv_hour_cost,tv_total_cost,tv_total,tv_payment_type;
    private Button btn_send,btn_total;
    private double hours=0.0,hours_cost=0.0;
    double  spare_part_cost=0.0,delivery=0.0,discount=0.0,total=0.0;
    private Spinner spinner;
    private String payment="";
    private String [] pay;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Bill getInstance(TechnicalOrderModel technicalOrderModel)
    {
        Fragment_Bill fragment_bill = new Fragment_Bill();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,technicalOrderModel);
        fragment_bill.setArguments(bundle);
        return fragment_bill;
    }
    private void initView(View view) {

        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();

        edt_hour = view.findViewById(R.id.edt_hour);
        edt_spare_part = view.findViewById(R.id.edt_spare_part);
        edt_delivery = view.findViewById(R.id.edt_delivery);
        edt_discount = view.findViewById(R.id.edt_discount);
        tv_hour_cost = view.findViewById(R.id.tv_hour_cost);
        tv_total_cost = view.findViewById(R.id.tv_total_cost);
        tv_total = view.findViewById(R.id.tv_total);
        tv_payment_type = view.findViewById(R.id.tv_payment_type);
        btn_send = view.findViewById(R.id.btn_send);
        btn_total = view.findViewById(R.id.btn_total);

        spinner = view.findViewById(R.id.spinner);
        pay = getActivity().getResources().getStringArray(R.array.payment);
        spinner.setAdapter(new ArrayAdapter<>(getActivity(),R.layout.spinner_row,pay));
        edt_hour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(edt_hour.getText().toString()))
                {
                    hours = Double.parseDouble(edt_hour.getText().toString());
                    if (hours>1.0)
                    {
                        try {
                            hours_cost = 120+((hours-1.0)*100.0);
                            tv_hour_cost.setText(String.valueOf(hours_cost));
                        }catch (NumberFormatException e){}

                    }else
                    {
                        try {
                            hours_cost = 120*hours;
                            tv_hour_cost.setText(String.valueOf(hours_cost));
                        }catch (NumberFormatException e){}

                    }
                }else
                    {
                        Log.e("v","vvvvvvvvvvv");
                        hours_cost=0.0;
                        hours=0.0;
                        tv_hour_cost.setText(null);
                        tv_total_cost.setText(null);
                        tv_total.setText(null);
                        tv_hour_cost.setHint("0.0");
                        tv_total_cost.setHint("0.0");
                        tv_total.setHint("0.0");

                    }


            }
        });

        edt_spare_part.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(edt_spare_part.getText().toString()))
                {
                    total=0.0;
                    tv_total_cost.setText(null);
                    tv_total.setText(null);
                    tv_total_cost.setHint("0.0");
                    tv_total.setHint("0.0");
                }


            }
        });

        edt_delivery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(edt_delivery.getText().toString()))
                {
                    total =0.0;
                    tv_total_cost.setText(null);
                    tv_total.setText(null);
                    tv_total_cost.setHint("0.0");
                    tv_total.setHint("0.0");
                }


            }
        });

        edt_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(edt_discount.getText().toString()))
                {
                    total =0.0;
                    tv_total_cost.setText(null);
                    tv_total.setText(null);
                    tv_total_cost.setHint("0.0");
                    tv_total.setHint("0.0");
                }


            }
        });


        btn_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData(0);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData(1);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position==0)
                {
                    payment="";
                }else if (position==1)
                {
                    payment= Tags.CASH;

                }else if (position==2)
                {
                    payment=Tags.BILL;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            technicalOrderModel = (TechnicalOrderModel) bundle.getSerializable(TAG);
            updateUI(technicalOrderModel);
        }
    }

    private void CheckData(int canSend) {
       String m_spare_part_cost = edt_spare_part.getText().toString();
       String m_delivery = edt_delivery.getText().toString();
       String m_discount = edt_discount.getText().toString();

       if (hours!=0.0&&
               !TextUtils.isEmpty(m_spare_part_cost)&&
               !TextUtils.isEmpty(m_delivery)&&
               !TextUtils.isEmpty(m_discount)&&
               !TextUtils.isEmpty(payment)
               )
       {
           spare_part_cost = Double.parseDouble(m_spare_part_cost);
           delivery = Double.parseDouble(m_delivery);
           discount = Double.parseDouble(m_discount);
           double total_before_discount;

           total_before_discount = (hours_cost+spare_part_cost+delivery);
           tv_total_cost.setText(String.valueOf(total_before_discount));

           if ((hours_cost+spare_part_cost+delivery)>discount)
           {
               total = (hours_cost+spare_part_cost+delivery)-discount;
               tv_total.setText(String.valueOf(total));
           }else
               {
                   total=0.0;
                   Toast.makeText(getActivity(), R.string.disc_larg_total, Toast.LENGTH_SHORT).show();
               }


           edt_hour.setError(null);
           edt_spare_part.setError(null);
           edt_delivery.setError(null);
           edt_discount.setError(null);

           if (canSend==1)
           {
               Send(hours,hours_cost,spare_part_cost,delivery,discount,payment,userModel.getUser_id(),technicalOrderModel.getId_order(),total,technicalOrderModel.getClient_id_fk());
           }

       }else
           {
               if (hours==0.0)
               {
                   edt_hour.setError(getString(R.string.hours_req));
               }else
                   {
                       edt_hour.setError(null);

                   }

                   if (TextUtils.isEmpty(m_spare_part_cost))
                   {
                       edt_spare_part.setError(getString(R.string.spare_req));
                   }else
                       {
                           edt_spare_part.setError(null);

                       }
               if (TextUtils.isEmpty(m_delivery))
               {
                   edt_delivery.setError(getString(R.string.delivery_req));
               }else
               {
                   edt_delivery.setError(null);

               }

               if (TextUtils.isEmpty(m_discount))
               {
                   edt_discount.setError(getString(R.string.discount_req));
               }else
               {
                   edt_discount.setError(null);

               }

               if (TextUtils.isEmpty(payment))
               {
                   Toast.makeText(getActivity(), R.string.ch_payment_type, Toast.LENGTH_SHORT).show();
               }

           }

    }

    private void Send(double hours, double hours_cost, double spare_part_cost, double delivery, double discount, final String payment, String user_id, String order_id, double total, String client_id_fk) {

        Log.e("hours",hours+"_");
        Log.e("hours_cost",hours_cost+"_");
        Log.e("spare_part_cost",spare_part_cost+"_");
        Log.e("delivery",delivery+"_");
        Log.e("discount",discount+"_");
        Log.e("payment",payment+"_");
        Log.e("total",total+"_");
        Log.e("user_id",user_id+"_");
        Log.e("client_id_fk",order_id+"_");


        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();
        Api.getService()
                .payment(user_id,order_id,client_id_fk,String.valueOf(hours),String.valueOf(hours_cost),String.valueOf(delivery),String.valueOf(total),String.valueOf(discount),payment)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();
                            if (response.body().getSuccess_payment()==1)
                            {
                                spinner.setVisibility(View.GONE);
                                tv_payment_type.setVisibility(View.VISIBLE);
                                edt_delivery.setEnabled(false);
                                edt_discount.setEnabled(false);
                                edt_hour.setEnabled(false);
                                edt_spare_part.setEnabled(false);
                                btn_send.setVisibility(View.GONE);
                                btn_total.setVisibility(View.GONE);
                                if (payment.equals(Tags.CASH))
                                {
                                    tv_payment_type.setText(R.string.cash);
                                }else if (payment.equals(Tags.BILL))
                                {
                                    tv_payment_type.setText(R.string.bill2);

                                }

                                Toast.makeText(getActivity(), R.string.succ, Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    private void updateUI(TechnicalOrderModel technicalOrderModel) {

        getOrder_State();
    }

    public void getOrder_State() {

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();

        Api.getService()
                .getOrderState(userModel.getUser_id(),technicalOrderModel.getId_order())
                .enqueue(new Callback<OrderStateModel>() {
                    @Override
                    public void onResponse(Call<OrderStateModel> call, Response<OrderStateModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getOrder_payment()==1)
                            {

                                getBill(userModel.getUser_id(),technicalOrderModel.getId_order());
                                Log.e("bill state",response.body().getOrder_step()+"state");
                                Log.e("iddddddddddd2",technicalOrderModel.getId_order());

                            }else if (response.body().getOrder_payment()==0)
                            {
                                if (response.body().getOrder_step().equals("1"))
                                {
                                    btn_send.setVisibility(View.GONE);
                                    btn_total.setVisibility(View.GONE);
                                    spinner.setVisibility(View.GONE);
                                    tv_payment_type.setVisibility(View.VISIBLE);
                                }else if (response.body().getOrder_step().equals("-1"))
                                {
                                    btn_send.setVisibility(View.GONE);
                                    btn_total.setVisibility(View.GONE);
                                    spinner.setVisibility(View.GONE);
                                    tv_payment_type.setVisibility(View.VISIBLE);
                                }else
                                    {
                                        btn_send.setVisibility(View.VISIBLE);
                                        btn_total.setVisibility(View.VISIBLE);
                                        spinner.setVisibility(View.VISIBLE);
                                        tv_payment_type.setVisibility(View.GONE);
                                    }

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderStateModel> call, Throwable t) {
                        try {
                            dialog.dismiss();

                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        }catch (Exception e){}
                    }
                });

    }

    private void getBill(String user_id,String order_id)
    {
        Log.e("user",user_id+"_");
        Log.e("order",order_id+"_");

        final ProgressDialog dialog = Common.createProgressDialog(getActivity(),getString(R.string.wait));
        dialog.show();
        Api.getService()
                .getBill(user_id,order_id)
                .enqueue(new Callback<List<BillModel>>() {
                    @Override
                    public void onResponse(Call<List<BillModel>> call, Response<List<BillModel>> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();
                            if (response.body().size()>0)
                            {
                                UpdateBillUI(response.body().get(0));

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<BillModel>> call, Throwable t) {
                        try {
                            dialog.dismiss();

                            Toast.makeText(getActivity(), R.string.something, Toast.LENGTH_SHORT).show();
                            Log.e("Error2",t.getMessage());
                        }catch (Exception e){}
                    }
                });
    }

    private void UpdateBillUI(BillModel billModel) {

        spinner.setVisibility(View.GONE);
        tv_payment_type.setVisibility(View.VISIBLE);
        edt_delivery.setEnabled(false);
        edt_discount.setEnabled(false);
        edt_hour.setEnabled(false);
        edt_spare_part.setEnabled(false);
        btn_send.setVisibility(View.GONE);
        btn_total.setVisibility(View.GONE);

        edt_hour.setText(billModel.getWork_hours());
        tv_hour_cost.setText(billModel.getHour_cost());
        edt_spare_part.setText(billModel.getSpare_parts_cost());
        edt_delivery.setText(billModel.getTransfer_cost());
        edt_discount.setText(billModel.getDiscount());
        tv_total.setText(billModel.getTotal_cost());
        try {
            double total_before_discount = Double.parseDouble(billModel.getTotal_cost())+Double.parseDouble(billModel.getDiscount());

            tv_total_cost.setText(String.valueOf(total_before_discount));
        }catch (NumberFormatException e){}


        if (billModel.getPayment_method().equals(Tags.CASH))
        {
            tv_payment_type.setText(R.string.cash);

        }else if (billModel.getPayment_method().equals(Tags.BILL))
        {
            tv_payment_type.setText(R.string.bill2);

        }
    }
}
