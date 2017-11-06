package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.customview.ZoomImageView;
import com.yun.ycw.crm.entity.Goods;
import com.yun.ycw.crm.entity.UndoneOrder;
import com.yun.ycw.crm.service.DelCustomerService;
import com.yun.ycw.crm.utils.InternetUtils;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是订单详情的页面
 * Created by wdyan on 2016/2/1.
 */
public class OrderDetailsActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.order_detail_back)
    private ImageButton back;//
    @ViewInject(R.id.img1)
    private ImageView img1;//提交订单
    @ViewInject(R.id.img2)
    private ImageView img2;//配送中
    @ViewInject(R.id.img3)
    private ImageView img3;//交易完成
    @ViewInject(R.id.return_order)
    private LinearLayout Return_order;//退货订单

    @ViewInject(R.id.return_order_imgone)
    private ImageView return_order_imgone;//退货1图
    @ViewInject(R.id.return_order_imgtwo)
    private ImageView return_order_imgtwo;///退货商品2图
    @ViewInject(R.id.return_order_imgthree)
    private ImageView return_order_imgthree;///退货商品3图


    @ViewInject(R.id.img_one)
    private ImageView img_one;//自营商品1图
    @ViewInject(R.id.img_two)
    private ImageView img_two;///自营商品2图
    @ViewInject(R.id.img_three)
    private ImageView img_third;///自营商品3图
    @ViewInject(R.id.imgone)
    private ImageView imgone;//三方商品1图
    @ViewInject(R.id.imgtwo)
    private ImageView imgtwo;//三方商品21图
    @ViewInject(R.id.imgthree)
    private ImageView imgthird;//三方商品2图
    @ViewInject(R.id.orderdetail_line1)
    private ImageView line1;//
    @ViewInject(R.id.orderdetail_line2)
    private ImageView line2;//
    @ViewInject(R.id.order_detail_status)
    private TextView status;//订单状态

    @ViewInject(R.id.order_detail_status_voucher)
    private Button order_detail_status_voucher;//签收凭证

    @ViewInject(R.id.order_detail_num)
    private TextView order_num;//订单编号


    @ViewInject(R.id.order_detail_customer)
    private TextView order_detail_customer;//客户
    @ViewInject(R.id.order_detail_customerphone)
    private TextView order_detail_customerphone;//客户电话
    @ViewInject(R.id.order_detail_address)
    private TextView order_address;//配送地址
    @ViewInject(R.id.order_detail_cus_name)
    private TextView name;//收货人
    @ViewInject(R.id.order_detail_phone)
    private TextView phone;//收货电话
    @ViewInject(R.id.self_income)
    private TextView self_income;//自营商品金额
    @ViewInject(R.id.self_total_num)
    private TextView self_total_num;//自营商品总数
    @ViewInject(R.id.self)
    private LinearLayout self;//自营商品

    @ViewInject(R.id.return_order_income)
    private TextView return_order_income;//退货商品金额
    @ViewInject(R.id.return_order_total_num)
    private TextView return_order_total_num;//退货商品总数

    @ViewInject(R.id.third)
    private LinearLayout third;//三方商品
    @ViewInject(R.id.third_income)
    private TextView third_income;//三方商品金额
    @ViewInject(R.id.third_total_num)
    private TextView third_total_num;//三方商品总数

    @ViewInject(R.id.pay_status)
    private TextView pay_status;//支付状态
    @ViewInject(R.id.pay_method)
    private TextView pay_method;//支付方式
    @ViewInject(R.id.distribution_method)
    private TextView distribution_method;//配送方式
    @ViewInject(R.id.distribution_time)
    private TextView distribution_time;//配送时间
    @ViewInject(R.id.order_allMoney)
    private TextView order_allMoney;//总金额
    @ViewInject(R.id.order_exMoney)
    private TextView order_exMoney;//优惠

    @ViewInject(R.id.receivable_money)
    private TextView receivable_money;//应收金额

    @ViewInject(R.id.pay_money)
    private TextView pay_money;//支付金额

    @ViewInject(R.id.order_isCreated_time)
    private TextView order_isCreated_time;//下单时间

    @ViewInject(R.id.order_detail_refresh)
    private TextView refresh;//刷新
    @ViewInject(R.id.order_detail_deduct)
    private TextView order_detail_deduct;
    @ViewInject(R.id.order_deduct_name)
    private TextView order_deduct_name;
    @ViewInject(R.id.self_layout)
    private RelativeLayout self_layout;//自营商品清单
    @ViewInject(R.id.return_order_layout)
    private RelativeLayout return_order_layout;//退货商品清单
    @ViewInject(R.id.third_layout)
    private RelativeLayout third_layout;//第三方商品清单

    private List<Goods> goodsList, goodsListThird, return_orderList;
    private UndoneOrder order;
    private String orderid;
    private Dialog dialog;
    private Context mContext;
    private String mPageName = "OrderDetailsActivity";
    private String flag;
    private String return_order_totalprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ViewUtils.inject(this);
        mContext = this;

        orderid = getIntent().getStringExtra("orderid");
        try {
            flag = getIntent().getStringExtra("flag");
        } catch (NullPointerException n) {

        }
        goodsList = new ArrayList<Goods>();
        goodsListThird = new ArrayList<Goods>();
        return_orderList = new ArrayList<Goods>();
        refresh();
        setListener();

    }

    private void refresh() {

        if (goodsList.size() != 0 || goodsListThird.size() != 0 || return_orderList.size() != 0) {
            goodsList.clear();
            goodsListThird.clear();
            return_orderList.clear();
        }
        if (InternetUtils.internet(this)) {
            dialog = LoadingUtils.createLoadingDialog(OrderDetailsActivity.this, "正在加载");
            dialog.show();
            downdate();
        }
    }

    protected void setListener() {
        back.setOnClickListener(this);
        self_layout.setOnClickListener(this);
        third_layout.setOnClickListener(this);
        return_order_layout.setOnClickListener(this);
        refresh.setOnClickListener(this);
        order_detail_customerphone.setOnClickListener(this);
        phone.setOnClickListener(this);
        order_detail_status_voucher.setOnClickListener(this);
    }

    protected void downdate() {

        order = new UndoneOrder();
        Log.i("SSSSSSSSS", orderid);
        String token = SharedUtils.getToken(this, "info");
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addHeader("Authorization", "Bearer" + " " + token);
        String url = Constants.ORDER_BASE_URL + orderid;
        Log.i("URL", url);
        httpUtils.send(HttpRequest.HttpMethod.GET, Constants.ORDER_BASE_URL + orderid, params,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Toast.makeText(getApplicationContext(), arg1, Toast.LENGTH_LONG).show();
                        Log.i("EEEEE", "fangwenshibai");
                        dialog.dismiss();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        String s = arg0.result;
                        Log.i("EEEEEAA", s);
                        try {
                            JSONObject o = new JSONObject(s);
                            order.setId(o.getInt("id"));
                            order.setOrderid(o.getString("orderid"));
                            order.setLaststep(String.valueOf(o.getInt("laststep")));
                            // order.setVoucher(String.valueOf(o.getInt("voucher")));
                            order.setVoucher("http://pic8.nipic.com/20100623/55218_100905033361_2.jpg");
                            order.setTime(o.getString("time"));
                            order.setAddress(o.getString("address"));
                            order.setTruename(o.getString("truename"));//收货人
                            order.setTel(o.getString("tel"));
                            try {
                                order.setCustonmer(o.getJSONObject("userinfo").getString("truename"));
                                order.setCustomerphone(o.getJSONObject("userinfo").getString("username"));
                            } catch (JSONException j) {
                                order.setCustonmer("暂无");
                                order.setCustomerphone("暂无");
                            }
                            order.setPaytype(o.getString("paytype"));
                            order.setPayprice(o.getString("payprice"));
                            order.setDelivery_type(o.getInt("delivery_type"));
                            order.setDeliver_time(o.getString("deliver_time"));

                            order.setDelivery(o.getString("delivery"));
                            double d = o.getDouble("remote_price");
                            if (d > 0) {
                                order.setRemote_price(d + "");
                            } else {
                                order.setRemote_price("0");
                            }
                            order.setTotal_price(o.getString("total_price"));
                            order.setGiftprice(o.getString("giftprice"));
                            order.setPayprice(o.getString("payprice"));
                            order.setUpstairsfee(o.getString("upstairsfee"));
//
//
//  order.setCommission(o.getString("staff_income"));


                            JSONArray comArray = o.getJSONArray("staff_income");
                            //如果是主管和上级
                            if (SharedUtils.getUser("user", OrderDetailsActivity.this).getSuperior_id().equals("0") || SharedUtils.getUser("user", OrderDetailsActivity.this).getSuperior_id().equals("28")) {
                                if (comArray.length() == 0) {
                                    order.setCommission("0.00/0.00");
                                } else if (comArray.length() == 1) {
                                    order.setCommission("0.00/" + comArray.getJSONObject(0).getString("amount"));
                                } else {
                                    order.setCommission(comArray.getJSONObject(1).getString("amount") + "/" + comArray.getJSONObject(0).getString("amount"));
                                }
                            } else {
                                if (comArray.length() == 0) {
                                    order.setCommission("0.00");
                                } else {
                                    order.setCommission(comArray.getJSONObject(0).getString("amount"));
                                }
                            }
                            order.setPaid(o.getInt("paid"));
                            String str = o.getString("return_order");
                            if (!str.equals("null")) {
                                Return_order.setVisibility(View.VISIBLE);
                                JSONObject obj = o.getJSONObject("return_order");
                                return_order_totalprice = obj.getString("totalprice");
                                JSONArray jsonArray = obj.getJSONArray("products");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject Obj = jsonArray.getJSONObject(i);
                                    Goods goods = new Goods();
                                    goods.setCatid(Obj.getJSONObject("info").getInt("catid"));
                                    goods.setPrice(Obj.getString("price"));
                                    goods.setTotal(String.valueOf(Obj.getInt("total")));
                                    goods.setName(Obj.getJSONObject("info").getString("name"));
                                    goods.setIsthird(Obj.getJSONObject("info").getInt("isthird"));
                                    goods.setLogurl(Obj.getJSONObject("info").getString("logourl"));
                                    goods.setSpecifications2(Obj.getJSONObject("info").getString("specifications2"));
                                    return_orderList.add(goods);
                                }

                            } else {
                                Return_order.setVisibility(View.GONE);
                            }
                            JSONArray array = o.getJSONArray("products");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject Obj = array.getJSONObject(i);
                                Goods goods = new Goods();
                                goods.setCatid(Obj.getJSONObject("info").getInt("catid"));
                                goods.setPrice(Obj.getString("price"));
                                goods.setTotal(String.valueOf(Obj.getInt("total")));
                                goods.setName(Obj.getJSONObject("info").getString("name"));
                                goods.setIsthird(Obj.getJSONObject("info").getInt("isthird"));
                                goods.setLogurl(Obj.getJSONObject("info").getString("logourl"));
                                goods.setSpecifications2(Obj.getJSONObject("info").getString("specifications2"));
                                if (goods.getIsthird() == 0) {
                                    goodsList.add(goods);
                                } else {
                                    goodsListThird.add(goods);
                                }
                            }
                            setdate();
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("EEEEE", e.toString());
                            dialog.dismiss();
                        }
                    }
                });
    }

    //获取总金额
    private double getTotalMoney(List<Goods> list) {
        double money = 0.00;
        for (Goods g : list) {
            money += Double.parseDouble(g.getPrice());
        }
        BigDecimal b = new BigDecimal(money);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    protected void setdate() {
        try {
            if (Integer.parseInt(order.getLaststep()) >= 500) {
                img2.setImageResource(R.mipmap.circular);
                line1.setImageResource(R.mipmap.progress_bar_green);
            }
            if (Integer.parseInt(order.getLaststep()) >= 950 && Integer.parseInt(order.getLaststep()) <= 1200) {
                img3.setImageResource(R.mipmap.circular);
                line2.setImageResource(R.mipmap.progress_bar_green);
                order_detail_status_voucher.setVisibility(View.VISIBLE);
            }

            status.setText(Constants.getOrderStatus(Integer.parseInt(order.getLaststep())));
            order_num.setText(order.getOrderid());//订单编号
            order_address.setText(order.getAddress());//配送地址
            name.setText(order.getTruename());//收货人
            phone.setText(order.getTel());//收货电话
            order_detail_customer.setText(order.getCustonmer());
            order_detail_customerphone.setText(order.getCustomerphone());
            if (goodsList.size() == 0) {
                self.setVisibility(View.GONE);
                self_income.setText("￥0元");//自营商品金额
            } else {
                self_income.setText("￥" + getTotalMoney(goodsList) + "元");//自营商品金额
            }

            if (return_orderList.size() == 0) {

                return_order_income.setText("￥0元");//退货商品金额
            } else {
                return_order_income.setText("￥" + getTotalMoney(return_orderList) + "元");//退货商品金额
            }

            if (goodsListThird.size() == 0) {
                third.setVisibility(View.GONE);
                third_income.setText("￥0元");//三方商品金额
            } else {
                third.setVisibility(View.VISIBLE);
                third_income.setText("￥" + getTotalMoney(goodsListThird) + "元");//三方商品金额
            }


            self_total_num.setText("...共" + goodsList.size() + "件");//自营商品总数
            third_total_num.setText("...共" + goodsListThird.size() + "件");//三方商品总数
            return_order_total_num.setText("...共" + return_orderList.size() + "件");//退货商品总数

            if (order.getPaid() == 1) {
                pay_status.setText("已支付");//支付状态
            } else {
                pay_status.setText("未支付");//支付状态
            }
            pay_method.setText(Constants.getPayType(order.getPaytype()));//支付方式
            if (order.getDelivery_type() == 1) {
                distribution_method.setText("云材配送");//配送方式
            } else {
                distribution_method.setText("自提");//配送方式
            }

            String d_time = order.getDeliver_time().substring(0, 10) + " " + order.getDeliver_time().substring(11, 19);
            distribution_time.setText(d_time);//配送时间

            order_allMoney.setText("￥" + order.getTotal_price() + "元(运费" + order.getDelivery() + "元,远程费" + order.getRemote_price() + "元,上楼费" + order.getUpstairsfee() + "元)");//总金额

            order_exMoney.setText("￥" + order.getGiftprice() + "元");//优惠
            pay_money.setText("￥" + (Double.parseDouble(order.getPayprice())) + "元");//支付金额


            if (SharedUtils.getUser("user", OrderDetailsActivity.this).getSuperior_id().equals("0") || SharedUtils.getUser("user", OrderDetailsActivity.this).getSuperior_id().equals("28")) {
                order_deduct_name.setText("订单提成(主管/专员):");
            } else {
                order_deduct_name.setText("订单提成:");
            }
            order_detail_deduct.setText("￥" + order.getCommission() + "元");


            String c_time = order.getTime().substring(0, 10) + " " + order.getTime().substring(11, 19);
            order_isCreated_time.setText(c_time);//下单时间
            switch (goodsList.size()) {
                case 0:
                    break;
                case 1:
                    setImageOne();
                    break;
                case 2:
                    setImageTwo();
                    break;
                default:
                    setImageThree();
                    break;
            }

            switch (goodsListThird.size()) {
                case 0:
                    break;
                case 1:
                    setImgOne();
                    break;
                case 2:
                    setImgTwo();
                    break;
                default:
                    setImgThree();
                    break;
            }
            switch (return_orderList.size()) {
                case 0:
                    break;
                case 1:
                    setImageReturnOne();
                    break;
                case 2:
                    setImageReturnTwo();
                    break;
                default:
                    setImageReturnThird();
                    break;
            }
            String order_allmoney = order_allMoney.getText().toString();
            String order_allmoney2 = order_allmoney.substring(1, order_allmoney.indexOf("元"));
            String return_order_in = return_order_income.getText().toString();
            String return_order_in2 = return_order_in.substring(1, return_order_in.length() - 1);
            Log.i("AAAAAA", order_allmoney2);
            Log.i("AAABBB", return_order_in2);
            double receivable = Double.parseDouble(order_allmoney2) - Double.parseDouble(return_order_in2);
            BigDecimal b = new BigDecimal(receivable);
            receivable_money.setText("￥" + b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "元");//应收金额
        } catch (Resources.NotFoundException not) {
        }

    }

    private void setImgThree() {
        setImgTwo();
        imgthird.setVisibility(View.VISIBLE);
        setIcon(imgthird, goodsListThird.get(2).getLogurl());
    }

    private void setImgTwo() {
        setImgOne();
        imgtwo.setVisibility(View.VISIBLE);
        setIcon(imgtwo, goodsListThird.get(1).getLogurl());
    }

    private void setImgOne() {
        imgone.setVisibility(View.VISIBLE);
        setIcon(imgone, goodsListThird.get(0).getLogurl());
    }

    private void setImageThree() {
        setImageTwo();
        img_third.setVisibility(View.VISIBLE);
        setIcon(img_third, goodsList.get(2).getLogurl());
    }

    private void setImageTwo() {
        setImageOne();
        img_two.setVisibility(View.VISIBLE);
        setIcon(img_two, goodsList.get(1).getLogurl());
    }

    private void setImageOne() {
        img_one.setVisibility(View.VISIBLE);
        setIcon(img_one, goodsList.get(0).getLogurl());
    }

    private void setImageReturnThird() {
        setImageReturnTwo();
        return_order_imgthree.setVisibility(View.VISIBLE);
        setIcon(return_order_imgthree, return_orderList.get(2).getLogurl());
    }

    private void setImageReturnTwo() {
        setImageReturnOne();
        return_order_imgtwo.setVisibility(View.VISIBLE);
        setIcon(return_order_imgtwo, return_orderList.get(1).getLogurl());
    }

    private void setImageReturnOne() {
        return_order_imgone.setVisibility(View.VISIBLE);
        setIcon(return_order_imgone, return_orderList.get(0).getLogurl());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(OrderDetailsActivity.this, GoodsDetailsActivity.class);
        switch (v.getId()) {
            case R.id.order_detail_back://back按钮
                finish();
                break;
            case R.id.self_layout://自营商品清单
                if (goodsList.size() != 0) {
                    intent.putExtra("goodslist", (Serializable) goodsList);
                    startActivity(intent);
                }
                break;
            case R.id.third_layout://第三方商品清单
                if (goodsListThird.size() != 0) {
                    intent.putExtra("goodslist", (Serializable) goodsListThird);
                    startActivity(intent);
                }
                break;

            case R.id.return_order_layout://退货商品清单
                if (return_orderList.size() != 0) {
                    intent.putExtra("goodslist", (Serializable) return_orderList);
                    startActivity(intent);
                }
                break;
            case R.id.order_detail_customerphone://拨打下单人电话
                callPhone(order_detail_customerphone.getText().toString());
                break;
            case R.id.order_detail_phone://拨打收货人电话
                callPhone(phone.getText().toString());
                break;
            case R.id.order_detail_refresh://刷新当前页面
                refresh();
                break;
            case R.id.order_detail_status_voucher://查看签收凭证

                downloaddialog();
                break;


            default:
                break;
        }
    }
    AlertDialog dialogg;
    ZoomImageView imageView;
    protected void downloaddialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
        dialogg = builder.create();
        View view = LayoutInflater.from(OrderDetailsActivity.this).inflate(R.layout.dialog_voucher, null);
        imageView = (ZoomImageView) view.findViewById(R.id.order_detail_status_voucher_im);
        //  setIcon(imageView,  order.getVoucher());
        GetLocalOrNetBitmap(imageView, dialogg, order.getVoucher());
        Log.i("MMMMMMMMM", order.getVoucher());
        dialogg.setView(view, 20, 20, 20, 50);
        dialogg.setCanceledOnTouchOutside(true);//点击边界取消


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageView.setImage((Bitmap) msg.obj);
            dialogg.show();
        }
    };

    public void GetLocalOrNetBitmap(final ZoomImageView zoomImageView, final AlertDialog dialog, final String url) {
        new Thread() {
            @Override
            public void run() {
                super.run();


                Bitmap bitmap = null;
                InputStream in = null;
                BufferedOutputStream out = null;
                try

                {
                    in = new BufferedInputStream(new URL(url).openStream());
                    final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                    out = new BufferedOutputStream(dataStream);
                    copy(in, out);
                    out.flush();
                    byte[] data = dataStream.toByteArray();
                    Message message=Message.obtain();
                    message.obj=  BitmapFactory.decodeByteArray(data, 0, data.length);
                    handler.sendMessage(message);

                } catch (
                        IOException e
                        )

                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[2 * 1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    //加载图片
    private void setIcon(ImageView iv, String url) {

        if (url.length() != 0) {
            Picasso.with(OrderDetailsActivity.this)
                    .load(url)//图片网址
                    .placeholder(R.mipmap.def)//默认图标
                    .into(iv);//控件
        }
    }

    private void callPhone(final String phoneNumber) {
        if (!phoneNumber.equals("") && phoneNumber != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
            builder.setTitle("拨号确认")
                    .setMessage("您确定要拨打 " + phoneNumber + " 的电话号码吗?")
                    .setPositiveButton("拨号", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(mContext);
        if (flag != null) {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }
}
