package com.yun.ycw.crm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.entity.Customer;

import java.util.HashMap;
import java.util.List;


/**
 * Created by scb on 2016/1/11.
 */
public class CustomerOrderAdapter extends BaseAdapter {
    private List<Customer> list;
    private boolean isCheckBox; //是否多选

    public CustomerOrderAdapter(List<Customer> list, boolean isCheckBox) {
        this.list = list;
        this.isCheckBox = isCheckBox;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Customer getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CustomerOrderHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_customershow, null);
            holder = new CustomerOrderHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (CustomerOrderHolder) convertView.getTag();
        }
       final   Customer n = getItem(position);
        if (isCheckBox) {

           // Log.i("QQQQQQQQQQQ",isCheckBox+"");
            holder.checkbox.setVisibility(View.VISIBLE);
            if (n.isChoice()) {
                holder.checkbox.setChecked(true);
            } else {
                holder.checkbox.setChecked(false);
            }
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        n.setIsChoice(true);
                        Log.i("CCCCLLL", getItem(position).isChoice() + "");
                        Log.i("CCCCLLL", position+"");
                        notifyDataSetChanged();
                    }else {
                        n.setIsChoice(false);
                        Log.i("CCCCLLL", getItem(position).isChoice() + "");
                        Log.i("CCCCLLL", position+"");
                        notifyDataSetChanged();
                    }
                }
            });
        } else {
           // Log.i("QQQQQQAAAAA",isCheckBox+"");
            holder.checkbox.setVisibility(View.GONE);
        }

        if (n.getCustomer_name().substring(0, 1).equals("#")) {
            holder.name.setText(n.getPhonenumber());
        } else {
            holder.name.setText(n.getCustomer_name());
        }
        if (n.getAllorder_num() != null) {
            holder.allorder.setText("总下单数" + " " + n.getAllorder_num());
        } else {
            holder.allorder.setText("总下单数 0");
        }
        if (n.getAll_money() != null) {
            holder.allmoney.setText("总金额" + " " + n.getAll_money());
        } else {
            holder.allmoney.setText("总金额 0");
        }
        if (n.getWaitorder_num() != null) {
            holder.waitorder.setText("待回款订单数" + " " + n.getWaitorder_num());
        } else {
            holder.waitorder.setText("待回款订单数 0");
        }
        if (n.getWait_money() != null) {
            holder.waitmoney.setText("待回款金额" + " " + n.getWait_money());
        } else {
            holder.waitmoney.setText("待回款金额 0");
        }


        return convertView;
    }

    public List<Customer> getNewList() {
        Log.i("ZZZ",list.size()+"");
        return list;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }


    public class CustomerOrderHolder {
        @ViewInject(R.id.listview_customershow_name)
        public TextView name;
        @ViewInject(R.id.listview_customershow_allorder)
        public TextView allorder;
        @ViewInject(R.id.listview_customershow_allmoney)
        public TextView allmoney;
        @ViewInject(R.id.listview_customershow_waitorder)
        public TextView waitorder;
        @ViewInject(R.id.listview_customershow_waitmoney)
        public TextView waitmoney;
        @ViewInject(R.id.listview_customershow_checkbox)
        public CheckBox checkbox;
    }
}
