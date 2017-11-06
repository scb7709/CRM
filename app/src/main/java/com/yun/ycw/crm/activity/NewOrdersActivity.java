package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.NewOrderAdapter;
import com.yun.ycw.crm.comparator.UndoneOrderComparator;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.UndoneOrder;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.DataChooseUtils;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.MyBigDecimal;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by scb on 2016/3/21.
 */
public class NewOrdersActivity extends Activity {
    @ViewInject(R.id.neworder_man)
    private TextView man;//
    @ViewInject(R.id.neworder_back)
    private ImageButton back;//
    @ViewInject(R.id.neworder_time)
    private TextView time;//
    @ViewInject(R.id.neworder_listview)
    private ListView listView;//
    @ViewInject(R.id.neworder_error)
    private LinearLayout neworder_error;//
    @ViewInject(R.id.neworder_spinner)
    private Spinner neworder_spinner;//

    @ViewInject(R.id.neworder_number)
    private TextView neworder_number;//
    @ViewInject(R.id.neworder_timee)
    private TextView neworder_timee;//

    @ViewInject(R.id.neworder_money)
    private TextView neworder_money;//
    @ViewInject(R.id.neworder_commission)
    private TextView neworder_commission;//
    @ViewInject(R.id.neworder_query_ed)
    private EditText neworder_query_ed;//

