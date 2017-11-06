package com.yun.ycw.crm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yun.ycw.crm.PullableView.PullToRefreshLayout;
import com.yun.ycw.crm.R;
import com.yun.ycw.crm.adapter.CheckInAdapter;
import com.yun.ycw.crm.adapter.CheckInSpinnerAdapter;
import com.yun.ycw.crm.comparator.IdComparator;
import com.yun.ycw.crm.constants.Constants;
import com.yun.ycw.crm.entity.CheckIn;
import com.yun.ycw.crm.entity.Leader;
import com.yun.ycw.crm.entity.User;
import com.yun.ycw.crm.utils.LoadingUtils;
import com.yun.ycw.crm.utils.SharedUtils;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 签到列表
 * Created by wdyan on 2016/3/28.
 */
public class CheckInListActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.checkIn_listview)
    private ListView checkIn_listview;//签到列表
    @ViewInject(R.id.add_checkIn)
    private ImageView add_checkIn;//新增签到button
    @ViewInject(R.id.checkIn_list_back)
    private ImageButton checkIn_list_back;//返回button
    @ViewInject(R.id.checkIn_title)
    private TextView checkIn_title;//签到列表的标题
    @ViewInject(R.id.spinnerTwo)
    private Spinner spinnerTwo;//级别二
    @ViewInject(R.id.spinnerThree)
    private Spinner spinnerThree;//级别三
    @ViewInject(R.id.spinner_time)
    private Spinner spinner_time;//时间选择
    @ViewInject(R.id.spinner_layout)
    private RelativeLayout spinner_layout;
    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout ptrl;//下拉刷新布局
    @ViewInject(R.id.show_error)
    private LinearLayout show_error;//没有数据的提示
    @ViewInject(R.id.tempLayout)
    private LinearLayout layout;//显示大图片的布局
    @ViewInject(R.id.tempImg)
    private ImageView tempImg;//显示的大图片

    private List<Map<User, List<User>>> list = new ArrayList<>();
    private Map<User, List<User>> map = new HashMap<>();
    private List<CheckIn> checkInList = new ArrayList<>();
    private Dialog dialog;
    private List<User> userThreeList;//存放级别3的user level=1时使用
    private List<User> userThreeListLevelTwo;//存放级别3的user level=2时使用

    private String level;
    private String id;
    private String name;
    private String urlId = "";//用于在请求网址里使用的id
    private PullToRefreshLayout pullToRefreshLayout;
    private String url;
    private BitmapUtils bitmapUtils;
    private String startTime;
    private String endTime;
    private int page = 1;//签到列表的分页
    private boolean UP;//是否上拉
    private boolean DOWN;//是否下拉

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_list);
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {
        bitmapUtils = new BitmapUtils(this);
        level = getIntent().getStringExtra("level");//获取级别
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        if (level.equals("one") || level.equals("two")) {
            checkIn_title.setText("下属签到");
        }
        if (SharedUtils.getUser("user", this).getLevel().equals("one") || SharedUtils.getUser("user", this).getLevel().equals("two")) {
            add_checkIn.setVisibility(View.GONE);
        }
        add_checkIn.setOnClickListener(this);//添加签到img
        checkIn_list_back.setOnClickListener(this);
        ptrl.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {//下拉刷新时
                page = 1;
                DOWN = true;
                initData(pullToRefreshLayout, urlId, startTime, endTime, page);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {//上拉加载时
                page = page + 1;
                UP = true;
                Log.i("aaaaa+Page", page + "");
                initData(pullToRefreshLayout, urlId, startTime, endTime, page);
            }
        });
    }

    private void init(PullToRefreshLayout ptr) {
        if (level.equals("one")) {
            //如果是何总，获取他的下属列表
            initDataLevelOne(ptr);
        } else if (level.equals("two")) {
            //如果是销售主管，获取他的下属列表
            initDataLevelTwo(ptr);
        } else if (level.equals("three")) {
            //如果是销售专员，获取他的数据
            spinnerTwo.setVisibility(View.INVISIBLE);
            spinnerThree.setVisibility(View.INVISIBLE);
            setSpinnerTime();
        }
    }

    //销售专员查看时调用的方法
    private void setSpinnerTime() {
        List<String> spinnerTimeList = new ArrayList<String>();
        spinnerTimeList.add("今日");
        spinnerTimeList.add("本周");
        spinnerTimeList.add("本月");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CheckInListActivity.this, R.layout.item_spinner_checkin, spinnerTimeList);
        adapter.setDropDownViewResource(R.layout.item_spinner_checkin);
        spinner_time.setAdapter(adapter);
        spinner_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {//今日签到统计
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String time = sdf.format(System.currentTimeMillis());
                    startTime = time;
                    endTime = time;
                    checkInList.clear();
                    page = 1;
                    initData(pullToRefreshLayout, urlId, startTime, endTime, 1);
                }

                if (position == 1) {//本周签到统计
                    //获取下周的第一天和第七天
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance(Locale.CHINA);
                    c.set(Calendar.DAY_OF_WEEK, 2);
                    //获取本周的第一天
                    startTime = sdf.format(c.getTime().getTime());
                    //获取本周的第七天
                    endTime = sdf.format(c.getTime().getTime() + (6 * 24 * 60 * 60 * 1000));
                    checkInList.clear();
                    page = 1;
                    initData(pullToRefreshLayout, urlId, startTime, endTime, 1);
                }

                if (position == 2) {//本月签到统计
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cStart = Calendar.getInstance();
                    cStart.set(Calendar.DAY_OF_MONTH, 1);//本月第一天
                    startTime = sdf.format(cStart.getTime());
                    Calendar cEnd = Calendar.getInstance();
                    cEnd.add(Calendar.MONTH, 1);//本月最后一天
                    cEnd.set(Calendar.DAY_OF_MONTH, 1);
                    cEnd.add(Calendar.DAY_OF_MONTH, -1);
                    endTime = sdf.format(cEnd.getTime());
                    checkInList.clear();
                    page = 1;
                    initData(pullToRefreshLayout, urlId, startTime, endTime, 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //销售主管查看时调用的方法
    private void initDataLevelTwo(final PullToRefreshLayout ptr) {
        spinnerThree.setVisibility(View.GONE);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        RequestParams params = new RequestParams();
        params.addHeader("Authorization", "Bearer " + SharedUtils.getToken(this, "info"));
        httpUtils.send(HttpRequest.HttpMethod.GET, Constants.LEVEL_RELATIONSHIP, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String myId = SharedUtils.getUser("user", CheckInListActivity.this).getId();
                try {
                    JSONObject LeaderObj = new JSONObject(responseInfo.result);
                    JSONArray levelTwoObjArray = LeaderObj.getJSONArray("subordinates");
                    for (int i = 0; i < levelTwoObjArray.length(); i++) {
                        JSONObject myObj = levelTwoObjArray.getJSONObject(i);
                        if (myObj.getString("id").equals(myId)) {//如果登录id与查询到的id相同
                            JSONArray levelThreeObjArray = myObj.getJSONArray("subordinates");
                            userThreeListLevelTwo = new ArrayList<User>();
                            for (int j = 0; j < levelThreeObjArray.length(); j++) {
                                JSONObject levelThreeObj = levelThreeObjArray.getJSONObject(j);
                                User user = new User();
                                user.setId(levelThreeObj.getString("id"));
                                user.setName(levelThreeObj.getString("user_login"));
                                userThreeListLevelTwo.add(user);
                            }
                            break;
                        }
                    }
                    Log.i("userThreeListLevelTwo", userThreeListLevelTwo.size() + "");
                    CheckInSpinnerAdapter adapter = new CheckInSpinnerAdapter(userThreeListLevelTwo, CheckInListActivity.this);
                    spinnerTwo.setAdapter(adapter);
                    spinnerTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            urlId = userThreeListLevelTwo.get(position).getId();
                            setSpinnerTime();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    //何总查看时调用的方法
    private void initDataLevelOne(final PullToRefreshLayout ptr) {
        HttpUtils utils = new HttpUtils();
        utils.configCurrentHttpCacheExpiry(0);
        RequestParams rParams = new RequestParams();
        rParams.addHeader("Authorization", "Bearer " + SharedUtils.getToken(this, "info"));
        utils.send(HttpRequest.HttpMethod.GET, Constants.LEVEL_RELATIONSHIP, rParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    map.clear();
                    list.clear();
                    JSONObject LeaderObj = new JSONObject(responseInfo.result);
                    JSONArray levelTwoObjArray = LeaderObj.getJSONArray("subordinates");
                    for (int i = 0; i < levelTwoObjArray.length(); i++) {
                        JSONObject levelTwoObj = levelTwoObjArray.getJSONObject(i);
                        User userLevelTwo = new User();
                        userLevelTwo.setId(levelTwoObj.getString("id"));
                        userLevelTwo.setName(levelTwoObj.getString("user_login"));
                        JSONArray levelThreeObjArray = levelTwoObj.getJSONArray("subordinates");
                        userThreeList = new ArrayList<User>();
                        for (int j = 0; j < levelThreeObjArray.length(); j++) {
                            JSONObject levelThreeObj = levelThreeObjArray.getJSONObject(j);
                            User userLevelThree = new User();
                            userLevelThree.setId(levelThreeObj.getString("id"));
                            userLevelThree.setName(levelThreeObj.getString("user_login"));
                            userThreeList.add(userLevelThree);
                        }
                        map.put(userLevelTwo, userThreeList);
                        list.add(map);
                    }

                    final List<User> spinnerTwoList = new ArrayList<User>();
                    Iterator iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry e = (Map.Entry) iterator.next();
                        User u = (User) e.getKey();
                        spinnerTwoList.add(u);
                    }

                    //map中的key是User对象，spinnerTwoList添加该对象，对该list按照user的id进行排序
                    //使得每次输出时顺序相同
                    IdComparator idComparator = new IdComparator();
                    Collections.sort(spinnerTwoList, idComparator);

                    CheckInSpinnerAdapter adapter = new CheckInSpinnerAdapter(spinnerTwoList, CheckInListActivity.this);
                    spinnerTwo.setAdapter(adapter);
                    spinnerTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            final List<User> spinnerThreeList = map.get(spinnerTwoList.get(position));
                            CheckInSpinnerAdapter adapter = new CheckInSpinnerAdapter(spinnerThreeList, CheckInListActivity.this);
                            spinnerThree.setAdapter(adapter);
                            spinnerThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    urlId = spinnerThreeList.get(position).getId();
                                    setSpinnerTime();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    private void initData(final PullToRefreshLayout ptr, String urlId, String startTime, String endTime, final int page) {
        dialog = LoadingUtils.createLoadingDialog(this, "正在加载");
        dialog.show();

        String token = SharedUtils.getToken(this, "info");
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0);
        RequestParams params = new RequestParams();
        params.addHeader("Authorization", "Bearer " + token);
        Log.i("Authorization", Constants.CHECKIN_URL + "?user_id=" + urlId);
        if (urlId.equals("")) {//没有user_id的情况下拼接url
            url = Constants.CHECKIN_URL + "?start_time=" + startTime + "&end_time=" + endTime + "&page=" + page;
        } else {//有user_id的情况下拼接url
            url = Constants.CHECKIN_URL + "?user_id=" + urlId + "&start_time=" + startTime + "&end_time=" + endTime + "&page=" + page;
        }
        httpUtils.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                try {
                    if (DOWN) {
                        checkInList.clear();
                    }
                    JSONArray checkInArr = new JSONArray(responseInfo.result);
                    if (checkInArr.length() == 0) {
                        if (page != 1) {
                        } else if (page == 1) {
                            show_error.setVisibility(View.VISIBLE);
                            ptrl.setVisibility(View.GONE);
                        }
                    } else {
                        ptrl.setVisibility(View.VISIBLE);
                        show_error.setVisibility(View.INVISIBLE);
                        for (int i = 0; i < checkInArr.length(); i++) {
                            JSONObject CheckInObject = checkInArr.getJSONObject(i);
                            CheckIn checkIn = new CheckIn();
                            checkIn.setTime(CheckInObject.getString("created_at").substring(0, 10) + " " + CheckInObject.getString("created_at").substring(11, 16));//设置签到的时间
                            checkIn.setLocation(CheckInObject.getString("address"));//设置签到的地址
                            JSONObject staffObj = CheckInObject.getJSONObject("staff");
                            checkIn.setName(staffObj.getString("user_login"));//设置签到人的姓名
                            JSONArray images = CheckInObject.getJSONArray("images");
                            checkIn.setCamUrl(images.get(0).toString());
                            checkIn.setContent(CheckInObject.getString("content"));
                            checkIn.setAvatarUrl(staffObj.getString("avatar"));
                            Log.i("AVATAR", staffObj.getString("avatar") + "");
                            checkInList.add(checkIn);
                        }

                        CheckInAdapter adapter = new CheckInAdapter(CheckInListActivity.this, checkInList, bitmapUtils, layout, tempImg);
                        checkIn_listview.setAdapter(adapter);


                        int length = checkInList.size();
                        Log.i("LENGTH", length + "");
                        int currentLength = length + checkInArr.length();
                        if (UP) {
                            checkIn_listview.setSelection(length - checkInArr.length());
                        }
                    }
                    if (ptr == null) {

                    } else {
                        if (UP) {//如果进行了上拉加载
                            ptr.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                            UP = false;//恢复原值
                        } else if (DOWN) {//如果进行了下拉刷新
                            ptr.refreshFinish(PullToRefreshLayout.SUCCEED);
                            DOWN = false;//恢复原值
                        }
                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("ERROR", s);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_checkIn://跳转 新增签到页面
                startActivity(new Intent(this, CheckInActivity.class));
                break;
            case R.id.checkIn_list_back://点击返回
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init(pullToRefreshLayout);
    }


}
