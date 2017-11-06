package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.ActiviteApplyAdapter;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.service.DelCustomerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by scb on 2016/3/25.
 */
public class ActiviteApplyActivity extends Activity {
    private List<String[]> list;
    private ActiviteApplyAdapter activiteApplyAdapter;
    @ViewInject(R.id.activiteapply_back)
    private ImageButton back;
    @ViewInject(R.id.activiteapply_listview)
    private ListView activiteapply_listview;//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activiteapply);
        ViewUtils.inject(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                setResult(Constants.ACTIVITE_APPLY, i);
                finish();
            }
        });
        list = (List<String[]>) getIntent().getSerializableExtra("apply");
        activiteApplyAdapter = new ActiviteApplyAdapter(list, ActiviteApplyActivity.this);
        activiteapply_listview.setAdapter(activiteApplyAdapter);
    }

    @OnItemClick({R.id.activiteapply_listview})
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final Map<String, String> map = new HashMap<String, String>();

        AlertDialog.Builder builder = new AlertDialog.Builder(ActiviteApplyActivity.this);
        builder.setTitle("同意激活")
                .setMessage("您是否同意" + list.get(position)[1] + "的激活申请？")
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        map.put("allow", "1");
                        DelCustomerService.getInstance(ActiviteApplyActivity.this).agreeactiviteCustomer(list.get(position)[13], map, ActiviteApplyActivity.this);
                        list.remove(position);
                        activiteApplyAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("不同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        map.put("allow", "0");
                        DelCustomerService.getInstance(ActiviteApplyActivity.this).agreeactiviteCustomer(list.get(position)[13], map, ActiviteApplyActivity.this);
                        list.remove(position);
                        activiteApplyAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);
        Dialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onPause() {
        Intent i = getIntent();
        setResult(Constants.ACTIVITE_APPLY, i);
        super.onPause();
    }
}
