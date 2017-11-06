package com.yun.ycw.crm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.entity.Goods;
import com.yun.ycw.crm.utils.GoodsCatgoryUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by scb on 2016/1/11.
 */
public class GoodsDetailsAdapter extends BaseAdapter {
    private List<Goods> list;
    private Context context;

    public GoodsDetailsAdapter(List<Goods> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Goods getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private List<String> categorName = new ArrayList<String>();
    private List<String> goodsName = new ArrayList<String>();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GoodsHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_goodsdetails, null);
            holder = new GoodsHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (GoodsHolder) convertView.getTag();
        }
        //获取数据
        Goods goods = getItem(position);
        //判断当前的类别是否是第一次出现
        String categor = getCatgory(goods.getCatid());

        String goodsname = goods.getName();
        if (!categorName.contains(categor)) {
            categorName.add(categor);
            goodsName.add(goodsname);
        }
        //如果类别是第一次出现，那么就是显示，否则就消失
        if (goodsName.contains(goodsname)) {
            //设置为可见
            holder.categor.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
            holder.categor.setText(categor);
        } else {
            //设置为不可见
            holder.categor.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
        }

        holder.name.setText(goods.getName());
        holder.specifications.setText("规格：" + goods.getSpecifications2());
        holder.price.setText("单价：" + Double.parseDouble(goods.getPrice()) / Double.parseDouble(goods.getTotal()) + "元/份");
        holder.number.setText("X" + goods.getTotal());
        //加载图片
        Picasso.with(parent.getContext())
                .load(goods.getLogurl())//图片网址
                .placeholder(R.mipmap.def)//默认图标
                .into(holder.icon);//控件

        return convertView;
    }

    public class GoodsHolder {
        @ViewInject(R.id.listview_goodsdetails_icon)
        public ImageView icon;
        @ViewInject(R.id.listview_goodsdetails_category)
        public TextView categor;
        @ViewInject(R.id.listview_goodsdetails_name)
        public TextView name;
        @ViewInject(R.id.listview_goodsdetails_specifications)
        public TextView specifications;
        @ViewInject(R.id.listview_goodsdetails_price)
        public TextView price;
        @ViewInject(R.id.listview_goodsdetails_number)
        public TextView number;
        @ViewInject(R.id.listview_goodsdetails_line)
        public View line;

    }

    private String getCatgory(int j) {
        try {
            String s = GoodsCatgoryUtils.getCatgory();
            JSONArray array = new JSONArray(s);
            for (int i = 0; i < array.length(); i++) {
                JSONObject arrobj = array.getJSONObject(i);
                if (j == arrobj.getInt("id")) {
                    return arrobj.getString("name");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return " ";
    }
}
