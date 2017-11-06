package com.yun.ycw.crm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.entity.User;

import java.util.List;

/**
 * Created by wdyan on 2016/3/29.
 */
public class CheckInSpinnerAdapter extends BaseAdapter {
    private List<User> userList;
    private Context context;

    public CheckInSpinnerAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_checkin, null);
        TextView name = (TextView) convertView.findViewById(R.id.spinner_name);
        name.setText(userList.get(position).getName());
        return convertView;
    }
}