    private NewOrderAdapter newOrderAdapter;
    private List<UndoneOrder> list;
    private List<UndoneOrder> sublist;
    private User user;
    private String startTime;
    private String endTime;
    private Achievement ach;
    private Dialog dialog;
    private int attached_type;
    private boolean isBigList = true;//判断当前排序是原来的集合排序 还是搜索出来的子集合排序
    private boolean isCommission;//判断当前按提成排序是 主管提成排序还是 销售提成排序 第一次点击是按 主管提成排序 第二次点击按销售提成排序
    private boolean isTime;//用来判断时间排序，第一次点击时间排序 倒序，第二次 正序...
    private UndoneOrderComparator undoneOrderComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neworder);
        ViewUtils.inject(this);
        list = new ArrayList<UndoneOrder>();
        sublist = new ArrayList<UndoneOrder>();
        setdata();
        startTime = ach.getStarttime();
        endTime = ach.getEndtime();
    }

    private void setdata() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(NewOrdersActivity.this, R.layout.item_spinner_checkin);
        adapter1.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        //加载数据源
        adapter1.add("自营订单");
        adapter1.add("三方订单");

        //绑定适配器
        neworder_spinner.setAdapter(adapter1);

        neworder_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                       @Override
                                                       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                           attached_type = position;
                                                           downdata(startTime, endTime, attached_type);
                                                       }

                                                       @Override
                                                       public void onNothingSelected(AdapterView<?> parent) {

                                                       }
                                                   }
        );

        ach = (Achievement) getIntent().getSerializableExtra("achievement");
        man.setText(ach.getUsername());
        time.setText(ach.getStarttime() + "——" + ach.getEndtime());

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DataChooseUtils(NewOrdersActivity.this).chooseTimeDialog(new DataChooseUtils.ChooseData() {
                    @Override
                    public void ChooseData(String StartTime, String EndTime) {
                        startTime = StartTime;
                        endTime = EndTime;
                        downdata(startTime, endTime, attached_type);
                    }
                });
            }
        });
        neworder_query_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sublist.clear();
                String str=  neworder_query_ed.getText().toString();
                if (str.length() != 0) {

                    for (UndoneOrder order : list) {
                        if (order.getCustonmer().contains(str)||order.getCustomerphone().contains(str)||order.getAddress().contains(str)) {
                            sublist.add(order);
                        }
                    }

                    setListview(sublist, false);
                } else {
                    setListview(list, true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void downdata(final String startTime, final String endTime, final int attached_typee) {

        if (InternetUtils.internet(this)) {
            dialog = LoadingUtils.createLoadingDialog(NewOrdersActivity.this, "正在加载");
            dialog.show();
            if (list.size() != 0) {
                list.clear();
            }
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.configCurrentHttpCacheExpiry(0);
            RequestParams params = new RequestParams();
            params.addHeader("Authorization", "Bearer" + " " + SharedUtils.getToken(NewOrdersActivity.this, "info"));
            params.addQueryStringParameter("start_time", startTime);
            params.addQueryStringParameter("end_time", endTime);
            params.addQueryStringParameter("attached_type", attached_typee + "");
           // Log.i("LLLLLLLLLLL", "" + attached_typee);
            Log.i("VVVVVV", ach.getUserid());
            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.ORDER_SELL_URL + ach.getUserid(), params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    time.setText(startTime + "——" + endTime);
                    String s = responseInfo.result;
                    Log.i("FFFFFFFF", s + "");
                    JSONArray array = null;
                    try {
                        array = new JSONArray(s);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject Obj = array.getJSONObject(i);
                            UndoneOrder order = new UndoneOrder();
                            String originTime = Obj.getString("time");
                            String year = originTime.substring(0, 10);
                            String time = originTime.substring(11, 19);
                            String formated_time = time + '\n' + year;
                            order.setTime(formated_time);//下单时间
                            order.setOriginTime(year + " " + time);
                            String str = Obj.getString("orderid");
                            order.setCustonmer(Obj.getJSONObject("userinfo").getString("truename"));
                            order.setCustomerphone(Obj.getString("tel"));//微信商城客户电话，CRM客户电话
                            order.setAddress(Obj.getString("address"));//收货地址
                            // + "\n"+str.substring(str.length() - 6, str.length())
                            order.setOrderid(Obj.getString("orderid"));//订单编号
                            order.setLaststep(Constants.getOrderStatus(Integer.parseInt(Obj.getString("laststep"))));//订单状态

                            order.setTotal_price(MyBigDecimal.getBigDecimal(Obj.getDouble("price")));  //订单金额

                            order.setPaid(Obj.getInt("paid"));//是否已支付
                            order.setId(Obj.getInt("id"));
                          //  Log.i("AAAAAAAAAAAAAA", order.getId() + "");

                            JSONArray comArray = Obj.getJSONArray("staff_income");
                            if (SharedUtils.getUser("user", NewOrdersActivity.this).getSuperior_id().equals("0") || SharedUtils.getUser("user", NewOrdersActivity.this).getSuperior_id().equals("28")) {
                                if (comArray.length() == 0) {
                                    order.setCommission("0.00/0.00");
                                } else if (comArray.length() == 1) {
                                    order.setCommission("0.00/" + comArray.getJSONObject(0).getString("amount"));
                                } else if (comArray.length() == 2) {
                                    order.setCommission(comArray.getJSONObject(1).getString("amount") + "/" + comArray.getJSONObject(0).getString("amount"));
                                }
                            } else {
                                if (comArray.length() == 0) {
                                    order.setCommission("0.00");
                                } else {
                                    order.setCommission(comArray.getJSONObject(0).getString("amount"));
                                }

                            }
                            list.add(order);
                        }
                        setListview(list, true);
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i("VVVVVV","失败");
                    dialog.dismiss();
                }
            });
        }
        dialog.dismiss();
    }

    private void setListview(final List<UndoneOrder> list, boolean isBigList) {
        this.isBigList = isBigList;
        if (list.size() > 0) {
            double sumMoney = 0;
            double commissionTeam = 0;
            double commissionStaff = 0;
            for (UndoneOrder order : list) {
                sumMoney += Double.parseDouble(order.getTotal_price());
                String commission = order.getCommission();
                if (commission.contains("/")) {
                    commissionTeam += Double.parseDouble(commission.substring(0, commission.indexOf("/")));
                    commissionStaff += Double.parseDouble(commission.substring(commission.indexOf("/") + 1, commission.length()));
                } else {
                    commissionStaff += Double.parseDouble(commission);

                }

            }
            neworder_number.setText("下单人\n\n" + list.size() + "");//订单数
            neworder_money.setText("金额\n\n" + MyBigDecimal.getBigDecimal(sumMoney));//订单总额
            if (commissionTeam > 0) {
                neworder_commission.setText("提成\n\n" + MyBigDecimal.getBigDecimal(commissionTeam) + "/" + MyBigDecimal.getBigDecimal(commissionStaff));//提成总额
            } else {
                neworder_commission.setText("提成\n\n" + MyBigDecimal.getBigDecimal(commissionStaff));//提成总额
            }

            listView.setVisibility(View.VISIBLE);
            neworder_error.setVisibility(View.GONE);
            newOrderAdapter = new NewOrderAdapter(list);
            listView.setAdapter(newOrderAdapter);
        } else {
            neworder_number.setText("下单人\n\n");//订单数
            neworder_money.setText("金额\n\n");//订单总额
            neworder_commission.setText("提成\n\n");//提成总额
            listView.setVisibility(View.GONE);
            neworder_error.setVisibility(View.VISIBLE);
        }
    }

    @OnItemClick({R.id.neworder_listview})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(NewOrdersActivity.this, OrderDetailsActivity.class);
        intent.putExtra("orderid", list.get(position).getId() + "");
        startActivity(intent);
    }

    public void typeSort(String info) {
        undoneOrderComparator = new UndoneOrderComparator(info, NewOrdersActivity.this, isTime, isCommission);
        if (isBigList) {
            Collections.sort(list, undoneOrderComparator);
            newOrderAdapter = new NewOrderAdapter(list);
        } else {
            Collections.sort(sublist, undoneOrderComparator);
            newOrderAdapter = new NewOrderAdapter(sublist);
        }
        listView.setAdapter(newOrderAdapter);
        newOrderAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.neworder_timee, R.id.neworder_money, R.id.neworder_commission})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.neworder_timee://按时间排序
                isTime = !isTime;
                setPressDownTextColor(R.id.neworder_timee);
                typeSort("UNDONE_TIME");
                break;
            case R.id.neworder_money://按订单金额排序
                setPressDownTextColor(R.id.neworder_money);
                typeSort("UNDONE_MONEY");
                break;
            case R.id.neworder_commission://按销售提成排序
                isCommission = !isCommission;
                setPressDownTextColor(R.id.neworder_commission);
                typeSort("UNDONE_COMMISSION");
                break;
            default:
                break;
        }
    }

    private void setPressDownTextColor(int id) {
        String str = neworder_commission.getText().toString();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(Color.BLACK), 0, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        switch (id) {
            case R.id.neworder_timee:
                neworder_timee.setTextColor(Color.parseColor("#ff029a44"));
                neworder_money.setTextColor(Color.BLACK);
                neworder_commission.setText(style);
                break;
            case R.id.neworder_money:
                neworder_money.setTextColor(Color.parseColor("#ff029a44"));
                neworder_timee.setTextColor(Color.BLACK);
                neworder_commission.setText(style);
                break;
            case R.id.neworder_commission:
                neworder_timee.setTextColor(Color.BLACK);
                neworder_money.setTextColor(Color.BLACK);
                if(SharedUtils.getUser("user",NewOrdersActivity.this).getLevel().equals("three")){
                    neworder_commission.setTextColor(Color.parseColor("#ff029a44"));
                }
                else {
                    if (isCommission) {
                        int fend = str.indexOf("/");

                        style.setSpan(new ForegroundColorSpan(Color.parseColor("#ff029a44")), 0, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        neworder_commission.setText(style);
                    } else {
                        int fstart = str.indexOf("/") + 1;
                        int fend = str.length();
                        style.setSpan(new ForegroundColorSpan(Color.parseColor("#ff029a44")), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        style.setSpan(new ForegroundColorSpan(Color.parseColor("#ff029a44")), 0, IndexOfFirstNumber(str), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        neworder_commission.setText(style);
                    }
                }

                break;


        }


    }

    public int IndexOfFirstNumber(String text) {

        for (int i = 0; i < text.length(); i++) {
            if (text.substring(i, i + 1).matches("^[1-9]$")) {
                return i;
            }

        }
        return 0;
    }
}
