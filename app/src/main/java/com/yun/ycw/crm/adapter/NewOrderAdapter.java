package com.yun.ycw.crm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.UndoneOrder;

import java.util.List;

/**
 * Created by scb on 2016/3/21.
 */
public class NewOrderAdapter  extends BaseAdapter {
    private List<UndoneOrder> list;

    public NewOrderAdapter(List<UndoneOrder> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public UndoneOrder getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewOrderHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_neworder, null);
            holder = new NewOrderHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (NewOrderHolder) convertView.getTag();
        }
        UndoneOrder undoneOrder = getItem(position);
        holder.customername.setText(undoneOrder.getCustonmer());
        holder.time.setText(undoneOrder.getTime());
        holder.status.setText(undoneOrder.getLaststep());
        holder.money.setText(undoneOrder.getTotal_price());
        holder.deduct.setText(undoneOrder.getCommission());
        holder. neworder_address.setText(undoneOrder.getAddress());
        return convertView;
    }

    public class NewOrderHolder {

        @ViewInject(R.id.neworder_customer)
        public TextView customername;
        @ViewInject(R.id.neworder_time)
        public TextView time;
        @ViewInject(R.id.neworder_status)
        public TextView status;
        @ViewInject(R.id.neworder_money)
        public TextView money;
        @ViewInject(R.id.neworder_deduct)
        public TextView deduct;
        @ViewInject(R.id.neworder_address)
        public TextView neworder_address;

    }
}
