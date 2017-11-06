package com.yun.ycw.crm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.activity.CustomerActivity;
import com.yun.ycw.crm.activity.CustomerDetailActivity;
import com.yun.ycw.crm.comparator.CharacterParser;
import com.yun.ycw.crm.comparator.PinyinComparator;
import com.yun.ycw.crm.entity.Achievement;
import com.yun.ycw.crm.entity.Customer;

import java.util.List;

/**
 * Created by scb on 2016/3/25.
 */
public class ActiviteApplyAdapter extends BaseAdapter {
    private String sortString;
    private List<String[]> list;
    private Context context;
    private CharacterParser characterParser;

    public ActiviteApplyAdapter(List<String[]> list, Context context) {
        this.list = list;
        this.context = context;

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();


    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String[] getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ActhievementTeamHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_activiteapply, null);
            holder = new ActhievementTeamHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ActhievementTeamHolder) convertView.getTag();
        }
        final String[] a = getItem(position);

        holder.time.setText(a[0]);
        holder.user.setText(a[1]);
        holder.customer.setText(a[2]);
        if (position % 2 == 0) {
            int color= Color.parseColor("#C8C8C8");
            holder.time.setBackgroundColor(color);
            holder.user.setBackgroundColor(color);
            holder.customer.setBackgroundColor(color);

        } else {
            int color=Color.parseColor("#EDEDED");
            holder.time.setBackgroundColor(color);
            holder.user.setBackgroundColor(color);
            holder.customer.setBackgroundColor(color);
        }

        holder.customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer customer = new Customer();
                customer.setCustomer_name(a[2]);
                customer.setId(a[12]);
                customer.setSource(Integer.parseInt(a[4]));
                customer.setPhonenumber(a[5]);
                customer.setSex(a[10]);
                customer.setLevel(a[11]);
                customer.setBirthplace(a[6]);
                customer.setCompany(a[7]);
                customer.setFirsttime(a[9]);
                customer.setFirstrecord(a[8]);
                customer.setNotes(a[14]);
                //如果客户的名字为空，就显示他的电话
                if (customer.getCustomer_name().equals("")) {
                    customer.setCustomer_name("#" + a[3]);
                }
                //汉字转换成拼音
                String pinyin = characterParser.getSelling(customer.getCustomer_name());
                sortString = pinyin.substring(0, 1).toUpperCase();
                customer.setNamePinyin(pinyin);

                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    customer.setSortLetters(sortString.toUpperCase());
                } else {
                    customer.setSortLetters("#");
                }
                Intent intent = new Intent(context, CustomerDetailActivity.class);
                intent.putExtra("headman_id", a[12]);
                intent.putExtra("customer", customer);
                intent.putExtra("isPublicOcean", "3");
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public class ActhievementTeamHolder {
        @ViewInject(R.id.listview_activiteapply_time)
        public TextView time;
        @ViewInject(R.id.listview_activiteapply_user)
        public TextView user;
        @ViewInject(R.id.listview_activiteapply_customer)
        public Button customer;
    }
}
