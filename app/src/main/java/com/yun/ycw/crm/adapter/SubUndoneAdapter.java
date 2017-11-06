package com.yun.ycw.crm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.entity.Leader;

import java.util.List;

/**
 * Created by wdyan on 2016/2/24.
 */
public class SubUndoneAdapter extends BaseAdapter {
    private Context context;
    private List<Leader> list;

    public SubUndoneAdapter(Context context, List<Leader> list) {
        this.context = context;
        this.list = list;
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
        SubUndoneViewholder viewholder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_subundone_listview, null);
            viewholder = new SubUndoneViewholder();
            viewholder.leaderName = (TextView) convertView.findViewById(R.id.item_subundone_name);
            viewholder.leaderNum = (TextView) convertView.findViewById(R.id.item_subundone_num);
            viewholder.leaderMoney = (TextView) convertView.findViewById(R.id.item_subundone_money);
            viewholder.next = (ImageView) convertView.findViewById(R.id.sub_undone_next);
            if (position == 0) {
                viewholder.next.setVisibility(View.INVISIBLE);
            } else {
                viewholder.next.setVisibility(View.VISIBLE);
            }
            convertView.setTag(viewholder);
        } else
            viewholder = (SubUndoneViewholder) convertView.getTag();
        viewholder.leaderName.setText(list.get(position).getName());
        viewholder.leaderNum.setText(list.get(position).getUndoneNum());
        viewholder.leaderMoney.setText(list.get(position).getUndoneMoney());

        return convertView;
    }
}

class SubUndoneViewholder {
    TextView leaderName;
    TextView leaderNum;
    TextView leaderMoney;
    ImageView next;
}
