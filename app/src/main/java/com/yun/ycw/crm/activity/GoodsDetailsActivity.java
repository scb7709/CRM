package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.GoodsDetailsAdapter;
import com.yun.ycw.crm.entity.Goods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scb on 2016/2/1.
 */
public class GoodsDetailsActivity extends Activity {

    @ViewInject(R.id.goodsdetails_return_bu)
    private ImageButton returnn;
    @ViewInject(R.id.goodsdetails_goods_lv)
    private ListView goods_lv;
    private List<Goods> list;
    private Goods goods;
    private GoodsDetailsAdapter goodsDetailsAdapter;
    private Context mContext;
    private String mPageName="GoodsDetailsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodsdetails);
        ViewUtils.inject(this);
        mContext=this;
        setdate();
        goodsDetailsAdapter = new GoodsDetailsAdapter(list,GoodsDetailsActivity.this);
        goods_lv.setAdapter(goodsDetailsAdapter);
        returnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setdate() {
        list =(ArrayList<Goods>) getIntent().getSerializableExtra("goodslist");
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
}
