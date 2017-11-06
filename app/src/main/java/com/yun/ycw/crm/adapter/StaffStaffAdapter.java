package com.yun.ycw.crm.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.Customer;

import java.util.List;


/**
 * Created by scb on 2016/1/11.
 */
public class StaffStaffAdapter extends BaseAdapter {
    private List<Achievement> list;

    public StaffStaffAdapter(List<Achievement> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Achievement getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StaffTeamHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_staffsalesman, null);
            holder = new StaffTeamHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (StaffTeamHolder) convertView.getTag();
        }
        Achievement achievement = getItem(position);
        if (position % 2 == 0) {
            int color= Color.parseColor("#C8C8C8");
            holder.name.setBackgroundColor(color);
            holder.allcustomer.setBackgroundColor(color);

        } else {
            int color=Color.parseColor("#EDEDED");
            holder.name.setBackgroundColor(color);
            holder.allcustomer.setBackgroundColor(color);
        }
        holder.name.setText(achievement.getUsername());
        holder.allcustomer.setText(achievement.getAllcustomers());
        return convertView;
    }


    public class StaffTeamHolder {
        @ViewInject(R.id.listview_stafsalesman_man)
        public TextView name;
        @ViewInject(R.id.listview_stafsalesman_customer)
        public TextView allcustomer;

    }
}
