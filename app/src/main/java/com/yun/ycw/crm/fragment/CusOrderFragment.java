package com.yun.ycw.crm.fragment;

import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.activity.OrderDetailsActivity;
import com.yun.ycw.crm.adapter.EAdapter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Customer;
import com.yun.ycw.crm.entity.UndoneOrder;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是 客户详情的订单
 * Created by wdyan on 2016/2/1.
 */
public class CusOrderFragment extends Fragment implements ExpandableListView.OnChildClickListener {
    private List<Customer> orderlist = new ArrayList<>();
    private List<UndoneOrder> undoneList = new ArrayList<>();
    private List<UndoneOrder> historyList = new ArrayList<>();
    private List<UndoneOrder> allList = new ArrayList<>();
    private List<List<UndoneOrder>> list = new ArrayList<>();
    private ExpandableListView Order_listview;
    private String status;//订单状态
    private List<UndoneOrder> listTemp = new ArrayList<>();
    private ExpandableListAdapter adapter;
    private String mPageName = "CusOrderFragment";
    private Dialog dialog;
    private TextView info;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cus_order, null);
        info = (TextView) view.findViewById(R.id.info);
        initView(view);
        return view;
    }

    private void initView(View view) {
        Order_listview = (ExpandableListView) view.findViewById(R.id.order_listview);
        Order_listview.setOnChildClickListener(this);
        Order_listview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        Customer customer = (Customer) getActivity().getIntent().getSerializableExtra("customer");
        String headman_id = customer.getId();

        dialog = LoadingUtils.createLoadingDialog(getActivity(), "正在加载");
        dialog.show();
        String token = SharedUtils.getToken(getActivity(), "info");
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addHeader("Authorization", "Bearer" + " " + token);
        String s = Constants.ORDER_DETAIL_URL + headman_id;
        httpUtils.send(HttpRequest.HttpMethod.GET, Constants.ORDER_DETAIL_URL + headman_id, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String jsonString = responseInfo.result;
                int iii = 0;
                try {
                    JSONArray array = new JSONArray(jsonString);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject Obj = array.getJSONObject(i);
                        UndoneOrder order = new UndoneOrder();
                        String originTime = Obj.getString("time");
                        String year = originTime.substring(0, 10);
                        String time = originTime.substring(11, 19);
                        String formated_time = time + '\n' + year;
                        order.setTime(formated_time);//下单时间
                        order.setOrderid(Obj.getString("orderid"));//订单编号
                        order.setLaststep(Constants.getOrderStatus(Integer.parseInt(Obj.getString("laststep"))));//订单状态
                        order.setTotal_price(Obj.getString("total_price"));//订单金额
                        order.setPaid(Obj.getInt("paid"));//是否已支付
                        order.setId(Obj.getInt("id"));
                        Log.i("AAAAAAAAAAAAAA", order.getId()+"");
                        //  order.setCommission(Obj.getString("staff_income"));

                        JSONArray comArray = Obj.getJSONArray("staff_income");
                        if (SharedUtils.getUser("user", getActivity()).getSuperior_id().equals("0") || SharedUtils.getUser("user", getActivity()).getSuperior_id().equals("28")) {
                            if (comArray.length() == 0) {
                                order.setCommission("0.00/0.00");
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
                        allList.add(order);
                        iii++;
                        Log.i("NUM", iii + " " + order.getId());
                    }

                    //如果客户没有订单
                    if (allList.size() == 0) {
                        info.setText("暂无订单");
                        Order_listview.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                    } else {
                        //如果客户有订单
                        UndoneOrder undoneOrder = null;
                        if ((SharedUtils.getUser("user", getActivity()).getSuperior_id().equals("0") || SharedUtils.getUser("user", getActivity()).getSuperior_id().equals("28"))) {
                            undoneOrder = new UndoneOrder("下单时间", "发货状态", "订单金额", "订单提成" + '\n' + "(主管/专员)");
                        } else {
                            undoneOrder = new UndoneOrder("下单时间", "发货状态", "订单金额", "订单提成");
                        }

                        undoneList.add(undoneOrder);
                        historyList.add(undoneOrder);
                        for (int i = 0; i < allList.size(); i++) {
                            if ((allList.get(i).getLaststep().equals("已结算")) || (allList.get(i).getLaststep().equals("已完成")) || (allList.get(i).getLaststep().equals("已取消"))) {
                                historyList.add(allList.get(i));
                            } else {
                                undoneList.add(allList.get(i));
                            }
                        }
                        if (undoneList.size() == 1) {
                            undoneList.clear();
                            orderlist.add(new Customer("未完成订单", null, undoneList.size() + ""));
                        } else {
                            orderlist.add(new Customer("未完成订单", null, (undoneList.size() - 1) + ""));
                        }
                        if (historyList.size() == 1) {
                            historyList.clear();
                            orderlist.add(new Customer("已完成订单", null, historyList.size() + ""));
                        } else {
                            orderlist.add(new Customer("已完成订单", null, (historyList.size() - 1) + ""));
                        }
                        list.add(undoneList);
                        list.add(historyList);


                        EAdapter adapter = new EAdapter(getActivity(), list, orderlist);
                        Order_listview.setAdapter(adapter);
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                info.setText("商城用户不存在");
                Order_listview.setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (childPosition != 0) {
            Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
            intent.putExtra("orderid", list.get(groupPosition).get(childPosition).getId() + "");
            startActivity(intent);
        }
        return false;
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
