package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.CustomerOrderAdapter;
import com.yun.ycw.crm.adapter.EAdapter;
import com.yun.ycw.crm.adapter.UndoneAdapter;
import com.yun.ycw.crm.comparator.*;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Customer;
import com.yun.ycw.crm.entity.UndoneOrder;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 待回款订单的activity
 * Created by wdyan on 2016/1/30.
 */
public class UndoneActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener {
    @ViewInject(R.id.undone_sort_rgs)
    private RadioGroup undone_sort_rgs;
    @ViewInject(R.id.undone_back)
    private ImageButton undone_back;
    @ViewInject(R.id.undone_listview)
    private ListView listView;

    @ViewInject(R.id.undone_spinner)
    private Spinner undone_spinner;

    private UndoneAdapter adapter;
    private boolean paytype;
    List<UndoneOrder> undoneOrderList = new ArrayList<UndoneOrder>();
    private UndoneOrder titleOne = new UndoneOrder("客户", "下单时间", "发货状态", "订单金额", "订单提成" + '\n' + "(主管/专员)");
    private UndoneOrder titleTwo = new UndoneOrder("客户", "下单时间", "发货状态", "订单金额", "订单提成");
    private List<UndoneOrder> tempList = new ArrayList<>();
    private String personId;
    private CharacterParser characterParser;
    private PinyinOrderComparator pinyinOrderComparator;
    private List<UndoneOrder> sortList = new ArrayList<>();
    private Context mContext;
    private String mPageName = "UndoneActivity";
    private UndoneOrderComparator undoneOrderComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undone);
        ViewUtils.inject(this);
        mContext = this;
        personId = getIntent().getStringExtra("personId");
        undone_back.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        undone_sort_rgs.setOnCheckedChangeListener(this);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(UndoneActivity.this, R.layout.item_spinner_checkin);
        adapter1.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        //加载数据源
        adapter1.add("个人支付");
        adapter1.add("企业支付");

        //绑定适配器
        undone_spinner.setAdapter(adapter1);

        undone_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                       @Override
                                                       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                           if(position==0){
                                                               paytype=true;
                                                           }
                                                           else {
                                                               paytype=false;
                                                           }
                                                        //   initView();
                                                       }

                                                       @Override
                                                       public void onNothingSelected(AdapterView<?> parent) {

                                                       }
                                                   }
        );
        initView();
    }

    public void initView() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinOrderComparator = new PinyinOrderComparator();
        final Dialog dialog = LoadingUtils.createLoadingDialog(UndoneActivity.this, "正在加载");
        dialog.show();
        String token = SharedUtils.getToken(this, "info");

        if (InternetUtils.internet(this)) {
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();
            params.addHeader("Authorization", "Bearer" + " " + token);
            String s = Constants.UNDONE_ORDER_LEADER + personId;
            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.UNDONE_ORDER_LEADER + personId, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String jsonString = responseInfo.result;
                    Log.i("AAAAAAAAAAAAAAA",jsonString);
                    try {
                        JSONArray array = new JSONArray(jsonString);
                        if (array.length() == 0) {

                        } else {
                            if (SharedUtils.getUser("user", UndoneActivity.this).getSuperior_id().equals("0") || SharedUtils.getUser("user", UndoneActivity.this).getSuperior_id().equals("28")) {
                                undoneOrderList.add(titleOne);
                            } else {
                                undoneOrderList.add(titleTwo);
                            }
                        }
                        double sumMoney=0;
                        for (int i = 0; i < array.length(); i++) {
                            //获取待回款订单对象
                            JSONObject undoneObj = array.getJSONObject(i);
                            UndoneOrder order = new UndoneOrder();

                            String originTime = undoneObj.getString("time");
                            String year = originTime.substring(0, 10);
                            String time = originTime.substring(11, 19);
                            order.setOriginTime(year + " " + time);
                            String formated_time = time + '\n' + year;
                            order.setId(undoneObj.getInt("id"));
                            if (undoneObj.getString("truename").equals("")) {
                                order.setTruename("#" + "未知");
                            } else {
                                order.setTruename(undoneObj.getString("truename"));//设置待回款订单的客户的名字
                            }
                            order.setTime(formated_time);//下单时间
                            order.setPaytype(undoneObj.getString("paytype"));//支付方式

                            order.setOrderid(undoneObj.getString("orderid"));//设置订单编号
                            order.setLaststep(Constants.getOrderStatus(undoneObj.getInt("laststep")));//设置发货状态
                            order.setTotal_price(undoneObj.getString("total_price"));//设置订单总额
                            sumMoney+=undoneObj.getDouble("total_price");
//                            order.setCommission(undoneObj.getString("staff_income"));//设置订单提成
                            JSONArray comArray = undoneObj.getJSONArray("staff_income");
                            Log.i("NUM--", i + "");
                            //如果是主管和上级
                            if (SharedUtils.getUser("user", UndoneActivity.this).getSuperior_id().equals("0") || SharedUtils.getUser("user", UndoneActivity.this).getSuperior_id().equals("28")) {
                                if (comArray.length() == 0) {
                                    order.setCommission("0.00/0.00");
                                } else if (comArray.length() == 1) {
                                    order.setCommission("0.00/" + comArray.getJSONObject(0).getString("amount"));
                                } else {
                                    order.setCommission(comArray.getJSONObject(1).getString("amount") + "/" + comArray.getJSONObject(0).getString("amount"));
                                }
                            } else {
                                if (comArray.length() == 0) {
                                    order.setCommission("0.00");
                                } else {
                                    order.setCommission(comArray.getJSONObject(0).getString("amount"));
                                }
                            }
                            Log.i("UNDONENUM--",i+" ");
                            undoneOrderList.add(order);
                        }
                        Log.i("AAAAAAAAAAAAAAA",sumMoney+"");
                        sortList = filledData(undoneOrderList);
                        // 根据a-z进行排序源数据
                        Collections.sort(sortList, pinyinOrderComparator);
                        if (sortList.size() > 0) {
                            if (SharedUtils.getUser("user", UndoneActivity.this).getSuperior_id().equals("0") || SharedUtils.getUser("user", UndoneActivity.this).getSuperior_id().equals("28")) {
                                tempList.add(titleOne);
                            } else {
                                tempList.add(titleTwo);
                            }
                            tempList.addAll(sortList);
                        }
                        setAdapter(tempList);

                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("EEEEEEE", "SSSSSSSSSSS");
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i("EEEEE", s);
                    dialog.dismiss();

                }
            });
        }
    }

    private void setAdapter(List<UndoneOrder> tempList) {
        List<UndoneOrder> list = new ArrayList<UndoneOrder>();
        list.clear();
        list.add(tempList.get(0));
        if(paytype){

            for(int i=1;i<tempList.size();i++){
                if(!tempList.get(i).getPaytype().equals("firm")){
                    list.add(tempList.get(i)) ;
                }

            }
        }
        else {
            for (int i = 1; i < tempList.size();i++){
                if(tempList.get(i).getPaytype().equals("firm")){
                    list.add(tempList.get(i)) ;

                }
            }
        }
        UndoneAdapter  adapterr = new UndoneAdapter(tempList, UndoneActivity.this);
        listView.setAdapter(adapterr);
    }

    public List<UndoneOrder> filledData(List<UndoneOrder> list) {
        //用于存放按照字母排好序的待回款订单
        List<UndoneOrder> undoneOrderList = new ArrayList<UndoneOrder>();

        for (int i = 1; i < list.size(); i++) {
            UndoneOrder order = new UndoneOrder();
            order.setTruename(list.get(i).getTruename());
            order.setTime(list.get(i).getTime());
            order.setOrderid(list.get(i).getOrderid());
            order.setLaststep(list.get(i).getLaststep());
            order.setTotal_price(list.get(i).getTotal_price());
            order.setCommission(list.get(i).getCommission());
            order.setId(list.get(i).getId());
            order.setPaytype(list.get(i).getPaytype());
            Log.i("SSSSSSSSSS",order.getPaytype());
            order.setOriginTime(list.get(i).getOriginTime());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(list.get(i).getTruename());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            order.setNamePinyin(pinyin);
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                order.setSortLetters(sortString.toUpperCase());
            } else {
                order.setSortLetters("#");
            }

            undoneOrderList.add(order);
        }
        return undoneOrderList;

    }

    //back按钮
    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            Intent intent = new Intent(UndoneActivity.this, OrderDetailsActivity.class);
            intent.putExtra("orderid", tempList.get(position).getId() + "");
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(mContext);
    }

    public void typeSort(String info) {
        undoneOrderComparator = new UndoneOrderComparator(info);
        UndoneOrder order = tempList.get(0);
        Collections.sort(sortList, undoneOrderComparator);
        tempList.clear();
        tempList.add(order);
        tempList.addAll(sortList);
        setAdapter(tempList);
       // adapter.notifyDataSetChanged();
    }
private String sort;
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.undone_time_sort://待回款中的按时间排序
                sort="UNDONE_TIME";
                break;
            case R.id.undone_money_sort://待回款中的按订单金额排序
                sort="UNDONE_TIME";
                break;
            case R.id.undone_name_sort://待回款中的按姓名排序
                sort="UNDONE_TIME";
                break;
            default:
                break;
        }
        typeSort(sort);
    }
}
