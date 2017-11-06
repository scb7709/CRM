package com.yun.ycw.crm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yun.ycw.crm.R;
import com.yun.ycw.crm.entity.UndoneOrder;
import com.yun.ycw.crm.utils.SharedUtils;

import java.util.List;

/**
 * 待回款订单的adapter
 * Created by Administrator on 2016/2/27.
 */
public class UndoneAdapter extends BaseAdapter {
    private List<UndoneOrder> list;
    private Context context;
    public UndoneAdapter(List<UndoneOrder> list, Context context) {
        this.list = list;
        this.context = context;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UndoneViewholder viewholder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_undone_order, null);
            viewholder = new UndoneViewholder();
            viewholder.order_time = (TextView) convertView.findViewById(R.id.order_time);
            viewholder.order_status = (TextView) convertView.findViewById(R.id.order_status);
            viewholder.order_money = (TextView) convertView.findViewById(R.id.order_money);
            viewholder.order_deduct = (TextView) convertView.findViewById(R.id.order_deduct);
            viewholder.order_name = (TextView) convertView.findViewById(R.id.order_name);
            convertView.setTag(viewholder);
        } else
            viewholder = (UndoneViewholder) convertView.getTag();
        viewholder.order_time.setText(list.get(position).getTime());
        viewholder.order_status.setText(list.get(position).getLaststep());
        viewholder.order_money.setText(list.get(position).getTotal_price());
        viewholder.order_deduct.setText(list.get(position).getCommission());
        viewholder.order_name.setText(list.get(position).getTruename());
        return convertView;
    }
}

class UndoneViewholder {
    TextView order_time;//下单时间
    TextView order_status;//订单状态
    TextView order_money;//订单金额
    TextView order_deduct;//订单提成
    TextView order_name;//订单所属客户的名字
}
