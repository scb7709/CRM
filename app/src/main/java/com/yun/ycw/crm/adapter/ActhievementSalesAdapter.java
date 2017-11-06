package com.yun.ycw.crm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.entity.Achievement;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by scb on 2016/2/25.
 */
public class ActhievementSalesAdapter extends BaseAdapter {
    private List<Achievement> list,list2;
    private BarData data;
    private BarDataSet dataSet;
    private Context context;

    public ActhievementSalesAdapter(List<Achievement> list, List<Achievement> list2, Context context) {
        this.list = list;
            this.list2 = list2;
            this.context = context;
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
            ActhievementSalesHolder holder = null;
            if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_achievementsalesman, null);
            holder = new ActhievementSalesHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ActhievementSalesHolder) convertView.getTag();
        }
        Achievement a = getItem(position);
        String user = a.getUsername();
        final Achievement b = getAchievement2(user);
        holder.allorder.setText(a.getAllorder());
        holder.allmoney.setText(a.getAllmoney());
        holder.allcustomer.setText(a.getAllcustomers());
        holder.username.setText(a.getUsername());
        holder.allcommission.setText(a.getAllcommission());
        //配置图表
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<String>();
        float customer=(Float.parseFloat(a.getAllcustomers())/Float.parseFloat(b.getAllcustomers())-1);
        float order=(Float.parseFloat(a.getAllorder())/Float.parseFloat(b.getAllorder())-1);
        float money=(Float.parseFloat(a.getAllmoney())/Float.parseFloat(b.getAllmoney())-1);
        float commission=(Float.parseFloat(a.getAllcommission())/Float.parseFloat(b.getAllcommission())-1);
        if (Float.parseFloat(b.getAllcustomers()) != 0) {

            entries.add(new BarEntry(customer * 100, 1));
        } else {
            entries.add(new BarEntry(0, 1));
        }
        if (Float.parseFloat(b.getAllorder()) != 0) {

            entries.add(new BarEntry(order * 100, 3));
        } else {
            entries.add(new BarEntry(0, 3));
        }
        if (Float.parseFloat(b.getAllmoney()) != 0) {

            entries.add(new BarEntry(money * 100, 5));
        } else {
            entries.add(new BarEntry(0, 5));
        }
        if (Float.parseFloat(b.getAllcommission()) != 0) {

            entries.add(new BarEntry(commission * 100, 7));
        } else {
            entries.add(new BarEntry(0, 7));
        }
        for (int i = 0; i <8; i++) {
          /*  if (i == 1)
                xVals.add("客    户");
            if (i == 3)
                xVals.add("订    单");
            if (i == 5)
                xVals.add("业    绩");
            if (i == 7)
                xVals.add("提    成");
            if (i != 1 && i != 3&& i != 5&& i != 7)*/
                xVals.add("");
        }
        Log.i("Xsize", xVals.size() + "");
        dataSet = new BarDataSet(entries, "较上月同期增长率（%）");
        dataSet.setColors(new int[]{Color.rgb(23, 155, 72)});
        data = new BarData(xVals, dataSet);
        holder. barChart.setData(data);
        holder. barChart.setDescription("");
        holder.  barChart.animateY(1000);
        //图表的点击事件
        holder.   barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                if (entry.getXIndex() == 1) {
                    Toast.makeText(context, "上月同期录入客户: " + b.getAllcustomers() + "个", Toast.LENGTH_SHORT).show();
                } else if (entry.getXIndex() == 3) {
                    Toast.makeText(context, "上月同期订单数: " + b.getAllorder()+"单", Toast.LENGTH_SHORT).show();
                }
                else if (entry.getXIndex() == 5) {
                    Toast.makeText(context, "上月同期业绩: " + b.getAllmoney()+"元", Toast.LENGTH_SHORT).show();
                }
                else if (entry.getXIndex() == 7) {
                    Toast.makeText(context, "上月同期提成: " + b.getAllcommission()+"元", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        return convertView;
    }
    private Achievement getAchievement2(String name) {
        for (Achievement achievement : list2) {
            if (achievement.getUsername().equals(name)) {
                return achievement;
            }
        }
        return new Achievement();
    }
    public class ActhievementSalesHolder {

        @ViewInject(R.id.listview_achievementsaleamen_username)
        public TextView username;
        @ViewInject(R.id.listview_achievementsaleamen__allmoney)
        public TextView allmoney;
        @ViewInject(R.id.listview_achievementsaleamen_allorder)
        public TextView allorder;
        @ViewInject(R.id.listview_achievementsaleamen__commission)
        public TextView allcommission;
        @ViewInject(R.id.listview_achievementsaleamen_allcustomer)
        public TextView allcustomer;
        @ViewInject(R.id.listview_achievementsaleamen_scheduleBarChart)
        private BarChart barChart;//柱状图
    }
}
