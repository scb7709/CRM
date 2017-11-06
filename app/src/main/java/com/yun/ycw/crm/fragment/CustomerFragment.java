package com.yun.ycw.crm.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.PullableView.PullToRefreshLayout;
import com.yun.ycw.crm.PullableView.PullableListView;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.activity.CustomerDetailActivity;
import com.yun.ycw.crm.adapter.CustomerOrderAdapter;
import com.yun.ycw.crm.comparator.CharacterParser;
import com.yun.ycw.crm.comparator.OrderNumComparator;
import com.yun.ycw.crm.comparator.PinyinComparator;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.customview.SideBar;
import com.yun.ycw.crm.entity.Customer;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class CustomerFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    public List<Customer> list1 = new ArrayList<>(), list2 = new ArrayList<>();
    private CustomerOrderAdapter customerOrderAdapter1, customerOrderAdapter2;
    private Customer customer;
    @ViewInject(R.id.customer_query_ed)
    private EditText query_ed;
    @ViewInject(R.id.customer_show_lv1)
    private PullableListView show_lv1;
    @ViewInject(R.id.customer_show_lv2)
    private ListView show_lv2;
    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout ptrl;
    @ViewInject(R.id.sidebar)
    private SideBar sideBar;
    @ViewInject(R.id.dialog)
    private TextView dialog;
    @ViewInject(R.id.sort_rgs)
    private RadioGroup sort_rgs;//排序的rgs
    @ViewInject(R.id.name_sort)
    private RadioButton name_sort;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private List<Customer> sortList = new ArrayList<>();
    private Dialog dialog1;
    private String sortString;
    private String mPageName = "CustomerFragment";
    private OrderNumComparator orderNumComparator;
    private PullToRefreshLayout pullToRefreshLayout;
    private String CheckFlag = "";//用于标记按照哪一个属性排序

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_customer, null);
        ViewUtils.inject(this, view);
        ptrl.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                setdate(pullToRefreshLayout);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar.setTextView(dialog);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = customerOrderAdapter1.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    show_lv1.setSelection(position);
                }

            }
        });
        dialog1 = LoadingUtils.createLoadingDialog(getActivity(), "正在加载");
        dialog1.show();
        setdate(pullToRefreshLayout);
        setListener();
        return view;
    }

    private RequestQueue requestQueue;

    public void setdate(final PullToRefreshLayout pullToRefreshLayout) {
        list1.clear();
        list2.clear();
        final String token = SharedUtils.getToken(getActivity(), "info");
        if (InternetUtils.internet(getActivity())) {
            requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest request = new StringRequest(Request.Method.GET, Constants.MY_CUSTOMER_URL + SharedUtils.getUser("user", getActivity()).getId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String jsonString = response;
                    try {
                        JSONArray array = new JSONArray(jsonString);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Customer customer = new Customer();
                            customer.setCustomer_name(obj.getString("headman_name"));
                            customer.setSource(Integer.parseInt(obj.getString("sources")));
                            customer.setPhonenumber(obj.getString("phone"));
                            customer.setBirthplace(obj.getString("origin"));
                            customer.setCompany(obj.getString("corporation"));
                            customer.setNotes(obj.getString("remark"));
                            customer.setId(obj.getString("headman_id"));
                            customer.setFirstrecord(obj.getString("recent_visit_record"));
                            customer.setFirsttime(obj.getString("last_visit_time"));
                            customer.setSex(String.valueOf(obj.getInt("sex") == 0 ? "未知" : (obj.getInt("sex") == 1 ? "男" : "女")));
                            customer.setLevel(obj.getString("level"));
                            JSONObject Obj = obj.getJSONObject("stats");
                            customer.setWaitorder_num(Obj.getString("waiting_recover_count"));
                            customer.setWait_money(Obj.getString("waiting_recover_price"));
                            customer.setAllorder_num(Obj.getString("count"));
                            customer.setAll_money(Obj.getString("price"));

                            list1.add(customer);
                            Log.i("NUM", i + "");
                        }
                        sortList.clear();
                        sortList = filledData(list1);
                        switch (CheckFlag) {
                            case "CUSTOMER_NAME"://姓名排序
                                typeSort("CUSTOMER_NAME", sortList);
                                break;
                            case "ALL_ORDER_NUM"://总下单
                                typeSort("ALL_ORDER_NUM", sortList);
                                break;
                            case "WAITING_ORDER_NUM"://待回款
                                typeSort("WAITING_ORDER_NUM", sortList);
                                break;
                            default:
                                typeSort("CUSTOMER_NAME", sortList);
                                break;
                        }
                        dialog1.dismiss();
                        if (pullToRefreshLayout == null) {
                        } else {
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer" + " " + token);
                    return headers;
                }
            };
            requestQueue.add(request);
        }
    }

    public void typeSort(String info, List<Customer> sortList) {
        if (info.equals("CUSTOMER_NAME")) {
            sideBar.setVisibility(View.VISIBLE);
        } else
            sideBar.setVisibility(View.GONE);
        orderNumComparator = new OrderNumComparator(info);
        Collections.sort(sortList, orderNumComparator);
        customerOrderAdapter1 = new CustomerOrderAdapter(sortList,false);
        show_lv1.setAdapter(customerOrderAdapter1);
        customerOrderAdapter1.notifyDataSetChanged();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.name_sort://点击姓名排序
                CheckFlag = "CUSTOMER_NAME";
                break;
            case R.id.order_num_sort://点击总下单排序
                CheckFlag = "ALL_ORDER_NUM";
                break;
            case R.id.order_money_sort://点击待回款排序
                CheckFlag = "WAITING_ORDER_NUM";
                break;

        }
        Log.i("CheckFlag", CheckFlag);
        typeSort(CheckFlag, sortList);
    }


    protected void setListener() {
        sort_rgs.setOnCheckedChangeListener(this);
        query_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (query_ed.getText().toString().length() == 0) {
                    show_lv1.setVisibility(View.VISIBLE);
                    show_lv2.setVisibility(View.INVISIBLE);
                } else {
                    show_lv1.setVisibility(View.INVISIBLE);
                    show_lv2.setVisibility(View.VISIBLE);
                    String str = query_ed.getText().toString();
                    if (list2 != null) {
                        list2.clear();
                    }
                    for (Customer c : sortList) {
                        if (c.getCustomer_name().contains(str) || c.getPhonenumber().contains(str)) {
                            list2.add(c);
                        }
                    }
                    if (list2 != null) {
                        show_lv1.setVisibility(View.INVISIBLE);
                        show_lv2.setVisibility(View.VISIBLE);
                        customerOrderAdapter2 = new CustomerOrderAdapter(list2,false);
                        show_lv2.setAdapter(customerOrderAdapter2);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //客户列表的某一项的点击
        show_lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CustomerDetailActivity.class);
                intent.putExtra("headman_id", sortList.get(position).getId());
                intent.putExtra("customer", sortList.get(position));
                startActivityForResult(intent, 1);
            }
        });
        show_lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CustomerDetailActivity.class);
                intent.putExtra("headman_id", list2.get(position).getId());
                intent.putExtra("customer", list2.get(position));
                startActivity(intent);
            }
        });
    }


    /**
     * @param list
     * @return
     */
    public List<Customer> filledData(List<Customer> list) {
        List<Customer> mcustomerList = new ArrayList<Customer>();
        for (int i = 0; i < list.size(); i++) {
            Customer customer = new Customer();
            customer.setCustomer_name(list.get(i).getCustomer_name());
            customer.setSource(list.get(i).getSource());
            customer.setPhonenumber(list.get(i).getPhonenumber());
            customer.setSex(list.get(i).getSex());
            customer.setLevel(list.get(i).getLevel());
            customer.setBirthplace(list.get(i).getBirthplace());
            customer.setCompany(list.get(i).getCompany());
            customer.setNotes(list.get(i).getNotes());
            customer.setId(list.get(i).getId());
            customer.setFirstrecord(list.get(i).getFirstrecord());
            customer.setFirsttime(list.get(i).getFirsttime());
            customer.setWaitorder_num(list.get(i).getWaitorder_num());
            customer.setWait_money(list.get(i).getWait_money());
            customer.setAllorder_num(list.get(i).getAllorder_num());
            customer.setAll_money(list.get(i).getAll_money());
            //如果客户的名字为空，就显示他的电话
            if (customer.getCustomer_name().equals("")) {
                customer.setCustomer_name("#" + list.get(i).getPhonenumber());
            }
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(customer.getCustomer_name());
            sortString = pinyin.substring(0, 1).toUpperCase();

            customer.setNamePinyin(pinyin);
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                customer.setSortLetters(sortString.toUpperCase());
            } else {
                customer.setSortLetters("#");
            }
            mcustomerList.add(customer);
        }
        return mcustomerList;
    }

    @OnClick({})
    public void onClick(View view) {

    }

    //标记是否从客户详情页面跳转过来
    private String FROM_CUS_DETAIL_FLAG = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            if (data.getStringExtra("REFRESH_FLAG").equals("NOT_REFRESH")) {
                FROM_CUS_DETAIL_FLAG = "FROM_CUS_DETAIL";
            } else if (data.getStringExtra("REFRESH_FLAG").equals("REFRESH")) {
                ptrl.autoRefresh();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }
}




