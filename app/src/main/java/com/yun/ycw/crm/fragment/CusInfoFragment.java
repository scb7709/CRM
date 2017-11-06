package com.yun.ycw.crm.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Customer;
import com.yun.ycw.crm.utils.SharedUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 这是 客户详情的详细资料
 * Created by wdyan on 2016/2/1.
 */
public class CusInfoFragment extends Fragment implements View.OnClickListener {
    @ViewInject(R.id.cus_detail_from)
    private TextView cus_detail_from;//来源
    @ViewInject(R.id.cus_detail_boos_ed)
    private TextView cus_detail_boos;//工长名
    @ViewInject(R.id.cus_detail_phonenumber_ed)
    private TextView cus_detail_phonenumber;//电话
    @ViewInject(R.id.cus_detail_sex_ed)
    private TextView cus_detail_sex_ed;//性别
    @ViewInject(R.id.cus_detail_level_ed)
    private TextView cus_detail_level_ed;//级别
    @ViewInject(R.id.cus_detailr_birthplace_ed)
    private TextView cus_detailr_birthplace;//籍贯
    @ViewInject(R.id.cus_detail_company_ed)
    private TextView cus_detail_company;//公司
    @ViewInject(R.id.cus_detail_firsttime_ed)
    private TextView cus_detail_firsttime;//第一次拜访时间
    @ViewInject(R.id.cus_detailr_firstrecord_ed)
    private TextView cus_detailr_firstrecord;//第一次拜访记录
    @ViewInject(R.id.cus_detail_notes_ed)
    private TextView cus_detail_notes;//备注
    private Customer c;
    private String source;
    private String mPageName = "CusInfoFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cus_info, null);
        ViewUtils.inject(this, view);

        c = (Customer) getActivity().getIntent().getSerializableExtra("customer");
        cus_detail_from.setText(Constants.getSource(c.getSource()));
        //工长名
        cus_detail_boos.setText(c.getCustomer_name() + "");
        //电话
        cus_detail_phonenumber.setText(c.getPhonenumber() + "");
        cus_detail_phonenumber.setOnClickListener(this);//点击电话拨打
        //性别
        cus_detail_sex_ed.setText(c.getSex() + "");
        //级别
        cus_detail_level_ed.setText(c.getLevel() + "");
        //籍贯
        cus_detailr_birthplace.setText(c.getBirthplace() + "");
        //公司
        cus_detail_company.setText(c.getCompany() + "");
        //备注
        cus_detail_notes.setText(c.getNotes() + "");
        //首次拜访时间
        String originTime = c.getFirsttime();
        if (originTime.length() != 0) {
            String year = originTime.substring(0, 10);
            String time = originTime.substring(11, 16);
            String formated_time = year + " " + time;
            cus_detail_firsttime.setText(formated_time);
        }
        //首次拜访记录
        cus_detailr_firstrecord.setText(c.getFirstrecord() + "");
        return view;
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

    @Override
    public void onClick(View v) {
        final String phone = cus_detail_phonenumber.getText() + "";
        if (!phone.equals("") && phone != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("拨号确认")
                    .setMessage("您确定要拨打此电话吗?")
                    .setPositiveButton("拨号", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
