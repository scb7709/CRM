package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.yun.ycw.crm.adapter.CustomerOrderAdapter;
import com.yun.ycw.crm.comparator.CharacterParser;
import com.yun.ycw.crm.comparator.OrderNumComparator;
import com.yun.ycw.crm.comparator.PinyinComparator;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.customview.PickerView;
import com.yun.ycw.crm.customview.SideBar;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.Customer;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.service.UpdateCustomerService;
import com.yun.ycw.crm.utils.GetStaffHierarchyUtils;
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

/**
 * Created by scb on 2016/2/25.
 */
public class CustomerActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    public List<Customer> list1 = new ArrayList<>(), list2 = new ArrayList<>();
    private CustomerOrderAdapter customerOrderAdapter1, customerOrderAdapter2;
    private Customer customer;
    @ViewInject(R.id.customer_query_ed)
    private EditText query_ed;
    @ViewInject(R.id.customer_man)
    private TextView man;//谁的客户
    @ViewInject(R.id.customer_re)
    private RelativeLayout relativeLayout;//
    @ViewInject(R.id.customer_back)
    private ImageButton back;//
    @ViewInject(R.id.customer_add)
    private ImageView customer_add;//
    @ViewInject(R.id.customer_show_lv2)
    private ListView show_lv2;
    @ViewInject(R.id.sidebar)
    private SideBar sideBar;
    @ViewInject(R.id.dialog)
    private TextView dialog;
    @ViewInject(R.id.sort_rgs)
    private RadioGroup sort_rgs;//排序的rgs
    @ViewInject(R.id.customer_show_lv1)
    private PullableListView show_lv1;
    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout ptrl;
    @ViewInject(R.id.name_sort)
    private RadioButton name_sort;
    @ViewInject(R.id.customer_error)
    private LinearLayout customer_error;

    @ViewInject(R.id.customer_checkbox_re)
    private LinearLayout customer_checkbox_re;
    @ViewInject(R.id.customer_checkbox_re_repeal)
    private Button customer_checkbox_re_repeal;
    @ViewInject(R.id.customer_checkbox_re_transfer)
    private Button customer_checkbox_re_transfer;
    @ViewInject(R.id.customer_checkbox_re_all)
    private Button customer_checkbox_re_all;

    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private OrderNumComparator orderNumComparator;
    private List<Customer> sortList = new ArrayList<>();
    private Dialog dialog1;
    private Handler handler;
    private Achievement achievement;
    private String sortString;
    private String mPageName = "CustomerActivity";
    private Context mContext;
    private PullToRefreshLayout pullToRefreshLayout;
    private String CheckFlag = "";
    private String url;
    private String isPublicOcean;
    private boolean isCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_customer);
        ViewUtils.inject(this);
        mContext = this;
        setData();
        ptrl.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                setdatee(pullToRefreshLayout);
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
        dialog1 = LoadingUtils.createLoadingDialog(CustomerActivity.this, "正在加载");
        dialog1.show();
        setdatee(pullToRefreshLayout);
        setListener();
    }

    private void setData() {
        achievement = (Achievement) getIntent().getSerializableExtra("achievement");
        //Log.i("VVVVVVVVVVVV", achievement.getUserid());
        String str="&start_time="+achievement.getStarttime()+"&end_time="+achievement.getEndtime();
        Log.i("VVVVVVVVVVVV", str);
        isPublicOcean = getIntent().getStringExtra("isPublicOcean");
        if (isPublicOcean.equals("1")) {
            man.setText("公海池客户");
            customer_add.setVisibility(View.GONE);
            url = Constants.MY_CUSTOMER_URL + "0"+str;
        } else {
            man.setText(achievement.getUsername());
            url = Constants.MY_CUSTOMER_URL + achievement.getUserid()+str;
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setListener() {
        sort_rgs.setOnCheckedChangeListener(this);
        query_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                list2.clear();
                if (query_ed.getText().toString().length() == 0) {
                    show_lv1.setVisibility(View.VISIBLE);
                    show_lv2.setVisibility(View.GONE);
                    sortList.clear();
                    sortList = filledData(list1);
                    orderNumComparator = new OrderNumComparator(CheckFlag);
                    Collections.sort(sortList, orderNumComparator);
                    customerOrderAdapter1 = new CustomerOrderAdapter(list1, false);
                    show_lv1.setAdapter(customerOrderAdapter1);

                } else {

                    show_lv1.setVisibility(View.GONE);
                    show_lv2.setVisibility(View.VISIBLE);
                    String str = query_ed.getText().toString();
                    for (Customer c : sortList) {
                        if (c.getCustomer_name().contains(str) || c.getPhonenumber().contains(str)) {
                            list2.add(c);
                        }
                    }
                    sortList.clear();
                    sortList = filledData(list2);
                    show_lv1.setVisibility(View.GONE);
                    show_lv2.setVisibility(View.VISIBLE);
                    orderNumComparator = new OrderNumComparator(CheckFlag);
                    Collections.sort(sortList, orderNumComparator);
                    customerOrderAdapter2 = new CustomerOrderAdapter(list2, false);
                    show_lv2.setAdapter(customerOrderAdapter2);
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
                if (!isCheckBox) {
                    Intent intent = new Intent(CustomerActivity.this, CustomerDetailActivity.class);
                    intent.putExtra("headman_id", sortList.get(position).getId());
                    intent.putExtra("customer", sortList.get(position));
                    intent.putExtra("isPublicOcean", isPublicOcean);
                    startActivity(intent);
                }
            }
        });
        show_lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isCheckBox) {
                    Intent intent = new Intent(CustomerActivity.this, CustomerDetailActivity.class);
                    intent.putExtra("headman_id", list2.get(position).getId());
                    intent.putExtra("customer", list2.get(position));
                    intent.putExtra("isPublicOcean", isPublicOcean);
                    startActivity(intent);
                }
            }
        });
        if (!isPublicOcean.equals("1") && !SharedUtils.getUser("user", CustomerActivity.this).getLevel().equals("one")) {
            show_lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    show_lv2.setVisibility(View.GONE);
                    show_lv1.setVisibility(View.VISIBLE);
                    isCheckBox = true;
                    customer_checkbox_re.setVisibility(View.VISIBLE);
                    sideBar.setVisibility(View.GONE);

                  /*  list2.clear();
                    list2 = list1;*/
                    Log.i("ssss", CheckFlag);
                    sortList.clear();
                    sortList = filledData(list1);
                    orderNumComparator = new OrderNumComparator(CheckFlag);
                    Collections.sort(sortList, orderNumComparator);
                    customerOrderAdapter1 = new CustomerOrderAdapter(sortList, true);
                    show_lv1.setAdapter(customerOrderAdapter1);
                    return true;
                }
            });
            show_lv2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    show_lv1.setVisibility(View.GONE);
                    show_lv2.setVisibility(View.VISIBLE);
                    isCheckBox = true;
                    customer_checkbox_re.setVisibility(View.VISIBLE);
                    sideBar.setVisibility(View.GONE);
                    if (list2 != null) {
                        orderNumComparator = new OrderNumComparator(CheckFlag);
                        Collections.sort(list2, orderNumComparator);
                        customerOrderAdapter2 = new CustomerOrderAdapter(list2, true);
                        show_lv2.setAdapter(customerOrderAdapter2);
                    }
                    return true;
                }
            });
        }


    }

    public void typeSort(String info, List<Customer> sortList) {
        customer_checkbox_re.setVisibility(View.GONE);
        show_lv1.setVisibility(View.VISIBLE);
        show_lv2.setVisibility(View.GONE);
        if (info.equals("CUSTOMER_NAME")) {
            sideBar.setVisibility(View.VISIBLE);
        } else
            sideBar.setVisibility(View.GONE);
        orderNumComparator = new OrderNumComparator(info);
        Collections.sort(sortList, orderNumComparator);
        customerOrderAdapter1 = new CustomerOrderAdapter(sortList, false);
        show_lv1.setAdapter(customerOrderAdapter1);
        customerOrderAdapter1.notifyDataSetChanged();
    }

    private RequestQueue requestQueue;

    public void setdate(final PullToRefreshLayout pullToRefreshLayout) {

        list1.clear();
        list2.clear();

        final String token = SharedUtils.getToken(CustomerActivity.this, "info");
        if (InternetUtils.internet(CustomerActivity.this)) {
            requestQueue = Volley.newRequestQueue(CustomerActivity.this);
            Log.i("URL", url);
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String s) {
                    try {
                        JSONArray array = new JSONArray(s);
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
                            customer.setSex((obj.getString("sex").equals("0") || obj.getString("sex") == null) ? "未知" : (obj.getString("sex").equals("1") ? "男" : "女"));
                            customer.setLevel(obj.getString("level") == null ? "未知" : obj.getString("level"));
                            JSONObject Obj = obj.getJSONObject("stats");
                            customer.setWaitorder_num(Obj.getString("waiting_recover_count"));
                            customer.setWait_money(Obj.getString("waiting_recover_price"));
                            customer.setAllorder_num(Obj.getString("count"));
                            customer.setAll_money(Obj.getString("price"));

                            list1.add(customer);
                            Log.i("NUM", i + "");
                        }
                        if (list1.size() == 0) {
                            customer_error.setVisibility(View.VISIBLE);
                            show_lv1.setVisibility(View.GONE);
                        } else {
                            customer_error.setVisibility(View.GONE);
                            show_lv1.setVisibility(View.VISIBLE);
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
                                CheckFlag = "CUSTOMER_NAME";
                                break;
                        }
                        dialog1.dismiss();

                        if (pullToRefreshLayout == null) {

                        } else {
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CustomerActivity.this, "JsonException", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (volleyError.getMessage() == null) {
                        Toast.makeText(CustomerActivity.this, "没有找到客户", Toast.LENGTH_LONG).show();
                    }
                    dialog1.dismiss();
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
    public void setdatee(final PullToRefreshLayout pullToRefreshLayout) {
        list1.clear();
        list2.clear();
        if (InternetUtils.internet(CustomerActivity.this)) {
            final  HttpUtils httpUtils = new HttpUtils();
            httpUtils.configCurrentHttpCacheExpiry(60);
            final RequestParams params = new RequestParams();
            params.addHeader("Authorization", "Bearer" + " " + SharedUtils.getToken(this, "info"));
            httpUtils.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String s = responseInfo.result;
                    try {
                        JSONArray array = new JSONArray(s);
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
                            customer.setSex((obj.getString("sex").equals("0") || obj.getString("sex") == null) ? "未知" : (obj.getString("sex").equals("1") ? "男" : "女"));
                            customer.setLevel(obj.getString("level") == null ? "未知" : obj.getString("level"));
                            JSONObject Obj = obj.getJSONObject("stats");
                            customer.setWaitorder_num(Obj.getString("waiting_recover_count"));
                            customer.setWait_money(Obj.getString("waiting_recover_price"));
                            customer.setAllorder_num(Obj.getString("count"));
                            customer.setAll_money(Obj.getString("price"));

                            list1.add(customer);
                            Log.i("NUM", i + "");
                        }
                        if (list1.size() == 0) {
                            customer_error.setVisibility(View.VISIBLE);
                            show_lv1.setVisibility(View.GONE);
                        } else {
                            customer_error.setVisibility(View.GONE);
                            show_lv1.setVisibility(View.VISIBLE);
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
                                CheckFlag = "CUSTOMER_NAME";
                                break;
                        }
                        dialog1.dismiss();

                        if (pullToRefreshLayout == null) {

                        } else {
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CustomerActivity.this, "JsonException", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(CustomerActivity.this, "没有找到客户", Toast.LENGTH_LONG).show();
                }

            } );

        }


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
            customer.setAllorder_num(list.get(i).getAllorder_num());
            customer.setAll_money(list.get(i).getAll_money());
            customer.setWaitorder_num(list.get(i).getWaitorder_num());
            customer.setWait_money(list.get(i).getWait_money());
            customer.setId(list.get(i).getId());
            customer.setSource(list.get(i).getSource());
            customer.setPhonenumber(list.get(i).getPhonenumber());
            customer.setSex(list.get(i).getSex());
            customer.setLevel(list.get(i).getLevel());
            customer.setBirthplace(list.get(i).getBirthplace());
            customer.setCompany(list.get(i).getCompany());
            customer.setFirsttime(list.get(i).getFirsttime());
            customer.setFirstrecord(list.get(i).getFirstrecord());
            customer.setNotes(list.get(i).getNotes());
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

    @OnClick({R.id.name_sort, R.id.order_num_sort, R.id.order_money_sort, R.id.customer_add, R.id.customer_checkbox_re_repeal, R.id.customer_checkbox_re_transfer, R.id.customer_checkbox_re_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.customer_add:

                Intent intent = new Intent(CustomerActivity.this, AddCustomerActivity.class);
                if (SharedUtils.getUser("user", CustomerActivity.this).getLevel().equals("three")) {
                    intent.putExtra("flag", "add_customer");
                } else if (SharedUtils.getUser("user", CustomerActivity.this).getLevel().equals("two") || SharedUtils.getUser("user", CustomerActivity.this).getLevel().equals("one")) {
                    intent.putExtra("flag", "allocation_customer");
                }
                intent.putExtra("name", achievement.getUsername());
                intent.putExtra("id", achievement.getUserid());
                startActivityForResult(intent, 1);
                break;

            case R.id.customer_checkbox_re_repeal:

                isCheckBox = false;
                sortList.clear();
                sortList = filledData(list1);
                orderNumComparator = new OrderNumComparator(CheckFlag);
                Collections.sort(sortList, orderNumComparator);
                typeSort(CheckFlag, sortList);
                break;
            case R.id.customer_checkbox_re_transfer:

                List<Customer> choicelist = new ArrayList<Customer>();
                if (list2.size() == 0) {

                    for (int i = 0; i < customerOrderAdapter1.getCount(); i++) {
                        Customer customer = customerOrderAdapter1.getItem(i);
                        Log.i("CCCCCMMM", customer.isChoice() + "");
                        if (customer.isChoice()) {
                            choicelist.add(customer);
                        }
                    }
                    Log.i("CCCCCNNNN", choicelist.size() + "");


                } else {
                    for (int i = 0; i < list2.size(); i++) {
                        Customer customer = customerOrderAdapter2.getItem(i);
                        //  Log.i("CCCCCMMM", customer.isChoice() + "");
                        if (customer.isChoice()) {
                            choicelist.add(customer);
                        }
                    }
                }
                StringBuffer customerId = new StringBuffer();
                for (int i = 0; i < choicelist.size(); i++) {
                    customerId.append(choicelist.get(i).getId() + ",");
                }
                Log.i("SSSSSSSSDD", customerId.toString());
                AlertDialog.Builder bu = new AlertDialog.Builder(CustomerActivity.this);
                final AlertDialog dia = bu.create();

                View v = setSpinner2(dia, customerId.toString().substring(0, customerId.length()-1));

                dia.setView(v, 20, 20, 20, 50);
                dia.setCanceledOnTouchOutside(true);//点击边界取消
                dia.show();
                break;
            case R.id.customer_checkbox_re_all:
                List<Customer> choiceAllList = new ArrayList<Customer>();
                if (list2.size() == 0) {
                    choiceAllList.clear();
                    Log.i("CCCCCMMM", customerOrderAdapter1.getCount() + "");
                    for (int i = 0; i < customerOrderAdapter1.getCount(); i++) {
                        Customer customer = customerOrderAdapter1.getItem(i);
                        if (customer.isChoice()) {
                            customer.setIsChoice(false);
                        } else {
                            customer.setIsChoice(true);
                        }
                        choiceAllList.add(customer);
                        Log.i("CCCCCMMM", sortList.size() + "");
                    }
                    Log.i("CCCCCAAAA", sortList.size() + "");
                    orderNumComparator = new OrderNumComparator(CheckFlag);
                    Collections.sort(choiceAllList, orderNumComparator);
                    customerOrderAdapter1 = new CustomerOrderAdapter(choiceAllList, true);
                    show_lv1.setAdapter(customerOrderAdapter1);

                } else {
                    choiceAllList.clear();
                    for (int i = 0; i < customerOrderAdapter2.getCount(); i++) {
                        Customer customer = customerOrderAdapter2.getItem(i);
                        if (customer.isChoice()) {
                            customer.setIsChoice(false);
                        } else {
                            customer.setIsChoice(true);
                        }
                        choiceAllList.add(customer);
                    }
                    orderNumComparator = new OrderNumComparator(CheckFlag);
                    Collections.sort(choiceAllList, orderNumComparator);
                    customerOrderAdapter2 = new CustomerOrderAdapter(choiceAllList, true);
                    show_lv2.setAdapter(customerOrderAdapter2);
                }
                break;

        }
    }


//    private String flag = "backFromCusDetail";

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
//        if (flag.equals("backFromCusDetail")) {
//        } else {
//            ptrl.autoRefresh();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(mContext);
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

    private PickerView one_pv;
    private PickerView second_pv;
    private List<String> seconds;
    private List<User> userlist2;
    private String user_id;

    private View setSpinner2(final AlertDialog dia, final String customerId) {
        List<User> userlist1 = GetStaffHierarchyUtils.getSubordinate("28", CustomerActivity.this);
        View view = LayoutInflater.from(CustomerActivity.this).inflate(R.layout.pickerview, null);

        one_pv = (PickerView) view.findViewById(R.id.pickerview_one_pv);
        second_pv = (PickerView) view.findViewById(R.id.pickerview_second_pv);
        Button ok = (Button) view.findViewById(R.id.pickerview_ok);
        List<String> data = new ArrayList<String>();
        seconds = new ArrayList<String>();
        Log.i("QQQQQQQ", userlist1.toString());
        for (int i = 0; i < userlist1.size(); i++) {
            data.add(userlist1.get(i).getName());
        }

        userlist2 = GetStaffHierarchyUtils.getSubordinate(userlist1.get(userlist1.size() / 2).getId(), CustomerActivity.this);
        final String myid = SharedUtils.getUser("user", CustomerActivity.this).getId();
        for (int i = 0; i < userlist2.size(); i++) {
            if (userlist2.get(i).getId().equals(myid)) {
                userlist2.remove(i);
            }
        }
        for (int i = 0; i < userlist2.size(); i++) {
            seconds.add(userlist2.get(i).getName());
        }
        one_pv.setData(data);
        second_pv.setData(seconds);
        user_id = userlist2.get(userlist2.size() / 2).getId() + "";
        Log.i("CCCCCCCC", userlist2.get(userlist2.size() / 2).getName());
        Log.i("CCCCCCCC", user_id);
        one_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                if (seconds.size() != 0) {
                    seconds.clear();
                }
                if (userlist2 != null && userlist2.size() != 0) {
                    userlist2.clear();
                }
                userlist2 = GetStaffHierarchyUtils.getSubordinate(GetStaffHierarchyUtils.getId(text, CustomerActivity.this), CustomerActivity.this);
                for (int i = 0; i < userlist2.size(); i++) {
                    if (userlist2.get(i).getId().equals(myid)) {
                        userlist2.remove(i);
                    }
                }
                for (int i = 0; i < userlist2.size(); i++) {
                    seconds.add(userlist2.get(i).getName());
                }
                //if(userlist2.size()!=0){
                user_id = userlist2.get(userlist2.size() / 2).getId() + "";
                Log.i("CCCCCCCC", userlist2.get(userlist2.size() / 2).getName());
                Log.i("CCCCCCCC", user_id);

                // }
                second_pv.setData(seconds);
            }
        });

        second_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                Log.i("SSSSaaaa", text);
                user_id = GetStaffHierarchyUtils.getId(text, CustomerActivity.this);
                Log.i("SSSSaaaa", user_id);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id != null) {
                    Log.i("SSSSbbbb", user_id);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", user_id);
                    Log.i("SSSSSXXXXX", customerId + "");
                    UpdateCustomerService.getInstance( CustomerActivity.this).transferCustomer(CustomerActivity.this, customerId, params);//转移
                    dia.dismiss();
                    onCreate(null);
                }

            }
        });
        return view;
    }
}
