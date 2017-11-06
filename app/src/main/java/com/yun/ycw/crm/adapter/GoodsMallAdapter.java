package com.yun.ycw.crm.adapter;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.entity.Goods;
import com.yun.ycw.crm.entity.GoodsModel;
import com.yun.ycw.crm.utils.GoodsCatgoryUtils;
import com.yun.ycw.crm.utils.ImageFileCache;
import com.yun.ycw.crm.utils.InternetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by scb on 2016/3/4.
 */
public class GoodsMallAdapter extends BaseAdapter {
    private List<GoodsModel> list;
    private Context context;
    private ImageLoader imageLoader ;
    public GoodsMallAdapter(List<GoodsModel> list, Context context) {
        this.list = list;
        this.context = context;
        imageLoader = new ImageLoader(Volley.newRequestQueue(context), new ImageFileCache());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public GoodsModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GoodsHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_goodsmall, null);
            holder = new GoodsHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (GoodsHolder) convertView.getTag();
        }
        GoodsModel goods = getItem(position);
        holder.name.setText(goods.getName());
        holder.brand.setText("品牌："+goods.getBrand());
        holder.specifications.setText("规格：" + goods.getSpecifications2());
        holder.price.setText("单价：" + goods.getPrice()+"/"+goods.getUnit());
        holder.icon.setDefaultImageResId(R.mipmap.def);
        holder.icon.setErrorImageResId(R.mipmap.def);
        holder.icon.setImageUrl(goods.getLogurl(), imageLoader);
        //加载图片
        return convertView;
    }

    public class GoodsHolder {
        @ViewInject(R.id.listview_goodsmall_icon)
        public NetworkImageView icon;
        @ViewInject(R.id.listview_goodsmall_name)
        public TextView name;
        @ViewInject(R.id.listview_goodsmall_specifications)
        public TextView specifications;
        @ViewInject(R.id.listview_goodsmall_price)
        public TextView price;
        @ViewInject(R.id.listview_goodsmall_brand)
        public TextView brand;
    }
}
