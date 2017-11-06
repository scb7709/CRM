package com.yun.ycw.crm.customview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.activity.AddCustomerActivity;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.Customer;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.service.DelCustomerService;
import com.yun.ycw.crm.service.UpdateCustomerService;
import com.yun.ycw.crm.utils.GetStaffHierarchyUtils;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编辑客户信息的menu
 * Created by wdyan on 2016/2/1.
 */
public class CustomPopmenu extends PopupWindow implements View.OnClickListener {
    private int resId;
    private Context context;
    private LayoutInflater inflater;
    public View defaultView;
    public Customer customer;
    private String customer_id;
    private Spinner spinner1;
    private Spinner spinner2;
    private String user_id;
    private String isPublicOcean;
    private List<User> userlist1;
    private List<User> userlist2;
    private PopupWindow popupWindow;

    public CustomPopmenu(Context context, Customer customer, int resId, String isPublicOcean) {
        super(context);
        this.context = context;
        this.customer = customer;
        this.resId = resId;
        this.isPublicOcean = isPublicOcean;
        initPopupWindow();
    }

    public void initPopupWindow() {
        customer_id = customer.getId();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        defaultView = inflater.inflate(this.resId, null);
        defaultView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setContentView(defaultView);
        TextView edit_info = (TextView) defaultView.findViewById(R.id.edit_info);
        TextView delete_customer = (TextView) defaultView.findViewById(R.id.delete_customer);
        TextView transfer_customer = (TextView) defaultView.findViewById(R.id.transfer_customer);
        LinearLayout transfer_customer_layout = (LinearLayout) defaultView.findViewById(R.id.transfer_customer_layout);
        LinearLayout delete_customer_layout = (LinearLayout) defaultView.findViewById(R.id.delete_customer_layout);
        LinearLayout edit_info_layout = (LinearLayout) defaultView.findViewById(R.id.edit_info_layout);
        LinearLayout popmenu_background = (LinearLayout) defaultView.findViewById(R.id.popmenu_background);

        ImageView delete_imageview = (ImageView) defaultView.findViewById(R.id.delete_imageview);

        if (isPublicOcean.equals("1")) {
            popmenu_background.setBackgroundResource(R.mipmap.more_kuang_);
            transfer_customer_layout.setVisibility(View.GONE);
            edit_info_layout.setVisibility(View.GONE);
            if (SharedUtils.getUser("user", context).getSuperior_id().equals("0") || SharedUtils.getUser("user", context).getSuperior_id().equals("28")) {
                delete_customer.setText("删除客户");
                delete_imageview.setImageResource(R.mipmap.delete);
                delete_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("删除确认")
                                .setMessage("确定要删除该客户吗?")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DelCustomerService.getInstance(context).delCustomer(customer_id, context);
                                        dialog.dismiss();
                                        CustomPopmenu.this.dismiss();
                                        Activity a = (Activity) context;
                                        a.finish();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false);
                        Dialog dialog = builder.create();
                        dialog.show();
                    }
                });
            } else {
                delete_customer.setText("激活客户");
                delete_imageview.setImageResource(R.mipmap.activation);
                delete_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("激活确认")
                                .setMessage("确定要申请重新激活该客户吗?")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("customer_id", customer_id);
                                        DelCustomerService.getInstance(context).activiteCustomer(map, context);
                                        dialog.dismiss();
                                        CustomPopmenu.this.dismiss();
                                        Activity a = (Activity) context;
                                        a.finish();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false);
                        Dialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        } else if (isPublicOcean.equals("2")) {//主管打开客户的详细资料只有转移
            popmenu_background.setBackgroundResource(R.mipmap.more_kuang_);
            delete_customer_layout.setVisibility(View.GONE);
            edit_info_layout.setVisibility(View.GONE);
            transfer_customer_layout.setVisibility(View.VISIBLE);
            transfer_customer.setOnClickListener(this);
        } else {
            edit_info_layout.setVisibility(View.VISIBLE);
            popmenu_background.setBackgroundResource(R.mipmap.more_kuang);
            delete_imageview.setImageResource(R.mipmap.delete);
            transfer_customer.setOnClickListener(this);
            delete_customer.setOnClickListener(this);
            edit_info.setOnClickListener(this);
        }

        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(android.R.color.white));
        setFocusable(true);
        setOutsideTouchable(true);

    }

    public View getDefaultView() {
        return defaultView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击编辑资料
            case R.id.edit_info:
                Intent intent = new Intent(context, AddCustomerActivity.class);
                intent.putExtra("customer", customer);
                intent.putExtra("flag", "editcustomer");
                context.startActivity(intent);
                CustomPopmenu.this.dismiss();
                Activity a = (Activity) context;
                a.finish();
                break;
            //点击放弃客户
            case R.id.delete_customer:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("放回公海池")
                        .setMessage("确定要放弃该客户吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DelCustomerService.getInstance(context).giveUpCustomer(customer_id, context);
                                dialog.dismiss();

                                CustomPopmenu.this.dismiss();
                                Activity a = (Activity) context;
                                a.finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false);
                Dialog dialog = builder.create();
                dialog.show();


                break;
            case R.id.transfer_customer:
                if (list1.size() != 0) {
                    list1.clear();
                }
                AlertDialog.Builder bu = new AlertDialog.Builder(context);
                final AlertDialog dia = bu.create();
                if (userlist1 != null && userlist1.size() != 0) {
                    userlist1.clear();
                }
                userlist1 = GetStaffHierarchyUtils.getSubordinate("28", context);
                View view = setSpinner2(dia);

                dia.setView(view, 20, 20, 20, 50);
                dia.setCanceledOnTouchOutside(true);//点击边界取消
                dia.show();


                break;

            default:
                break;
        }
    }

    private PickerView one_pv;
    private PickerView second_pv;
    private List<String> seconds;

    private View setSpinner2(final AlertDialog dia) {
        View view = LayoutInflater.from(context).inflate(R.layout.pickerview, null);

        one_pv = (PickerView) view.findViewById(R.id.pickerview_one_pv);
        second_pv = (PickerView) view.findViewById(R.id.pickerview_second_pv);
        Button ok = (Button) view.findViewById(R.id.pickerview_ok);
        List<String> data = new ArrayList<String>();
        seconds = new ArrayList<String>();
        Log.i("QQQQQQQ", userlist1.toString());
        for (int i = 0; i < userlist1.size(); i++) {
            data.add(userlist1.get(i).getName());
        }

        if (userlist2 != null && userlist2.size() != 0) {
            userlist2.clear();
        }
        userlist2 = GetStaffHierarchyUtils.getSubordinate(userlist1.get(userlist1.size() / 2).getId(), context);
        final String myid = SharedUtils.getUser("user", context).getId();
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
                userlist2 = GetStaffHierarchyUtils.getSubordinate(GetStaffHierarchyUtils.getId(text, context), context);
                for (int i = 0; i < userlist2.size(); i++) {
                    if (userlist2.get(i).getId().equals(myid)) {
                        userlist2.remove(i);
                    }
                }
                for (int i = 0; i < userlist2.size(); i++) {
                    seconds.add(userlist2.get(i).getName());
                }
                user_id = userlist2.get(userlist2.size() / 2).getId() + "";
                Log.i("CCCCCCCC", userlist2.get(userlist2.size() / 2).getName());
                Log.i("CCCCCCCC", user_id);
                second_pv.setData(seconds);
            }
        });

        second_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                Log.i("SSSSaaaa", text);
                user_id = GetStaffHierarchyUtils.getId(text, context);
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
                    UpdateCustomerService.getInstance(context).transferCustomer(context, customer.getId(), params);//转移
                    dia.dismiss();
                }

            }
        });
        return view;
    }

    private List<Achievement> list1 = new ArrayList<Achievement>();
    private List<Achievement> list2 = new ArrayList<Achievement>();

    private void setSpaner(int flag) {
        if (flag == 1) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
            adapter1.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);
            //加载数据源
            for (Achievement achievement : list1) {
                adapter1.add(achievement.getUsername());
            }
            //绑定适配器
            spinner1.setAdapter(adapter1);
            //给Spinner添加事件监听
            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    if (list2.size() != 0) {
                        list2.clear();
                        //重新配置空适配器，避免没有下属的主管 显示之前的员工
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter);

                    }
                    downdata(list1.get(position).getUserid(), 2);

                }

                public void onNothingSelected(AdapterView<?> parent) {
                }

            });
        } else {
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);

            //加载数据源
            Log.i("AAAAAAAAAA数据1", "" + list2.size());
            String myid = SharedUtils.getUser("user", context).getId();
            for (int i = 0; i < list2.size(); i++) {
                if (list2.get(i).getUserid().equals(myid)) {
                    list2.remove(i);
                }
            }
            Log.i("AAAAAAAAAA数据2", "" + list2.size());
            for (Achievement achievement : list2) {
                adapter2.add(achievement.getUsername());
            }
            //绑定适配器
            spinner2.setAdapter(adapter2);
            //给Spinner添加事件监听
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    Log.i(list2.get(position).getUserid() + "VVVVV" + list2.get(position).getUsername(), "");
                    user_id = list2.get(position).getUserid();
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }

            });

        }

    }

    private void downdata(String id, final int flag) {

        if (InternetUtils.internet(context)) {
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("achievement", "0");
            params.addHeader("Authorization", "Bearer" + " " + SharedUtils.getToken(context, "info"));
            httpUtils.send(HttpRequest.HttpMethod.GET, Constants.STAFF_URL + id + "/sub-list", params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String s = responseInfo.result;
                    JSONArray array = null;

                    try {
                        array = new JSONArray(s);
                        for (int i = 0; i < array.length(); i++) {
                            Achievement achievement = new Achievement();
                            achievement.setUsername(array.getJSONObject(i).getString("user_login"));
                            achievement.setUserid(array.getJSONObject(i).getString("id"));
                            if (flag == 1) {
                                list1.add(achievement);
                                setSpaner(1);

                            }
                            if (flag == 2) {

                                list2.add(achievement);
                                setSpaner(2);

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(HttpException e, String s) {
                }
            });
        }
    }
}
