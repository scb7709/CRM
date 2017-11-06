package com.yun.ycw.crm.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.entity.Customer;
import com.yun.ycw.crm.entity.UndoneOrder;

import java.util.List;

/**
 * 待回款订单的listview的适配器
 * Created by wdyan on 2016/1/30.
 */
public class EAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<List<UndoneOrder>> orderList;

    private List<Customer> customerList;


    public EAdapter(Context context, List<List<UndoneOrder>> orderList, List<Customer> customerList) {
        this.context = context;
        this.orderList = orderList;
        this.customerList = customerList;
    }

    @Override
    public int getGroupCount() {
        return customerList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return orderList.get(groupPosition).size();
    }

    @Override
    public Customer getGroup(int groupPosition) {
        return customerList.get(groupPosition);
    }

    @Override
    public UndoneOrder getChild(int groupPosition, int childPosition) {
        return orderList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        CustomerViewholder customerViewholder = null;
        if (convertView == null) {
            customerViewholder = new CustomerViewholder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_undone_customer, null);
            customerViewholder.customer_name = (TextView) convertView.findViewById(R.id.customer_name);
            customerViewholder.total_order_num = (TextView) convertView.findViewById(R.id.total_order_num);
            convertView.setTag(customerViewholder);
        } else {
            customerViewholder = (CustomerViewholder) convertView.getTag();
        }
        customerViewholder.total_order_num.setText("共" + customerList.get(groupPosition).getTotal_num() + "单");
        customerViewholder.customer_name.setText(customerList.get(groupPosition).getCustomer_name() + "");
        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        OrderViewholder orderViewholder = null;
        if (convertView == null) {
            orderViewholder = new OrderViewholder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cus_order, null);
            orderViewholder.order_time = (TextView) convertView.findViewById(R.id.order_time);
            orderViewholder.order_status = (TextView) convertView.findViewById(R.id.order_status);
            orderViewholder.order_money = (TextView) convertView.findViewById(R.id.order_money);
            orderViewholder.order_deduct = (TextView) convertView.findViewById(R.id.order_deduct);
            convertView.setTag(orderViewholder);

        } else {
            orderViewholder = (OrderViewholder) convertView.getTag();
        }
        orderViewholder.order_time.setText(orderList.get(groupPosition).get(childPosition).getTime() + "");
        String s = orderList.get(groupPosition).get(childPosition).getLaststep();
        orderViewholder.order_status.setText(orderList.get(groupPosition).get(childPosition).getLaststep() + "");
        orderViewholder.order_money.setText(orderList.get(groupPosition).get(childPosition).getTotal_price() + "");
        orderViewholder.order_deduct.setText(orderList.get(groupPosition).get(childPosition).getCommission() + "");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

class CustomerViewholder {
    TextView customer_name;
    TextView total_order_num;
}

class OrderViewholder {
    TextView order_time;
    TextView order_status;
    TextView order_money;
    TextView order_deduct;
}
